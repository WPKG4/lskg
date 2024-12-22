package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.payloads.ActionPayload;
import ovh.wpkg.lskg.server.types.payloads.BinaryPayload;
import ovh.wpkg.lskg.server.types.payloads.MessagePayload;
import ovh.wpkg.lskg.server.types.payloads.SubscribePayload;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class WtpDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte payloadType = in.readByte();
        
        log.debug("Inbound handler processing: {}", payloadType);
        switch (payloadType) {
            case 'm' -> handleMessage(in, out);
            case 'b' -> handleBinary(in, out);
            case 'a' -> handleAction(in, out);
            case 's' -> handleSubscribe(in, out);
            default -> throw new IllegalStateException("Unexpected value: " + payloadType);
        }
    }

    private void handleMessage(ByteBuf in, List<Object> out) throws Exception {

        StringBuilder lengthBuilder = new StringBuilder();
        while (in.isReadable()) {
            char c = (char) in.readByte();
            if (c == '\n') {
                break;
            }
            lengthBuilder.append(c);
        }

        int messageLength;
        try {
            messageLength = Integer.parseInt(lengthBuilder.toString().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid message length format", e);
        }

        if (in.readableBytes() < messageLength) {
            in.resetReaderIndex();
            return;
        }

        ByteBuf messageBytes = in.readSlice(messageLength);

        String message = messageBytes.toString(StandardCharsets.US_ASCII);
        log.debug("Received message: {}", message);

        MessagePayload payload = new MessagePayload();
        payload.setMessage(message);

        out.add(payload);
    }


    private void handleBinary(ByteBuf in, List<Object> out) throws Exception {
        out.add(new BinaryPayload());
    }

    private void handleAction(ByteBuf in, List<Object> out) throws Exception {
        out.add(new ActionPayload());
    }

    private void handleSubscribe(ByteBuf in, List<Object> out) throws  Exception {
        out.add(new SubscribePayload());
    }

}
