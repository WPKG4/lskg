package ovh.wpkg.lskg.server.command.commands;

import ovh.wpkg.lskg.server.command.Command;

public class DefaultCommands {

    @Command(name = "hello")
    public static String hello() {
        return "Hello, world!";
    }

    @Command(name = "echo")
    public static String echo(String input) {
        return "Echo: " + input;
    }
}
