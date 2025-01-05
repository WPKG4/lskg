package ovh.wpkg.lskg.server.command.commands;

import io.netty.channel.Channel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.services.WtpClientService;
import ovh.wpkg.lskg.server.types.CommandOutput;

import java.util.Map;

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
}
