package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Singleton
@Slf4j
public class WtpClientService {

    // TODO: Add methods to add and delete clients
    private List<WtpClient> clients = new ArrayList<>();

    public void addClient(Channel channel, UUID uuid){
        clients.add(new WtpClient(channel, uuid));
    }

    public void removeClient(UUID uuid){
        clients.removeIf(client -> client.getUuid().equals(uuid));
    }
}
