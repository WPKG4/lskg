package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.handler.decoder.ActionDecoder;
import ovh.wpkg.lskg.server.handler.decoder.MessageDecoder;

@Slf4j
public class WtpDecoder extends DelimiterBasedFrameDecoder  {

    public WtpDecoder(int maxFrameLength, ByteBuf delimiter) {
        super(maxFrameLength, delimiter);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decoded = (ByteBuf) super.decode(ctx, in);

        if (decoded != null) {
            log.debug("Frame decoded: {}", decoded.toString(io.netty.util.CharsetUtil.UTF_8));
            String[] header = decoded.toString(io.netty.util.CharsetUtil.UTF_8).split(" ");
            switch (header[0]) {
                case "m" -> {
                    ctx.pipeline().addAfter("HeaderDecoder", "MessageDecoder", new MessageDecoder(Integer.parseInt(header[1])));
                    ctx.pipeline().remove(this);
                    if (in.isReadable()) {
                        ctx.fireChannelRead(in.readBytes(in.readableBytes()));
                    }
                }
                case "a" -> {
                    ctx.pipeline().addAfter("HeaderDecoder", "ActionDecoder", new ActionDecoder(header[1]));
                    ctx.pipeline().remove(this);
                    if (in.isReadable()) {
                        ctx.fireChannelRead(in.readBytes(in.readableBytes()));
                    }
                }
                default -> throw new Exception(String.format("Packet type \"%s\" not implemented", header[0]));
            }
        }
        return null;
    }
}
