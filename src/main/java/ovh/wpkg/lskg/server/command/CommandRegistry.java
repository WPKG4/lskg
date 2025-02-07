package ovh.wpkg.lskg.server.command;

import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class CommandRegistry {
    private final Map<String, CommandEntry> commands = new HashMap<>();

    public void registerCommand(Object instance) {
        Class<?> cls = instance.getClass();
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                log.debug("Registering command: {}", command.name());
                commands.put(command.name(), new CommandEntry(instance, method));
            }
        }
    }

    public CommandEntry getCommand(String name) {
        return commands.get(name);
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    public record CommandEntry(Object instance, Method method) {}
}
