package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.types.CommandOutput;
import ovh.wpkg.lskg.server.types.WTPPayload;
import ovh.wpkg.lskg.server.types.payloads.ActionPayload;
import ovh.wpkg.lskg.server.types.payloads.MessagePayload;

import java.lang.reflect.Method;

@Slf4j
public class CommandExecutorHandler extends SimpleChannelInboundHandler<WTPPayload> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WTPPayload msg) {
        switch (msg) {
            case MessagePayload test -> {
                log.debug("Received MessagePayload: {}", test.Message);
            }
            case ActionPayload actionPayload -> {
                handleActionPayload(ctx ,actionPayload);
            }
            default -> throw new IllegalStateException("Unexpected value: " + msg);
        }
    }

    private void handleActionPayload(ChannelHandlerContext ctx, ActionPayload payload) { //TODO: naprawić tą starą funkcje, działa ale chujowo
        String commandResult;

        if (CommandRegistry.hasCommand(payload.name)) {
            try {
                Method command = CommandRegistry.getCommand(payload.name);
                Object result;
                if (command.getParameterCount() == 0) {
                    result = command.invoke(null);
                } else {
                    result = command.invoke(null, payload.parameters);
                }
                commandResult = result.toString();
            } catch (Exception e) {
                log.error("Error executing command", e);
                commandResult = "Error: " + e.getMessage();
            }
        } else {
            commandResult = "Unknown command: " + payload.name;
        }

        log.debug("Command result: {}", commandResult);
        ctx.writeAndFlush(new CommandOutput(commandResult, 0)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private void handleOtherPayload(ChannelHandlerContext ctx, WTPPayload msg) {
        ctx.writeAndFlush(new CommandOutput("Unsupported payload type", 1));
    }
}
