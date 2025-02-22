package ovh.wpkg.lskg.server.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class RatClient {
    public WtpClient masterClient;
    public UUID uuid;
    public String username;
    public String hostname;
    public List<WtpClient> sockets;
}
