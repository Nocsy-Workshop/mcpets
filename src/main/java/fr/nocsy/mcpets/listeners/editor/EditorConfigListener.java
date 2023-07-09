package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.data.editor.Editor;
import fr.nocsy.mcpets.data.editor.EditorItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EditorConfigListener implements Listener {

    @EventHandler
    public void select_editor(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        ItemStack it = e.getCurrentItem();
        EditorItems editorItem = EditorItems.getFromItemstack(it);

        if (editorItem != null)
        {
            e.setCancelled(true);
            Editor editor = Editor.getEditor(p);

            if(editorItem.is(EditorItems.CONFIG_EDITOR_DEFAULT_NAME))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_USE_DEFAULT_MYTHICMOBS_NAMES))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_OVERRIDE_DEFAULT_NAME))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_RIGHT_CLICK_TO_OPEN_MENU))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_LEFT_CLICK_TO_OPEN_MENU))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_SNEAKMODE))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_NAMEABLE))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_MOUNTABLE))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_DISTANCE_TELEPORT))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_MAX_NAME_LENGTH))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_INVENTORY_SIZE))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_ENABLE_CLICK_BACK_TO_MENU))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_ACTIVATE_BACK_MENU_ICON))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_DISMOUNT_ON_DAMAGED))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_DISABLE_INVENTORY_WHILE_SIGNAL_STICK))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_PERCENT_HEALTH_ON_RESPAWN))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_AUTO_SAVE_DELAY))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_DEFAULT_RESPAWN_COOLDOWN))
            {

            }
            else if(editorItem.is(EditorItems.CONFIG_EDITOR_GLOBAL_RESPAWN_COOLDOWN))
            {

            }

        }
    }

}
