package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WtpOutPayload;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;

@Slf4j
public class WtpOutboundHandler extends ChannelOutboundHandlerAdapter {

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (!(msg instanceof WtpOutPayload wtpResponse)) {
            ctx.writeAndFlush(msg, promise);
            return;
        }

        byte[] reply = switch (wtpResponse) {
            case ActionOutPayload a -> {
                log.debug("<SENT> [{}] <ACTION PAYLOAD> \"{}\" len {}: {}",
                        ctx.channel().id().asShortText(),
                        a.getName(),
                        a.getMessage().length(),
                        a.getMessage());
                yield a.toString().getBytes();
            }
            case MessagePayload m -> {
                log.debug("<SENT> [{}] <MESSAGE PAYLOAD> len {}: {}",
                        ctx.channel().id().asShortText(),
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
