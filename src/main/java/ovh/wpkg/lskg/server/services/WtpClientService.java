package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.WtpClient;

import java.util.*;

@Singleton
@Slf4j
public class WtpClientService {
    private final HashMap<String, WtpClient> clients = new HashMap<>();

    public void addClient(Channel channel) {
        log.debug("Adding new WTP Client");
        clients.put(channel.id().asShortText(), new WtpClient(channel));
    }

    public WtpClient getClient(String id) {
        return clients.get(id);
    }

    public WtpClient[] getClientList() {
        return clients.values().toArray(WtpClient[]::new);
    }

    public WtpClient getClient(Channel channel) {
        return clients.get(channel.id().asShortText());
    }

    public void removeByChannel(Channel channel) {
        log.debug("Removing WTP Client");
        clients.remove(channel.id().asShortText());
    }
}
