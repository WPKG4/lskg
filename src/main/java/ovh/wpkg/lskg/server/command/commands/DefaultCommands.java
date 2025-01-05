package ovh.wpkg.lskg.server.command.commands;

import io.netty.channel.Channel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.CommandOutput;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class DefaultCommands {

    @Inject
    public WtpClientService wtpClientService;

    @Command(name = "hello")
    public CommandOutput hello(Channel channel) {
        return new CommandOutput("Hello World!", 0);
    }

    @Command(name = "echo")
    public CommandOutput echo(Map<String, String> params, Channel channel) {
        StringBuilder result = new StringBuilder("Echo: ");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(" ");
        }
        return new CommandOutput(result.toString().trim(), 0);
    }

    @Command(name = "core-init")
    public CommandOutput coreInit(Map<String, String> params, Channel channel) {
        List<String> requiredKeys = Arrays.asList("uuid", "user", "hostname");

        for (String key : requiredKeys) {
            if (!params.containsKey(key) || params.get(key) == null || params.get(key).isEmpty()) {
                return new CommandOutput("Missing or empty parameter: " + key, 1);
            }
        }

        try {
            UUID uuid = UUID.fromString(params.get("uuid"));
            String user = params.get("user");
            String hostname = params.get("hostname");

            wtpClientService.addClient(channel, uuid, user, hostname);
            return new CommandOutput("Client " + user + " has been added!", 0);
        } catch (IllegalArgumentException e) {
            return new CommandOutput("Invalid UUID format: " + params.get("uuid"), 2);
        } catch (Exception e) {
            return new CommandOutput("An unexpected error occurred: " + e.getMessage(), 3);
        }
    }
}
