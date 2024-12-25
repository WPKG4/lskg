package ovh.wpkg.lskg.server.types.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ovh.wpkg.lskg.server.types.WTPPayload;


@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public @Data class MessagePayload extends WTPPayload {
    public String Message;
}
