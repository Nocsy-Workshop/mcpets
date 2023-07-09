package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.data.editor.Editor;
import fr.nocsy.mcpets.data.editor.EditorItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class EditorTemplateListener implements Listener {

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
        }
    }

}
