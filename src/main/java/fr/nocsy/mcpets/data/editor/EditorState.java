package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

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
    PET_EDITOR_SKINS("Edit the skins of the pet");


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

            EditorItems[] icons = {
                    EditorItems.CONFIG_EDITOR_PREFIX,
                    EditorItems.CONFIG_EDITOR_DEFAULT_NAME,
                    EditorItems.CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES,
                    EditorItems.CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME,
                    EditorItems.CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU,
                    EditorItems.CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU,
                    EditorItems.CONFIG_EDITOR_SNEAKMODE,
                    EditorItems.CONFIG_EDITOR_NAMEABLE,
                    EditorItems.CONFIG_EDITOR_MOUNTABLE,
                    EditorItems.CONFIG_EDITOR_DISTANCE_TELEPORT,
                    EditorItems.CONFIG_EDITOR_MAX_NAME_LENGTH,
                    EditorItems.CONFIG_EDITOR_INVENTORY_SIZE,
                    EditorItems.CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU,
                    EditorItems.CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON,
                    EditorItems.CONFIG_EDITOR_DISMOUNT_ON_DAMAGED,
                    EditorItems.CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK,
                    EditorItems.CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN,
                    EditorItems.CONFIG_EDITOR_AUTO_SAVE_DELAY,
                    EditorItems.CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN,
                    EditorItems.CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN,
                    EditorItems.BACK_TO_GLOBAL_SELECTION,
            };

            for(EditorItems editorItem : icons)
            {
                currentView.addItem(editorItem.getItem());
            }

        }
        else if(this.equals(EditorState.PET_EDITOR))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            for(int i = 45; i < 53; i++)
            {
                currentView.setItem(i, EditorItems.FILLER.getItem());
            }
            currentView.setItem(53, EditorItems.PET_EDITOR_PAGE_SELECTOR.getItem());
            currentView.setItem(49, EditorItems.PET_EDITOR_CREATE_NEW.getItem());
            currentView.setItem(45, EditorItems.BACK_TO_GLOBAL_SELECTION.getItem());

            int page = EditorPageSelection.get(p);
            ArrayList<Pet> pets = Pet.getObjectPets();

            int currentIndex = 0;
            int currentViewAmount = 0;
            for(Pet pet : pets)
            {
                if(EditorItems.getCachedDeleted().contains(pet.getId()))
                    continue;
                if(currentIndex < 45*page)
                {
                    currentIndex++;
                    continue;
                }
                else if(currentViewAmount <= 72)
                {
                    currentViewAmount++;

                    ItemStack icon = EditorItems.PET_EDITOR_EDIT_PET.setupPetIcon(pet).getItem();

                    currentView.addItem(icon);
                    continue;
                }
                break;
            }

        }
        else if(this.equals(EditorState.PET_EDITOR_EDIT))
        {
            currentView = Bukkit.createInventory(null, 54, this.getMenuTitle());

            Pet pet = EditorPetEditing.get(p);
            String filePath = PetConfig.getFilePath(pet.getId());

            HashMap<ItemStack, Integer> icons = new HashMap<>();

            icons.put(EditorItems.PET_EDITOR_DELETE.getItem(), 8);
            icons.put(EditorItems.BACK_TO_PET_SELECTION.getItem(), 0);
            icons.put(EditorItems.PET_EDITOR_LEVELS.getItem(), 11);
            icons.put(EditorItems.PET_EDITOR_SKINS.getItem(), 15);
            icons.put(EditorItems.PET_EDITOR_MYTHICMOB.setFilePath(filePath).getItem(), 49);
            icons.put(EditorItems.PET_EDITOR_ICON.setFilePath(filePath).setupPetIconEdit(pet).getItem(), 13);
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
            icons.put(EditorItems.PET_EDITOR_INVENTORY_SIZE.setFilePath(filePath).getItem(), 34);
            icons.put(EditorItems.PET_EDITOR_SIGNALS.setFilePath(filePath).getItem(), 40);
            icons.put(EditorItems.PET_EDITOR_SIGNAL_STICK.setFilePath(filePath).setupSignalStickItem(pet).getItem(), 41);
            icons.put(EditorItems.PET_EDITOR_GET_SIGNAL_STICK_FROM_MENU.setFilePath(filePath).getItem(), 42);

            for(ItemStack item : icons.keySet())
            {
                int position = icons.get(item);
                currentView.setItem(position, item);
            }

        }

    }

    public boolean equals(EditorState other)
    {
        return other.getStateName().equals(this.stateName);
    }

}
