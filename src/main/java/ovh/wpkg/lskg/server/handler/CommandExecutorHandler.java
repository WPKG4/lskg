package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ovh.wpkg.lskg.server.types.CommandOutput;
import ovh.wpkg.lskg.server.types.WPKGPayload;

public class CommandExecutorHandler extends SimpleChannelInboundHandler<WPKGPayload> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WPKGPayload msg) {
        // Obsługa logiki
        String commandResult = executeCommand(msg);

        // Wysłanie wyniku w dół pipeline (odpowiedź)
        ctx.writeAndFlush(new CommandOutput(commandResult, 0));
    }

    private String executeCommand(WPKGPayload payload) {
        // Tu napisz logikę przetwarzania
        return "Executed: " + new String(payload.getPayload());
    }
}
