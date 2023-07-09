package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.editor.*;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
            // If it's the page selector of the pet we rearrange the visual to the next page
            else if(editorItem.getType().equals(EditorExpectationType.PAGE_SELECTOR))
            {
                int value = 1;
                if(e.getClick() == ClickType.LEFT)
                    value = -1;

                EditorPageSelection.set(p, Math.max(0, EditorPageSelection.get(p) + value));
                editor.openEditor();
            }
            // If it's a pet icon, then we dive in the pet editing
            else if(editorItem.getType().equals(EditorExpectationType.PET))
            {
                int slot = e.getSlot();
                Pet pet = Pet.getObjectPets().get(slot + 45 * EditorPageSelection.get(p));
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                EditorPetEditing.register(p, pet);
                editor.setState(EditorState.PET_EDITOR_EDIT);
                editor.openEditor();
            }
            // If it's an item, we need to serialize it
            else if(editorItem.getType().equals(EditorExpectationType.ITEM))
            {
                ItemStack replaceItem = e.getCursor();
                if(replaceItem == null || replaceItem.getType().isAir())
                    return;
                if(!replaceItem.getItemMeta().hasDisplayName())
                    return;
                editorItem.setValue(replaceItem);
                editorItem.save();
                MCPets.loadConfigs();

                editor.openEditor();
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
