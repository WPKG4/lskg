package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.config.ServerConfig;
import ovh.wpkg.lskg.server.handler.decoder.ActionDecoder;
import ovh.wpkg.lskg.server.handler.decoder.MessageDecoder;

@Slf4j
public class HeaderDecoder extends DelimiterBasedFrameDecoder  {

    public HeaderDecoder() {
        super(ServerConfig.MAX_HEADER_SIZE, ServerConfig.HEADER_DELIMITER);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decoded = (ByteBuf) super.decode(ctx, in);

        if (decoded != null) {
            log.debug("Header decoded: {}", decoded.toString(CharsetUtil.UTF_8));
            String[] header = decoded.toString(CharsetUtil.UTF_8).split(" ");
            switch (header[0]) {
                case "p" -> {
                    var buffer = ctx.alloc().buffer(1);
                    log.debug("Received ping from: {}", ctx.channel().localAddress());
                    buffer.writeByte((byte) 0x70);
                    ctx.write(buffer);
                }
                case "m" -> {
                    ctx.pipeline().addAfter(ctx.name(), "MessageDecoder", new MessageDecoder(Integer.parseInt(header[1])));
                    ctx.pipeline().remove(this);
                    if (in.isReadable()) {
                        return in.readBytes(in.readableBytes());
                    }
                }
                case "a" -> {
                    ctx.pipeline().addAfter(ctx.name(), "ActionDecoder", new ActionDecoder(header[1]));
                    ctx.pipeline().remove(this);
                    if (in.isReadable()) {
                        return in.readBytes(in.readableBytes());
                    }
                }
                default -> throw new Exception(String.format("Packet type \"%s\" not implemented", header[0]));
            }
        }
        return null;
    }
}
