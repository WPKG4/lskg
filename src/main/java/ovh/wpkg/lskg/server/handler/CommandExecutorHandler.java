package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.CommandOutput;
import ovh.wpkg.lskg.server.types.WTPPayload;
import ovh.wpkg.lskg.server.types.payloads.ActionPayload;
import ovh.wpkg.lskg.server.types.payloads.MessagePayload;

import java.lang.reflect.Method;

@Slf4j
public class CommandExecutorHandler extends SimpleChannelInboundHandler<WTPPayload> {

    private WtpClientService clientService;

    public CommandExecutorHandler(WtpClientService clientService) {
        this.clientService = clientService;
    }

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

    private void handleActionPayload(ChannelHandlerContext ctx, ActionPayload payload) {
        String commandResult;

        if (CommandRegistry.hasCommand(payload.name)) {
            try {
                CommandRegistry.CommandEntry commandEntry = CommandRegistry.getCommand(payload.name);
                Method commandMethod = commandEntry.method();
                Object commandInstance = commandEntry.instance();

                Object result;
                if (commandMethod.getParameterCount() == 0) {
                    result = commandMethod.invoke(commandInstance);
                } else {
                    result = commandMethod.invoke(commandInstance, payload.parameters, ctx.channel());
                }
                commandResult = result.toString();
            } catch (Exception e) {
                log.error("Error executing command", e);
                commandResult = "Error: " + e.getMessage();
            }
        } else {
            commandResult = "Unknown command: " + payload.name;
        }

        log.debug("{}Command result: {}",ctx.channel().isActive(), commandResult);
        //ctx.writeAndFlush(new CommandOutput(commandResult, 0));
    }

    private void handleOtherPayload(ChannelHandlerContext ctx, WTPPayload msg) {
        ctx.writeAndFlush(new CommandOutput("Unsupported payload type", 1));
    }
}
