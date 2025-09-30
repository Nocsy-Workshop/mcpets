package fr.nocsy.mcpets.listeners.editor;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.AbstractConfig;
import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.data.editor.Editor;
import fr.nocsy.mcpets.data.editor.EditorConversation;
import fr.nocsy.mcpets.data.editor.EditorEditing;
import fr.nocsy.mcpets.data.editor.EditorExpectationType;
import fr.nocsy.mcpets.data.editor.EditorItems;
import fr.nocsy.mcpets.data.editor.EditorPageSelection;
import fr.nocsy.mcpets.data.editor.EditorState;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.utils.debug.Debugger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class EditorGlobalListener implements Listener {

    @EventHandler
    public void diveInMenus(final InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final ItemStack it = e.getCurrentItem();
        final EditorItems editorItem = EditorItems.getFromItemstack(it);

        if (editorItem != null) {
            e.setCancelled(true);
            final Editor editor = Editor.getEditor(p);

            // if the item has a next state, just keep going in the menus
            if (editorItem.getNextState() != null) {
                EditorPageSelection.set(p, 0);
                editor.setState(editorItem.getNextState());
                editor.openEditor();
            }
        }
    }

    @EventHandler
    public void editorItemsClick(final InventoryClickEvent e) {
        final Player p = (Player) e.getWhoClicked();
        final ItemStack it = e.getCurrentItem();
        final EditorItems editorItem = EditorItems.getFromItemstack(it);

        if (editorItem != null && e.getClickedInventory() != null
                && e.getClickedInventory().getHolder() instanceof final PetInventoryHolder holder
                && holder.getType() == PetInventoryHolder.Type.EDITOR_MENU) {

            final Editor editor = Editor.getEditor(p);

            e.setCancelled(true);
            // If it has no type, do nothing
            if (editorItem.getType() == null)
                return;

            if (editorItem.name().contains("_DELETE") && e.getClick() != ClickType.SHIFT_LEFT) {
                p.sendMessage("§c§lWARNING:§7 Are you sure you want to delete it ? Click §cSHIFT + CLICK§7 if so.");
                return;
            }

            if (resetFeature(e, editor, editorItem, p))
                return;

            boolean changesMade = false;

            // If it's boolean, just toggle
            if (editorItem.getType().equals(EditorExpectationType.BOOLEAN)) {
                editorItem.toggleBooleanValue();
                editorItem.save(p);
                editor.openEditor();

                changesMade = true;
            }

            // If it's the page selector of the pet we rearrange the visual to the next page
            else if (editorItem.getType().equals(EditorExpectationType.PAGE_SELECTOR)) {
                int value = 1;
                if (e.getClick() == ClickType.LEFT)
                    value = -1;

                EditorPageSelection.set(p, Math.max(0, EditorPageSelection.get(p) + value));
                editor.openEditor();
            }

            // If it's a pet icon, then we dive in the pet editing
            else if (editorItem.getType().equals(EditorExpectationType.PET)) {
                final int slot = e.getSlot();
                final Pet pet = Pet.getObjectPets().get(slot + 45 * EditorPageSelection.get(p));
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final EditorEditing editing = EditorEditing.get(p);
                editing.setPetId(pet.getId());
                editor.setState(EditorState.PET_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete the pet
            else if (editorItem.getType().equals(EditorExpectationType.PET_DELETE)) {
                final Pet pet = PetConfig.loadConfigPet(EditorEditing.get(p).getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                changesMade = PetConfig.getConfig(pet.getId()).delete();

                if (changesMade) {
                    EditorItems.getCachedDeleted().add(pet.getId());
                    p.sendMessage("§cThe pet §e" + pet.getId() + "§c was deleted successfully.");
                }

                editor.setState(EditorState.PET_EDITOR);
                editor.openEditor();
            }

            // If we should edit a pet level
            else if (editorItem.getType().equals(EditorExpectationType.PET_LEVEL_EDIT)) {
                final EditorEditing editorPet = EditorEditing.get(p);
                final Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final PetLevel level = PetConfig.loadConfigPet(editorPet.getPetId()).getPetLevels().stream()
                        .filter(petLevel -> petLevel.getLevelId().equals(editorPet.getEditorMapping().get(e.getSlot())))
                        .findFirst().orElse(null);

                editorPet.setMappedId(level.getLevelId());
                editor.setState(EditorState.PET_EDITOR_LEVEL_EDIT);
                editor.openEditor();
            }

            // If we should create a pet level
            else if (editorItem.getType().equals(EditorExpectationType.PET_LEVEL_CREATE)) {
                final EditorEditing editorPet = EditorEditing.get(p);
                final Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final PetConfig config = PetConfig.getConfig(pet.getId());
                config.registerCleanPetLevel(null);
                final PetLevel level = pet.getPetLevels().getLast();

                editorPet.setMappedId(level.getLevelId());
                editor.setState(EditorState.PET_EDITOR_LEVEL_EDIT);
                editor.openEditor();
            }

            // If we should delete a pet level
            else if (editorItem.getType().equals(EditorExpectationType.PET_LEVEL_DELETE)) {
                final EditorEditing editorPet = EditorEditing.get(p);
                final Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final PetLevel level = PetConfig.loadConfigPet(editorPet.getPetId()).getPetLevels().stream()
                        .filter(petLevel -> petLevel.getLevelId().equals(editorPet.getMappedId()))
                        .findFirst().orElse(null);

                final PetConfig config = PetConfig.getConfig(pet.getId());
                config.deletePetLevel(level.getLevelId());

                editor.setState(EditorState.PET_EDITOR_LEVELS);
                editor.openEditor();
            }

            // If we should edit a pet skin
            else if (editorItem.getType().equals(EditorExpectationType.PET_SKIN_EDIT)) {
                final EditorEditing editorPet = EditorEditing.get(p);
                final Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final int slotMapping = e.getSlot();
                final PetSkin skin = PetSkin.getSkins(pet).stream()
                        .filter(petSkin -> petSkin.getPathId().equals(editorPet.getEditorMapping().get(slotMapping)))
                        .findFirst().orElse(null);

                editorPet.setMappedId(skin.getPathId());
                editor.setState(EditorState.PET_EDITOR_SKIN_EDIT);
                editor.openEditor();
            }

            // If we should create a pet skin
            else if (editorItem.getType().equals(EditorExpectationType.PET_SKIN_CREATE)) {
                final EditorEditing editorPet = EditorEditing.get(p);
                final Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final PetConfig config = PetConfig.getConfig(pet.getId());
                config.registerCleanPetSkin();
                final List<PetSkin> skins = PetSkin.getSkins(pet);
                final PetSkin skin = skins.getLast();

                editorPet.setMappedId(skin.getPathId());
                editor.setState(EditorState.PET_EDITOR_SKIN_EDIT);
                editor.openEditor();
            }

            // If we should delete a pet skin
            else if (editorItem.getType().equals(EditorExpectationType.PET_SKIN_DELETE)) {
                final EditorEditing editorPet = EditorEditing.get(p);
                final Pet pet = PetConfig.loadConfigPet(editorPet.getPetId());
                if (pet == null) {
                    Debugger.send("§cPet could not be found.");
                    return;
                }
                final PetConfig config = PetConfig.getConfig(pet.getId());
                config.deletePetSkin(editorPet.getMappedId());

                editor.setState(EditorState.PET_EDITOR_SKINS);
                editor.openEditor();
            }

            // If it's an item, we need to serialize it
            else if (editorItem.getType().equals(EditorExpectationType.ITEM)) {
                final ItemStack replaceItem = e.getCursor();
                if (replaceItem == null || replaceItem.getType().isAir())
                    return;
                if (!replaceItem.hasItemMeta())
                    return;
                editorItem.setValue(replaceItem);
                editorItem.save(p);

                editor.openEditor();

                changesMade = true;
            }

            // If we should create a category
            else if (editorItem.getType().equals(EditorExpectationType.CATEGORY_CREATE)) {
                CategoryConfig.registerCleanCategory(p);
                editor.setState(EditorState.CATEGORY_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete a category
            else if (editorItem.getType().equals(EditorExpectationType.CATEGORY_DELETE)) {
                final EditorEditing editing = EditorEditing.get(p);
                final Category category = CategoryConfig.loadConfigCategory(editing.getMappedId());
                if (category == null) {
                    Debugger.send("§cCategory could not be found.");
                    return;
                }
                final CategoryConfig config = CategoryConfig.getMapping().get(category.getId());
                config.delete();

                CategoryConfig.load(AbstractConfig.getPath() + "Categories/", true);
                editor.setState(EditorState.CATEGORY_EDITOR);
                editor.openEditor();
            }

            // If we should edit a category
            else if (editorItem.getType().equals(EditorExpectationType.CATEGORY_EDIT)) {
                final EditorEditing editing = EditorEditing.get(p);
                final Category category = CategoryConfig.loadConfigCategory(editing.getEditorMapping().get(e.getSlot()));
                if (category == null) {
                    Debugger.send("§cCategory could not be found.");
                    return;
                }
                editing.setMappedId(category.getId());
                editor.setState(EditorState.CATEGORY_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should create an item
            else if (editorItem.getType().equals(EditorExpectationType.ITEM_CREATE)) {
                final EditorEditing editing = EditorEditing.get(p);

                final String key = UUID.randomUUID().toString();
                ItemsListConfig.getInstance().setItemStack(key, Items.UNKNOWN.getItem().clone());
                ItemsListConfig.getInstance().reload();

                editing.setMappedId(key);
                editor.setState(EditorState.ITEM_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should edit an item
            else if (editorItem.getType().equals(EditorExpectationType.ITEM_EDIT)) {
                final EditorEditing editing = EditorEditing.get(p);
                final String itemId = editing.getEditorMapping().get(e.getSlot());
                final ItemStack item = ItemsListConfig.getInstance().getItemStack(itemId);
                if (item == null) {
                    Debugger.send("§cItem could not be found.");
                    return;
                }
                editing.setMappedId(itemId);
                editor.setState(EditorState.ITEM_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete an item
            else if (editorItem.getType().equals(EditorExpectationType.ITEM_DELETE)) {

                final EditorEditing editing = EditorEditing.get(p);
                final String itemId = editing.getMappedId();
                ItemsListConfig.getInstance().removeItemStack(itemId);

                editor.setState(EditorState.ITEM_EDITOR);
                editor.openEditor();
            }

            // If we should create an item
            else if (editorItem.getType().equals(EditorExpectationType.PETFOOD_CREATE)) {
                final EditorEditing editing = EditorEditing.get(p);

                final String key = PetFoodConfig.getInstance().registerCleanPetfood();

                editing.setMappedId(PetFood.getFromId(key).getId());
                editor.setState(EditorState.PETFOOD_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should edit a petfood
            else if (editorItem.getType().equals(EditorExpectationType.PETFOOD_EDIT)) {
                final EditorEditing editing = EditorEditing.get(p);
                final String petFoodId = editing.getEditorMapping().get(e.getSlot());
                final PetFood petFood = PetFoodConfig.loadConfigPetFood(petFoodId);
                if (petFood == null) {
                    Debugger.send("§cPetfood could not be found.");
                    return;
                }
                editing.setMappedId(petFood.getId());
                editor.setState(EditorState.PETFOOD_EDITOR_EDIT);
                editor.openEditor();
            }

            // If we should delete an item
            else if (editorItem.getType().equals(EditorExpectationType.PETFOOD_DELETE)) {
                final EditorEditing editing = EditorEditing.get(p);
                final PetFood petFood = PetFoodConfig.loadConfigPetFood(editing.getMappedId());
                PetFoodConfig.getInstance().removePetFood(petFood.getId());

                editor.setState(EditorState.PETFOOD_EDITOR);
                editor.openEditor();
            }

            // If it's more advanced, it needs typing, so starts a conversation!
            else {
                final EditorConversation conversation = new EditorConversation(p, editorItem);
                conversation.start();
                p.closeInventory();
            }

            if (changesMade)
                p.sendMessage("§aChanges saved! Make sure you §nreload MCPets§a to apply the changes.");

        }

    }

    private boolean resetFeature(final InventoryClickEvent e, final Editor editor, final EditorItems editorItem, final Player p) {
        // Reset the edited feature
        if (e.getClick() == ClickType.SHIFT_LEFT && editorItem.isResetable()) {
            editorItem.setValue(EditorItems.RESET_VALUE_TAG);
            if (editorItem.save(p)) {
                editor.openEditor();
                p.sendMessage("§aThe feature was reseted successfully! Please §nreload§a MCPets so the changes take effect!");
                return true;
            }
        }
        return false;
    }

    @EventHandler
    private void refreshEditor(final PlayerQuitEvent e) {
        Editor.refreshEditor(e.getPlayer());
    }
}
