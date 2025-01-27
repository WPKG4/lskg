package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import ovh.wpkg.lskg.server.types.in.ActionInPayload;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WtpDecoder extends ByteToMessageDecoder {

    private static final byte MESSAGE_PREFIX = 'm';
    private static final byte BINARY_PREFIX = 'b';
    private static final byte ACTION_PREFIX = 'a';
    private static final byte SUBSCRIBE_PREFIX = 's';
    private static final byte PING_PREFIX = 'p';
    private static final byte DEFAULT_DELIMITER = '\n';

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) { // TODO: refactor that nigga
        in.markReaderIndex();

        if (in.readableBytes() < 1) {
            return;
        }

        byte prefix = in.readByte();

        if (prefix == PING_PREFIX) {
            if (in.readableBytes() >= 1 && in.readByte() == DEFAULT_DELIMITER) {
                var buffer = ctx.alloc().buffer(1);
                log.debug("Received ping from: {}", ctx.channel().localAddress());
                buffer.writeByte((byte) 0x70);
                ctx.write(buffer);
                return;
            } else {
                throw new IllegalArgumentException("Invalid ping format");
            }
        }

        if (in.readableBytes() < 2 || in.readByte() != ' ') {
            throw new IllegalArgumentException("Invalid message format: missing space after prefix");
        }

        if (prefix == MESSAGE_PREFIX) {
            decodeMessage(in, out);
            return;
        }

        if (prefix == BINARY_PREFIX) {
            throw new UnsupportedOperationException("Binary payload type is not yet implemented");
        }

        if (prefix == ACTION_PREFIX) {
            decodeAction(in, out);
            return;
        }

        if (prefix == SUBSCRIBE_PREFIX) {
            throw new UnsupportedOperationException("Subscribe payload type is not yet implemented");
        }

        throw new IllegalArgumentException("Invalid message prefix");
    }

    private void decodeMessage(ByteBuf in, List<Object> out) {
        int messageLength = 0;
        boolean foundNewline = false;
        while (in.isReadable()) {
            byte b = in.readByte();
            if (b == DEFAULT_DELIMITER) {
                foundNewline = true;
                break;
            }
            if (b < '0' || b > '9') {
                throw new IllegalArgumentException("Invalid message length: must be numeric");
            }
            messageLength = messageLength * 10 + (b - '0');
        }

        if (!foundNewline) {
            in.resetReaderIndex();
            return;
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf messageBuf = in.readBytes(messageLength);
        String message = messageBuf.toString(StandardCharsets.UTF_8);

        out.add(new MessagePayload(message));
    }

    private void decodeAction(ByteBuf in, List<Object> out) {
        StringBuilder actionNameBuilder = new StringBuilder();
        while (in.isReadable()) {
            byte b = in.readByte();
            if (b == DEFAULT_DELIMITER) {
                break;
            }
            actionNameBuilder.append((char) b);
        }
        String actionName = actionNameBuilder.toString();

        StringBuilder packet = new StringBuilder();
        while (in.isReadable()) {
            byte b = in.readByte();
            packet.append((char) b);
            if (b == DEFAULT_DELIMITER && in.isReadable() && in.getByte(in.readerIndex()) == DEFAULT_DELIMITER) {
                in.readByte();
                break;
            }
        }

        String packetString = packet.toString();
        String[] rawParameters = packetString.split("\n");

        Map<String, String> parameters = new HashMap<>();

        for (String entry : rawParameters) {
            if (entry.trim().isEmpty()) continue;
            String[] parts = entry.split(": ");
            if (parts.length == 2) {
                parameters.put(parts[0].trim(), parts[1].trim());
            }
        }
        out.add(new ActionInPayload(actionName, parameters));
    }
}
