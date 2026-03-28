package fr.nocsy.mcpets.utils.debug;

import fr.nocsy.mcpets.utils.Utils;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Debugger {

    // list of the people listening to the debugger
    private static final List<UUID> listeners = new ArrayList<>();

    /**
     * Says whether the debugger is being listened to at the moment
     */
    public static boolean isEnabled() {
        return !listeners.isEmpty();
    }

    /**
     * Send a message to the debug listeners
     */
    public static void send(final String msg) {
        if (!isEnabled())
            return;

        ((Audience) Bukkit.getConsoleSender()).sendMessage(Utils.toComponent("§7[MCPETS DEBUG]: §6" + msg));
        for (final UUID uuid : listeners) {
            final Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                ((Audience) p).sendMessage(Utils.toComponent("§7[DEBUG]: §6" + msg));
            }
        }
    }

    /**
     * Join the listening conversation to the debugger
     */
    public static void join(final UUID uuid) {
        if (listeners.contains(uuid))
            return;
        listeners.add(uuid);
    }

    /**
     * Leave the listening conversation of the debugger
     */
    public static void leave(final UUID uuid) {
        listeners.remove(uuid);
    }

    /**
     * Says whether the said uuid is listening to the debugger
     */
    public static boolean isListening(final UUID uuid) {
        return listeners.contains(uuid);
    }

}
