package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.sql.PlayerData;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Menu to display the list of available mounts for a player
 */
public class MountMenu {

    @Getter
    private static final String title = Language.INVENTORY_MOUNTS_MENU.getMessage();

    private static final PetInventoryHolder.Type petInvType = PetInventoryHolder.Type.MOUNT_MENU;

    @Getter
    private final Inventory inventory;

    @Getter
    private final UUID owner;

    public MountMenu(final Player p, final int page) {
        // Load the data from the player
        // Mainly for the pet stats
        PlayerData.get(p.getUniqueId());
        owner = p.getUniqueId();

        // Get only mounts (pets that are mountable)
        final List<Pet> availablePets = Pet.getAvailablePets(p);
        final List<Pet> availableMounts = new ArrayList<>();
        
        for (final Pet pet : availablePets) {
            if (pet.isMountable()) {
                availableMounts.add(pet);
            }
        }

        // Count the amount of mounts that are being selected at that page
        // One page is up to 53 mounts, so the page P has already seen 53 * P mounts
        // 53 mounts because we gotta leave one spot available for the pager everytime
        final List<Pet> selectedMounts = new ArrayList<>();
        // Let's see if we need to add a pager to the inventory
        // Either we have more than 53 mounts or we are at a page greater than 0
        boolean addPager = page > 0;
        int pageSize = 53;
        if (GlobalConfig.getInstance().getAdaptiveInventory() > 0) {
            pageSize = GlobalConfig.getInstance().getAdaptiveInventory() - 1;
        }
        for (int i = pageSize * page; i < availableMounts.size(); i++)
        {
            // We can not have more than 53 mounts selected at a given page
            if(selectedMounts.size() >= 53)
            {
                addPager = true;
                break;
            }
            selectedMounts.add(availableMounts.get(i));
        }

        // We can now easily compute the inventory size in the adaptive case
        // by taking the amount of mounts selected and adding one for the pager
        // then we round it up to the nearest multiple of 9
        int invSize = GlobalConfig.getInstance().getAdaptiveInventory();
        if (invSize <= 0) {
            invSize = selectedMounts.size() + 1;
            while (invSize % 9 != 0) {
                invSize++;
            }
        }

        // Let's fill the view with the selected mounts
        inventory = new PetInventoryHolder(invSize, title, petInvType).getInventory();
        for (final Pet mount : selectedMounts) {
            inventory.addItem(mount.buildItem(mount.getIcon(), true, null, null, null, null, 0, null));
        }

        // If we need to add a pager, we do so
        if (addPager) {
            inventory.setItem(invSize - 1, Items.page(page, p));
        }
    }

    public void open(final Player p) {
        if (p.getUniqueId().equals(owner) && !Category.getCategories(CategoryType.MOUNT).isEmpty()) {
            CategoriesMenu.openFiltered(p, CategoryType.MOUNT);
            return;
        }
        p.openInventory(inventory);
    }
}
