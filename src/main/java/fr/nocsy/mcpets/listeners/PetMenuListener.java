package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class PetMenuListener implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(PetMenu.getTitle()) && Category.getCategories().size() == 0) {
            e.setCancelled(true);
            Player p = (Player) e.getWhoClicked();
            ItemStack it = e.getCurrentItem();
            if (it != null) {
                if (it.hasItemMeta() && it.getItemMeta().hasItemName() && it.getItemMeta().getItemName().contains("AlmPetPage;")) {
                    int page = Integer.parseInt(it.getItemMeta().getItemName().split(";")[1]);
                    p.closeInventory();
                    if (e.getClick() == ClickType.LEFT) {
                        PetMenu menu = new PetMenu(p, Math.max(page - 1, 0));
                        menu.open(p);
                    } else {
                        PetMenu menu = new PetMenu(p, page + 1);
                        menu.open(p);
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
