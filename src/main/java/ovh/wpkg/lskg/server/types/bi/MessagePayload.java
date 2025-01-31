package ovh.wpkg.lskg.server.types.bi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.WtpOutPayload;


@AllArgsConstructor
public @Data class MessagePayload implements WtpInPayload, WtpOutPayload {
    public String message;

    @Override
    public String toString() {
        return String.format("m %s\n%s", message.getBytes().length, message);
    }
}
