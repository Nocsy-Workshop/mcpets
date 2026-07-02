package fr.nocsy.mcpets.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;

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

        String tag = it.hasItemMeta() ? PDCTag.get(it.getItemMeta()) : null;

        if (tag != null) {
            if (tag.startsWith("AlmPetPreviousPage;")) {
                String[] parts = tag.split(";");
                int page = Integer.parseInt(parts[1]);
                CategoryType filterType = parts.length > 2 ? CategoryType.valueOf(parts[2]) : CategoryType.PET;
                CategoriesMenu.openFiltered(p, filterType, Math.max(page - 1, 0));
                return;
            }

            if (tag.startsWith("AlmPetNextPage;")) {
                String[] parts = tag.split(";");
                int page = Integer.parseInt(parts[1]);
                CategoryType filterType = parts.length > 2 ? CategoryType.valueOf(parts[2]) : CategoryType.PET;
                CategoriesMenu.openFiltered(p, filterType, page + 1);
                return;
            }
        }

        CategoriesMenu.openSubCategory(p, it);
    }

}
