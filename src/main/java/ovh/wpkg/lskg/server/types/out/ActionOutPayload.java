package ovh.wpkg.lskg.server.types.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import ovh.wpkg.lskg.server.types.WtpOutPayload;

@AllArgsConstructor
public @Data class ActionOutPayload implements WtpOutPayload {
    public String name;
    public int errorCode;
    public String message;
    public int messageLength;

    public ActionOutPayload(String name, int errorCode, String message) {
        this.name = name;
        this.errorCode = errorCode;
        this.message = message;
        this.messageLength = message.getBytes().length;
    }

    @Override
    public String toString() {
        return String.format("a %s %s %d\n%s", name, errorCode == 0 ? "OK" : "ERROR", messageLength, message);
    }
}
