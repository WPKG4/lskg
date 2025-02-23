package ovh.wpkg.lskg.server.dto;

import io.netty.channel.Channel;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.WtpOutPayload;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static ovh.wpkg.lskg.server.handler.WtpChannelAttributes.CLIENT_ID;

@RequiredArgsConstructor
@Slf4j
@Getter
@Setter
@ToString
public class WtpClient {
    public final Channel channel;

    public boolean locked = false;
    ReceiveCallback receiveCallback = null;
    Consumer<Exception> errorCallback = null;

    public interface ReceiveCallback {
        void onReceive(WtpClient client, WtpInPayload payload);
    }

    public WtpClient receiveData(ReceiveCallback callback) {
        receiveCallback = callback;
        return this;
    }

    public WtpClient onError(Consumer<Exception> callback) {
        errorCallback = callback;
        return this;
    }

    public void stopReceive() {
        receiveCallback = null;
        errorCallback = null;
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
        return Mono.create(sink -> {
            channel.writeAndFlush(outPayload).addListener(future -> {
                if (future.isSuccess()) {
                    sink.success();
                } else {
                    sink.error(future.cause());
                }
            });
        });
    }

    public String id() {
        return channel.attr(CLIENT_ID).get();
    }
}
