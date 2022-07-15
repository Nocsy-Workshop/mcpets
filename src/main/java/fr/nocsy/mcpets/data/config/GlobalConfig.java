package fr.nocsy.mcpets.data.config;

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
    private String prefix = "§8[§6MCPets§8] » ";
    @Getter
    private String defaultName = "§9Pet of %player%";
    @Getter
    private int adaptiveInventory = -1;
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
    private int distanceTeleport = 30;
    @Getter
    private int maxNameLenght = 16;
    @Getter
    private boolean activateBackMenuIcon = true;
    @Getter
    private boolean disableMySQL = false;
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
            getConfig().set("Prefix", prefix);
        if (getConfig().get("DefaultName") == null)
            getConfig().set("DefaultName", defaultName);
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
            getConfig().set("MaxNameLenght", maxNameLenght);
        if (getConfig().get("InventorySize") == null)
            getConfig().set("InventorySize", -1);
        if (getConfig().get("ActivateBackMenuIcon") == null)
            getConfig().set("ActivateBackMenuIcon", activateBackMenuIcon);
        if (getConfig().get("DisableMySQL") == null)
            getConfig().set("DisableMySQL", disableMySQL);
        if (getConfig().get("MySQL.User") == null)
            getConfig().set("MySQL.User", "user");
        if (getConfig().get("MySQL.Password") == null)
            getConfig().set("MySQL.Password", "password");
        if (getConfig().get("MySQL.Host") == null)
            getConfig().set("MySQL.Host", "localhost");
        if (getConfig().get("MySQL.Port") == null)
            getConfig().set("MySQL.Port", "2560");
        if (getConfig().get("MySQL.Database") == null)
            getConfig().set("MySQL.Database", "advancedpet_db");
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
        activateBackMenuIcon = getConfig().getBoolean("ActivateBackMenuIcon");
        adaptiveInventory = getConfig().getInt("InventorySize");
        // Says it'll be an adaptive inventory
        if (adaptiveInventory <= 0)
            adaptiveInventory = -1;
        // Inventory can't grow over 54
        if (adaptiveInventory > 54)
            adaptiveInventory = 54;
        // Get the size of the inventory to place
        while (adaptiveInventory > 0 && adaptiveInventory % 9 != 0 && adaptiveInventory < 54)
            adaptiveInventory++;

        disableMySQL = getConfig().getBoolean("DisableMySQL");
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

}
