package fr.nocsy.mcpets;

import com.sk89q.worldguard.WorldGuard;
import fr.nocsy.mcpets.commands.CommandHandler;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.config.BlacklistConfig;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.LanguageConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.flags.FlagsManager;
import fr.nocsy.mcpets.data.inventories.PlayerData;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.listeners.EventListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class MCPets extends JavaPlugin {

    @Getter
    private static MCPets instance;
    @Getter
    private static final Logger log = Bukkit.getLogger();

    @Getter
    private static final String prefix = "§8[§»";

    @Getter
    private static final String logName = "[MCPets] : ";

    public static void loadConfigs() {
        GlobalConfig.getInstance().init();
        LanguageConfig.getInstance().init();
        BlacklistConfig.getInstance().init();
        PetConfig.loadPets(AbstractConfig.getPath() + "Pets/", true);
        Databases.init();
        PlayerData.initAll();
    }

    @Override
    public void onEnable() {

        instance = this;
        checkWorldGuard();
        CommandHandler.init(this);
        EventListener.init(this);

        loadConfigs();
        getLog().info("-=-=-=-= MCPets loaded =-=-=-=-");
        getLog().info("        Plugin made by Nocsy");
        getLog().info("-=-=-=-= -=-=-=-=-=-=- =-=-=-=-");

        try {
            if (GlobalConfig.getInstance().isWorldguardsupport())
                FlagsManager.init(this);
        } catch (IllegalPluginAccessException ex) {
            getLog().warning(getLogName() + "Flag manager encountered an exception " + ex.getClass().getSimpleName());
        }

    }

    @Override
    public void onDisable() {
        getLog().info("-=-=-=-= MCPets disable =-=-=-=-");
        getLog().info("            See you soon");
        getLog().info("-=-=-=-= -=-=-=-=-=-=- =-=-=-=-");

        Pet.clearPets();
        PlayerData.saveDB();
        FlagsManager.stopFlags();

    }

    public void checkWorldGuard() {
        try {
            WorldGuard wg = WorldGuard.getInstance();
            if (wg != null)
                GlobalConfig.getInstance().setWorldguardsupport(true);
        } catch (NoClassDefFoundError error) {
            GlobalConfig.getInstance().setWorldguardsupport(false);
            getLogger().warning("[MCPets] : WorldGuard could not be found. Flags won't be available.");
        }
    }

}
