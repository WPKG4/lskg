package ovh.wpkg.lskg.server.handler.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.payloads.ActionPayload;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ActionDecoder extends DelimiterBasedFrameDecoder {

    private final String actionName;

    public ActionDecoder(String actionName) {
        super(Integer.MAX_VALUE, Unpooled.wrappedBuffer(new byte[]{'\n', '\n'})); // TODO: niewiem czy tu powinny być 3x \n czy 2x \n ale działa
        this.actionName = actionName;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception { // TODO: brak parametrów? to się spierdoli
        if (in.readableBytes() < 2) {
            return null;
        }

        boolean foundDelimiter = false;
        int i = 0;

        while (i < in.readableBytes() - 1) {
            if (in.getByte(i) == '\n' && in.getByte(i + 1) == '\n') {
                foundDelimiter = true;
                break;
            }
            i++;
        }

        if (!foundDelimiter) {
            return null;
        }

        ByteBuf packet = in.readSlice(i);

        String packetString = packet.toString(CharsetUtil.UTF_8);

        String[] rawParameters = packetString.split("\n");

        Map<String, String> parameters = new HashMap<>();

        for (String entry : rawParameters) {
            String[] parts = entry.split(": ");
            if (parts.length == 2) {
                parameters.put(parts[0], parts[1]);
            }
        }

        ctx.fireChannelRead(new ActionPayload(actionName, parameters));
        return null;
    }
}
