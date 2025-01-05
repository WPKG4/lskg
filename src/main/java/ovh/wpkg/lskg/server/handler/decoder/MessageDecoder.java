package ovh.wpkg.lskg.server.handler.decoder;

import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.handler.HeaderDecoder;
import ovh.wpkg.lskg.server.types.payloads.MessagePayload;

@Slf4j
public class MessageDecoder extends FixedLengthFrameDecoder {
    public MessageDecoder(int frameLength) {
        super(frameLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        ctx.pipeline().addBefore(ctx.name(), "HeaderDecoder", new HeaderDecoder());
        ctx.pipeline().remove(this);

        byte[] data = new byte[frame.readableBytes()];
        frame.readBytes(data);

        String parsedData = new String(data).trim();

        return new MessagePayload(parsedData);
    }
}
