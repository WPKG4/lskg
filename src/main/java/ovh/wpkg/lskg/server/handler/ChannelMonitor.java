package ovh.wpkg.lskg.server.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.services.WtpClientService;

@Slf4j
public class ChannelMonitor extends ChannelDuplexHandler {

    private final WtpClientService wtpClientService;

    public ChannelMonitor(WtpClientService wtpClientService) {
        this.wtpClientService = wtpClientService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("WTP client connected");
        wtpClientService.addClient(ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("WTP client disconnected");
        wtpClientService.removeByChannel(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        wtpClientService.removeByChannel(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }
}
