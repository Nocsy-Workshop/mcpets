package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum EditorState {

    DEFAULT("No title - Default"),

    // First menu
    GLOBAL_EDITOR("What do you want to edit ?"),

    // Second dive menu
    CONFIG_EDITOR("Click a config option to edit"),
    PET_EDITOR("Click a pet you want to edit"),
    CATEGORY_EDITOR("Click a category you want to edit"),
    ITEM_EDITOR("Click an item you want to edit"),
    PETFOOD_EDITOR("Click a pet food you want to edit"),

    // Pet editor menu
    PET_EDITOR_EDIT("Edit the pet"),
    PET_EDITOR_LEVELS("Edit the living pet features"),
    PET_EDITOR_SKINS("Edit the skins of the pet"),
    // Levels editor menu
    PET_EDITOR_LEVEL_EDIT("Edit the level features"),
    // Skins editor menu
    PET_EDITOR_SKIN_EDIT("Edit the skin parameters"),

    // Categories editor menu
    CATEGORY_EDITOR_EDIT("Edit the category"),

    // Item editor menu
    ITEM_EDITOR_EDIT("Edit the item"),

    // Petfood editor menu
    PETFOOD_EDITOR_EDIT("Edit the pet food");


    @Getter
    private String stateName;
    @Getter
    private String menuTitle;
    @Getter
    private Inventory currentView;

    EditorState(String title)
    {
        this.stateName = this.name();
        this.menuTitle = title;
    }

    public void openView(Player p)
    {
        this.buildInventory(p);
        p.openInventory(currentView);
    }

    private void buildInventory(Player p)
    {

        if(this.equals(EditorState.GLOBAL_EDITOR))
        {
            currentView = Bukkit.createInventory(null, InventoryType.HOPPER, this.getMenuTitle());

            EditorItems[] icons = {
                    EditorItems.CONFIG_EDITOR,
                    EditorItems.PET_EDITOR,
                    EditorItems.CATEGORY_EDITOR,
                    EditorItems.ITEM_EDITOR,
                    EditorItems.PETFOOD_EDITOR,
            };

            for(EditorItems editorItem : icons)
            {
                currentView.addItem(editorItem.getItem());
            }

        }


        else if(this.equals(EditorState.CONFIG_EDITOR))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            HashMap<EditorItems, Integer> icons = new HashMap<>();

            icons.put(EditorItems.BACK_TO_GLOBAL_SELECTION, 0);

            icons.put(EditorItems.CONFIG_EDITOR_PREFIX, 18);
            icons.put(EditorItems.CONFIG_EDITOR_DEFAULT_NAME, 19);
            icons.put(EditorItems.CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES, 20);
            icons.put(EditorItems.CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME, 21);
            icons.put(EditorItems.CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU, 22);
            icons.put(EditorItems.CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU, 23);
            icons.put(EditorItems.CONFIG_EDITOR_SNEAKMODE, 24);
            icons.put(EditorItems.CONFIG_EDITOR_NAMEABLE, 25);
            icons.put(EditorItems.CONFIG_EDITOR_MOUNTABLE, 26);
            icons.put(EditorItems.CONFIG_EDITOR_DISTANCE_TELEPORT, 27);
            icons.put(EditorItems.CONFIG_EDITOR_MAX_NAME_LENGTH, 28);
            icons.put(EditorItems.CONFIG_EDITOR_INVENTORY_SIZE, 29);
            icons.put(EditorItems.CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU, 30);
            icons.put(EditorItems.CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON, 31);
            icons.put(EditorItems.CONFIG_EDITOR_DISMOUNT_ON_DAMAGED, 32);
            icons.put(EditorItems.CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK, 33);
            icons.put(EditorItems.CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN, 34);
            icons.put(EditorItems.CONFIG_EDITOR_AUTO_SAVE_DELAY, 35);
            icons.put(EditorItems.CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN, 39);
            icons.put(EditorItems.CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN, 40);
            icons.put(EditorItems.CONFIG_EDITOR_GLOBAL_AUTORESPAWN, 41);

            for(EditorItems item : icons.keySet())
            {
                int position = icons.get(item);
                currentView.setItem(position, item.getItem());
            }

        }


        else if(this.equals(EditorState.PET_EDITOR))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i < 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(53, EditorItems.PAGE_SELECTOR.getItem());
            currentView.setItem(49, EditorItems.PET_EDITOR_CREATE_NEW.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_GLOBAL_SELECTION.getItem());

            int page = EditorPageSelection.get(p);
            ArrayList<Pet> pets = Pet.getObjectPets();

            int currentIndex = 0;
            int inventoryPosition = 0;
            for(Pet pet : pets)
            {
                if(EditorItems.getCachedDeleted().contains(pet.getId()))
                    continue;
                if(currentIndex < 45*page)
                {
                    currentIndex++;
                    continue;
                }
                else if(inventoryPosition < currentView.getSize()-9)
                {

                    ItemStack icon = EditorItems.PET_EDITOR_EDIT_PET.setupPetIcon(pet.getId()).getItem();

                    currentView.setItem(inventoryPosition, icon);
                    inventoryPosition++;
                    continue;
                }
                break;
            }

        }


        else if(this.equals(EditorState.PET_EDITOR_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            Pet pet = PetConfig.loadConfigPet(EditorEditing.get(p).getPetId());
            String filePath = PetConfig.getFilePath(pet.getId());

            HashMap<ItemStack, Integer> icons = new HashMap<>();

            icons.put(EditorItems.PET_EDITOR_DELETE.getItem(), 8);
            icons.put(EditorItems.BACK_TO_PET_SELECTION.getItem(), 0);
            icons.put(EditorItems.PET_EDITOR_LEVELS.getItem(), 11);
            icons.put(EditorItems.PET_EDITOR_SKINS.getItem(), 15);
            icons.put(EditorItems.PET_EDITOR_MYTHICMOB.setFilePath(filePath).getItem(), 49);
            icons.put(EditorItems.PET_EDITOR_ICON.setFilePath(filePath).setupPetIconEdit(pet.getId()).getItem(), 13);
            icons.put(EditorItems.PET_EDITOR_PERMISSION.setFilePath(filePath).getItem(), 32);
            icons.put(EditorItems.PET_EDITOR_MOUNTABLE.setFilePath(filePath).getItem(), 27);
            icons.put(EditorItems.PET_EDITOR_MOUNT_TYPE.setFilePath(filePath).getItem(), 28);
            icons.put(EditorItems.PET_EDITOR_DESPAWN_ON_DISMOUNT.setFilePath(filePath).getItem(), 29);
            icons.put(EditorItems.PET_EDITOR_AUTORIDE.setFilePath(filePath).getItem(), 30);
            icons.put(EditorItems.PET_EDITOR_MOUNT_PERMISSION.setFilePath(filePath).getItem(), 31);
            icons.put(EditorItems.PET_EDITOR_DESPAWN_SKILL.setFilePath(filePath).getItem(), 33);
            icons.put(EditorItems.PET_EDITOR_DISTANCE.setFilePath(filePath).getItem(), 38);
            icons.put(EditorItems.PET_EDITOR_SPAWN_RANGE.setFilePath(filePath).getItem(), 35);
            icons.put(EditorItems.PET_EDITOR_COMING_BACK_RANGE.setFilePath(filePath).getItem(), 39);
            icons.put(EditorItems.PET_EDITOR_TAMING_PROGRESS_SKILL.setFilePath(filePath).getItem(), 37);
            icons.put(EditorItems.PET_EDITOR_TAMING_FINISHED_SKILL.setFilePath(filePath).getItem(), 36);
            icons.put(EditorItems.PET_EDITOR_INVENTORY_SIZE.setFilePath(filePath).getItem(), 34);
            icons.put(EditorItems.PET_EDITOR_SIGNALS.setFilePath(filePath).getItem(), 40);
            icons.put(EditorItems.PET_EDITOR_SIGNAL_STICK.setFilePath(filePath).setupSignalStickItem(pet.getId()).getItem(), 41);
            icons.put(EditorItems.PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU.setFilePath(filePath).getItem(), 42);

            for(ItemStack item : icons.keySet())
            {
                int position = icons.get(item);
                currentView.setItem(position, item);
            }

        }


        else if(this.equals(EditorState.PET_EDITOR_LEVELS))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i <= 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(49, EditorItems.PET_EDITOR_LEVEL_CREATE_NEW.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_PET_EDIT.getItem());

            EditorEditing editorPet = EditorEditing.get(p);
            Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
            String filePath = PetConfig.getFilePath(pet.getId());

            for(int i = 0; i < 45 && i < pet.getPetLevels().size(); i++)
            {
                PetLevel level = pet.getPetLevels().get(i);

                EditorItems icon = EditorItems.PET_EDITOR_EDIT_LEVEL
                        .setFilePath(filePath).replaceVariablePath(level.getLevelId())
                        .setupPetLevelIcon(pet.getId(), level.getLevelId());

                ItemStack item = icon.getItem().clone();
                item.setAmount(i+1);
                currentView.setItem(i, item);

                editorPet.getEditorMapping().put(i, level.getLevelId());
            }

        }


        else if(this.equals(EditorState.PET_EDITOR_LEVEL_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());


            EditorEditing editorPet = EditorEditing.get(p);
            Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
            String filePath = PetConfig.getFilePath(pet.getId());
            PetLevel level = pet.getPetLevels().stream().filter(petLevel -> petLevel.getLevelId().equals(editorPet.getMappedId())).findFirst().orElse(null);

            HashMap<EditorItems, Integer> icons = new HashMap<>();

            icons.put(EditorItems.BACK_TO_PET_LEVELS_EDIT, 0);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_DELETE, 8);

            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_NAME, 12);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_EXP_THRESHOLD, 14);

            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_MAX_HEALTH, 27);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_REGENERATION, 28);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_RESISTANCE_MODIFIER, 29);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_DAMAGE_MODIFIER, 36);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_POWER, 37);

            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_COOLDOWN_RESPAWN, 30);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_COOLDOWN_REVOKE, 31);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_INVENTORY_EXTENSION, 32);

            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TEXT, 39);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_TYPE, 40);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_ANNOUNCEMENT_SKILL, 41);

            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_EVOLUTION_PET_ID, 33);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_EVOLUTION_DELAY, 34);
            icons.put(EditorItems.PET_EDITOR_EDIT_LEVEL_EVOLUTION_REMOVE_ACCESS, 35);

            for(EditorItems item : icons.keySet())
            {
                int position = icons.get(item);
                currentView.setItem(position, item.setFilePath(filePath)
                                                    .replaceVariablePath(level.getLevelId())
                                                    .getItem());
            }
        }


        else if(this.equals(EditorState.PET_EDITOR_SKINS))
        {

            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i <= 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(49, EditorItems.PET_EDITOR_SKIN_CREATE_NEW.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_PET_EDIT.getItem());

            EditorEditing editorPet = EditorEditing.get(p);
            Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
            String filePath = PetConfig.getFilePath(pet.getId());

            ArrayList<PetSkin> skins = PetSkin.getSkins(pet);
            for(int i = 0; i < 45 && i < skins.size(); i++)
            {
                PetSkin skin = skins.get(i);

                EditorItems icon = EditorItems.PET_EDITOR_EDIT_SKIN
                        .setFilePath(filePath).replaceVariablePath(skin.getPathId())
                        .setupSkinIcon(pet.getId(), skin.getPathId());

                currentView.setItem(i, icon.getItem());

                editorPet.getEditorMapping().put(i, skin.getPathId());
            }

        }


        else if(this.equals(EditorState.PET_EDITOR_SKIN_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            EditorEditing editorPet = EditorEditing.get(p);
            Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
            String filePath = PetConfig.getFilePath(pet.getId());
            PetSkin skin = PetSkin.getSkins(pet).stream().filter(petSkin -> petSkin.getPathId().equals(editorPet.getMappedId())).findFirst().orElse(null);

            HashMap<EditorItems, Integer> icons = new HashMap<>();

            icons.put(EditorItems.BACK_TO_PET_SKINS_EDIT, 0);
            icons.put(EditorItems.PET_EDITOR_EDIT_SKIN_DELETE, 8);

            icons.put(EditorItems.PET_EDITOR_EDIT_SKIN_MYTHICMOB, 20);
            icons.put(EditorItems.PET_EDITOR_EDIT_SKIN_ICON, 22);
            icons.put(EditorItems.PET_EDITOR_EDIT_SKIN_PERMISSION, 24);

            for(EditorItems item : icons.keySet())
            {
                if(item.equals(EditorItems.PET_EDITOR_EDIT_SKIN_ICON))
                    item.setupEditSkinIcon(pet.getId(), skin.getPathId());
                int position = icons.get(item);
                currentView.setItem(position, item.setFilePath(filePath)
                        .replaceVariablePath(skin.getPathId())
                        .getItem());
            }
        }

        else if(this.equals(EditorState.CATEGORY_EDITOR))
        {

            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i <= 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(49, EditorItems.CATEGORY_EDITOR_CATEGORY_CREATE.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_GLOBAL_SELECTION.getItem());

            EditorEditing editorEditing = EditorEditing.get(p);

            List<Category> categories = Category.getCategories();
            for(int i = 0; i < 45 && i < categories.size(); i++)
            {
                Category category = categories.get(i);
                CategoryConfig config = CategoryConfig.getMapping().get(category.getId());

                EditorItems icon = EditorItems.CATEGORY_EDITOR_EDIT_CATEGORY
                        .setFilePath(config.getFullPath())
                        .setupCategoryIcon(category.getId());

                currentView.setItem(i, icon.getItem());

                editorEditing.getEditorMapping().put(i, category.getId());
            }

        }

        else if(this.equals(EditorState.CATEGORY_EDITOR_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            EditorEditing editorEditing = EditorEditing.get(p);
            Category category = CategoryConfig.loadConfigCategory(editorEditing.getMappedId());
            String filePath = CategoryConfig.getMapping().get(category.getId()).getFullPath();

            HashMap<EditorItems, Integer> icons = new HashMap<>();

            icons.put(EditorItems.BACK_TO_CATEGORIES_EDIT, 0);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_DELETE, 8);

            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_ID, 11);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_ICON, 13);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_DEFAULT_CATEGORY, 15);

            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_ICON_NAME, 29);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_TITLE_NAME, 38);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_EXCLUDED_CATEGORIES, 31);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_PET_ADD, 33);
            icons.put(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_PET_REMOVE, 42);


            for(EditorItems item : icons.keySet())
            {
                if(item.equals(EditorItems.CATEGORY_EDITOR_CATEGORY_EDIT_ICON))
                    item.setupEditCategoryIcon(category.getId());
                int position = icons.get(item);
                currentView.setItem(position, item.setFilePath(filePath).getItem());
            }

        }


        else if(this.equals(EditorState.ITEM_EDITOR))
        {

            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i <= 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(49, EditorItems.ITEMS_CREATE.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_GLOBAL_SELECTION.getItem());
            currentView.setItem(53, EditorItems.PAGE_SELECTOR.getItem());

            EditorEditing editing = EditorEditing.get(p);
            editing.getEditorMapping().clear();

            HashMap<String, ItemStack> items = ItemsListConfig.getInstance().getItems();
            List<String> itemsId = new ArrayList<>(items.keySet());
            int page = EditorPageSelection.get(p);
            for(int i = 45*page; i - 45*page < 45 && i - 45*page < items.size(); i++)
            {
                if(i >= itemsId.size())
                    break;
                String itemId = itemsId.get(i);

                EditorItems icon = EditorItems.ITEMS_EDIT
                        .replaceVariablePath(itemId)
                        .setupItemIcon(itemId);

                currentView.setItem(i, icon.getItem());
                editing.getEditorMapping().put(i - 45*page, itemId);
            }

        }

        else if(this.equals(EditorState.ITEM_EDITOR_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            EditorEditing editorEditing = EditorEditing.get(p);
            String itemId = editorEditing.getMappedId();

            HashMap<EditorItems, Integer> icons = new HashMap<>();

            icons.put(EditorItems.BACK_TO_ITEM_EDITOR, 0);
            icons.put(EditorItems.ITEMS_DELETE, 8);

            icons.put(EditorItems.ITEMS_EDIT_ID, 12);
            icons.put(EditorItems.ITEMS_EDIT_ITEM, 14);


            for(EditorItems item : icons.keySet())
            {
                if(item.equals(EditorItems.ITEMS_EDIT_ITEM))
                    item.setupEditItemIcon(itemId);
                int position = icons.get(item);
                currentView.setItem(position, item.setFilePath(item.getInputFilePath()).replaceVariablePath(itemId).setValue(itemId).getItem());
            }

        }


        else if(this.equals(EditorState.PETFOOD_EDITOR))
        {

            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i <= 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(49, EditorItems.PETFOOD_EDITOR_EDIT_CREATE.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_GLOBAL_SELECTION.getItem());
            currentView.setItem(53, EditorItems.PAGE_SELECTOR.getItem());

            EditorEditing editing = EditorEditing.get(p);
            editing.getEditorMapping().clear();

            HashMap<String, PetFood> items = PetFoodConfig.getInstance().getPetFoods();
            List<String> itemsId = new ArrayList<>(items.keySet());
            int page = EditorPageSelection.get(p);
            for(int i = 45*page; i - 45*page < 45 && i - 45*page < items.size(); i++)
            {
                if(i >= itemsId.size())
                    break;
                String itemId = itemsId.get(i);

                EditorItems icon = EditorItems.PETFOOD_EDITOR_EDIT
                        .replaceVariablePath(itemId)
                        .setupPetfoodIcon(items.get(itemId).getId());

                currentView.setItem(i, icon.getItem());

                editing.getEditorMapping().put(i - 45*page, items.get(itemId).getId());
            }

        }

        else if(this.equals(EditorState.PETFOOD_EDITOR_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            EditorEditing editorEditing = EditorEditing.get(p);
            PetFood petFood = PetFoodConfig.loadConfigPetFood(editorEditing.getMappedId());

            HashMap<EditorItems, Integer> icons = new HashMap<>();

            icons.put(EditorItems.BACK_TO_PETFOOD_EDITOR, 0);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_DELETE, 8);

            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_ID, 12);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_ITEM_ID, 13);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_TYPE, 14);

            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_POWER, 29);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_OPERATOR, 30);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_SIGNAL, 31);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_PETS_ADD, 32);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_PETS_REMOVE, 33);

            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_EVOLUTION, 26);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_EXP_THRESHOLD, 35);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_DELAY, 44);

            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_PERMISSION, 48);
            icons.put(EditorItems.PETFOOD_EDITOR_EDIT_UNLOCKED_PET, 49);

            for(EditorItems item : icons.keySet())
            {
                EditorItems replacedItem = item.replaceVariablePath(petFood.getId());
                if(item.equals(EditorItems.PETFOOD_EDITOR_EDIT_ID))
                    replacedItem.setValue(petFood.getId());

                if(item.equals(EditorItems.PETFOOD_EDITOR_EDIT_ITEM_ID))
                    replacedItem.setupPetFoodEditorEditItem(petFood.getId());

                int position = icons.get(item);
                currentView.setItem(position, replacedItem.getItem());
            }

        }

    }

    public boolean equals(EditorState other)
    {
        return other.getStateName().equals(this.stateName);
    }

}
