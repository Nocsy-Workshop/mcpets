package fr.nocsy.mcpets.data.editor;

import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public enum EditorState {

    DEFAULT("No title - Default"),

    // First menu
    GLOBAL_EDITOR("What do you want to edit ?"),

    // Second dive menu
    CONFIG_EDITOR("Click a config option to edit"),
    PET_EDITOR("Click a pet you want to edit"),
    CATEGORY_EDITOR("Click a category you want to edit"),
    ITEM_EDITOR("Click an item you want to edit"),
    PETFOOD_EDITOR("Click a pet food you want to edit");

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
        this.buildInventory();
        p.openInventory(currentView);
    }

    public void buildInventory()
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

    }

    public boolean equals(EditorState other)
    {
        return other.getStateName().equals(this.stateName);
    }

}
