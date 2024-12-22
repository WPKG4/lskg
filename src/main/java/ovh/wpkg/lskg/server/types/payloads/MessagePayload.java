package ovh.wpkg.lskg.server.types.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ovh.wpkg.lskg.server.types.WTPPayload;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public @Data class MessagePayload extends WTPPayload {
    public String Message;
}
