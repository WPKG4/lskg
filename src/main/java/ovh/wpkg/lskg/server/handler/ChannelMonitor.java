package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.services.WtpClientService;

import static ovh.wpkg.lskg.server.handler.WtpChannelAttributes.CLIENT_ID;

@Slf4j
public class ChannelMonitor extends ChannelDuplexHandler {

    private final WtpClientService wtpClientService;

    public ChannelMonitor(WtpClientService wtpClientService) {
        this.wtpClientService = wtpClientService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        var id = ctx.channel().id().asShortText();

        ctx.channel().attr(CLIENT_ID).set(id);
        super.channelActive(ctx);

        log.debug("[{}] WTP client connected", id);
        wtpClientService.addClient(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

        var id = ctx.channel().attr(CLIENT_ID).get();
        log.debug("[{}] WTP client disconnected", id);
        wtpClientService.removeByChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        wtpClientService.removeByChannel(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }
}
