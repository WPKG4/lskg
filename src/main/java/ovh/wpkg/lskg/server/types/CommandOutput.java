package ovh.wpkg.lskg.server.types;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class CommandOutput {
    private String result;
    private int statusCode;
}
