package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.CommandOutput;
import ovh.wpkg.lskg.server.types.WpkgPayload;

@Slf4j
public class CommandExecutorHandler extends SimpleChannelInboundHandler<WpkgPayload> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WpkgPayload msg) {
        log.debug("Executing {}", msg.getPayload());

        String commandResult = executeCommand(msg);

        log.debug("Command result {}", commandResult);

        ctx.writeAndFlush(new CommandOutput(commandResult, 0));
    }

    private String executeCommand(WpkgPayload payload) {
        return "Executed: " + payload.getPayload();
    }
}
