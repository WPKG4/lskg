package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.WtpClient;

import java.util.*;

@Singleton
@Slf4j
public class WtpClientService {
    private final List<WtpClient> clients = new ArrayList<>();

    public void addClient(Channel channel) {
        log.debug("Adding new WTP Client");
        clients.add(new WtpClient(channel));
    }

    public WtpClient[] getClientList() {
        return clients.toArray(WtpClient[]::new);
    }

    public WtpClient getClient(Channel channel) {
        return clients.stream()
                .filter(s -> s.getChannel() == channel)
                .findFirst()
                .orElse(null);
    }

    public void removeByChannel(Channel channel) {
        log.debug("Removing WTP Client");
        clients.removeIf(client -> client.getChannel().equals(channel));
    }
}
