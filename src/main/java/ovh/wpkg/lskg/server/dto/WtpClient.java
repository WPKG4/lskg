package ovh.wpkg.lskg.server.dto;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.WtpOutPayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@Slf4j
public @Data class WtpClient {
    public final Channel channel;

    Sinks.Many<WtpInPayload> receive = Sinks.many().multicast().onBackpressureBuffer();
    Flux<WtpInPayload> receiveFlux = receive.asFlux();

    public boolean locked = false;

    public static @Data class ReceiveHandler {
        private Runnable disposeCallback;

        private boolean isDisposed = false;

        public void dispose() {
            isDisposed = true;

            if (disposeCallback != null)
                disposeCallback.run();
        }
    }

    public interface ReceiveCallback {
        void onReceive(WtpClient client, ReceiveHandler handler, WtpInPayload payload);
    }

    public Mono<Void> receiveData(ReceiveCallback callback) {
        var handler = new ReceiveHandler();
        var flux = receive.asFlux();

        var disposable = flux.subscribe((data) -> callback.onReceive(this, handler, data));

        if (handler.isDisposed()) {
            disposable.dispose();
            return Mono.empty();
        }

        return Mono.create(sink -> {
            handler.setDisposeCallback(() -> {
                disposable.dispose();
                sink.success();
            });
        });
    }

    public void lock() {
        log.debug("WTP client locked");
        locked = true;
    }

    public void unlock() {
        log.debug("WTP client unlocked");
        locked = false;
    }

    public Mono<Void> send(WtpOutPayload outPayload) {
        return Mono.create(sink -> {
            channel.writeAndFlush(outPayload)
                    .addListener(future -> sink.success());
        });
    }
}
