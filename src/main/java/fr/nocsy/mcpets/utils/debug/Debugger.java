package fr.nocsy.mcpets.utils.debug;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class Debugger {

    // list of the people listening to the debugger
    private static ArrayList<UUID> listeners = new ArrayList<>();

    /**
     * Says whether the debugger is being listened to at the moment
     */
    public static boolean isEnabled() {
        return !listeners.isEmpty();
    }

    /**
     * Send a message to the debug listeners
     */
    public static void send(String msg) {
        if (!isEnabled())
            return;

        Bukkit.getConsoleSender().sendMessage("§7[MCPETS DEBUG]: §6" + msg);
        for (UUID uuid : listeners) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                p.sendMessage("§7[DEBUG]: §6" + msg);
            }
        }
    }

    /**
     * Join the listening conversation to the debugger
     */
    public static void join(UUID uuid) {
        if (listeners.contains(uuid))
            return;
        listeners.add(uuid);
    }

    /**
     * Leave the listening conversation of the debugger
     */
    public static void leave(UUID uuid) {
        listeners.remove(uuid);
    }

    /**
     * Says whether the said uuid is listening to the debugger
     */
    public static boolean isListening(UUID uuid) {
        return listeners.contains(uuid);
    }

}
