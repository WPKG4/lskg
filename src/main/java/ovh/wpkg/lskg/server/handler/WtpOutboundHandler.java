package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WtpOutPayload;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;

import static ovh.wpkg.lskg.server.handler.WtpChannelAttributes.CLIENT_ID;

@Slf4j
public class WtpOutboundHandler extends ChannelOutboundHandlerAdapter {

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (!(msg instanceof WtpOutPayload wtpResponse)) {
            ctx.writeAndFlush(msg, promise);
            return;
        }

        var wtpId = ctx.channel().attr(CLIENT_ID).get();

        byte[] reply = switch (wtpResponse) {
            case ActionOutPayload a -> {
                log.debug("[{}] <SENT> a \"{}\" {} len {}: {}",
                        wtpId,
                        a.getName(),
                        a.getErrorCode() == 0 ? "OK" : "ERROR",
                        a.getMessage().length(),
                        a.getMessage());
                yield a.toString().getBytes();
            }
            case MessagePayload m -> {
                log.debug("[{}] <SENT> m [len {}]: {}",
                        wtpId,
                        m.getMessage().length(),
                        m.getMessage());
                yield m.toString().getBytes();
            }
            default -> throw new IllegalStateException("Unexpected value: " + wtpResponse);
        };

        var buf = ctx.alloc().buffer(reply.length);
        buf.writeBytes(reply);

        ctx.writeAndFlush(buf, promise);
    }
}
