package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.listeners.editor.EditorConversationListener;
import fr.nocsy.mcpets.listeners.editor.EditorGlobalListener;
import fr.nocsy.mcpets.mythicmobs.MythicListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class EventListener implements Listener {

    private static final ArrayList<Listener> listeners = new ArrayList<>();

    public static void init(JavaPlugin plugin) {

        listeners.add(new PetMenuListener());
        listeners.add(new PetInteractionMenuListener());
        listeners.add(new PetListener());
        listeners.add(new PetDamageFlagListener());
        listeners.add(new CategoriesMenuListener());
        listeners.add(new CategoryMenuListener());
        listeners.add(new PetInventoryListener());
        listeners.add(new SignalStickListener());
        listeners.add(new PetSkinsMenuListener());
        listeners.add(new VanillaDismountListener());

        listeners.add(new LivingPetsListener());

        listeners.add(new MythicListener());

        listeners.add(new EditorGlobalListener());
        listeners.add(new EditorConversationListener());

        listeners.add(new DatabaseRefreshListener());

        for (Listener l : listeners) {
            plugin.getServer().getPluginManager().registerEvents(l, plugin);
        }

    }

}
