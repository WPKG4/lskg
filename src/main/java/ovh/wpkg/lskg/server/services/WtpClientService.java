package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.dto.WtpClient;

import java.util.*;

import static ovh.wpkg.lskg.server.handler.WtpChannelAttributes.CLIENT_ID;

@Singleton
@Slf4j
public class WtpClientService {
    private final HashMap<String, WtpClient> clients = new HashMap<>();

    public void addClient(Channel channel) {
        clients.put(channel.attr(CLIENT_ID).get(), new WtpClient(channel));
    }

    public WtpClient getClient(String id) {
        return clients.get(id);
    }

    public WtpClient[] getClientList() {
        return clients.values().toArray(WtpClient[]::new);
    }

    public WtpClient getClient(Channel channel) {
        return clients.get(channel.attr(CLIENT_ID).get());
    }

    public void removeByChannel(Channel channel) {
        clients.remove(channel.attr(CLIENT_ID).get());
    }
}
