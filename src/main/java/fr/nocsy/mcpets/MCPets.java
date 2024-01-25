package fr.nocsy.mcpets;

import com.sk89q.worldguard.WorldGuard;
import fr.nocsy.mcpets.commands.CommandHandler;
import fr.nocsy.mcpets.compat.PlaceholderAPICompat;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.*;
import fr.nocsy.mcpets.data.editor.Editor;
import fr.nocsy.mcpets.data.editor.EditorItems;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.listeners.EventListener;
import fr.nocsy.mcpets.mythicmobs.placeholders.PetPlaceholdersManager;
import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
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
    private static boolean luckPermsNotFound = false;

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

        for(EditorItems item : EditorItems.values())
            item.refreshData();
    }

    @Override
    public void onLoad() {

        instance = this;

        if(!checkMythicMobs())
        {
            getLog().severe("无法加载 MCPets : 找不到 MythicMobs 或该版本与插件不兼容.");
            return;
        }

        checkWorldGuard();
        checkLuckPerms();
        checkPlaceholderApi();

        try {
            if (GlobalConfig.getInstance().isWorldguardsupport())
                FlagsManager.init(this);
        } catch (Exception ex) {
            getLog().warning(getLogName() + "标志管理器引发了一个异常 " + ex.getClass().getSimpleName());
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

        getLog().info("-=-=-=-= MCPets 已加载 =-=-=-=-");
        getLog().info("      Nocsy 制作的插件");
        getLog().info("-=-=-=-= -=-=-=-=-=-=- =-=-=-=-");

        FlagsManager.launchFlags();
    }


    @Override
    public void onDisable() {
        getLog().info("-=-=-=-= MCPets 禁用 =-=-=-=-");
        getLog().info("          再见");
        getLog().info("-=-=-=-= -=-=-=-=-=-=- =-=-=-=-");

        PetStats.saveAll();
        Pet.clearPets();
        PlayerData.saveDB();
        FlagsManager.stopFlags();

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
        } catch (NoClassDefFoundError error) {
            if (!luckPermsNotFound) {
                luckPermsNotFound = true;
                Bukkit.getLogger().warning("[MCPets] : 无法找到 LuckPerms. 与授予权限相关的某些功能将不可用.");
            }
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
        } catch (NoClassDefFoundError error) {
            GlobalConfig.getInstance().setWorldguardsupport(false);
            Bukkit.getLogger().warning("[MCPets] : 无法找到 WorldGuard. 标志将不可用.");
        }
    }

    /**
     * Check and initialize MythicMobs instance
     * @return
     */
    private static boolean checkMythicMobs() {
        if(mythicMobs != null)
            return true;
        try {
            MythicBukkit inst = MythicBukkit.inst();
            if (inst != null)
            {
                mythicMobs = inst;
                return true;
            }
        } catch (NoClassDefFoundError error) {
            getLog().warning("[MCPets] : 无法找到 MythicMobs.");
        }
        return false;
    }

    private static boolean checkPlaceholderApi(){
        if(placeholderAPI != null){
            return true;
        }

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            placeholderAPI = new PlaceholderAPICompat();
            placeholderAPI.register();
            return true;
        }

        return false;
    }

    /**
     * Return MythicMobs instance
     * @return
     */
    public static MythicBukkit getMythicMobs()
    {
        if(mythicMobs == null)
            checkMythicMobs();
        return mythicMobs;
    }


    /**
     * Return LuckPerms instance
     * @return
     */
    public static LuckPerms getLuckPerms()
    {
        if(luckPerms == null)
            checkLuckPerms();
        return luckPerms;
    }


}
