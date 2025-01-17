package ovh.wpkg.lskg.server.types.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ovh.wpkg.lskg.server.types.WtpInPayload;

import java.util.Map;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public @Data class ActionInPayload implements WtpInPayload {
    public String name;
    public Map<String, String> parameters;
}
