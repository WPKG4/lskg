package ovh.wpkg.lskg.server.command.commands;

import io.netty.channel.Channel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.responses.ActionResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
@Slf4j
public class DefaultCommands {

    @Inject
    public WtpClientService wtpClientService;

    @Command(name = "hello")
    public ActionResponse hello(Channel channel) {
        String message = "Hello World!";
        return new ActionResponse("hello", 0, message, message.length());
    }

    @Command(name = "echo")
    public ActionResponse echo(Map<String, String> params, Channel channel) {
        StringBuilder result = new StringBuilder("Echo: ");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(" ");
        }
        String message = result.toString().trim();
        return new ActionResponse("echo", 0, message, message.length());
    }

    @Command(name = "core-init")
    public ActionResponse coreInit(Map<String, String> params, Channel channel) {
        List<String> requiredKeys = Arrays.asList("uuid", "user", "hostname");

        for (String key : requiredKeys) {
            if (!params.containsKey(key) || params.get(key) == null || params.get(key).isEmpty()) {
                String message = "Missing or empty parameter: " + key;
                return new ActionResponse("core-init", 1, message, message.length());
            }
        }

        try {
            UUID uuid = UUID.fromString(params.get("uuid"));
            String user = params.get("user");
            String hostname = params.get("hostname");

            wtpClientService.addClient(channel, uuid, user, hostname);
            String message = "Registered client " + user + " " + hostname + " has been added!";
            log.debug("Registered client {}, username={}, hostname={}", uuid, user, hostname);
            return new ActionResponse("core-init", 0, message, message.length());
        } catch (IllegalArgumentException e) {
            String message = "Invalid UUID format: " + params.get("uuid");
            return new ActionResponse("core-init", 2, message, message.length());
        } catch (Exception e) {
            String message = "An unexpected error occurred: " + e.getMessage();
            return new ActionResponse("core-init", 3, message, message.length());
        }
    }
}