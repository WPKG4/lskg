package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.in.ActionInPayload;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;

import java.lang.reflect.Method;

@Slf4j
public class PayloadLogicHandler extends SimpleChannelInboundHandler<WtpInPayload> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WtpInPayload msg) {
        switch (msg) {
            case MessagePayload messagePayload -> {
                handleMessagePayload(messagePayload);
            }
            case ActionInPayload actionPayload -> {
                ctx.writeAndFlush(handleActionPayload(ctx ,actionPayload));
            }
            default -> throw new IllegalStateException("Unexpected value: " + msg);
        }
    }

    private ActionOutPayload handleActionPayload(ChannelHandlerContext ctx, ActionInPayload payload) {
        ActionOutPayload commandResult = null;

        if (CommandRegistry.hasCommand(payload.name)) {
            try {
                CommandRegistry.CommandEntry commandEntry = CommandRegistry.getCommand(payload.name);
                Method commandMethod = commandEntry.method();
                Object commandInstance = commandEntry.instance();

                Object result;
                if (commandMethod.getParameterCount() == 1) {
                    result = commandMethod.invoke(commandInstance, ctx.channel());
                } else {
                    result = commandMethod.invoke(commandInstance, payload.parameters, ctx.channel());
                }
                if (result instanceof ActionOutPayload) {
                    commandResult = (ActionOutPayload) result;
                } else {
                    String message = "Invalid command result type.";
                    commandResult = new ActionOutPayload(payload.name, 1, message, message.length());
                }
            } catch (Exception e) {
                String message = "Error executing command: " + e.getMessage();
                log.error(message, e);
                commandResult = new ActionOutPayload(payload.name, 2, message, message.length());
            }
        } else {
            String message = "Unknown command!";
            commandResult = new ActionOutPayload(payload.name, 1, message, message.length());
        }
        return commandResult;
    }

    private void handleMessagePayload(MessagePayload msg) {
        //TODO: zrób coś z message
    }
}
