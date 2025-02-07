package ovh.wpkg.lskg.server.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import ovh.wpkg.lskg.server.dto.WtpClient;
import ovh.wpkg.lskg.server.types.out.ActionOutPayload;

import java.util.Map;

@AllArgsConstructor
public @Data class CommandContext {
    private String commandName;
    private WtpClient wtpClient;
    private Map<String, String> params;

    public boolean checkParams(String... keys) {
        if (params == null) {
            return true;
        }
        for (String key : keys) {
            if (!params.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    public String param(String key) {
        return params.get(key);
    }

    public ActionOutPayload response(int code, String message) {
        return new ActionOutPayload(commandName, code, message);
    }
}
