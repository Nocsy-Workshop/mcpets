package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.CategoriesMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CategoriesMenuListener implements Listener {

    @EventHandler
    public void invClick(InventoryClickEvent e) {
        String viewTitle = e.getView().getTitle();
        boolean isCategoryMenu = viewTitle.equalsIgnoreCase(Language.CATEGORY_MENU_TITLE.getMessage());
        boolean isPetCategoryMenu = viewTitle.equalsIgnoreCase(Language.INVENTORY_PETS_MENU.getMessage());
        boolean isMountCategoryMenu = viewTitle.equalsIgnoreCase(Language.INVENTORY_MOUNTS_MENU.getMessage());
        
        if (isCategoryMenu || isPetCategoryMenu || isMountCategoryMenu) {
            ItemStack icon = e.getCurrentItem();
            if (icon != null) {
                CategoriesMenu.openSubCategory((Player) e.getWhoClicked(), icon);
            }
            e.setCancelled(true);
        }
    }
}
