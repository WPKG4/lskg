package ovh.wpkg.lskg.server.command;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandRegistry {
    private static final Map<String, CommandEntry> commands = new HashMap<>();

    public static void registerCommand(Object instance) {
        Class<?> cls = instance.getClass();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                log.debug("Registering command: {}", command.name());
                commands.put(command.name(), new CommandEntry(instance, method));
            }
        }
    }

    public static CommandEntry getCommand(String name) {
        return commands.get(name);
    }

    public static boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    public record CommandEntry(Object instance, Method method) {}
}
