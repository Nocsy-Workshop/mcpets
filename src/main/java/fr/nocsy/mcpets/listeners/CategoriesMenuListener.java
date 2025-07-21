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
    public void invClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof PetInventoryHolder holder)) return;

        if (holder.getType() != PetInventoryHolder.Type.CATEGORIES_MENU) return;

        if (!(e.getWhoClicked() instanceof Player p)) return;

        e.setCancelled(true);

        ItemStack icon = e.getCurrentItem();

        if (icon == null ) return;
        CategoriesMenu.openSubCategory(p, icon);
    }
}
