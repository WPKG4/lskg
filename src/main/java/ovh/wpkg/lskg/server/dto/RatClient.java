package ovh.wpkg.lskg.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public @Data class RatClient {
    public WtpClient masterClient;
    public UUID uuid;
    public String username;
    public String hostname;
    public List<WtpClient> sockets;
}
