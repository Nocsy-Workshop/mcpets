package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CategoryMenuListener implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e) {

        if(Category.getCategories().size() == 0)
            return;

        Category category = Category.getFromInventory(e.getClickedInventory());

        if (category != null) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            ItemStack it = e.getCurrentItem();
            if (it != null) {
                if (it.hasItemMeta() && it.getItemMeta().hasLocalizedName() && it.getItemMeta().getLocalizedName().contains("MCPetsPage;")) {

                    int currentPage = category.getCurrentPage(e.getClickedInventory());
                    p.closeInventory();
                    if (e.getClick() == ClickType.LEFT) {
                        category.openInventory(p, Math.max(currentPage - 1, 0));
                    } else {
                        category.openInventory(p, Math.max(currentPage + 1, 0));
                    }
                    return;
                }

                Pet petObject = Pet.getFromIcon(it);
                if (petObject != null) {
                    p.closeInventory();
                    Pet pet = petObject.copy();
                    pet.spawnWithMessage(p, p.getLocation());
                }
            }

        }
    }
}
