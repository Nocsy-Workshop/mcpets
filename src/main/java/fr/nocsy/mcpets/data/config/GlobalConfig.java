package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.utils.PetAnnouncement;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GlobalConfig extends AbstractConfig {

    public static GlobalConfig instance;

    @Getter
    @Setter
    private boolean worldguardsupport = true;

    @Getter
    private String prefix;
    @Getter
    private String defaultName;
    @Getter
    private boolean overrideDefaultName;
    @Getter
    private int adaptiveInventory;
    @Getter
    private boolean useDefaultMythicMobNames;
    @Getter
    private boolean nameable;
    @Getter
    private boolean mountable;
    @Getter
    private boolean rightClickToOpen;
    @Getter
    private boolean leftClickToOpen;
    @Getter
    private boolean disableInventoryWhileHoldingSignalStick;
    @Getter
    private boolean sneakMode;
    @Getter
    private boolean dismountOnDamaged;
    @Getter
    private boolean spawnPetOnReconnect;
    @Getter
    private boolean enableClickBackToMenu;
    @Getter
    private int distanceTeleport;
    @Getter
    private int maxNameLenght;
    @Getter
    private boolean activateBackMenuIcon;

    @Getter
    private double percentHealthOnRespawn;
    @Getter
    private boolean autorespawn;

    @Getter
    private int autoSave;

    @Getter
    private int experienceBarSize;
    @Getter
    private String experienceSymbol;
    @Getter
    private String experienceColorDone;
    @Getter
    private String experienceColorLeft;


    @Getter
    private boolean globalRespawnCooldown;
    @Getter
    private int defaultRespawnCooldown;

    @Getter
    private PetAnnouncement tamingAnnouncementType = PetAnnouncement.CHAT;
    @Getter
    private int tamingBarSize;
    @Getter
    private String tamingSymbol;
    @Getter
    private String tamingColorDone;
    @Getter
    private String tamingColorLeft;

    @Getter
    private boolean disableMySQL;
    @Getter
    private String MySQL_Prefix;
    @Getter
    private String MySQL_USER;
    @Getter
    private String MySQL_PASSWORD;
    @Getter
    private String MySQL_HOST;
    @Getter
    private String MySQL_PORT;
    @Getter
    private String MySQL_DB;
    @Getter
    private List<String> blackListedWorlds;

    @Getter
    @Setter
    private boolean databaseSupport = false;

    public static GlobalConfig getInstance() {
        if (instance == null)
            instance = new GlobalConfig();

        return instance;
    }

    public void init() {
        super.init("", "config.yml");

        if (getConfig().get("Prefix") == null)
            getConfig().set("Prefix", "§8[§6MCPets§8] » ");
        if (getConfig().get("DefaultName") == null)
            getConfig().set("DefaultName", "§9Pet of %player%");
        if (getConfig().get("OverrideDefaultName") == null)
            getConfig().set("OverrideDefaultName", true);
        if (getConfig().get("EnableClickBackToMenu") == null)
            getConfig().set("EnableClickBackToMenu", true);
        if (getConfig().get("UseDefaultMythicMobsNames") == null)
            getConfig().set("UseDefaultMythicMobsNames", false);
        if (getConfig().get("RightClickToOpenMenu") == null)
            getConfig().set("RightClickToOpenMenu", true);
        if (getConfig().get("LeftClickToOpenMenu") == null)
            getConfig().set("LeftClickToOpenMenu", true);
        if (getConfig().get("DisableInventoryWhileHoldingSignalStick") == null)
            getConfig().set("DisableInventoryWhileHoldingSignalStick", true);
        if (getConfig().get("DismountOnDamaged") == null)
            getConfig().set("DismountOnDamaged", true);
        if (getConfig().get("SpawnPetOnReconnect") == null)
            getConfig().set("SpawnPetOnReconnect", true);
        if (getConfig().get("SneakMode") == null)
            getConfig().set("SneakMode", false);
        if (getConfig().get("Nameable") == null)
            getConfig().set("Nameable", true);
        if (getConfig().get("Mountable") == null)
            getConfig().set("Mountable", true);
        if (getConfig().get("DistanceTeleport") == null)
            getConfig().set("DistanceTeleport", 30);
        if (getConfig().get("MaxNameLenght") == null)
            getConfig().set("MaxNameLenght", 16);
        if (getConfig().get("InventorySize") == null)
            getConfig().set("InventorySize", -1);
        if (getConfig().get("PercentHealthOnRespawn") == null)
            getConfig().set("PercentHealthOnRespawn", 0.2);
        if (getConfig().get("ActivateBackMenuIcon") == null)
            getConfig().set("ActivateBackMenuIcon", true);
        if(getConfig().get("AutoSaveDelay") == null)
            getConfig().set("AutoSaveDelay", 3600);

        if(getConfig().get("GlobalRespawnCooldown") == null)
            getConfig().set("GlobalRespawnCooldown", false);
        if(getConfig().get("DefaultRespawnCooldown") == null)
            getConfig().set("DefaultRespawnCooldown", 0);
        if(getConfig().get("AutoRespawn") == null)
            getConfig().set("AutoRespawn", false);
        if (getConfig().get("Experience.BarSize") == null)
            getConfig().set("Experience.BarSize", 40);
        if (getConfig().get("Experience.Symbol") == null)
            getConfig().set("Experience.Symbol", "|");
        if (getConfig().get("Experience.ColorDone") == null)
            getConfig().set("Experience.ColorDone", "§a");
        if (getConfig().get("Experience.ColorLeft") == null)
            getConfig().set("Experience.ColorLeft", "§f");

        if (getConfig().get("Taming.AnnouncementType") == null)
            getConfig().set("Taming.AnnouncementType", PetAnnouncement.CHAT.name());
        if (getConfig().get("Taming.BarSize") == null)
            getConfig().set("Taming.BarSize", 40);
        if (getConfig().get("Taming.Symbol") == null)
            getConfig().set("Taming.Symbol", "|");
        if (getConfig().get("Taming.ColorDone") == null)
            getConfig().set("Taming.ColorDone", "§a");
        if (getConfig().get("Taming.ColorLeft") == null)
            getConfig().set("Taming.ColorLeft", "§f");


        if (getConfig().get("MySQL.Prefix") == null)
            getConfig().set("MySQL.Prefix", "");
        if (getConfig().get("MySQL.User") == null)
            getConfig().set("MySQL.User", "user");
        if (getConfig().get("MySQL.Password") == null)
            getConfig().set("MySQL.Password", "password");
        if (getConfig().get("MySQL.Host") == null)
            getConfig().set("MySQL.Host", "localhost");
        if (getConfig().get("MySQL.Port") == null)
            getConfig().set("MySQL.Port", "2560");
        if (getConfig().get("MySQL.Database") == null)
            getConfig().set("MySQL.Database", "mcpets_db");
        if(getConfig().get("BlackListedWorlds") == null)
            getConfig().set("BlackListedWorlds", new ArrayList<String>());

        save();
        reload();
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void reload() {

        loadConfig();

        prefix = getConfig().getString("Prefix");
        defaultName = getConfig().getString("DefaultName");
        overrideDefaultName = getConfig().getBoolean("OverrideDefaultName");
        useDefaultMythicMobNames = getConfig().getBoolean("UseDefaultMythicMobsNames");
        rightClickToOpen = getConfig().getBoolean("RightClickToOpenMenu");
        leftClickToOpen = getConfig().getBoolean("LeftClickToOpenMenu");
        disableInventoryWhileHoldingSignalStick = getConfig().getBoolean("DisableInventoryWhileHoldingSignalStick");
        sneakMode = getConfig().getBoolean("SneakMode");
        nameable = getConfig().getBoolean("Nameable");
        mountable = getConfig().getBoolean("Mountable");
        dismountOnDamaged = getConfig().getBoolean("DismountOnDamaged");
        spawnPetOnReconnect = getConfig().getBoolean("SpawnPetOnReconnect");
        distanceTeleport = getConfig().getInt("DistanceTeleport");
        maxNameLenght = getConfig().getInt("MaxNameLenght");
        enableClickBackToMenu = getConfig().getBoolean("EnableClickBackToMenu");
        activateBackMenuIcon = getConfig().getBoolean("ActivateBackMenuIcon");
        adaptiveInventory = getConfig().getInt("InventorySize");
        percentHealthOnRespawn = getConfig().getDouble("PercentHealthOnRespawn");
        autorespawn = getConfig().getBoolean("AutoRespawn");
        defaultRespawnCooldown = Math.max(0, getConfig().getInt("DefaultRespawnCooldown"));
        globalRespawnCooldown = getConfig().getBoolean("GlobalRespawnCooldown");
        // Says it'll be an adaptive inventory
        if (adaptiveInventory <= 0)
            adaptiveInventory = -1;
        // Inventory can't grow over 54
        if (adaptiveInventory > 54)
            adaptiveInventory = 54;
        // Get the size of the inventory to place
        while (adaptiveInventory > 0 && adaptiveInventory % 9 != 0 && adaptiveInventory < 54)
            adaptiveInventory++;

        autoSave = getConfig().getInt("AutoSaveDelay");

        experienceBarSize = getConfig().getInt("Experience.BarSize");
        experienceSymbol = getConfig().getString("Experience.Symbol");
        experienceColorDone = getConfig().getString("Experience.ColorDone");
        experienceColorLeft = getConfig().getString("Experience.ColorLeft");

        tamingAnnouncementType = PetAnnouncement.get(getConfig().getString("Taming.AnnouncementType"));
        tamingBarSize = getConfig().getInt("Taming.BarSize");
        tamingSymbol = getConfig().getString("Taming.Symbol");
        tamingColorDone = getConfig().getString("Taming.ColorDone");
        tamingColorLeft = getConfig().getString("Taming.ColorLeft");

        disableMySQL = getConfig().getBoolean("DisableMySQL");
        MySQL_Prefix = getConfig().getString("MySQL.Prefix");
        MySQL_USER = getConfig().getString("MySQL.User");
        MySQL_PASSWORD = getConfig().getString("MySQL.Password");
        MySQL_HOST = getConfig().getString("MySQL.Host");
        MySQL_PORT = getConfig().getString("MySQL.Port");
        MySQL_DB = getConfig().getString("MySQL.Database");
        blackListedWorlds = getConfig().getStringList("BlackListedWorlds");
    }

    public boolean hasBlackListedWorld(String worldName)
    {
        return blackListedWorlds.contains(worldName);
    }

    public boolean getBooleanField(String path)
    {
        return getConfig().getBoolean(path);
    }

    public String getStringField(String path)
    {
        return getConfig().getString(path);
    }

}
