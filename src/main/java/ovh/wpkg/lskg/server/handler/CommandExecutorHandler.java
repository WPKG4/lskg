package ovh.wpkg.lskg.server.handler;

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
            case MessagePayload messagePayload -> {
                ctx.writeAndFlush(handleMessagePayload(messagePayload));
            }
            case ActionPayload actionPayload -> {
                ctx.writeAndFlush(handleActionPayload(ctx ,actionPayload));
            }
            default -> throw new IllegalStateException("Unexpected value: " + msg);
        }
    }

    private CommandOutput handleActionPayload(ChannelHandlerContext ctx, ActionPayload payload) {
        CommandOutput commandResult = null;

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
                commandResult = (CommandOutput) result;
            } catch (Exception e) {
                log.error("Error executing command", e);
            }
        } else {
            commandResult = new CommandOutput("Unknown command!", 1);
        }
        return commandResult;
    }

    private CommandOutput handleMessagePayload(MessagePayload msg) {
        return new CommandOutput(msg.Message, 0);
    }
}
