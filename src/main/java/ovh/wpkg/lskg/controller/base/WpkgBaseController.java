package ovh.wpkg.lskg.controller.base;

import jakarta.inject.Inject;
import ovh.wpkg.lskg.server.services.ConnectedRatService;
import ovh.wpkg.lskg.server.services.RatClientPoller;
import ovh.wpkg.lskg.server.types.bi.MessagePayload;

import java.util.Map;

public class WpkgBaseController {
    @Inject
    protected ConnectedRatService connectedRatService;

    @Inject
    protected RatClientPoller ratClientPoller;

    protected MessagePayload commandPayload(String name, Map<String,String> args) {
        StringBuilder payload = new StringBuilder();
        
        payload.append(name).append("\n");

        for (String arg : args.keySet()) {
            String value = args.get(arg);
            payload.append(arg)
                    .append(":")
                    .append(value.getBytes().length)
                    .append(":")
                    .append(value)
                    .append("\n");
        }
        payload.deleteCharAt(payload.length() - 1);

        return new MessagePayload(payload.toString());
    }
}
