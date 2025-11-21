package hotel.factory;

import hotel.commands.Command;
import hotel.utils.map.Map;
import hotel.utils.map.MyMap;

/**
 * Registry/factory for creating command instances by name.
 */
public class CommandRegistry {
    private final Map<String, Class<? extends Command>> commands = new MyMap<>();

    /**
     * Register a command name to its implementing class.
     * The name is stored in lower-case.
     */
    public void registerCommand(String name, Class<? extends Command> cls) {
        if (name == null || cls == null) return;
        commands.put(name.toLowerCase(), cls);
    }

    /**
     * Create a command instance for the given name using no-arg constructor.
     * Returns null if the name is not registered or instantiation fails.
     */
    public Command createCommand(String name) {
        if (name == null) return null;
        Class<? extends Command> cls = commands.get(name.toLowerCase());
        if (cls == null) return null;
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get space separated list of registered command names.
     */
    public String listNames() {
        return String.join(",", commands.keys());
    }
}
