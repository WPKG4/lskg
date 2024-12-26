package ovh.wpkg.lskg.server.command.commands;

import io.netty.channel.Channel;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import ovh.wpkg.lskg.server.command.Command;
import ovh.wpkg.lskg.server.services.WtpClientService;

import java.util.Map;
import java.util.UUID;

@Singleton
public class DefaultCommands {

    @Inject
    public WtpClientService test;

    @Command(name = "hello")
    public String hello() {
        test.testlol();
        return "Hello, World";
    }

    @Command(name = "echo")
    public String echo(Map<String, String> params, Channel test2) {
        test.addClient(test2, UUID.randomUUID());
        StringBuilder result = new StringBuilder("Echo: ");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(" ");
        }
        return result.toString().trim();
    }
}
