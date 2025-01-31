package ovh.wpkg.lskg.server.types.bi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ovh.wpkg.lskg.server.types.WtpInPayload;
import ovh.wpkg.lskg.server.types.WtpOutPayload;

@AllArgsConstructor
@Getter
public class BinaryPayload implements WtpInPayload, WtpOutPayload {
    public byte[] bytes;
}
