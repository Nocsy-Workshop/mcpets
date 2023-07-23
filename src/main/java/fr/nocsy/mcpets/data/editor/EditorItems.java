package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.*;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EditorItems {

    UNKNOWN(UNKNOWN(), null, null, null, null, false),
    FILLER(FILLER(), null, null, null, null, false),

    BACK_TO_GLOBAL_SELECTION(BACK_TO_ITEM("global menu"), null, null, null, EditorState.GLOBAL_EDITOR, false),
    BACK_TO_PET_SELECTION(BACK_TO_ITEM("pet selection menu"), null, null, null, EditorState.PET_EDITOR, false),
    BACK_TO_PET_EDIT(BACK_TO_ITEM("pet editor"), null, null, null, EditorState.PET_EDITOR_EDIT, false),
    BACK_TO_PET_LEVELS_EDIT(BACK_TO_ITEM("pet levels"), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    BACK_TO_PET_SKINS_EDIT(BACK_TO_ITEM("pet skins"), null, null, null, EditorState.PET_EDITOR_SKINS, false),
    BACK_TO_CATEGORIES_EDIT(BACK_TO_ITEM("categories"), null, null, null, EditorState.CATEGORY_EDITOR, false),
    BACK_TO_ITEM_EDITOR(BACK_TO_ITEM("items"), null, null, null, EditorState.ITEM_EDITOR, false),
    BACK_TO_PETFOOD_EDITOR(BACK_TO_ITEM("pet foods"), null, null, null, EditorState.PETFOOD_EDITOR, false),

    // Default selection menu
    CONFIG_EDITOR(CONFIG_EDITOR(), null, null, null, EditorState.CONFIG_EDITOR, false),
    PET_EDITOR(PET_EDITOR(), null, null, null, EditorState.PET_EDITOR, false),
    CATEGORY_EDITOR(CATEGORY_EDITOR(), null, null, null, EditorState.CATEGORY_EDITOR, false),
    ITEM_EDITOR(ITEM_EDITOR(), null, null, null, EditorState.ITEM_EDITOR, false),
    PETFOOD_EDITOR(PETFOOD_EDITOR(), null, null, null, EditorState.PETFOOD_EDITOR, false),

    // Config editor
    CONFIG_EDITOR_PREFIX(CONFIG_EDITOR_PREFIX(), "Prefix", "config", EditorExpectationType.STRING, null, true),
    CONFIG_EDITOR_DEFAULT_NAME(CONFIG_EDITOR_DEFAULT_NAME(), "DefaultName", "config", EditorExpectationType.STRING, null, true),
    CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES(), "UseDefaultMythicMobsNames", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME(), "OverrideDefaultName", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU(), "RightClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU(), "LeftClickToOpenMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_SNEAKMODE(CONFIG_EDITOR_SNEAKMODE(), "SneakMode", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_NAMEABLE(CONFIG_EDITOR_NAMEABLE(), "Nameable", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_MOUNTABLE(CONFIG_EDITOR_MOUNTABLE(), "Mountable", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISTANCE_TELEPORT(CONFIG_EDITOR_DISTANCE_TELEPORT(), "DistanceTeleport", "config", EditorExpectationType.FLOAT, null, true),
    CONFIG_EDITOR_MAX_NAME_LENGTH(CONFIG_EDITOR_MAX_NAME_LENGTH(), "MaxNameLenght", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_INVENTORY_SIZE(CONFIG_EDITOR_INVENTORY_SIZE(), "InventorySize", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU(), "EnableClickBackToMenu", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON(), "ActivateBackMenuIcon", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(CONFIG_EDITOR_DISMOUNT_ON_DAMAGED(), "DismountOnDamaged", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK(), "DisableInventoryWhileHoldingSignalStick", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN(), "PercentHealthOnRespawn", "config", EditorExpectationType.FLOAT, null, true),
    CONFIG_EDITOR_AUTO_SAVE_DELAY(CONFIG_EDITOR_AUTO_SAVE_DELAY(), "AutoSaveDelay", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN(), "DefaultRespawnCooldown", "config", EditorExpectationType.INT, null, true),
    CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN(), "GlobalRespawnCooldown", "config", EditorExpectationType.BOOLEAN, null, true),
    CONFIG_EDITOR_GLOBAL_AUTORESPAWN(CONFIG_EDITOR_GLOBAL_AUTORESPAWN(), "AutoRespawn", "config", EditorExpectationType.BOOLEAN, null, true),

    // Pet editor
    PET_EDITOR_EDIT_PET(UNKNOWN(), null, null, EditorExpectationType.PET, null, false),
    PET_EDITOR_CREATE_NEW(CREATE_NEW_ITEM("pet", Material.MAGMA_CUBE_SPAWN_EGG), null, null, EditorExpectationType.PET_CREATE, null, false),
    PAGE_SELECTOR(PAGE_SELECTOR(), null, null, EditorExpectationType.PAGE_SELECTOR, null, false),

    PET_EDITOR_DELETE(DELETE("pet"), null, null, EditorExpectationType.PET_DELETE, null, false),
    PET_EDITOR_LEVELS(PET_EDITOR_LEVELS(), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    PET_EDITOR_SKINS(PET_EDITOR_SKINS(), null, null, null, EditorState.PET_EDITOR_SKINS, false),

    PET_EDITOR_ICON(PET_EDITOR_ICON(), "Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_MYTHICMOB(PET_EDITOR_MYTHICMOB(), "MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_PERMISSION(PET_EDITOR_PERMISSION(), "Permission", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_MOUNTABLE(PET_EDITOR_MOUNTABLE(), "Mountable", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_MOUNT_TYPE(PET_EDITOR_MOUNT_TYPE(), "MountType", null, EditorExpectationType.MOUNT_TYPE, null, true),
    PET_EDITOR_DESPAWN_ON_DISMOUNT(PET_EDITOR_DESPAWN_ON_DISMOUNT(), "DespawnOnDismount", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_AUTORIDE(PET_EDITOR_AUTORIDE(), "AutoRide", null, EditorExpectationType.BOOLEAN, null, true),
    PET_EDITOR_MOUNT_PERMISSION(PET_EDITOR_MOUNT_PERMISSION(), "MountPermission", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_DESPAWN_SKILL(PET_EDITOR_DESPAWN_SKILL(), "DespawnSkill", null, EditorExpectationType.SKILL, null, true),
    PET_EDITOR_DISTANCE(PET_EDITOR_DISTANCE(), "Distance", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_SPAWN_RANGE(PET_EDITOR_SPAWN_RANGE(), "SpawnRange", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_COMING_BACK_RANGE(PET_EDITOR_COMING_BACK_RANGE(), "ComingBackRange", null, EditorExpectationType.FLOAT, null, true),
    PET_EDITOR_INVENTORY_SIZE(PET_EDITOR_INVENTORY_SIZE(), "InventorySize", null, EditorExpectationType.INT, null, true),
    PET_EDITOR_TAMING_PROGRESS_SKILL(PET_EDITOR_TAMING_PROGRESS_SKILL(), "Taming.TamingProgressSkill", null, EditorExpectationType.INT, null, true),
    PET_EDITOR_TAMING_FINISHED_SKILL(PET_EDITOR_TAMING_FINISHED_SKILL(), "Taming.TamingFinishedSkill", null, EditorExpectationType.INT, null, true),
    PET_EDITOR_SIGNALS(PET_EDITOR_SIGNALS(), "Signals.Values", null, EditorExpectationType.STRING_LIST, null, true),
    PET_EDITOR_SIGNAL_STICK(PET_EDITOR_SIGNAL_STICK(), "Signals.Item.Raw", null, EditorExpectationType.ITEM, null, true),
    PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU(), "Signals.Item.GetFromMenu", null, EditorExpectationType.BOOLEAN, null, true),
    // Pet editor - Levels
    PET_EDITOR_EDIT_LEVEL(UNKNOWN(), null, null, EditorExpectationType.PET_LEVEL_EDIT, null, false),
    PET_EDITOR_EDIT_LEVEL_DELETE(DELETE("level"), null, null, EditorExpectationType.PET_LEVEL_DELETE, null, false),
    PET_EDITOR_LEVEL_CREATE_NEW(CREATE_NEW_ITEM("level", Material.EXPERIENCE_BOTTLE), null, null, EditorExpectationType.PET_LEVEL_CREATE, null, false),
    PET_EDITOR_EDIT_LEVEL_NAME(PET_EDITOR_LEVEL_NAME(), "Levels.%path%.Name", null, EditorExpectationType.STRING, null, false),
    PET_EDITOR_EDIT_LEVEL_EXP_THRESHOLD(PET_EDITOR_LEVEL_EXP_THRESHOLD(), "Levels.%path%.ExperienceThreshold", null, EditorExpectationType.POSITIVE_INT, null, false),
    PET_EDITOR_EDIT_LEVEL_MAX_HEALTH(PET_EDITOR_LEVEL_MAX_HEALTH(), "Levels.%path%.MaxHealth", null, EditorExpectationType.POSITIVE_INT, null, false),
    PET_EDITOR_EDIT_LEVEL_REGENERATION(PET_EDITOR_LEVEL_REGENERATION(), "Levels.%path%.Regeneration", null, EditorExpectationType.POSITIVE_FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_RESISTANCE_MODIFIER(PET_EDITOR_LEVEL_RESISTANCE_MODIFIER(), "Levels.%path%.ResistanceModifier", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_DAMAGE_MODIFIER(PET_EDITOR_LEVEL_DAMAGE_MODIFIER(), "Levels.%path%.DamageModifier", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_POWER(PET_EDITOR_LEVEL_POWER(), "Levels.%path%.Power", null, EditorExpectationType.FLOAT, null, false),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_RESPAWN(PET_EDITOR_LEVEL_COOLDOWN_RESPAWN(), "Levels.%path%.Cooldowns.Respawn", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_COOLDOWN_REVOKE(PET_EDITOR_LEVEL_COOLDOWN_REVOKE(), "Levels.%path%.Cooldowns.Revoke", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_INVENTORY_EXTENSION(PET_EDITOR_LEVEL_INVENTORY_EXTENSION(), "Levels.%path%.InventoryExtension", null, EditorExpectationType.INVENTORY_SIZE, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TEXT(PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT(), "Levels.%path%.Announcement.Text", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TYPE(PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE(), "Levels.%path%.Announcement.Type", null, EditorExpectationType.ANNOUNCEMENT_TYPE, null, true),
    PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_SKILL(PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL(), "Levels.%path%.Announcement.Skill", null, EditorExpectationType.STRING, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_PET_ID(PET_EDITOR_LEVEL_EVOLUTION_PET_ID(), "Levels.%path%.Evolution.PetId", null, EditorExpectationType.PET_ID, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_DELAY(PET_EDITOR_LEVEL_EVOLUTION_DELAY(), "Levels.%path%.Evolution.DelayBeforeEvolution", null, EditorExpectationType.POSITIVE_INT, null, true),
    PET_EDITOR_EDIT_LEVEL_EVOLUTION_REMOVE_ACCESS(PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS(), "Levels.%path%.Evolution.RemoveAccess", null, EditorExpectationType.BOOLEAN, null, true),
    // Pet editor - Skins
    PET_EDITOR_EDIT_SKIN(UNKNOWN(), null, null, EditorExpectationType.PET_SKIN_EDIT, null, false),
    PET_EDITOR_EDIT_SKIN_DELETE(DELETE("skin"), null, null, EditorExpectationType.PET_SKIN_DELETE, null, false),
    PET_EDITOR_SKIN_CREATE_NEW(CREATE_NEW_ITEM("skin", Material.LEATHER), null, null, EditorExpectationType.PET_SKIN_CREATE, null, false),
    PET_EDITOR_EDIT_SKIN_ICON(UNKNOWN(), "%path%.Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_EDIT_SKIN_MYTHICMOB(PET_EDITOR_SKIN_MYTHICMOB(), "%path%.MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_EDIT_SKIN_PERMISSION(PET_EDITOR_SKIN_PERMISSION(), "%path%.Permission", null, EditorExpectationType.STRING, null, true),

    // Category editor
    CATEGORY_EDITOR_EDIT_CATEGORY(UNKNOWN(), null, null, EditorExpectationType.CATEGORY_EDIT, null, false),

    CATEGORY_EDITOR_CATEGORY_CREATE(CREATE_NEW_ITEM("category", Material.KNOWLEDGE_BOOK), null, null, EditorExpectationType.CATEGORY_CREATE, null, false),
    CATEGORY_EDITOR_CATEGORY_DELETE(DELETE("category"), null, null, EditorExpectationType.CATEGORY_DELETE, null, false),

    CATEGORY_EDITOR_CATEGORY_EDIT_ID(CATEGORY_EDITOR_CATEGORY_EDIT_ID(), "Id", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON(UNKNOWN(), "Icon", null, EditorExpectationType.ITEM, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(), "IconName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(), "DisplayName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(), "DefaultCategory", null, EditorExpectationType.BOOLEAN, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(), "ExcludedCategories", null, EditorExpectationType.STRING_LIST, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_ADD, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_REMOVE, null, false),

    // Items editor
    ITEMS_EDIT(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_EDIT, null, false),
    ITEMS_DELETE(DELETE("item"), null, null, EditorExpectationType.ITEM_DELETE, null, false),
    ITEMS_CREATE(CREATE_NEW_ITEM("item", Material.EMERALD), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_CREATE, null, false),
    ITEMS_EDIT_ID(ITEMS_EDIT_ID(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_SECTION_ID, null, false),
    ITEMS_EDIT_ITEM(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM, null, false),

    // Pet food
    PETFOOD_EDITOR_EDIT(UNKNOWN(), null, "petfoods", EditorExpectationType.PETFOOD_EDIT, null, false),
    PETFOOD_EDITOR_EDIT_CREATE(CREATE_NEW_ITEM("pet food", Material.COOKED_CHICKEN), null, "petfoods", EditorExpectationType.PETFOOD_CREATE, null, false),

    PETFOOD_EDITOR_EDIT_DELETE(DELETE("pet food"), null, "petfoods", EditorExpectationType.PETFOOD_DELETE, null, false),
    PETFOOD_EDITOR_EDIT_ID(PETFOOD_EDITOR_EDIT_ID(), "%path%", "petfoods", EditorExpectationType.PETFOOD_ID, null, false),
    PETFOOD_EDITOR_EDIT_ITEM_ID(UNKNOWN(), "%path%.ItemId", "petfoods", EditorExpectationType.ITEM_ID_OR_MATERIAL, null, false),
    PETFOOD_EDITOR_EDIT_TYPE(PETFOOD_EDITOR_EDIT_TYPE(), "%path%.Type", "petfoods", EditorExpectationType.PETFOOD_TYPE, null, false),
    PETFOOD_EDITOR_EDIT_POWER(PETFOOD_EDITOR_EDIT_POWER(), "%path%.Power", "petfoods", EditorExpectationType.FLOAT, null, false),
    PETFOOD_EDITOR_EDIT_OPERATOR(PETFOOD_EDITOR_EDIT_OPERATOR(), "%path%.Operator", "petfoods", EditorExpectationType.OPERATOR_TYPE, null, true),
    PETFOOD_EDITOR_EDIT_SIGNAL(PETFOOD_EDITOR_EDIT_SIGNAL(), "%path%.Signal", "petfoods", EditorExpectationType.STRING, null, true),
    PETFOOD_EDITOR_EDIT_PETS_ADD(PETFOOD_EDITOR_EDIT_PETS_ADD(), "%path%.Pets", "petfoods", EditorExpectationType.PETFOOD_PET_LIST_ADD, null, false),
    PETFOOD_EDITOR_EDIT_PETS_REMOVE(PETFOOD_EDITOR_EDIT_PETS_REMOVE(), "%path%.Pets", "petfoods", EditorExpectationType.PETFOOD_PET_LIST_REMOVE, null, false),

    PETFOOD_EDITOR_EDIT_EVOLUTION(PETFOOD_EDITOR_EDIT_EVOLUTION(), "%path%.Evolution", "petfoods", EditorExpectationType.PET_ID, null, true),
    PETFOOD_EDITOR_EDIT_EXP_THRESHOLD(PETFOOD_EDITOR_EDIT_EXP_THRESHOLD(), "%path%.ExperienceThreshold", "petfoods", EditorExpectationType.POSITIVE_INT, null, true),
    PETFOOD_EDITOR_EDIT_DELAY(PETFOOD_EDITOR_EDIT_DELAY(), "%path%.DelayBeforeEvolution", "petfoods", EditorExpectationType.POSITIVE_INT, null, true),

    PETFOOD_EDITOR_EDIT_PERMISSION(PETFOOD_EDITOR_EDIT_PERMISSION(), "%path%.Permission", "petfoods", EditorExpectationType.STRING, null, true),
    PETFOOD_EDITOR_EDIT_UNLOCKED_PET(PETFOOD_EDITOR_EDIT_UNLOCKED_PET(), "%path%.UnlockPet", "petfoods", EditorExpectationType.PET_ID, null, true),
    ;

    private final static String editorTag = "MCPets:Editor:";
    @Getter
    private static ArrayList<String> cachedDeleted = new ArrayList<>();

    public static String RESET_VALUE_TAG = "ResetValue°897698575";

    @Getter
    private String id;
    @Setter
    private ItemStack item;
    @Getter
    private String filePath;
    @Getter
    private String inputFilePath;
    @Getter
    private String variablePath;
    private String variablePathPlaceholder;
    @Getter
    private Object value;
    @Getter
    private EditorExpectationType type;
    @Getter
    private EditorState nextState;
    @Getter
    private boolean resetable;

    EditorItems(ItemStack item, String variablePath, String filePath, EditorExpectationType type, EditorState nextState, boolean resetable)
    {
        this.id = this.name().toUpperCase();
        this.item = item;
        this.inputFilePath = filePath;
        this.filePath = "./plugins/MCPets/" + filePath + ".yml";
        this.variablePath = variablePath;
        this.variablePathPlaceholder = "";
        this.type = type;
        this.nextState = nextState;
        this.resetable = resetable;

        refreshData();
    }

    public void refreshData()
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

    public boolean save(Player creator)
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
        else if(this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD) ||
                this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_REMOVE))
        {
            Pet pet = Pet.getFromId(this.value + "");
            if(pet == null)
                return false;

            EditorEditing editing = EditorEditing.get(creator);
            CategoryConfig config = CategoryConfig.getMapping().get(editing.getMappedId());
            if(this.getType().equals(EditorExpectationType.CATEGORY_PET_LIST_ADD))
            {
                config.addPet(pet);
            }
            else
            {
                config.removePet(pet);
            }
            return true;
        }
        else if(this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD) ||
                this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_REMOVE))
        {
            Pet pet = Pet.getFromId(this.value + "");
            if(pet == null)
                return false;

            EditorEditing editing = EditorEditing.get(creator);
            String key = editing.getMappedId();
            PetFoodConfig config = PetFoodConfig.getInstance();
            if(this.getType().equals(EditorExpectationType.PETFOOD_PET_LIST_ADD))
            {
                config.addPet(key, pet.getId());
            }
            else
            {
                config.removePet(key, pet.getId());
            }
            return true;
        }

        File file = new File(filePath);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);


        if(this.getType().equals(EditorExpectationType.ITEM_SECTION_ID))
        {
            EditorEditing editing = EditorEditing.get(creator);
            String itemId = editing.getMappedId();

            ItemStack item = config.getItemStack(itemId);

            config.set(itemId, null);
            config.set(this.value.toString(), item);

            try
            {
                config.save(file);

                editing.setMappedId(this.value.toString());
                ItemsListConfig.reloadInstance();
                return true;
            }
            catch (IOException ex)
            {
                return false;
            }
        }
        else if(this.getType().equals(EditorExpectationType.PETFOOD_ID))
        {
            EditorEditing editing = EditorEditing.get(creator);
            PetFood petFood = PetFood.getFromId(editing.getMappedId());

            PetFoodConfig.getInstance().changePetFoodKey(petFood, this.value.toString());
            editing.setMappedId(this.value.toString());
            return true;
        }
        else
        {
            if(this.value.equals(RESET_VALUE_TAG) && resetable)
                this.value = null;
            config.set(variablePath.replace("%path%", variablePathPlaceholder), this.value);
        }

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
        if(this.type == null)
            return;
        if(value == null && this.type.equals(EditorExpectationType.BOOLEAN))
        {
            value = true;
        }
        else if(value != null && this.type.equals(EditorExpectationType.BOOLEAN))
        {
            value = !(Boolean) value;
        }
    }

    public ItemStack getItem()
    {
        ItemStack it = item.clone();
        ItemMeta meta = it.getItemMeta();
        if(it.getType().equals(Material.FILLED_MAP))
            it.setType(Material.MAP);
        meta.setLocalizedName(editorTag + getId());

        // Basically, we are replacing the placeholder for the value within the lores
        List<String> lores = meta.getLore();
        ArrayList<String> newLores = new ArrayList<>();
        if(lores != null)
        {
            for(String lore : lores)
            {

                if(lore.contains("%value%")
                        && value != null && value instanceof List)
                {
                    newLores.add(lore.replace("%value%", ""));
                    if(((List<String>) value).size() > 0)
                    {
                        for(String entry : (List<String>) value)
                        {
                            newLores.add("§7 - §e" + entry);
                        }
                    }
                    else
                    {
                        newLores.add("§cempty");
                    }

                }
                else
                {
                    String valueStr = value == null ? "§6default (not set)" : value.toString();

                    if(value == null && this.type == EditorExpectationType.BOOLEAN)
                        valueStr = "false";

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
        }

        if(resetable)
        {
            newLores.add(" ");
            newLores.add("§cSHIFT + click§7 to §creset§7 the value.");
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

    public EditorItems setValue(Object any)
    {
        this.value = any;
        return this;
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

    private static ItemStack CONFIG_EDITOR_GLOBAL_AUTORESPAWN()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Autorespawn");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Whether when a pet dies, it is respawned");
        lores.add("§7automatically at the player's side after reborn.");
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

    public EditorItems setupPetIcon(String petId)
    {
        Pet pet = PetConfig.loadConfigPet(petId);

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

    public EditorItems setupPetIconEdit(String petId)
    {
        Pet pet = PetConfig.loadConfigPet(petId);
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

    public EditorItems setupSignalStickItem(String petId)
    {
        Pet pet = PetConfig.loadConfigPet(petId);
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

    private static ItemStack PAGE_SELECTOR()
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
        lores.add("§cSHIFT§7 + Click to delete the " + what + ".");
        lores.add(" ");
        lores.add("§c§lWARNING: this is permanent.");

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
        meta.setDisplayName("§6Signal stick from menu");

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

    public EditorItems setupPetLevelIcon(String petId, String levelId)
    {
        Pet pet = PetConfig.loadConfigPet(petId);
        PetLevel level = pet.getPetLevels().stream().filter(petLevel -> petLevel.getLevelId().equals(levelId)).findFirst().orElse(null);

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

    public EditorItems setupSkinIcon(String petId, String skinId)
    {

        Pet pet = PetConfig.loadConfigPet(petId);
        PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(skinId)).findFirst().orElse(null);

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

    public EditorItems setupEditSkinIcon(String petId, String skinId)
    {

        Pet pet = PetConfig.loadConfigPet(petId);
        PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(skinId)).findFirst().orElse(null);

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


    /*
     * Category icons
     */

    public EditorItems setupCategoryIcon(String categoryId)
    {
        Category category = CategoryConfig.loadConfigCategory(categoryId);
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        if(category.getIcon() != null)
            it = category.getIcon().clone();

        ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6Category: §e" + category.getIconName());
        ArrayList<String> lores = new ArrayList<>();

        lores.add("§eClick to edit the category.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = category;

        return this;
    }


    public EditorItems setupEditCategoryIcon(String categoryId)
    {
        Category category = CategoryConfig.loadConfigCategory(categoryId);
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        if(category.getIcon() != null)
            it = category.getIcon().clone();

        ItemMeta meta = it.getItemMeta();

        meta.setDisplayName("§6Category: §e" + category.getIconName());
        ArrayList<String> lores = new ArrayList<>();

        lores.add("§aExcluded categories:");
        if(category.getExcludedCategoriesId().size() == 0)
            lores.add("§7- §6None");
        for(String excludedCategoryId : category.getExcludedCategoriesId())
            lores.add("§7- " + excludedCategoryId);

        lores.add(" ");

        if(category.isDefaultCategory())
        {
            lores.add("§aIncludes all pets §7(default category)");
        }
        else
        {
            lores.add("§aIncluded Pets:");
            if(category.getPets().size() == 0)
                lores.add("§7- §6None");
            for(Pet pet : category.getPets())
                lores.add(" §7 - " + pet.getId());
        }

        lores.add(" ");

        lores.add("§eClick with an item to edit");
        lores.add("§ethe icon of the category.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = category;

        return this;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ID()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Category ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Click to edit the category ID.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Default category");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7(Optional) Should all the pet go");
        lores.add("§7into that category by default ?");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Excluded categories");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a(Optional)§7 Exclude all the pets");
        lores.add("§7from the specified categories.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD()
    {
        ItemStack it = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aAdd§6 a pets");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Add a pet to the category.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE()
    {
        ItemStack it = new ItemStack(Material.NETHER_BRICK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cRemove§6 a pet");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Remove a pet from the category.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Category Inventory title");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the title of the category");
        lores.add("§7inventory in the GUI.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Category icon name");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the name of the icon");
        lores.add("§7for the category in the GUI.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /*
     * Items editor
     */

    public EditorItems setupItemIcon(String itemId)
    {
        ItemStack it = new ItemStack(Material.BEDROCK);
        ItemStack loadedIt = ItemsListConfig.loadConfigItem(itemId);
        if(loadedIt != null)
            it = loadedIt.clone();

        ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();

        lores.add(" ");
        lores.add("§aId: §b" + itemId);
        lores.add(" ");
        lores.add("§eClick to edit that item.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = itemId;

        return this;
    }

    public EditorItems setupEditItemIcon(String itemId)
    {
        ItemStack it = new ItemStack(Material.BEDROCK);
        ItemStack loadedIt = ItemsListConfig.loadConfigItem(itemId);
        if(loadedIt != null)
            it = loadedIt.clone();

        ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();
        if(it.getItemMeta().hasLore() && it.getItemMeta().getLore() != null)
        {
            lores = (ArrayList<String>) it.getItemMeta().getLore();
        }

        lores.add(" ");
        lores.add("§eClick with an item to change it.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = itemId;

        return this;
    }

    private static ItemStack ITEMS_EDIT_ID()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Item ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Edit the ID of the item.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * Pet food editor
     */

    public EditorItems setupPetfoodIcon(String petFoodId)
    {
        ItemStack it = new ItemStack(Material.BEDROCK);
        PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        if(petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();

        ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();

        lores.add(" ");
        lores.add("§aId: §b" + petFood.getId());
        lores.add(" ");
        lores.add("§eClick to edit that petfood.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = petFood;

        return this;
    }

    public EditorItems setupEditPetFoodIcon(String petFoodId)
    {
        ItemStack it = new ItemStack(Material.BEDROCK);
        PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        if(petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();

        ItemMeta meta = it.getItemMeta();

        ArrayList<String> lores = new ArrayList<>();
        if(it.getItemMeta().hasLore() && it.getItemMeta().getLore() != null)
        {
            lores = (ArrayList<String>) it.getItemMeta().getLore();
        }

        lores.add(" ");
        lores.add("§eClick with an item to change it.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        this.value = petFood;

        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_ID()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Petfood ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Edit the ID of the petfood.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    public EditorItems setupPetFoodEditorEditItem(String petFoodId)
    {
        PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        if(petFood.getItemStack() != null)
            it = petFood.getItemStack().clone();
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Food item");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the food item.");
        lores.add("§7It can be either a registered §apet item§7");
        lores.add("§7for more customization, or any §bMATERIAL§7.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;
        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_TYPE()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Pet food type");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the pet food type (see wiki).");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_POWER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Power value");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the power of the pet food.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_OPERATOR()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Operator");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the mathematical operation");
        lores.add("§7to be perfomed on the power of the food. (wiki)");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_SIGNAL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Signal");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set the signal triggered when giving");
        lores.add("§7the pet food to the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_ADD()
    {
        ItemStack it = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§aAdd§6 pet");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§aAdd§7 a compatible pet. §a(Optional)");
        lores.add(" ");
        lores.add("§7Currently restricted pets: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_REMOVE()
    {
        ItemStack it = new ItemStack(Material.NETHER_BRICK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§cRemove§6 pet");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§cRemove§7 a compatible pet. §a(Optional)");
        lores.add(" ");
        lores.add("§7Currently restricted pets: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EVOLUTION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Evolution");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a(Optional)§7 Set the evolution triggered");
        lores.add("§7when the pet eats the food.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EXP_THRESHOLD()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Experience threshold");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Set a value of experience after which");
        lores.add("§7the food can be consumed by the pet.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DELAY()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Delay before evolution");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7If you're using an evolution food, how long");
        lores.add("§7before the evolution should be triggered.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Permission");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7Necessary permission to use the pet food.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_UNLOCKED_PET()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6Unlocked pet");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7If the food type is §aUNLOCK§7,");
        lores.add("§7which pet should be unlocked.");
        lores.add(" ");
        lores.add("§7Current value: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


}
