package ovh.wpkg.lskg.server.services;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Singleton
@Getter
@Slf4j
public class RatClientPoller {
    Sinks.Many<String> clientSink = Sinks.many().multicast().onBackpressureBuffer();

    @Inject
    RatService ratService;

    @Inject
    WtpClientService wtpClientService;

    public Flux<String> getClientIdFlux() {
        return clientSink.asFlux();
    }

    public WtpClient poolClient(RatClient ratClient) {
        var notLocked = ratClient.getSockets()
                .stream()
                .filter(client -> !client.isLocked())
                .findFirst();


        if (notLocked.isPresent()) {
            log.debug("Unlocked clients: {}", notLocked.get());
            return wtpClientService.getClient(notLocked.get().getChannel().id().asShortText());
        }

        ratClient.getMasterClient().send(new MessagePayload("NEW")).block();

        // TODO: Make it better
        return wtpClientService.getClient(clientSink.asFlux().blockFirst());
    }

}
