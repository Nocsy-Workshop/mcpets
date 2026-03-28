package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CategoriesMenuListener implements Listener {

    @EventHandler
    public void invClick(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)) {
            return;
        }

        if (holder.getType() != PetInventoryHolder.Type.CATEGORIES_MENU) {
            return;
        }

        if (!(e.getWhoClicked() instanceof final Player p)) {
            return;
        }

        e.setCancelled(true);

        final ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) {
            return;
        }

        CategoriesMenu.openSubCategory(p, it);
    }
}
