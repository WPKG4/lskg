package ovh.wpkg.lskg.server.services;

import jakarta.inject.Singleton;
import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Singleton
@Getter
public class RatClientPoller {
    Sinks.Many<String> clientSink = Sinks.many().multicast().onBackpressureBuffer();

    public Flux<String> getClientIdFlux() {
        return clientSink.asFlux();
    }

}
