package ovh.wpkg.lskg.server.services;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.RatClient;
import ovh.wpkg.lskg.server.dto.WtpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Singleton
public class RatService {

    private final List<RatClient> clients = new ArrayList<>();

    public void addClient(WtpClient client, UUID uuid, String username, String hostname) {
        log.debug("Registered client {}, username={}, hostname={}", uuid, username, hostname);
        clients.add(new RatClient(client, uuid, username, hostname));
    }

    public RatClient[] getClientList() {
        return clients.toArray(RatClient[]::new);
    }

    public void removeByWtpClient(WtpClient wtpClient) {
        clients.removeIf(client -> client.getWtpClient().equals(wtpClient));
    }
}
