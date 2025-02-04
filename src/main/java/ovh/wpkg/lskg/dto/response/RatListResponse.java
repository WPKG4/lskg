package ovh.wpkg.lskg.dto.response;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import ovh.wpkg.lskg.db.entities.RatInfo;

@Introspected
@Serdeable
@AllArgsConstructor
public @Data class RatListResponse {
    private RatInfo[] clientList;
}
