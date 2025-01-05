package ovh.wpkg.lskg.server.types.responses;

import lombok.AllArgsConstructor;
import ovh.wpkg.lskg.server.types.WTPResponse;

@AllArgsConstructor
public class ActionResponse extends WTPResponse {
    public String name;
    public int errorCode;
    public String message;
    public int messageLength;

    @Override
    public String toString() {
        return String.format("%s %s %d\n%s", name, errorCode == 0 ? "OK" : "ERROR", messageLength, message);
    }
}
