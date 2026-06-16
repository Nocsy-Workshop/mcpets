package fr.nocsy.mcpets;

import com.google.common.collect.Lists;
import com.sk89q.worldguard.WorldGuard;
import fr.nocsy.mcpets.commands.CommandHandler;
import fr.nocsy.mcpets.compat.PlaceholderAPICompat;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.config.BlacklistConfig;
import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.LanguageConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.data.editor.EditorConversation;
import fr.nocsy.mcpets.data.editor.EditorItems;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.listeners.EventListener;
import fr.nocsy.mcpets.modeler.AbstractModeler;
import fr.nocsy.mcpets.modeler.BetterModelModeler;
import fr.nocsy.mcpets.modeler.ModelEngineModeler;
import fr.nocsy.mcpets.velocity.VelocitySyncManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.CustomComponentRegistry;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.nocsy.mcpets.mythicmobs.MythicListener.PLACEHOLDER_PACKAGE;

public class MCPets extends JavaPlugin {

    @Getter
    private static MCPets instance;

    private static MythicBukkit mythicMobs;
    private static LuckPerms luckPerms;
    private static boolean itemsAdderFound = false;
    private static boolean luckPermsNotFound = false;
    private static boolean nexoFound = false;
    private static boolean nexoChecked = false;

    @Getter
    private static CustomComponentRegistry componentRegistry;

    @Getter
    private static AbstractModeler modeler;

    @Getter
    private static PlaceholderAPICompat placeholderAPI;

    @Getter
    private static final String prefix = "§8[§»";

    public static void loadConfigs() {
        ItemsListConfig.getInstance().init();
        PetFoodConfig.getInstance().init();
        GlobalConfig.getInstance().init();
        LanguageConfig.getInstance().init();
        BlacklistConfig.getInstance().init();
        PetConfig.loadPets(AbstractConfig.getPath() + "Pets/", true);
        CategoryConfig.load(AbstractConfig.getPath() + "Categories/", true);

        // Run DB initialization asynchronously to avoid freezing the main thread.
        // Tasks that depend on isDatabaseSupport() being correctly set (autosave scheduler,
        // Velocity init) must run AFTER this completes — see scheduleDbDependentTasks().
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            Databases.init();
            PlayerData.initAll();
            // Hop back to main thread for tasks that must schedule on the main scheduler
            Bukkit.getScheduler().runTask(instance, MCPets::scheduleDbDependentTasks);
        });

        for (final EditorItems item : EditorItems.values())
            item.refreshData();
    }

    /**
     * Tasks that depend on the DB connection state being known (isDatabaseSupport()).
     * Called from the async DB init path so that PetStats.saveStats() picks the correct
     * sync/async branch — otherwise a sync YAML autosave gets scheduled while DB init is
     * still pending, then runs heavy MySQL writes on the main thread once DB connects.
     */
    private static void scheduleDbDependentTasks() {
        PetStats.saveStats();
        if (GlobalConfig.getInstance().isVelocityEnabled()) {
            VelocitySyncManager.init();
            getLog().info("[MCPets] : Velocity sync enabled.");
        }
    }

    @Override
    public void onLoad() {
        instance = this;

        // Reset static flags for PlugMan reload support
        itemsAdderFound = false;
        nexoFound = false;
        nexoChecked = false;
        luckPermsNotFound = false;
        modeler = null;

        if (!checkMythicMobs()) {
            getLog().severe("MCPets could not be loaded : MythicMobs could not be found or this version is not compatible with the plugin.");
            return;
        }

        if (!checkModeler()) {
            getLog().severe("MCPets could not be loaded : Neither ModelEngine nor BetterModel could be found.");
            return;
        }

        checkWorldGuard();
        checkLuckPerms();
        checkPlaceholderApi();
        checkNexo();
        checkItemsAdder();
        if (!nexoFound && !itemsAdderFound) {
            getLog().info("Neither Nexo nor ItemsAdder were found. Custom items features won't be available.");
        }

        try {
            if (GlobalConfig.getInstance().isWorldguardsupport()) {
                FlagsManager.init(this);
            }
        } catch (final Exception ex) {
            getLog().log(Level.SEVERE, "Flag manager has raised an exception", ex);
        }
    }

    @Override
    public void onEnable() {
        CommandHandler.init(this);
        EventListener.init(this);
        modeler.registerListeners(this);

        loadConfigs();
        // PetStats.saveStats() and VelocitySyncManager.init() are scheduled inside
        // loadConfigs() once async DB init completes — see scheduleDbDependentTasks()

        // Register the placeholders
        componentRegistry = new CustomComponentRegistry(instance, Lists.newArrayList());
        componentRegistry.registerCustomComponent(CustomComponentRegistry.MythicComponentType.PLACEHOLDER, PLACEHOLDER_PACKAGE);

        getLog().info("-=-=-=-= MCPets loaded =-=-=-=-");
        getLog().info("      Plugin made by Nocsy     ");
        getLog().info("-=-=-=-= -=-=-=-=-=-=- =-=-=-=-");

        FlagsManager.launchFlags();
    }

    @Override
    public void onDisable() {
        getLog().info("-=-=-=-= MCPets disabled =-=-=-=-");
        getLog().info("          See you soon           ");
        getLog().info("-=-=-=-= -=-=-=-=-=-=-=- =-=-=-=-");

        // Cancel pending editor conversations before the JAR is unloaded to avoid
        // IllegalStateException (zip file closed) if a listener fires after disable.
        EditorConversation.clearAll();

        if (modeler != null) {
            modeler.unregisterListeners();
        }

        // Run all DB saves on a separate thread to avoid freezing the main thread
        final CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() -> {
            PetStats.saveAll();

            // Save all active pets to DB before clearing them so that a server restart
            // does not wipe the mcpets_active_pet records — players rejoin with their pet intact.
            if (GlobalConfig.getInstance().isVelocityEnabled()
                    && GlobalConfig.getInstance().isDatabaseSupport()) {
                for (Map.Entry<UUID, List<Pet>> entry : Pet.getActivePets().entrySet()) {
                    List<Pet> activePets = entry.getValue();
                    if (activePets != null && !activePets.isEmpty()) {
                        List<String> ids = new ArrayList<>();
                        Map<String, String> skinIds = new HashMap<>();
                        for (Pet pet : activePets) {
                            if (pet != null) {
                                ids.add(pet.getId());
                                final PetSkin skin = pet.getActiveSkin();
                                if (skin != null) {
                                    skinIds.put(pet.getId(), skin.getPathId());
                                }
                            }
                        }
                        if (!ids.isEmpty()) {
                            Databases.saveActivePet(entry.getKey(), ids, skinIds);
                        }
                    }
                }
            }
        });

        FlagsManager.stopFlags();
        VelocitySyncManager.shutdown();

        // Wait for DB saves to complete before cleaning up
        try {
            saveFuture.join();
        } catch (final Exception e) {
            getLog().log(Level.SEVERE, "Error saving data on disable", e);
        }

        Pet.clearPets();
        Databases.closeConnection();
    }

    /**
     * Check and initialize LuckPerms instance
     */
    private static void checkLuckPerms() {
        try {
            final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        } catch (final NoClassDefFoundError error) {
            if (!luckPermsNotFound) {
                luckPermsNotFound = true;
                getLog().warning("LuckPerms could not be found. Some features relating to giving permissions won't be available.");
            }
        }
    }

    public static boolean checkNexo() {
        if (nexoFound) return true;
        if (nexoChecked) return false;

        try {
            Class.forName("com.nexomc.nexo.api.NexoItems");
            nexoFound = true;
            if (!nexoChecked) {
                getLog().info("Nexo found. Nexo Custom items features are available.");
            }
        } catch (final ClassNotFoundException e) {
            nexoFound = false;
        } catch (final Exception e) {
            // Handle cases like zip file closed during plugin reload
            nexoFound = false;
            if (!nexoChecked) {
                getLog().warning("Could not check for Nexo (" + e.getClass().getSimpleName() + "). Nexo Custom items features won't be available.");
            }
        } finally {
            nexoChecked = true;
        }

        return nexoFound;
    }

    private static void checkItemsAdder() {

        try {
            Class.forName("dev.lone.itemsadder.api.CustomStack");
            itemsAdderFound = true;
        } catch (final ClassNotFoundException e) {
            itemsAdderFound = false;
        }
    }

    /**
     * Check and initialize WorldGuard instance
     */
    private static void checkWorldGuard() {
        try {
            final WorldGuard wg = WorldGuard.getInstance();
            if (wg != null)
                GlobalConfig.getInstance().setWorldguardsupport(true);
        } catch (final NoClassDefFoundError error) {
            GlobalConfig.getInstance().setWorldguardsupport(false);
            getLog().warning("WorldGuard could not be found. Flags won't be available.");
        }
    }

    /**
     * Check and initialize MythicMobs instance
     */
    private static boolean checkMythicMobs() {
        if (mythicMobs != null)
            return true;

        try {
            final MythicBukkit inst = MythicBukkit.inst();
            if (inst != null) {
                mythicMobs = inst;
                return true;
            }
        } catch (final NoClassDefFoundError error) {
            getLog().warning("MythicMobs could not be found.");
        }

        return false;
    }

    /**
     * Check and initialize the modeler (BetterModel or ModelEngine)
     */
    private static boolean checkModeler() {
        if (modeler != null)
            return true;

        // Try BetterModel first
        try {
            Class.forName("kr.toxicity.model.api.BetterModel");
            modeler = new BetterModelModeler();
            getLog().info("BetterModel found. Using BetterModel as modeler.");
            return true;
        } catch (final ClassNotFoundException ignored) {}

        // Fallback to ModelEngine
        try {
            Class.forName("com.ticxo.modelengine.api.ModelEngineAPI");
            modeler = new ModelEngineModeler();
            getLog().info("ModelEngine found. Using ModelEngine as modeler.");
            return true;
        } catch (final ClassNotFoundException ignored) {}

        return false;
    }

    private static boolean checkPlaceholderApi() {
        if (placeholderAPI != null) {
            return true;
        }

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPI = new PlaceholderAPICompat();
            placeholderAPI.register();
            return true;
        }

        return false;
    }

    /**
     * Return MythicMobs instance
     */
    public static MythicBukkit getMythicMobs() {
        if (mythicMobs == null)
            checkMythicMobs();

        return mythicMobs;
    }

    /**
     * Return LuckPerms instance
     */
    public static LuckPerms getLuckPerms() {
        if (luckPerms == null)
            checkLuckPerms();

        return luckPerms;
    }

    /**
     * Check ItemsAdder is loaded or not
     */
    public static boolean isItemsAdderLoaded() {
        return itemsAdderFound;
    }

    public static Logger getLog() {
        final MCPets plugin = getInstance();
        if (plugin != null)
            return plugin.getLogger();
        return Bukkit.getLogger();
    }
}
