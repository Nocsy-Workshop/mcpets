package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CategoryMenuListener implements Listener {

    @EventHandler
    public void click(final InventoryClickEvent e) {

        if (Category.getCategories().isEmpty()) return;

        if (!(e.getWhoClicked() instanceof final Player p)) return;

        final Category category = Category.getCategoryView(p);

        if (category == null) return;

        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)) return;

        if (holder.getType() != PetInventoryHolder.Type.CATEGORY_MENU) return;

        e.setCancelled(true);

        if (e.getClickedInventory() == null && GlobalConfig.getInstance().isEnableClickBackToMenu()) {
            CategoriesMenu.open(p);
            return;
        }

        final ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir() || !it.hasItemMeta()) return;
        if (it.getItemMeta().hasItemName() && it.getItemMeta().getItemName().contains("MCPetsPage;")) {
            final int currentPage = category.getCurrentPage(e.getClickedInventory());
            if (e.getClick() == ClickType.LEFT) {
                category.openInventory(p, currentPage - 1);
            } else {
                category.openInventory(p, currentPage + 1);
            }
            return;
        }

        final Pet petObject = Pet.getFromIcon(it);
        if (petObject != null) {
            p.closeInventory();
            final Pet pet = petObject.copy();
            pet.spawnWithMessage(p);
            Category.unregisterPlayerView(p);
        }
    }
}
