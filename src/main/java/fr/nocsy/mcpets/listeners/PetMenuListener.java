package fr.nocsy.mcpets.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetMenu;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;

public class PetMenuListener implements Listener {

    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof final PetInventoryHolder holder)) {
            return;
        }

        if (holder.getType() != PetInventoryHolder.Type.PET_MENU) {
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

        if (MenuPaginationHelper.handlePagination(it, p,
                "AlmPetPreviousPage;", "AlmPetNextPage;",
            (player, page) -> new PetMenu(player, page).open(player))) {
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
