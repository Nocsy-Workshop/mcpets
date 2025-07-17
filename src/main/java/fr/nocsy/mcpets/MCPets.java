package fr.nocsy.mcpets;

import com.sk89q.worldguard.WorldGuard;
import fr.nocsy.mcpets.commands.CommandHandler;
import fr.nocsy.mcpets.compat.PlaceholderAPICompat;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.*;
import fr.nocsy.mcpets.data.editor.EditorItems;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.listeners.EventListener;
import fr.nocsy.mcpets.mythicmobs.placeholders.PetPlaceholdersManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MCPets extends JavaPlugin {

    @Getter
    private static MCPets instance;

    private static MythicBukkit mythicMobs;
    private static LuckPerms luckPerms;
    private static boolean itemsAdderFound = false;
    private static boolean luckPermsNotFound = false;
    private static boolean nexoFound = false;

    @Getter
    private static PlaceholderAPICompat placeholderAPI;

    @Getter
    private static final Logger log = Bukkit.getLogger();

    @Getter
    private static final String prefix = "§8[§»";

    @Getter
    private static final String logName = "[MCPets] : ";

    public static void loadConfigs() {
        ItemsListConfig.getInstance().init();
        PetFoodConfig.getInstance().init();
        GlobalConfig.getInstance().init();
        LanguageConfig.getInstance().init();
        BlacklistConfig.getInstance().init();
        PetConfig.loadPets(AbstractConfig.getPath() + "Pets/", true);
        CategoryConfig.load(AbstractConfig.getPath() + "Categories/", true);
        Databases.init();
        PlayerData.initAll();

        for (EditorItems item : EditorItems.values())
            item.refreshData();
    }

    @Override
    public void onLoad() {
        instance = this;

        if (!checkMythicMobs()) {
            getLog().severe("MCPets could not be loaded : MythicMobs could not be found or this version is not compatible with the plugin.");
            return;
        }

        checkWorldGuard();
        checkLuckPerms();
        checkPlaceholderApi();
        checkItemsAdder();

        try {
            if (GlobalConfig.getInstance().isWorldguardsupport()) {
                FlagsManager.init(this);
            }
        }
        catch (Exception ex) {
            getLog().warning(getLogName() + "Flag manager has raised an exception " + ex.getClass().getSimpleName());
            ex.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        CommandHandler.init(this);
        EventListener.init(this);

        loadConfigs();
        PetStats.saveStats();
        // Register the placeholders
        PetPlaceholdersManager.registerPlaceholders();

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

        PetStats.saveAll();
        Pet.clearPets();
        PlayerData.saveDB();
        FlagsManager.stopFlags();
        Databases.closeConnection();
    }

    /**
     * Check and initialize LuckPerms instance
     */
    private static void checkLuckPerms() {
        try {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
            }
        }
        catch (NoClassDefFoundError error) {
            if (!luckPermsNotFound) {
                luckPermsNotFound = true;
                Bukkit.getLogger().warning("[MCPets] : LuckPerms could not be found. Some features relating to giving permissions won't be available.");
            }
        }
    }

    public static boolean checkNexo() {
        if (nexoFound) return true;

        try {
            Class.forName("com.nexomc.nexo.api.NexoItems");
            Bukkit.getLogger().info("[MCPets] : Nexo found. Nexo Custom items features are available.");
            nexoFound = true;
        } catch (ClassNotFoundException e) {
            nexoFound = false;
            Bukkit.getLogger().warning("[MCPets] : Nexo could not be found. Nexo Custom items features won't be available.");
        }

        return nexoFound;
    }

    private static void checkItemsAdder() {

        try {
            Class.forName("dev.lone.itemsadder.api.CustomStack");
            itemsAdderFound = true;
        } catch (ClassNotFoundException e) {
            itemsAdderFound = false;
            Bukkit.getLogger().warning("[MCPets] : ItemsAdder could not be found. IA Custom items features won't be available.");
        }
    }

    /**
     * Check and initialize WorldGuard instance
     */
    private static void checkWorldGuard() {
        try {
            WorldGuard wg = WorldGuard.getInstance();
            if (wg != null)
                GlobalConfig.getInstance().setWorldguardsupport(true);
        }
        catch (NoClassDefFoundError error) {
            GlobalConfig.getInstance().setWorldguardsupport(false);
            Bukkit.getLogger().warning("[MCPets] : WorldGuard could not be found. Flags won't be available.");
        }
    }

    /**
     * Check and initialize MythicMobs instance
     */
    private static boolean checkMythicMobs() {
        if (mythicMobs != null)
            return true;

        try {
            MythicBukkit inst = MythicBukkit.inst();
            if (inst != null) {
                mythicMobs = inst;
                return true;
            }
        }
        catch (NoClassDefFoundError error) {
            getLog().warning("[MCPets] : MythicMobs could not be found.");
        }

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
}
