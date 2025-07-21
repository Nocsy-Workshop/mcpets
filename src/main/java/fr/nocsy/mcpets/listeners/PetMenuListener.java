package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
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
        if (!(e.getInventory().getHolder() instanceof PetInventoryHolder holder)) return;

        if (holder.getType() != PetInventoryHolder.Type.PET_MENU) return;

        if (!(e.getWhoClicked() instanceof Player p)) return;

        e.setCancelled(true);

        ItemStack it = e.getCurrentItem();
        if (it == null || it.getType().isAir()) return;

        if (it.hasItemMeta() && it.getItemMeta().hasItemName() && it.getItemMeta().getItemName().contains("AlmPetPage;")) {
            int page = Integer.parseInt(it.getItemMeta().getItemName().split(";")[1]);
            p.closeInventory();

            PetMenu menu;
            if (e.getClick() == ClickType.LEFT) {
                menu = new PetMenu(p, Math.max(page - 1, 0));
            } else {
                menu = new PetMenu(p, page + 1);
            }
            menu.open(p);
            return;
        }

        Pet petObject = Pet.getFromIcon(it);
        if (petObject != null) {
            p.closeInventory();
            Pet pet = petObject.copy();
            pet.spawnWithMessage(p);
        }
    }
}
