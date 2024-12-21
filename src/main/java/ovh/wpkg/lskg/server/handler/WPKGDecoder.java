package ovh.wpkg.lskg.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import ovh.wpkg.lskg.server.types.WPKGPayload;

import java.util.Arrays;
import java.util.List;

public class WPKGDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Sprawdź, czy mamy wystarczająco danych, np. nagłówek i payload
        if (in.readableBytes() < 4) { // Minimalny rozmiar nagłówka
            return;
        }

        // Odczytanie danych z ByteBuf
        int payloadLength = in.readInt(); // Przykład: długość payloadu
        if (in.readableBytes() < payloadLength) {
            in.resetReaderIndex();
            return;
        }

        // Tworzenie obiektu z danych
        byte[] payload = new byte[payloadLength];
        in.readBytes(payload);
        System.out.println(Arrays.toString(payload));
        WPKGPayload wpkgPayload = new WPKGPayload(payload);

        // Przekazanie obiektu do kolejnego handlera
        out.add(wpkgPayload);
    }
}

