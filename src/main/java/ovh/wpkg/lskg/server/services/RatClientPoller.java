package ovh.wpkg.lskg.server.services;

import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;
import reactor.core.publisher.Sinks;

@Singleton
@Getter
@Slf4j
@AllArgsConstructor
public class RatClientPoller {
    private final Sinks.Many<String> clientSink = Sinks.many().multicast().directBestEffort();

    private final ConnectedRatService connectedRatService;
    private final WtpClientService wtpClientService;

    public void notifyRegistered(WtpClient wtpClient) {
        clientSink.tryEmitNext(wtpClient.id());
    }

    public WtpClient poolClient(RatClient ratClient) {
        var notLocked = ratClient.getSockets()
                .stream()
                .filter(client -> !client.isLocked())
                .findFirst();

        if (notLocked.isPresent()) {
            log.debug("Detected unlocked client with id: {}", notLocked.get().id());
            return wtpClientService.getClient(notLocked.get().id());
        } else {
            ratClient.getMasterClient().send(new MessagePayload("NEW")).subscribe();

            var id = clientSink.asFlux().next().block();

            log.debug("New WTP client for RAT detected: {}", id);

            // TODO: Make it better
            return wtpClientService.getClient(id);
        }
    }

}
