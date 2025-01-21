package ovh.wpkg.lskg.server.dto;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import ovh.wpkg.lskg.server.types.WtpOutPayload;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public @Data class WtpClient {
    public Channel channel;

    public Mono<Void> send(WtpOutPayload outPayload) {
        return Mono.create(sink -> {
            channel.writeAndFlush(outPayload)
                    .addListener(future -> sink.success());
        });
    }
}
