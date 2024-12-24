package ovh.wpkg.lskg.server.command.commands;

import ovh.wpkg.lskg.server.command.Command;

import java.util.Map;

public class DefaultCommands {

    @Command(name = "hello")
    public static String hello() {
        return "Hello, world!";
    }

    @Command(name = "echo")
    public static String echo(Map<String, String> params) {
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
