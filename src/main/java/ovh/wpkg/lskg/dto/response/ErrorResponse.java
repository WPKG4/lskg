package ovh.wpkg.lskg.dto.response;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Introspected
@Serdeable
@AllArgsConstructor
public @Data class ErrorResponse {
    private int code;
    private String message;
}