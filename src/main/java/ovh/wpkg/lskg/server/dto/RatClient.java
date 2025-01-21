package ovh.wpkg.lskg.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
public @Data class RatClient {
    public WtpClient wtpClient;
    public UUID uuid;
    public String username;
    public String hostname;
}
