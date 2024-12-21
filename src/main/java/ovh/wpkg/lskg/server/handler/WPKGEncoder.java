package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ovh.wpkg.lskg.server.types.CommandOutput;

public class WPKGEncoder extends MessageToByteEncoder<CommandOutput> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CommandOutput msg, ByteBuf out) {
        // Zapisanie danych do ByteBuf
        byte[] resultBytes = msg.getResult().getBytes();
        out.writeInt(resultBytes.length); // Nagłówek
        out.writeBytes(resultBytes); // Payload
    }
}
