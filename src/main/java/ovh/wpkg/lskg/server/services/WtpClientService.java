package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Singleton
@Slf4j
public class WtpClientService {
    private List<WtpClient> clients = new ArrayList<>();

    public void addClient(Channel channel, UUID uuid, String username, String hostname){
        clients.add(new WtpClient(channel, uuid, username, hostname));
    }

    public void removeClient(UUID uuid){
        clients.removeIf(client -> client.getUuid().equals(uuid));
    }
}
