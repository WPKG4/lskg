package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.CommandOutput;

@Slf4j
public class WtpOutboundHandler extends ChannelOutboundHandlerAdapter {

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        CommandOutput commandOutput = (CommandOutput) msg;

        log.debug("Outbound handler processing: {}", commandOutput.getResult());

        var buf = ctx.alloc().buffer(commandOutput.getResult().getBytes().length);
        buf.writeBytes(commandOutput.getResult().getBytes());

        ctx.writeAndFlush(buf, promise);
    }
}
