package ovh.wpkg.lskg.server.services;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Singleton
@Slf4j
public class WtpClientService {

    // TODO: Add methods to add and delete clients
    private Map<UUID, SocketChannel> clients = new HashMap<>();
}
