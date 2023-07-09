package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.data.editor.*;
import fr.nocsy.mcpets.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EditorGlobalListener implements Listener {

    @EventHandler
    public void diveInMenus(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        ItemStack it = e.getCurrentItem();
        EditorItems editorItem = EditorItems.getFromItemstack(it);

        if(editorItem != null)
        {
            e.setCancelled(true);
            Editor editor = Editor.getEditor(p);

            // if the item has a next state, just keep going in the menus
            if(editorItem.getNextState() != null)
            {
                editor.setState(editorItem.getNextState());
                editor.openEditor();
            }

        }

    }

    @EventHandler
    public void editorItemsClick(InventoryClickEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        ItemStack it = e.getCurrentItem();
        EditorItems editorItem = EditorItems.getFromItemstack(it);

        if(editorItem != null)
        {
            Editor editor = Editor.getEditor(p);

            e.setCancelled(true);
            // If it has no type, do nothing
            if(editorItem.getType() == null)
                return;

            // If it's boolean, just toggle
            if(editorItem.getType().equals(EditorExpectationType.BOOLEAN))
            {
                editorItem.toggleBooleanValue();
                editorItem.save();
                editor.openEditor();

                p.sendMessage("§aChanges saved! Make sure you §nreload MCPets§a to apply the changes.");
            }
            // If it's more advanced, it needs typing, so starts a conversation!
            else
            {
                EditorConversation conversation = new EditorConversation(p, editorItem);
                conversation.start();
                p.closeInventory();
            }

        }

    }

}
