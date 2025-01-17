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

        String reply;

        switch (wtpResponse) {
            case ActionOutPayload actionResponse -> {
                reply = actionResponse.toString();
                log.debug("Outbound handler processing action: {}", actionResponse.message);
            }
            case MessagePayload messagePayload -> {
                reply = messagePayload.toString();
                log.debug("Outbound handler processing message: {}", messagePayload.getMessage());
            }
            default -> throw new IllegalStateException("Unexpected value: " + wtpResponse);
        }

        var buf = ctx.alloc().buffer(reply.length());
        buf.writeBytes(reply.getBytes());

        ctx.writeAndFlush(buf, promise);
    }
}
