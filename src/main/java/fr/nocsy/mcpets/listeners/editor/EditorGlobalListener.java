package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.editor.*;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

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
                EditorPageSelection.set(p, 0);
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
            if(editorItem.name().contains("_DELETE") && e.getClick() != ClickType.SHIFT_LEFT)
            {
                p.sendMessage("§c§lWARNING:§7 Are you sure you want to delete it ? Click §cSHIFT + CLICK§7 if so.");
                return;
            }

            Editor editor = Editor.getEditor(p);

            e.setCancelled(true);
            // If it has no type, do nothing
            if(editorItem.getType() == null)
                return;

            boolean changesMade = false;

            // If it's boolean, just toggle
            if(editorItem.getType().equals(EditorExpectationType.BOOLEAN))
            {
                editorItem.toggleBooleanValue();
                editorItem.save(p);
                editor.openEditor();

                changesMade = true;
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
                EditorEditing.register(p, pet);
                editor.setState(EditorState.PET_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete the pet
            else if(editorItem.getType().equals(EditorExpectationType.PET_DELETE))
            {
                Pet pet = EditorEditing.get(p).getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                changesMade = PetConfig.getConfig(pet.getId()).delete();

                if(changesMade)
                {
                    EditorItems.getCachedDeleted().add(pet.getId());
                    p.sendMessage("§cThe pet §e" + pet.getId() + "§c was deleted successfully.");
                }

                editor.setState(EditorState.PET_EDITOR);
                editor.openEditor();
            }

            // If we should edit a pet level
            else if(editorItem.getType().equals(EditorExpectationType.PET_LEVEL_EDIT))
            {
                EditorEditing editorPet = EditorEditing.get(p);
                Pet pet = editorPet.getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                PetLevel level = editorPet.getEditorPetLevelMapping().get(e.getSlot());

                editorPet.setLevel(level);
                editor.setState(EditorState.PET_EDITOR_LEVEL_EDIT);
                editor.openEditor();
            }

            // If we should create a pet level
            else if(editorItem.getType().equals(EditorExpectationType.PET_LEVEL_CREATE))
            {
                EditorEditing editorPet = EditorEditing.get(p);
                Pet pet = editorPet.getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                PetConfig config = PetConfig.getConfig(pet.getId());
                config.registerCleanPetLevel(null);
                PetLevel level = pet.getPetLevels().get(pet.getPetLevels().size() - 1);

                editorPet.setLevel(level);
                editor.setState(EditorState.PET_EDITOR_LEVEL_EDIT);
                editor.openEditor();
            }

            // If we should delete a pet level
            else if(editorItem.getType().equals(EditorExpectationType.PET_LEVEL_DELETE))
            {
                EditorEditing editorPet = EditorEditing.get(p);
                Pet pet = editorPet.getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                PetLevel petLevel = editorPet.getLevel();

                PetConfig config = PetConfig.getConfig(pet.getId());
                config.deletePetLevel(petLevel.getLevelId());

                editor.setState(EditorState.PET_EDITOR_LEVELS);
                editor.openEditor();
            }

            // If we should edit a pet skin
            else if(editorItem.getType().equals(EditorExpectationType.PET_SKIN_EDIT))
            {
                EditorEditing editorPet = EditorEditing.get(p);
                Pet pet = editorPet.getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                int slotMapping = e.getSlot();
                PetSkin skin = editorPet.getEditorPetSkinMapping().get(slotMapping);

                editorPet.setSkin(skin);
                editor.setState(EditorState.PET_EDITOR_SKIN_EDIT);
                editor.openEditor();
            }

            // If we should create a pet skin
            else if(editorItem.getType().equals(EditorExpectationType.PET_SKIN_CREATE))
            {
                EditorEditing editorPet = EditorEditing.get(p);
                Pet pet = editorPet.getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                PetConfig config = PetConfig.getConfig(pet.getId());
                config.registerCleanPetSkin();
                List<PetSkin> skins = PetSkin.getSkins(pet);
                PetSkin skin = skins.get(skins.size()-1);

                editorPet.setSkin(skin);
                editor.setState(EditorState.PET_EDITOR_SKIN_EDIT);
                editor.openEditor();
            }

            // If we should delete a pet skin
            else if(editorItem.getType().equals(EditorExpectationType.PET_SKIN_DELETE))
            {
                EditorEditing editorPet = EditorEditing.get(p);
                Pet pet = editorPet.getPet();
                if(pet == null)
                {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                PetConfig config = PetConfig.getConfig(pet.getId());
                config.deletePetSkin(editorPet.getSkin().getPathId());

                editor.setState(EditorState.PET_EDITOR_SKINS);
                editor.openEditor();
            }

            // If it's an item, we need to serialize it
            else if(editorItem.getType().equals(EditorExpectationType.ITEM))
            {
                ItemStack replaceItem = e.getCursor();
                if(replaceItem == null || replaceItem.getType().isAir())
                    return;
                if(!replaceItem.hasItemMeta())
                    return;
                editorItem.setValue(replaceItem);
                editorItem.save(p);

                editor.openEditor();

                changesMade = true;
            }

            // If we should create a category
            else if(editorItem.getType().equals(EditorExpectationType.CATEGORY_CREATE))
            {

                CategoryConfig.registerCleanCategory(p);
                editor.setState(EditorState.CATEGORY_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete a category
            else if(editorItem.getType().equals(EditorExpectationType.CATEGORY_DELETE))
            {

                EditorEditing editing = EditorEditing.get(p);
                Category category = editing.getCategory();
                if(category == null)
                {
                    Debugger.send("§cCategory could not be found.");
                    return;
                }
                CategoryConfig config = CategoryConfig.getMapping().get(category.getId());
                config.delete();

                CategoryConfig.load(AbstractConfig.getPath() + "Categories/", true);
                editor.setState(EditorState.CATEGORY_EDITOR);
                editor.openEditor();
            }

            // If we should delete a category
            else if(editorItem.getType().equals(EditorExpectationType.CATEGORY_EDIT))
            {
                EditorEditing editing = EditorEditing.get(p);
                Category category = editing.getEditorCategoryMapping().get(e.getSlot());
                if(category == null)
                {
                    Debugger.send("§cCategory could not be found.");
                    return;
                }
                editing.setCategory(category);
                editor.setState(EditorState.CATEGORY_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should create an item
            else if(editorItem.getType().equals(EditorExpectationType.ITEM_CREATE))
            {
                EditorEditing editing = EditorEditing.get(p);

                String key = UUID.randomUUID().toString();
                ItemsListConfig.getInstance().setItemStack(key, Items.UNKNOWN.getItem().clone());
                ItemsListConfig.getInstance().reload();

                editing.setItemId(key);
                editor.setState(EditorState.ITEM_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should create an item
            else if(editorItem.getType().equals(EditorExpectationType.ITEM_EDIT))
            {
                EditorEditing editing = EditorEditing.get(p);
                String itemId = editing.getEditorItemMapping().get(e.getSlot());
                ItemStack item = ItemsListConfig.getInstance().getItemStack(itemId);
                if(item == null)
                {
                    Debugger.send("§cItem could not be found.");
                    return;
                }
                editing.setItemId(itemId);
                editor.setState(EditorState.ITEM_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete a category
            else if(editorItem.getType().equals(EditorExpectationType.ITEM_DELETE))
            {

                EditorEditing editing = EditorEditing.get(p);
                String itemId = editing.getItemId();
                ItemsListConfig.getInstance().removeItemStack(itemId);

                editor.setState(EditorState.ITEM_EDITOR);
                editor.openEditor();
            }

            // If it's more advanced, it needs typing, so starts a conversation!
            else
            {
                EditorConversation conversation = new EditorConversation(p, editorItem);
                conversation.start();
                p.closeInventory();
            }

            if(changesMade)
                p.sendMessage("§aChanges saved! Make sure you §nreload MCPets§a to apply the changes.");

        }

    }

}
