package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class PetInventoryListener implements Listener {

    @EventHandler
    public void inventory(InventoryCloseEvent e)
    {

        Player p = (Player)e.getPlayer();
        PetInventory petInventory = PetInventory.fromCurrentView(p);

        if(petInventory != null)
        {
            petInventory.setInventory(e.getView().getTopInventory());
            petInventory.close(p);
        }

    }

}
