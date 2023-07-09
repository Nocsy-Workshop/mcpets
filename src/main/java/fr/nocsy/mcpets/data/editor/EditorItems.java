package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EditorItems {

    UNKNOWN(UNKNOWN(), null, null, null, null),

    BACK_TO_GLOBAL_SELECTION(BACK_TO_GLOBAL_SELECTION(), null, null, null, EditorState.GLOBAL_EDITOR),

    // Default selection menu
    CONFIG_EDITOR(CONFIG_EDITOR(), null, null, null, EditorState.CONFIG_EDITOR),
    PET_EDITOR(PET_EDITOR(), null, null, null, EditorState.PET_EDITOR),
    CATEGORY_EDITOR(CATEGORY_EDITOR(), null, null, null, EditorState.CATEGORY_EDITOR),
    ITEM_EDITOR(ITEM_EDITOR(), null, null, null, EditorState.ITEM_EDITOR),
    PETFOOD_EDITOR(PETFOOD_EDITOR(), null, null, null, EditorState.PETFOOD_EDITOR),

    // Config editor
    CONFIG_EDITOR_PREFIX(CONFIG_EDITOR_PREFIX(), "Prefix", "config.yml", EditorExpectationType.STRING, null),
    CONFIG_EDITOR_DEFAULT_NAME(CONFIG_EDITOR_DEFAULT_NAME(), "DefaultName", "config.yml", EditorExpectationType.STRING, null),
    CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(), "UseDefaultMythicMobsNames", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(), "OverrideDefaultName", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(), "RightClickToOpenMenu", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(), "LeftClickToOpenMenu", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_SNEAKMODE(CONFIG_EDITOR_SNEAKMODE(), "SneakMode", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_NAMEABLE(CONFIG_EDITOR_NAMEABLE(), "Nameable", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_MOUNTABLE(CONFIG_EDITOR_MOUNTABLE(), "Mountable", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_DISTANCE_TELEPORT(CONFIG_EDITOR_DISTANCE_TELEPORT(), "DistanceTeleport", "config.yml", EditorExpectationType.FLOAT, null),
    CONFIG_EDITOR_MAX_NAME_LENGTH(CONFIG_EDITOR_MAX_NAME_LENGTH(), "MaxNameLenght", "config.yml", EditorExpectationType.INT, null),
    CONFIG_EDITOR_INVENTORY_SIZE(CONFIG_EDITOR_INVENTORY_SIZE(), "InventorySize", "config.yml", EditorExpectationType.INT, null),
    CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(), "EnableClickBackToMenu", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(), "ActivateBackMenuIcon", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(), "DismountOnDamaged", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(), "DisableInventoryWhileHoldingSignalStick", "config.yml", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(), "PercentHealthOnRespawn", "config.yml", EditorExpectationType.FLOAT, null),
    CONFIG_EDITOR_AUTO_SAVE_DELAY(CONFIG_EDITOR_AUTO_SAVE_DELAY(), "AutoSaveDelay", "config.yml", EditorExpectationType.INT, null),
    CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(), "DefaultRespawnCooldown", "config.yml", EditorExpectationType.INT, null),
    CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(), "GlobalRespawnCooldown", "config.yml", EditorExpectationType.BOOLEAN, null);

    private final static String editorTag = "MCPets:Editor:";

    @Getter
    private String id;
    private ItemStack item;
    @Getter
    private String filePath;
    @Getter
    private String variablePath;
    @Getter
    @Setter
    private Object value;
    @Getter
    private EditorExpectationType type;
    @Getter
    private EditorState nextState;

    EditorItems(ItemStack item, String variablePath, String filePath, EditorExpectationType type, EditorState nextState)
    {
        this.id = this.name().toUpperCase();
        this.item = item;
        this.filePath = filePath;
        this.variablePath = variablePath;
        this.type = type;
        this.nextState = nextState;

        refreshData();
    }

    private void refreshData()
    {
        if(filePath != null && variablePath != null)
        {
            File file = new File("./plugins/MCPets/" + filePath);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            this.value = config.get(variablePath);
        }
    }

    public boolean is(EditorItems other)
    {
        return other.getId().equals(this.getId());
    }

    public boolean save()
    {
        File file = new File("./plugins/MCPets/" + filePath);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set(variablePath, this.value);
        try
        {
            config.save(file);
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }


    public void toggleBooleanValue()
    {
        if(value instanceof Boolean)
        {
            value = !(Boolean) value;
        }
    }

    public ItemStack getItem()
    {
        ItemStack it = item.clone();
        ItemMeta meta = it.getItemMeta();
        meta.setLocalizedName(editorTag + getId());

        // We gotta replace the value in the lores if there is one to replace
        if(value != null)
        {
            // Basically, we are replacing the placeholder for the value within the lores
            List<String> lores = meta.getLore();
            ArrayList<String> newLores = new ArrayList<>();
            if(lores != null)
            {
                for(String lore : lores)
                {
                    if(!lore.contains("%value%"))
                    {
                        newLores.add(lore);
                        continue;
                    }
                    String valueStr = value.toString();

                    // Just some cute formatting for lores
                    if(valueStr.equalsIgnoreCase("true"))
                        valueStr = "§a" + value;
                    else if(valueStr.equalsIgnoreCase("false"))
                        valueStr = "§c" + false;

                    if(Utils.isNumeric(valueStr))
                        valueStr = "§b" + valueStr;

                    newLores.add(lore.replace("%value%", valueStr));
                }
            }

            meta.setLore(newLores);
        }

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_DYE);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        it.setItemMeta(meta);
        return it;
    }

    public static EditorItems getFromItemstack(ItemStack it)
    {
        if(it == null || !it.hasItemMeta())
            return null;

        String localName = it.getItemMeta().getLocalizedName();
        // Item does not have the editor tag, so it's not an editor item
        if(!localName.contains(editorTag))
            return null;

        String id = localName.replace(editorTag, "");

        return Arrays.stream(EditorItems.values()).filter(editorItems -> editorItems.getId().equals(id)).findFirst().orElse(null);
    }

    /*
     * Items builder methods
     */

    private static ItemStack UNKNOWN()
    {
        ItemStack it = new ItemStack(Material.BARRIER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cUnknown item");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack BACK_TO_GLOBAL_SELECTION()
    {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cBack to global editor menu");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to go back to the editor menu selection.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /*
     * MENU SELECTOR ICONS
     */
    private static ItemStack CONFIG_EDITOR()
    {
        ItemStack it = new ItemStack(Material.MOJANG_BANNER_PATTERN);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Edit configuration");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit the config options.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR()
    {
        ItemStack it = new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Edit pets");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit/create pets.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Edit categories");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit/create categories.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack ITEM_EDITOR()
    {
        ItemStack it = new ItemStack(Material.EMERALD);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Edit items");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit/add items.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR()
    {
        ItemStack it = new ItemStack(Material.COOKED_CHICKEN);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Edit pet food");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit/add pet food.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /*
     * CONFIG EDIT ICONS
     */

    private static ItemStack CONFIG_EDITOR_PREFIX()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Prefix");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit the plugin's prefix.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Default pet name");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the name of the pet when");
        lores.add("§7none is set.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Use default MythicMobs name");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the MythicMobs name should");
        lores.add("§7show up as the default name.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Override default name");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether renaming the pet should override");
        lores.add("§7the default name when it's empty.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Right click to open menu");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the menu should open on right click.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Left click to open menu");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the menu should open on left click.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_SNEAKMODE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Sneak mode to open menu");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether sneaking is required");
        lores.add("§7to open the menu when interacting.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_NAMEABLE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Nameable");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether all pets can have custom names.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MOUNTABLE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Override default name");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether all pets should be mountable");
        lores.add("§7by default, if the feature is enabled");
        lores.add("§7and parameterized for the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISTANCE_TELEPORT()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Distance before teleport");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the minimum distance before the");
        lores.add("§7pet is teleported back to its owner.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MAX_NAME_LENGTH()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Max name length");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the maximum length of a");
        lores.add("§7custom pet name.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_INVENTORY_SIZE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Summoning Inventory size");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the size of the inventory to a given");
        lores.add("§7value, or -1 for adaptive inventory.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Enable click back to menu (category)");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether clicking outside a category menu");
        lores.add("§7should open back the category selection.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Activate back menu icon");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the \"back to menu\" icon should");
        lores.add("§7show up in the interaction menu.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISMOUNT_ON_DAMAGED()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Dismount on damaged");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the player should be dismounted");
        lores.add("§7when taking damages.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Percent health on respawn");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Percentage of health restored to the pet");
        lores.add("§7restored after dying (living pet).");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_AUTO_SAVE_DELAY()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Auto-save database delay");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7How long before the database should save");
        lores.add("§7on a regular basis.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Default respawn cooldown");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7How long before the pet should be revived");
        lores.add("§7by default (living pet).");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Global respawn cooldown");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the player can not summon ANY pet");
        lores.add("§7while being on cooldown.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Disable inventory while signal stick");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the inventory should be accessible while");
        lores.add("§7clicking the pet with a signal stick.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }
}
