package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.Pet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class PetInteractionMenu {

    @Getter
    private static String title = Language.INVENTORY_PETS_MENU_INTERACTIONS.getMessage();

    @Getter
    private Inventory inventory;

    public PetInteractionMenu(Pet pet)
    {
        inventory = Bukkit.createInventory(null, InventoryType.HOPPER, title);

        if(GlobalConfig.getInstance().isActivateBackMenuIcon())
            inventory.setItem(0, Items.PETMENU.getItem());
        else
            inventory.setItem(0, Items.deco(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        if(GlobalConfig.getInstance().isNameable())
            inventory.setItem(1, Items.RENAME.getItem());
        else
            inventory.setItem(1, Items.deco(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        inventory.setItem(2, Items.petInfo(pet));

        if(GlobalConfig.getInstance().isMountable() && pet.isMountable()) {
            inventory.setItem(3, Items.MOUNT.getItem());
        }
        else
            inventory.setItem(3, Items.deco(Material.LIGHT_BLUE_STAINED_GLASS_PANE));

        if(!pet.getSignals().isEmpty())
        {
            inventory.setItem(4, pet.getSignalStick());
        }
        else
            inventory.setItem(4, Items.deco(Material.LIGHT_BLUE_STAINED_GLASS_PANE));
    }

    public void open(Player p)
    {
        p.openInventory(inventory);
    }

}
