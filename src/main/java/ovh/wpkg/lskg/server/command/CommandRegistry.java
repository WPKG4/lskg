package ovh.wpkg.lskg.server.command;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandRegistry {
    private static final Map<String, Method> commands = new HashMap<>();

    public static void registerCommand(Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                log.debug("Registering command: {}", command.name());
                commands.put(command.name(), method);
            }
        }
    }

    public static Method getCommand(String name) {
        return commands.get(name);
    }

    public static boolean hasCommand(String name) {
        return commands.containsKey(name);
    }
}
