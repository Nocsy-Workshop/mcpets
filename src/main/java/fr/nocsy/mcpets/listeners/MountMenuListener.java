package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.inventories.MountMenu;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener to handle interactions in the mounts menu
 */
public class MountMenuListener implements Listener {

    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)) {
            return;
        }

        if (holder.getType() != PetInventoryHolder.Type.MOUNT_MENU) {
            return;
        }

        if (!(e.getWhoClicked() instanceof final Player p)) {
            return;
        }

        final ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) {
            return;
        }

        e.setCancelled(true);

        String tag = it.hasItemMeta() ? PDCTag.get(it.getItemMeta()) : null;
        if (tag != null && tag.contains("AlmPetPage;")) {

            final int page = Integer.parseInt(tag.split(";")[1]);
            p.closeInventory();

            final MountMenu menu;
            if (e.getClick() == ClickType.LEFT) {
                menu = new MountMenu(p, Math.max(page - 1, 0));
            } else {
                menu = new MountMenu(p, page + 1);
            }

            menu.open(p);
            return;
        }

        final Pet petObject = Pet.getFromIcon(it);
        if (petObject != null) {
            p.closeInventory();
            final Pet pet = petObject.copy();
            pet.spawnWithMessage(p);
        }
    }
}
