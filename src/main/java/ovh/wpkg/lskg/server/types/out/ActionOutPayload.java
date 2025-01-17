package ovh.wpkg.lskg.server.types.out;

import lombok.AllArgsConstructor;
import ovh.wpkg.lskg.server.types.WtpOutPayload;

@AllArgsConstructor
public class ActionOutPayload implements WtpOutPayload {
    public String name;
    public int errorCode;
    public String message;
    public int messageLength;

    @Override
    public String toString() {
        return String.format("a %s %s %d\n%s", name, errorCode == 0 ? "OK" : "ERROR", messageLength, message);
    }
}
