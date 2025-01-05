package ovh.wpkg.lskg.server.services;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
public @Data class WtpClient {
    public Channel channel;
    public UUID uuid;
    public String username;
    public String hostname;
}
