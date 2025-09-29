package fr.nocsy.mcpets.compat;

import fr.nocsy.mcpets.MCPets;
import net.momirealms.craftengine.bukkit.api.event.CraftEngineReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftEngineListener implements Listener {
    private final JavaPlugin plugin;

    public CraftEngineListener(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onCraftEngineLoad(CraftEngineReloadEvent e) {
        MCPets.loadConfigs();
        HandlerList.unregisterAll(this);
    }
}
