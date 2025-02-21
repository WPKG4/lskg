package ovh.wpkg.lskg.dto.response;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Introspected
@Serdeable
@AllArgsConstructor
@Builder
public @Data class RatDto {
    private UUID uuid;
    private String username;
    private String hostname;
    private boolean connected;
    private String coreVersion;
    private String os;
    private String arch;
    private int connectedAmount;
}
