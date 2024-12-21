package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.types.CommandOutput;
import ovh.wpkg.lskg.server.types.WpkgPayload;

import java.lang.reflect.Method;

@Slf4j
public class CommandExecutorHandler extends SimpleChannelInboundHandler<WpkgPayload> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WpkgPayload msg) {
        String payload = msg.getPayload();
        log.debug("Received payload: {}", payload);

        if (payload.startsWith("$lskg ")) {
            handleCommand(ctx, payload.substring("$lskg ".length()));
        } else {
            handleOtherPayload(ctx, msg);
        }
    }

    private void handleCommand(ChannelHandlerContext ctx, String commandPayload) {
        String[] parts = commandPayload.split(" ", 2);
        String commandName = parts[0];
        String commandArg = parts.length > 1 ? parts[1] : null;

        String commandResult;

        if (CommandRegistry.hasCommand(commandName)) {
            try {
                Method command = CommandRegistry.getCommand(commandName);
                Object result;
                if (command.getParameterCount() == 0) {
                    result = command.invoke(null);
                } else {
                    result = command.invoke(null, commandArg);
                }
                commandResult = result.toString();
            } catch (Exception e) {
                log.error("Error executing command", e);
                commandResult = "Error: " + e.getMessage();
            }
        } else {
            commandResult = "Unknown command: " + commandName;
        }

        log.debug("Command result: {}", commandResult);
        ctx.writeAndFlush(new CommandOutput(commandResult, 0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private void handleOtherPayload(ChannelHandlerContext ctx, WpkgPayload msg) {
        log.debug("Handling non-command payload: {}", msg.getPayload());

        ctx.writeAndFlush(new CommandOutput("Unsupported payload type", 1));
    }
}
