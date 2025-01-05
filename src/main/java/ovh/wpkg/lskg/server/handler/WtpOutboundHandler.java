package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WTPResponse;
import ovh.wpkg.lskg.server.types.responses.ActionResponse;

import java.util.Objects;

@Slf4j
public class WtpOutboundHandler extends ChannelOutboundHandlerAdapter {

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        WTPResponse WTPResponse = (WTPResponse) msg;

        String reply = "";

        if (Objects.requireNonNull(WTPResponse) instanceof ActionResponse actionResponse) {
            reply = actionResponse.toString();
        } else {
            throw new IllegalStateException("Unexpected value: " + WTPResponse);
        }

        log.debug("Outbound handler processing: {}", actionResponse.message);

        var buf = ctx.alloc().buffer(reply.length());
        buf.writeBytes(reply.getBytes());

        ctx.writeAndFlush(buf, promise);
    }
}
