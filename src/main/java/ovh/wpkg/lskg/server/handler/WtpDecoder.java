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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WtpDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte payloadType = in.readByte();

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

        StringBuilder packetBuilder = new StringBuilder();
        do {
            char c = (char) in.readByte();
            packetBuilder.append(c);
        } while (!packetBuilder.toString().endsWith("\n\n"));

        String packetData = packetBuilder.toString().trim();

        // Split the data into action name and parameters
        String[] parts = packetData.split("\n", 2);
        String actionName = parts[0].trim();
        log.debug("Received action name: {}", actionName);

        Map<String, String> parameters = new HashMap<>();
        if (parts.length > 1) {
            String params = parts[1].trim();
            String[] paramLines = params.split("\n");

            for (String paramLine : paramLines) {
                paramLine = paramLine.trim();
                if (!paramLine.isEmpty()) {
                    String[] paramParts = paramLine.split(":", 2);
                    if (paramParts.length == 2) {
                        parameters.put(paramParts[0].trim(), paramParts[1].trim());
                    } else {
                        log.warn("Skipping invalid parameter: {}", paramLine);
                    }
                }
            }
        }

        // Create the ActionPayload object
        ActionPayload payload = new ActionPayload();
        payload.setName(actionName);
        payload.setParameters(parameters);

        out.add(payload); // Add the payload to the output list
    }


    private void handleSubscribe(ByteBuf in, List<Object> out) throws  Exception {
        out.add(new SubscribePayload());
    }

}
