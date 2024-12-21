package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WpkgPayload;

@Slf4j
public class WtpInboundHandler extends ChannelInboundHandlerAdapter {

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buffer = (ByteBuf) msg;

        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        String message = new String(data);

        log.debug("Inbound handler processing: {}", message);

        ctx.fireChannelRead(new WpkgPayload(message));
    }

}
