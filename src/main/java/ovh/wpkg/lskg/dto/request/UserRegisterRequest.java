package ovh.wpkg.lskg.dto.request;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Serdeable
@Introspected
public @Data class UserRegisterRequest {
    private String email,password;
}