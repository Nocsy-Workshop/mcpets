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

    BACK_TO_GLOBAL_SELECTION(BACK_TO_ITEM("全局菜单"), null, null, null, EditorState.GLOBAL_EDITOR, false),
    BACK_TO_PET_SELECTION(BACK_TO_ITEM("宠物选择菜单"), null, null, null, EditorState.PET_EDITOR, false),
    BACK_TO_PET_EDIT(BACK_TO_ITEM("宠物编辑"), null, null, null, EditorState.PET_EDITOR_EDIT, false),
    BACK_TO_PET_LEVELS_EDIT(BACK_TO_ITEM("宠物等级"), null, null, null, EditorState.PET_EDITOR_LEVELS, false),
    BACK_TO_PET_SKINS_EDIT(BACK_TO_ITEM("宠物皮肤"), null, null, null, EditorState.PET_EDITOR_SKINS, false),
    BACK_TO_CATEGORIES_EDIT(BACK_TO_ITEM("类别"), null, null, null, EditorState.CATEGORY_EDITOR, false),
    BACK_TO_ITEM_EDITOR(BACK_TO_ITEM("物品"), null, null, null, EditorState.ITEM_EDITOR, false),
    BACK_TO_PETFOOD_EDITOR(BACK_TO_ITEM("宠物食物"), null, null, null, EditorState.PETFOOD_EDITOR, false),

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
    PET_EDITOR_CREATE_NEW(CREATE_NEW_ITEM("宠物", Material.MAGMA_CUBE_SPAWN_EGG), null, null, EditorExpectationType.PET_CREATE, null, false),
    PAGE_SELECTOR(PAGE_SELECTOR(), null, null, EditorExpectationType.PAGE_SELECTOR, null, false),

    PET_EDITOR_DELETE(DELETE("宠物"), null, null, EditorExpectationType.PET_DELETE, null, false),
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
    PET_EDITOR_EDIT_LEVEL_DELETE(DELETE("等级"), null, null, EditorExpectationType.PET_LEVEL_DELETE, null, false),
    PET_EDITOR_LEVEL_CREATE_NEW(CREATE_NEW_ITEM("等级", Material.EXPERIENCE_BOTTLE), null, null, EditorExpectationType.PET_LEVEL_CREATE, null, false),
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
    PET_EDITOR_EDIT_SKIN_DELETE(DELETE("皮肤"), null, null, EditorExpectationType.PET_SKIN_DELETE, null, false),
    PET_EDITOR_SKIN_CREATE_NEW(CREATE_NEW_ITEM("皮肤", Material.LEATHER), null, null, EditorExpectationType.PET_SKIN_CREATE, null, false),
    PET_EDITOR_EDIT_SKIN_ICON(UNKNOWN(), "%path%.Icon.Raw", null, EditorExpectationType.ITEM, null, false),
    PET_EDITOR_EDIT_SKIN_MYTHICMOB(PET_EDITOR_SKIN_MYTHICMOB(), "%path%.MythicMob", null, EditorExpectationType.MYTHICMOB, null, false),
    PET_EDITOR_EDIT_SKIN_PERMISSION(PET_EDITOR_SKIN_PERMISSION(), "%path%.Permission", null, EditorExpectationType.STRING, null, true),

    // Category editor
    CATEGORY_EDITOR_EDIT_CATEGORY(UNKNOWN(), null, null, EditorExpectationType.CATEGORY_EDIT, null, false),

    CATEGORY_EDITOR_CATEGORY_CREATE(CREATE_NEW_ITEM("类别", Material.KNOWLEDGE_BOOK), null, null, EditorExpectationType.CATEGORY_CREATE, null, false),
    CATEGORY_EDITOR_CATEGORY_DELETE(DELETE("类别"), null, null, EditorExpectationType.CATEGORY_DELETE, null, false),

    CATEGORY_EDITOR_CATEGORY_EDIT_ID(CATEGORY_EDITOR_CATEGORY_EDIT_ID(), "Id", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON(UNKNOWN(), "图标", null, EditorExpectationType.ITEM, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME(), "IconName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME(), "DisplayName", null, EditorExpectationType.STRING, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY(), "DefaultCategory", null, EditorExpectationType.BOOLEAN, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES(), "ExcludedCategories", null, EditorExpectationType.STRING_LIST, null, true),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_ADD, null, false),
    CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE(), "Pets", null, EditorExpectationType.CATEGORY_PET_LIST_REMOVE, null, false),

    // Items editor
    ITEMS_EDIT(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_EDIT, null, false),
    ITEMS_DELETE(DELETE("物品"), null, null, EditorExpectationType.ITEM_DELETE, null, false),
    ITEMS_CREATE(CREATE_NEW_ITEM("物品", Material.EMERALD), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_CREATE, null, false),
    ITEMS_EDIT_ID(ITEMS_EDIT_ID(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM_SECTION_ID, null, false),
    ITEMS_EDIT_ITEM(UNKNOWN(), "%path%", ItemsListConfig.getInstance().getFullPath(), EditorExpectationType.ITEM, null, false),

    // Pet food
    PETFOOD_EDITOR_EDIT(UNKNOWN(), null, "petfoods", EditorExpectationType.PETFOOD_EDIT, null, false),
    PETFOOD_EDITOR_EDIT_CREATE(CREATE_NEW_ITEM("宠物食物", Material.COOKED_CHICKEN), null, "petfoods", EditorExpectationType.PETFOOD_CREATE, null, false),

    PETFOOD_EDITOR_EDIT_DELETE(DELETE("宠物食物"), null, "petfoods", EditorExpectationType.PETFOOD_DELETE, null, false),
    PETFOOD_EDITOR_EDIT_ID(PETFOOD_EDITOR_EDIT_ID(), "%path%", "petfoods", EditorExpectationType.PETFOOD_ID, null, false),
    PETFOOD_EDITOR_EDIT_ITEM_ID(UNKNOWN(), "%path%.ItemId", "petfoods", EditorExpectationType.ITEM_ID_OR_MATERIAL, null, false),
    PETFOOD_EDITOR_EDIT_TYPE(PETFOOD_EDITOR_EDIT_TYPE(), "%path%.Type", "petfoods", EditorExpectationType.PETFOOD_TYPE, null, false),
    PETFOOD_EDITOR_EDIT_POWER(PETFOOD_EDITOR_EDIT_POWER(), "%path%.Power", "petfoods", EditorExpectationType.FLOAT, null, false),
    PETFOOD_EDITOR_EDIT_DURATION(PETFOOD_EDITOR_EDIT_DURATION(), "%path%.Duration", "petfoods", EditorExpectationType.INT, null, false),
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
                    String valueStr = value == null ? "§6默认(未设置)" : value.toString();

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
            newLores.add("§c按住§cSHIFT键+点击§7可§c重置§7该值.");
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
        meta.setDisplayName("§c未知物品");
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
        meta.setDisplayName("§c回到 " + where);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击返回 " + where + ".");

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
        meta.setDisplayName("§6编辑配置");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击编辑配置选项.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR()
    {
        ItemStack it = new ItemStack(Material.MAGMA_CUBE_SPAWN_EGG);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6编辑宠物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击编辑/创建宠物.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6编辑类别");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击编辑/创建类别.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack ITEM_EDITOR()
    {
        ItemStack it = new ItemStack(Material.EMERALD);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6编辑物品");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击编辑/添加物品.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR()
    {
        ItemStack it = new ItemStack(Material.COOKED_CHICKEN);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6编辑宠物食物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击编辑/添加宠物食物.");

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
        meta.setDisplayName("§6前缀");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击编辑插件的前缀.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6默认宠物名");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7未设置宠物名称");
        lores.add("§7时设置.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6使用默认的 MythicMobs 名称");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7MythicMobs 名称是否应");
        lores.add("§7显示为默认名称.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6覆盖默认名称");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7当宠物为空时重命名");
        lores.add("§7是否应覆盖默认名称.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6右键点击打开菜单");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7右键点击时是否打开菜单.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6左键点击打开菜单");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7左键点击是否打开菜单.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_SNEAKMODE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6潜行状态打开菜单");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7交互时是否需要");
        lores.add("§7潜行才能打开菜单.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_NAMEABLE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6可命名");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7是否所有宠物都可以自定义名字.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MOUNTABLE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6覆盖默认名称");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7如果该功能已为宠物启用");
        lores.add("§7并参数化,则默认情况下");
        lores.add("§7所有宠物是否都应可安装.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISTANCE_TELEPORT()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6传送前距离");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物传送回主人");
        lores.add("§7身边的最短距离.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_MAX_NAME_LENGTH()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6最大名称长度");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置自定义宠物名");
        lores.add("§7的最大长度.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_INVENTORY_SIZE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6召唤物物品栏大小");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7将物品栏大小设置为给定");
        lores.add("§7值,或 -1 表示自适应物品栏.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6启用点击返回菜单(类别)");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击类别菜单外部");
        lores.add("§7是否应打开类别选择.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6激活返回菜单图标");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7交互菜单中是否");
        lores.add("§7显示“返回菜单”图标.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISMOUNT_ON_DAMAGED()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6受伤时下来");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7玩家受到伤害时");
        lores.add("§7是否应该下来.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6重生时的健康百分比");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7宠物死亡后恢复的");
        lores.add("§7生命值百分比(活体宠物).");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_AUTO_SAVE_DELAY()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6自动保存数据库延迟");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7数据库应定期");
        lores.add("§7多久保存.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6默认重生冷却时间");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7默认情况下宠物");
        lores.add("§7多久会复活(活体宠物).");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6全局重生冷却时间");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7玩家在冷却时是否");
        lores.add("§7无法召唤任何宠物.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_GLOBAL_AUTORESPAWN()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6自动重生");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7宠物死亡后是否重生后");
        lores.add("§7自动重生到玩家身边.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6信号棒时禁用物品栏");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7用信号棒点击宠物时");
        lores.add("§7是否可以访问物品栏.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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
        lores.add("§e点击以编辑该宠物.");

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

        if(og_lores.contains("§e点击该图标上的物品"))
            return this;

        ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§e点击该图标上的物品");
        lores.add("§e以替换宠物图标.");

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
        if(meta.getDisplayName().equals("§c未定义"))
            meta.setDisplayName("§6信号棒");

        List<String> og_lores = it.getItemMeta().getLore();
        if(og_lores == null)
            og_lores = new ArrayList<>();

        ArrayList<String> lores = (ArrayList<String>) og_lores;
        lores.add(" ");
        lores.add("§e点击该图标上的物品");
        lores.add("§e以替换信号棒.");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;

        return this;
    }

    private static ItemStack PAGE_SELECTOR()
    {
        ItemStack it = new ItemStack(Material.ARROW);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a页面选择器");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§c左键§7点击转到上一页");
        lores.add("§a右键§7点击进入下一页");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CREATE_NEW_ITEM(String what, Material type)
    {
        ItemStack it = new ItemStack(type);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a创建一个新的 " + what);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7点击创建一个新的 " + what + ".");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack DELETE(String what)
    {
        ItemStack it = new ItemStack(Material.BARRIER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c删除 " + what);

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§cSHIFT§7 + 点击删除 " + what + ".");
        lores.add(" ");
        lores.add("§c§l警告: 这是永久性的.");

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
        lores.add("§7设置处理宠物的 MythicMob.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6权限");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物");
        lores.add("§7解锁权限.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNTABLE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6坐骑");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7宠物是否可骑乘.");
        lores.add("§7这要求宠物在 Blockbench 文件");
        lores.add("§7中具有“安装”骨骼(查看 wiki).");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_TYPE()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6坐骑类型");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物的坐骑类型.");
        lores.add("§7仅当宠物可骑乘时才有效.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_ON_DISMOUNT()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6下来后消失");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7玩家在宠物消失");
        lores.add("§7时是否应该下来.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_AUTORIDE()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6自动骑乘");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7玩家是否应该在宠物");
        lores.add("§7生成时自动骑乘它.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_MOUNT_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6坐骑权限");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置允许玩家骑乘坐骑");
        lores.add("§7的权限(如果启用了坐骑)");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DESPAWN_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6消失技能");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置可选的消失技能.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_DISTANCE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6返回距离");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物回到主人身边");
        lores.add("§7之前的最短距离.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SPAWN_RANGE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6生成范围");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物可能");
        lores.add("§7生成的半径.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_COMING_BACK_RANGE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6近距离回来距离");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7一旦宠物离主人足够近");
        lores.add("§7设定宠物站立的距离");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_INVENTORY_SIZE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6物品栏大小");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物物品栏的大小.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_PROGRESS_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6驯服 - 进化技能");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置玩家驯服宠物");
        lores.add("§7时的技能.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_TAMING_FINISHED_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6驯服 - 完成技能");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置玩家驯服");
        lores.add("§7宠物后的技能.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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
        lores.add("§7设置代表宠物的图标.");
        lores.add(" ");
        lores.add("§7点击库存中的物品");
        lores.add("§7以修改当前物品.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNALS()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6信号");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置可以使用信号棒");
        lores.add("§7投射的信号列表.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SIGNAL_STICK()
    {
        ItemStack it = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6信号棒");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置信号棒物品.");
        lores.add(" ");
        lores.add("§7点击物品栏中的物品");
        lores.add("§7以修改当前物品.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6菜单中的信号棒");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7是否可以在交互菜单");
        lores.add("§7中直接访问信号棒.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKINS()
    {
        ItemStack it = new ItemStack(Material.LEATHER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6皮肤");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7添加/删除该宠物的皮肤.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVELS()
    {
        ItemStack it = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6活体宠物特征");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7添加/编辑活体宠物特征.");

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
        lores.add("§7经验阈值: §a" + level.getExpThreshold());
        lores.add(" ");
        lores.add("§e点击编辑该等级.");

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
        meta.setDisplayName("§6等级名称");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置等级的显示名称.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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
        lores.add("§设置宠物进入该");
        lores.add("§7级别的最低经验值.");
        lores.add("§c请注意第 1 级从 0 XP 开始");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_MAX_HEALTH()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6最大生命值");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物的生命值.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_REGENERATION()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6再生");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置随着时间的推移生命值");
        lores.add("§7的恢复速度. §c(生命值/秒)");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_RESISTANCE_MODIFIER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6修改抗性");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置受到的");
        lores.add("§7伤害除以多少.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_DAMAGE_MODIFIER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6修改伤害");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物所造成的");
        lores.add("§7伤害可以乘以多少.");
        lores.add("§c这不是自动的,应该在 MythicMobs");
        lores.add("§c中用作占位符(参见 wiki)");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_POWER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6修改力量");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物的法术");
        lores.add("§7力量可以乘以多少.");
        lores.add("§c这不是自动的,应该在 MythicMobs");
        lores.add("§c中用作占位符(参见 wiki)");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_RESPAWN()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6冷却时间 - 重生");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物死亡后");
        lores.add("§7多久可以重生.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_COOLDOWN_REVOKE()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6冷却时间 - 撤销");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物被撤销");
        lores.add("§7后多久可以重生.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_INVENTORY_EXTENSION()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6物品栏扩展");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置该等级的宠物物品栏");
        lores.add("§7中还解锁了多少个槽位.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TEXT()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6公告 - 文字");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物进化时");
        lores.add("§7要宣布的文字.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_TYPE()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6公告 - 类型");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置公告类型.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_ANNOUNCEMENT_SKILL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6公告 - 技能");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物进化时");
        lores.add("§7施展的技能.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_PET_ID()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6进化 - 宠物ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置进化宠物ID.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_DELAY()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6进化 - 延迟");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置触发进化之前的");
        lores.add("§7延迟(如技能持续时间,以刻度为单位).");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_LEVEL_EVOLUTION_REMOVE_ACCESS()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6进化 - 删除旧的访问权限");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7进化时是否移除之前");
        lores.add("§7的宠物权限(建议为true)");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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

        meta.setDisplayName("§6皮肤: §e" + skin.getMythicMobId());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§e点击以编辑该皮肤.");

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

        meta.setDisplayName("§6皮肤: §e" + skin.getMythicMobId());
        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§e点击某个物品即可");
        lores.add("§e编辑皮肤的图标.");

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
        meta.setDisplayName("§6皮肤 - MythicMob");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7将 Mythicmob 设置为交换皮肤");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PET_EDITOR_SKIN_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6皮肤 - 权限");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置皮肤");
        lores.add("§7解锁权限.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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

        meta.setDisplayName("§6类别: §e" + category.getIconName());
        ArrayList<String> lores = new ArrayList<>();

        lores.add("§e点击编辑类别.");

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

        meta.setDisplayName("§6类别: §e" + category.getIconName());
        ArrayList<String> lores = new ArrayList<>();

        lores.add("§a排除类别:");
        if(category.getExcludedCategoriesId().size() == 0)
            lores.add("§7- §6无");
        for(String excludedCategoryId : category.getExcludedCategoriesId())
            lores.add("§7- " + excludedCategoryId);

        lores.add(" ");

        if(category.isDefaultCategory())
        {
            lores.add("§a包括所有宠物§7(默认类别)");
        }
        else
        {
            lores.add("§a包含宠物:");
            if(category.getPets().size() == 0)
                lores.add("§7- §6无");
            for(Pet pet : category.getPets())
                lores.add(" §7 - " + pet.getId());
        }

        lores.add(" ");

        lores.add("§e点击某个物品可");
        lores.add("§e编辑该类别的图标.");

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
        meta.setDisplayName("§6类别ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7单击可编辑类别ID.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY()
    {
        ItemStack it = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6默认类别");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7(可选)默认情况下所有");
        lores.add("§7宠物都应该属于该类别吗?");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6排除类别");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a(可选)§7 从指定类别");
        lores.add("§7中排除所有宠物.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD()
    {
        ItemStack it = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a添加§6宠物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7将宠物添加到类别中.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE()
    {
        ItemStack it = new ItemStack(Material.NETHER_BRICK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c删除§6宠物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7从类别中删除宠物.");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6类别 物品名称");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7在 GUI 中设置");
        lores.add("§7类别物品栏的标题.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6类别图标名称");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置 GUI 中");
        lores.add("§7类别的图标名称.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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
        lores.add("§e点击以编辑该物品.");

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
        lores.add("§e点击某个物品即可更改它.");

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
        meta.setDisplayName("§6物品ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7编辑物品.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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
        lores.add("§e点击以编辑该宠物食物.");

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
        lores.add("§e点击某个物品即可更改它.");

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
        meta.setDisplayName("§6宠物食物ID");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7编辑宠物食物ID.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

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
        meta.setDisplayName("§6食物物品");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置食物物品.");
        lores.add("§7它可以是注册的§a宠物物品§7");
        lores.add("§7以进行更多定制也可以是任何§b材料§7.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);

        this.item = it;
        return this;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_TYPE()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6宠物食物类型");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物食物类型(参见 wiki).");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_POWER()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6力量值");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置宠物食物的力量.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DURATION()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6增益效果的持续时间");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7如果宠物食物是增益效果,");
        lores.add("§7则设置效果的持续时间(以刻度为单位).");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_OPERATOR()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6操作员");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置要对食物的力量");
        lores.add("§7执行的数学运算. (wiki)");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_SIGNAL()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6信号");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置给宠物喂食");
        lores.add("§7时触发的信号.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_ADD()
    {
        ItemStack it = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§a添加§6宠物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a添加§7兼容宠物. §a(可选)");
        lores.add(" ");
        lores.add("§7目前限制宠物: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PETS_REMOVE()
    {
        ItemStack it = new ItemStack(Material.NETHER_BRICK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§c移除§6宠物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§c移除§7兼容宠物. §a(可选)");
        lores.add(" ");
        lores.add("§7目前限制宠物: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EVOLUTION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6进化");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§a(可选)§7 设置宠物吃食物");
        lores.add("§7时触发进化.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_EXP_THRESHOLD()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6经验门槛");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7设置经验值,之后");
        lores.add("§7宠物可以食用食物.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_DELAY()
    {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6进化前的延迟");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7如果您使用的是进化食物,");
        lores.add("§7进化应该提前多久触发.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_PERMISSION()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6权限");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7使用宠物食物所需的权限.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack PETFOOD_EDITOR_EDIT_UNLOCKED_PET()
    {
        ItemStack it = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§6解锁宠物");

        ArrayList<String> lores = new ArrayList<>();
        lores.add(" ");
        lores.add("§7如果食物类型为§a解锁§7,");
        lores.add("§7应该解锁哪只宠物.");
        lores.add(" ");
        lores.add("§7当前值: §e%value%");

        meta.setLore(lores);
        it.setItemMeta(meta);
        return it;
    }


}
