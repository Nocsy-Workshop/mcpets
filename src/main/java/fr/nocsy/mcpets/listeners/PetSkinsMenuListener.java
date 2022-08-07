package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetSkin;
import fr.nocsy.mcpets.data.config.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PetSkinsMenuListener implements Listener {

    @EventHandler
    public void click(InventoryClickEvent e)
    {

        Player p = (Player)e.getWhoClicked();

        if(PetSkin.hasMetadata(p))
        {
            e.setCancelled(true);

            ItemStack it = e.getCurrentItem();
            if(it != null && it.hasItemMeta())
            {
                PetSkin petSkin = PetSkin.fromIcon(it);
                if(petSkin != null)
                {
                    Pet pet = Pet.fromOwner(p.getUniqueId());
                    if(pet == null)
                    {
                        Language.REVOKED_BEFORE_CHANGES.sendMessage(p);
                        p.closeInventory();
                        return;
                    }

                    if(petSkin.apply(pet))
                        Language.SKIN_APPLIED.sendMessage(p);
                    else
                        Language.SKIN_COULD_NOT_APPLY.sendMessage(p);

                    pet.setActiveSkin(petSkin);
                    p.closeInventory();
                }
            }

        }

    }

    @EventHandler
    public void close(InventoryCloseEvent e)
    {

        Player p = (Player) e.getPlayer();
        if(PetSkin.hasMetadata(p))
        {
            PetSkin.removeMetadata(p);
        }

    }

}
