package ovh.wpkg.lskg.server.dto;

import io.netty.channel.Channel;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.WtpOutPayload;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@ToString
public class WtpClient {
    public final Channel channel;

    public boolean locked = false;
    ReceiveCallback receiveCallback = null;

    public interface ReceiveCallback {
        void onReceive(WtpClient client, WtpInPayload payload);
    }

    public void receiveData(ReceiveCallback callback) {
        receiveCallback = callback;
    }

    public void stopReceive() {
        receiveCallback = null;
    }

    public void lock() {
        log.debug("[{}] WTP client locked", id());
        locked = true;
    }

    public void unlock() {
        log.debug("[{}] WTP client unlocked", id());
        locked = false;
    }

    public Mono<Void> send(WtpOutPayload outPayload) {
        return Mono.create(sink -> channel.writeAndFlush(outPayload)
                .addListener(future -> sink.success()));
    }

    public String id() {
        return getChannel().id().asShortText();
    }
}
