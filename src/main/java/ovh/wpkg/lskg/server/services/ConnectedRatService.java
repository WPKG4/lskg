package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Singleton
public class ConnectedRatService {

    private final List<RatClient> clients = new ArrayList<>();

    public void addClient(WtpClient client, UUID uuid, String username, String hostname) {
        log.debug("Registered client {}, username={}, hostname={}", uuid, username, hostname);
        clients.add(new RatClient(client, uuid, username, hostname, new ArrayList<>()));
    }

    public List<RatClient> getClientList() {
        return clients.stream().toList();
    }

    public RatClient getByUUID(UUID uuid) {
        for (RatClient client : clients) {
            if (client.getUuid().equals(uuid)) {
                return client;
            }
        }
        return null;
    }

    public RatClient getByChannel(Channel channel) {
        return clients.stream()
                .filter((it) ->
                        it.getMasterClient()
                                .getChannel()
                                .id()
                                .asShortText()
                                .equals(channel.id().asShortText())
                ).findFirst().orElseThrow();
    }

    public boolean isWtpClientRat(WtpClient wtpClient) {
        if (wtpClient == null)
            return false;
        return clients.stream()
                .anyMatch((it) ->
                        it.getMasterClient()
                                .getChannel()
                                .id()
                                .asShortText()
                                .equals(wtpClient.getChannel().id().asShortText())
                );
    }

    public void removeByChannel(Channel channel) {
        clients.removeIf(client -> client.getMasterClient().getChannel().id().asShortText()
                .equals(channel.id().asShortText()));
    }

    public void removeByWtpClient(WtpClient wtpClient) {
        clients.removeIf(client -> client.getMasterClient().getChannel().id().asShortText()
                .equals(wtpClient.getChannel().id().asShortText()));
    }
}
