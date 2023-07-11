package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
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
    FILLER(FILLER(), null, null, null, null),

    BACK_TO_GLOBAL_SELECTION(BACK_TO_ITEM("global menu"), null, null, null, EditorState.GLOBAL_EDITOR),
    BACK_TO_PET_SELECTION(BACK_TO_ITEM("pet selection menu"), null, null, null, EditorState.PET_EDITOR),
    BACK_TO_PET_EDIT(BACK_TO_ITEM("pet editor"), null, null, null, EditorState.PET_EDITOR_EDIT),
    BACK_TO_PET_LEVELS_EDIT(BACK_TO_ITEM("pet levels"), null, null, null, EditorState.PET_EDITOR_LEVELS),
    BACK_TO_PET_SKINS_EDIT(BACK_TO_ITEM("pet skins"), null, null, null, EditorState.PET_EDITOR_SKINS),

    // Default selection menu
    CONFIG_EDITOR(CONFIG_EDITOR(), null, null, null, EditorState.CONFIG_EDITOR),
    PET_EDITOR(PET_EDITOR(), null, null, null, EditorState.PET_EDITOR),
    CATEGORY_EDITOR(CATEGORY_EDITOR(), null, null, null, EditorState.CATEGORY_EDITOR),
    ITEM_EDITOR(ITEM_EDITOR(), null, null, null, EditorState.ITEM_EDITOR),
    PETFOOD_EDITOR(PETFOOD_EDITOR(), null, null, null, EditorState.PETFOOD_EDITOR),

    // Config editor
    CONFIG_EDITOR_PREFIX(CONFIG_EDITOR_PREFIX(), "Prefix", "config", EditorExpectationType.STRING, null),
    CONFIG_EDITOR_DEFAULT_NAME(CONFIG_EDITOR_DEFAULT_NAME(), "DefaultName", "config", EditorExpectationType.STRING, null),
    CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(), "UseDefaultMythicMobsNames", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(), "OverrideDefaultName", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(), "RightClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(), "LeftClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_SNEAKMODE(CONFIG_EDITOR_SNEAKMODE(), "SneakMode", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_NAMEABLE(CONFIG_EDITOR_NAMEABLE(), "Nameable", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_MOUNTABLE(CONFIG_EDITOR_MOUNTABLE(), "Mountable", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_DISTANCE_TELEPORT(CONFIG_EDITOR_DISTANCE_TELEPORT(), "DistanceTeleport", "config", EditorExpectationType.FLOAT, null),
    CONFIG_EDITOR_MAX_NAME_LENGTH(CONFIG_EDITOR_MAX_NAME_LENGTH(), "MaxNameLenght", "config", EditorExpectationType.INT, null),
    CONFIG_EDITOR_INVENTORY_SIZE(CONFIG_EDITOR_INVENTORY_SIZE(), "InventorySize", "config", EditorExpectationType.INT, null),
    CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(), "EnableClickBackToMenu", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(), "ActivateBackMenuIcon", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(), "DismountOnDamaged", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(), "DisableInventoryWhileHoldingSignalStick", "config", EditorExpectationType.BOOLEAN, null),
    CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(), "PercentHealthOnRespawn", "config", EditorExpectationType.FLOAT, null),
    CONFIG_EDITOR_AUTO_SAVE_DELAY(CONFIG_EDITOR_AUTO_SAVE_DELAY(), "AutoSaveDelay", "config", EditorExpectationType.INT, null),
    CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(), "DefaultRespawnCooldown", "config", EditorExpectationType.INT, null),
    CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(), "GlobalRespawnCooldown", "config", EditorExpectationType.BOOLEAN, null),

    // Pet editor
    PET_EDITOR_EDIT_PET(UNKNOWN(), null, null, EditorExpectationType.PET, null),
    PET_EDITOR_CREATE_NEW(CREATE_NEW_ITEM("pet", Material.MAGMA_CUBE_SPAWN_EGG), null, null, EditorExpectationType.PET_CREATE, null),
    PET_EDITOR_PAGE_SELECTOR(PET_EDITOR_PAGE_SELECTOR(), null, null, EditorExpectationType.PAGE_SELECTOR, null),

    PET_EDITOR_DELETE(DELETE("pet"), null, null, EditorExpectationType.PET_DELETE, null),
    PET_EDITOR_LEVELS(PET_EDITOR_LEVELS(), null, null, null, EditorState.PET_EDITOR_LEVELS),
    PET_EDITOR_SKINS(PET_EDITOR_SKINS(), null, null, null, EditorState.PET_EDITOR_SKINS),

    PET_EDITOR_ICON(PET_EDITOR_ICON(), "Icon.Raw", null, EditorExpectationType.ITEM, null),
    PET_EDITOR_MYTHICMOB(PET_EDITOR_MYTHICMOB(), "MythicMob", null, EditorExpectationType.MYTHICMOB, null),
    PET_EDITOR_PERMISSION(PET_EDITOR_PERMISSION(), "Permission", null, EditorExpectationType.STRING, null),
    PET_EDITOR_MOUNTABLE(PET_EDITOR_MOUNTABLE(), "Mountable", null, EditorExpectationType.BOOLEAN, null),
    PET_EDITOR_MOUNT_TYPE(PET_EDITOR_MOUNT_TYPE(), "MountType", null, EditorExpectationType.MOUNT_TYPE, null),
    PET_EDITOR_DESPAWN_ON_DISMOUNT(PET_EDITOR_DESPAWN_ON_DISMOUNT(), "DespawnOnDismount", null, EditorExpectationType.BOOLEAN, null),
    PET_EDITOR_AUTORIDE(PET_EDITOR_AUTORIDE(), "AutoRide", null, EditorExpectationType.BOOLEAN, null),
    PET_EDITOR_MOUNT_PERMISSION(PET_EDITOR_MOUNT_PERMISSION(), "MountPermission", null, EditorExpectationType.STRING, null),
    PET_EDITOR_DESPAWN_SKILL(PET_EDITOR_DESPAWN_SKILL(), "DespawnSkill", null, EditorExpectationType.SKILL, null),
    PET_EDITOR_DISTANCE(PET_EDITOR_DISTANCE(), "Distance", null, EditorExpectationType.FLOAT, null),
    PET_EDITOR_SPAWN_RANGE(PET_EDITOR_SPAWN_RANGE(), "SpawnRange", null, EditorExpectationType.FLOAT, null),
    PET_EDITOR_COMING_BACK_RANGE(PET_EDITOR_COMING_BACK_RANGE(), "ComingBackRange", null, EditorExpectationType.FLOAT, null),
    PET_EDITOR_INVENTORY_SIZE(PET_EDITOR_INVENTORY_SIZE(), "InventorySize", null, EditorExpectationType.INT, null),
    PET_EDITOR_TAMING_PROGRESS_SKILL(PET_EDITOR_TAMING_PROGRESS_SKILL(), "Taming.TamingProgressSkill", null, EditorExpectationType.INT, null),
    PET_EDITOR_TAMING_FINISHED_SKILL(PET_EDITOR_TAMING_FINISHED_SKILL(), "Taming.TamingFinishedSkill", null, EditorExpectationType.INT, null),
    PET_EDITOR_SIGNALS(PET_EDITOR_SIGNALS(), "Signals.Values", null, EditorExpectationType.STRING_LIST, null),
    PET_EDITOR_SIGNAL_STICK(PET_EDITOR_SIGNAL_STICK(), "Signals.Item.Raw", null, EditorExpectationType.ITEM, null),
    PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(), "Signals.Item.GetFromMenu", null, EditorExpectationType.ITEM, null),
    // Pet editor - Levels
    PET_EDITOR_EDIT_LEVEL(UNKNOWN(), null, null, EditorExpectationType.PET_LEVEL_EDIT, null),
    PET_EDITOR_EDIT_LEVEL_DELETE(DELETE("level"), null, null, EditorExpectationType.PET_LEVEL_DELETE, null),
    PET_EDITOR_LEVEL_CREATE_NEW(CREATE_NEW_ITEM("level", Material.EXPERIENCE_BOTTLE), null, null, EditorExpectationType.PET_LEVEL_CREATE, null),
    PET_EDITOR_EDIT_LEVEL_NAME(PET_EDITOR_LEVEL_NAME(), "Levels.%path%.Name", null, EditorExpectationType.STRING, null),
    PET_EDITOR_EDIT_LEVEL_EXP_THRESHOLD(PET_EDITOR_LEVEL_EXP_THRESHOLD(), "Levels.%path%.ExperienceThreshold", null, EditorExpectationType.POSITIVE_INT, null),
    PET_EDITOR_EDIT_LEVEL_MAX_HEALTH(PET_EDITOR_LEVEL_MAX_HEALTH(), "Levels.%path%.MaxHealth", null, EditorExpectationType.POSITIVE_INT, null),
    PET_EDITOR_EDIT_LEVEL_REGENERATION(PET_EDITOR_LEVEL_REGENERATION(), "Levels.%path%.Regeneration", null, EditorExpectationType.POSITIVE_FLOAT, null),
    PET_EDITOR_EDIT_LEVEL_RESISTANCE_MODIFIER(PET_EDITOR_LEVEL_RESISTANCE_MODIFIER(), "Levels.%path%.ResistanceModifier", null, EditorExpectationType.FLOAT, null),
    PET_EDITOR_EDIT_LEVEL_DAMAGE_MODIFIER(PET_EDITOR_LEVEL_DAMAGE_MODIFIER(), "Levels.%path%.DamageModifier", null, EditorExpectationType.FLOAT, null),
    PET_EDITOR_EDIT_LEVEL_POWER(PET_EDITOR_LEVEL_POWER(), "Levels.%path%.Power", null, EditorExpectationType.FLOAT, null),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_RESPAWN(PET_EDITOR_LEVEL_COOLDOWN_RESPAWN(), "Levels.%path%.Cooldowns.Respawn", null, EditorExpectationType.POSITIVE_INT, null),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_REVOKE(PET_EDITOR_LEVEL_COOLDOWN_REVOKE(), "Levels.%path%.Cooldowns.Revoke", null, EditorExpectationType.POSITIVE_INT, null),
    PET_EDITOR_EDIT_LEVEL_INVENTORY_EXTENSION(PET_EDITOR_LEVEL_INVENTORY_EXTENSION(), "Levels.%path%.InventoryExtension", null, EditorExpectationType.INVENTORY_SIZE, null),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TEXT(PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT(), "Levels.%path%.Announcement.Text", null, EditorExpectationType.STRING, null),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TYPE(PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE(), "Levels.%path%.Announcement.Type", null, EditorExpectationType.ANNOUNCEMENT_TYPE, null),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_SKILL(PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL(), "Levels.%path%.Announcement.Skill", null, EditorExpectationType.STRING, null),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_PET_ID(PET_EDITOR_LEVEL_EVOLUTION_PET_ID(), "Levels.%path%.Evolution.PetId", null, EditorExpectationType.PET, null),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_DELAY(PET_EDITOR_LEVEL_EVOLUTION_DELAY(), "Levels.%path%.Evolution.DelayBeforeEvolution", null, EditorExpectationType.POSITIVE_INT, null),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_REMOVE_ACCESS(PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS(), "Levels.%path%.Evolution.RemoveAccess", null, EditorExpectationType.BOOLEAN, null),
    // Pet editor - Skins
    PET_EDITOR_EDIT_SKIN(UNKNOWN(), null, null, EditorExpectationType.PET_SKIN_EDIT, null),
    PET_EDITOR_EDIT_SKIN_DELETE(DELETE("skin"), null, null, EditorExpectationType.PET_SKIN_DELETE, null),
    PET_EDITOR_SKIN_CREATE_NEW(CREATE_NEW_ITEM("skin", Material.LEATHER), null, null, EditorExpectationType.PET_SKIN_CREATE, null),
    PET_EDITOR_EDIT_SKIN_ICON(UNKNOWN(), "Skins.%path%.Icon.Raw", null, EditorExpectationType.ITEM, null),
    PET_EDITOR_EDIT_SKIN_MYTHICMOB(PET_EDITOR_SKIN_MYTHICMOB(), "Skins.%path%.MythicMob", null, null, null),
    PET_EDITOR_EDIT_SKIN_PERMISSION(PET_EDITOR_SKIN_PERMISSION(), "Skins.%path%.Permission", null, null, null),
    ;

    private final static String editorTag = "MCPets:Editor:";
    @Getter
    private static ArrayList<String> cachedDeleted = new ArrayList<>();

    @Getter
    private String id;
    @Setter
    private ItemStack item;
    @Getter
    private String filePath;
    @Getter
    private String variablePath;
    private String variablePathPlaceholder;
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
        this.filePath = "./plugins/MCPets/" + filePath + ".yml";
        this.variablePath = variablePath;
        this.variablePathPlaceholder = "";
        this.type = type;
        this.nextState = nextState;

        refreshData();
    }

    private void refreshData()
    {
        if(filePath != null && variablePath != null)
        {
            File file = new File(this.filePath);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            this.value = config.get(variablePath.replace("%path%", variablePathPlaceholder));
        }
    }

    public boolean is(EditorItems other)
    {
        return other.getId().equals(this.getId());
    }

    public EditorItems setFilePath(String path)
    {
        this.filePath = path;
        refreshData();
        return this;
    }

    public EditorItems replaceVariablePath(String pathPlaceholder)
    {
        this.variablePathPlaceholder = pathPlaceholder;
        refreshData();
        return this;
    }

    public boolean save()
    {
        if(this.value == null)
            return false;

        if(this.getType().equals(EditorExpectationType.PET_CREATE))
        {
            String illegalCharacters = "#%<>&*{}?/\\$§+!`|'\"=:@.";
            for(char character : illegalCharacters.toCharArray())
            {
                this.value = this.value.toString().replace(""+character, "");
            }
            this.value = this.value.toString().replace(" ", "_");
            // Create the pet config and add it to the pet objects for editing
            PetConfig petConfig = new PetConfig("Pets/", this.value.toString() + ".yml");
            Pet.getObjectPets().add(petConfig.getPet());
            return true;
        }
        File file = new File(filePath);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.set(variablePath.replace("%path%", variablePathPlaceholder), this.value);

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

        // Basically, we are replacing the placeholder for the value within the lores
        List<String> lores = meta.getLore();
        ArrayList<String> newLores = new ArrayList<>();
        if(lores != null)
        {
            for(String lore : lores)
            {

                String valueStr = value == null ? "§6default (not set)" : value.toString();

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

    private static ItemStack FILLER()
    {
        ItemStack it = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§0");
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack BACK_TO_ITEM(String where)
    {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cBack to " + where);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to go back to the " + where + ".");

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

    /*
     * PET EDITOR icons
      */

    public EditorItems setupPetIcon(Pet pet)
    {
        ItemStack it = pet.getIcon().clone();
        if(value != null && value instanceof ItemStack)
        {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        ItemMeta meta = it.getItemMeta();

        List<String> og_lores = it.getItemMeta().getLore();
        if(og_lores == null)
            og_lores = new ArrayList<>();

        ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§eClick to edit that pet.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    public EditorItems setupPetIconEdit(Pet pet)
    {
        ItemStack it = pet.getIcon().clone();
        if(value != null && value instanceof ItemStack)
        {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        ItemMeta meta = it.getItemMeta();

        List<String> og_lores = it.getItemMeta().getLore();
        if(og_lores == null)
            og_lores = new ArrayList<>();

        if(og_lores.contains("§eClick with an item on that icon"))
            return this;

        ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§eClick with an item on that icon");
        lores.add("§eto replace the pet icon.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    public EditorItems setupSignalStickItem(Pet pet)
    {
        ItemStack it = pet.getSignalStick().clone();
        if(value != null && value instanceof ItemStack)
        {
            it = (ItemStack) ((ItemStack) value).clone();
        }
        ItemMeta meta = it.getItemMeta();
        if(meta.getDisplayName().equals("§cUndefined"))
            meta.setDisplayName("§6Signal stick");

        List<String> og_lores = it.getItemMeta().getLore();
        if(og_lores == null)
            og_lores = new ArrayList<>();

        ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§eClick with an item on that icon");
        lores.add("§eto replace the signal stick.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    private static ItemStack PET_EDITOR_PAGE_SELECTOR()
    {
        ItemStack it = new ItemStack(Material.ARROW);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aPage selector");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§cLeft§7 click to go on the previous page");
        lores.add("§aRight§7 click to go on the next page");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CREATE_NEW_ITEM(String what, Material type)
    {
        ItemStack it = new ItemStack(type);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aCreate a new " + what);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to create a new " + what + ".");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack DELETE(String what)
    {
        ItemStack it = new ItemStack(Material.BARRIER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cDelete the " + what);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to delete the " + what + ".");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MYTHICMOB()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMob");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the MythicMob that handles the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Permission");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the permission that enables");
        lores.add("§7to unlock the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNTABLE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Mountable");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether or not the pet is mountable.");
        lores.add("§7This requires the pet to have a \"mount\" bone");
        lores.add("§7within the Blockbench file (check wiki).");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_TYPE()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Mount type");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the mount type of the pet.");
        lores.add("§7Only active if the pet is mountable.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_ON_DISMOUNT()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Despawn on Dismount");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the player should be dismounted");
        lores.add("§7of the pet when it is despawned.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_AUTORIDE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Auto-ride");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the player should automatically");
        lores.add("§7ride its pet when it's spawned.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Mount permission");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a permission to allow the player");
        lores.add("§7to ride the mount (if mounting enabled)");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Despawn skill");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set an optional despawn skill.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DISTANCE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Come back distance");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the minimum distance before the pet");
        lores.add("§7comes back to the owner.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SPAWN_RANGE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Spawn range");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the radius in which the pet is");
        lores.add("§7potentially being spawned into.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_COMING_BACK_RANGE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Close up come back distance");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set distance at which the pets stands");
        lores.add("§7once it is close enough to the owner");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_INVENTORY_SIZE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Inventory size");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a size for the pet inventory.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_PROGRESS_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Taming - Progress skill");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a skill for when a player");
        lores.add("§7tames the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_FINISHED_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Taming - Finished skill");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a skill for when a player");
        lores.add("§7has finished taming the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_ICON()
    {
        ItemStack it = new ItemStack(Material.END_CRYSTAL);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6MythicMob");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the icon that represents the pet.");
        lores.add(" ");
        lores.add("§7Click it with an item from your inventory");
        lores.add("§7to modify the current item.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNALS()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Signals");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the list of signals that");
        lores.add("§7can be casted using the signal stick.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNAL_STICK()
    {
        ItemStack it = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Signal stick");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the signal stick item.");
        lores.add(" ");
        lores.add("§7Click it with an item from your inventory");
        lores.add("§7to modify the current item.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Signal stick");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the signal stick is accessible");
        lores.add("§7in the interaction menu directly.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKINS()
    {
        ItemStack it = new ItemStack(Material.LEATHER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Skins");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Add/Delete skins for that pet.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVELS()
    {
        ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Living pet features");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Add/Edit living pet features.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /*
     * PET EDITOR LEVEL icon
     */

    public EditorItems setupPetLevelIcon(PetLevel level)
    {
        ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§a" + level.getLevelName());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Experience threshold: §a" + level.getExpThreshold());
        lores.add(" ");
        lores.add("§eClick to edit that level.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = level;

        return this;
    }

    private static ItemStack PET_EDITOR_LEVEL_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Level name");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the display name of the level.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EXP_THRESHOLD()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Experience threshold");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the minimum experience value");
        lores.add("§7for the pet to access that level.");
        lores.add("§cNote that the first level starts at 0 XP");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_MAX_HEALTH()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Maximum health");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the pet's health.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_REGENERATION()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Regeneration");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the regeneration of health");
        lores.add("§7over time. §c(health/second)");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_RESISTANCE_MODIFIER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Resistance modifier");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the by how much the damage received");
        lores.add("§7are divided by.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_DAMAGE_MODIFIER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Damage modifier");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set by how much the damage done by");
        lores.add("§7the pet can be multiplicated by.");
        lores.add("§cThis is not automatic and should be used");
        lores.add("§cas a placeholder in MythicMobs (see wiki)");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_POWER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Power modifier");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set by how much the spell power done of");
        lores.add("§7the pet can be multiplicated by.");
        lores.add("§cThis is not automatic and should be used");
        lores.add("§cas a placeholder in MythicMobs (see wiki)");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_RESPAWN()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Cooldown - Respawn");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set how long before the pet can");
        lores.add("§7be respawned after dying.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_REVOKE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Cooldown - Revoke");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set how long before the pet can");
        lores.add("§7be respawned after being revoked.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_INVENTORY_EXTENSION()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Inventory extension");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set how many more slots are unlocked");
        lores.add("§7in the pet inventory at that level.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Announcement - Text");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a text to be announced when");
        lores.add("§7the pet evolves.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Announcement - Type");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the announcement type.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Announcement - Skill");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the skill casted when");
        lores.add("§7the pet evolves.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_PET_ID()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Evolution - Pet ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the pet ID of the evolution.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_DELAY()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Evolution - Delay");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a delay before the evolution");
        lores.add("§7is triggered (like the skill duration, in ticks).");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Evolution - Remove old access");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether the previous pet permission should");
        lores.add("§7be removed when evolving (recommended to true)");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /*
     * PET EDITOR SKIN icon
     */

    public EditorItems setupSkinIcon(PetSkin skin)
    {
        ItemStack it = new ItemStack(Material.LEATHER);
        if(skin.getIcon() != null)
            it = skin.getIcon().clone();

        ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6Skin: §e" + skin.getMythicMobId());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§eClick to edit that skin.");

        meta.setLore(lores);

        it.setItemMeta(meta);

        this.item = it;

        this.value = skin;

        return this;
    }

    public EditorItems setupEditSkinIcon(PetSkin skin)
    {
        ItemStack it = new ItemStack(Material.LEATHER);
        if(skin.getIcon() != null)
            it = skin.getIcon().clone();

        ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6Skin: §e" + skin.getMythicMobId());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§eClick with an item to edit");
        lores.add("§ethe icon of the skin.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = skin;

        return this;
    }

    private static ItemStack PET_EDITOR_SKIN_MYTHICMOB()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Skin - MythicMob");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the mythicmob to swap to as a skin");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKIN_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Skin - Permission");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the permission to");
        lores.add("§7unlock the skin.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

}
