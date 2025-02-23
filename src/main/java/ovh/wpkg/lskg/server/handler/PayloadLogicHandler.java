package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.CommandContext;
import ovh.wpkg.lskg.server.command.CommandRegistry;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.services.ConnectedRatService;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.bi.BinaryPayload;
import ovh.wpkg.lskg.server.types.in.ActionInPayload;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;

import java.io.IOException;

import static ovh.wpkg.lskg.server.handler.WtpChannelAttributes.CLIENT_ID;

@Slf4j
public class PayloadLogicHandler extends SimpleChannelInboundHandler<WtpInPayload> {

    public WtpClientService wtpClientService;
    public CommandRegistry commandRegistry;
    public ConnectedRatService connectedRatService;

    public PayloadLogicHandler(WtpClientService wtpClientService, CommandRegistry commandRegistry, ConnectedRatService connectedRatService) {
        this.wtpClientService = wtpClientService;
        this.commandRegistry = commandRegistry;
        this.connectedRatService = connectedRatService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WtpInPayload msg) {
        switch (msg) {
            case MessagePayload messagePayload -> handleMessagePayload(ctx, messagePayload);
            case ActionInPayload actionPayload -> ctx.writeAndFlush(handleActionPayload(ctx ,actionPayload));
            case BinaryPayload binaryPayload -> handleBinaryPayload(ctx, binaryPayload);
            default -> throw new IllegalStateException("Unexpected value: " + msg);
        }
    }

    private ActionOutPayload handleActionPayload(ChannelHandlerContext ctx, ActionInPayload payload) {
        log.debug("[{}] <RECEIVE> a {} {}", ctx.channel().attr(CLIENT_ID).get(), payload.getName(),
                String.join(" ", payload.getParameters()
                        .entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).toList()));
        ActionOutPayload commandResult;

        if (commandRegistry.hasCommand(payload.getName())) {
            try {
                var commandEntry = commandRegistry.getCommand(payload.getName());

                var wtpClient = wtpClientService.getClient(ctx.channel());
                var context = new CommandContext(payload.getName(), wtpClient, payload.getParameters());

                Object result = commandEntry.method().invoke(commandEntry.instance(), context);

                commandResult = (result instanceof ActionOutPayload a)
                        ? a : new ActionOutPayload(payload.getName(), 1,"Invalid command result type.");
            } catch (Exception e) {
                String message = "Error executing command: " + e.getMessage();
                log.error(message, e);
                commandResult = new ActionOutPayload(payload.getName(), 2, message, message.length());
            }
        } else {
            commandResult = new ActionOutPayload(payload.getName(), 1, "Unknown command!");
        }
        return commandResult;
    }

    private void handleMessagePayload(ChannelHandlerContext ctx, MessagePayload payload) {
        log.debug("[{}] <RECEIVE> m [len {}]: {}", ctx.channel().attr(CLIENT_ID).get(),
                payload.getMessage().getBytes().length, payload.getMessage());

        redirectPayload(ctx, payload);
    }

    private void handleBinaryPayload(ChannelHandlerContext ctx, BinaryPayload payload) {
        log.debug("[{}] <RECEIVE> b [len {}]", ctx.channel().attr(CLIENT_ID).get(), payload.getBytes().length);

        redirectPayload(ctx, payload);
    }

    private void redirectPayload(ChannelHandlerContext ctx, WtpInPayload payload) {
        WtpClient client = wtpClientService.getClient(ctx.channel());

        if (client.getReceiveCallback() != null) {
            client.getReceiveCallback().onReceive(client, payload);
        } else {
            log.warn("[{}] Payload was not received by RAT client", client.id());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        WtpClient client = wtpClientService.getClient(ctx.channel());

        if (client != null && client.getErrorCallback() != null) {
            client.getErrorCallback().accept(new IOException("WTP client closed"));
        }

        if (client != null && connectedRatService.isClientRatByWtp(client)) {
            log.debug("[{}] RAT disconnected", client.id());
            connectedRatService.removeByWtpClient(client);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        WtpClient client = wtpClientService.getClient(ctx.channel());

        if (client.getErrorCallback() != null) {
            client.getErrorCallback().accept((Exception) cause);
        }
    }
}
