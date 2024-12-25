package ovh.wpkg.lskg.server.types.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ovh.wpkg.lskg.server.types.WTPPayload;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public @Data class ActionPayload extends WTPPayload {
    public String name;
    public Map<String, String> parameters;
}
