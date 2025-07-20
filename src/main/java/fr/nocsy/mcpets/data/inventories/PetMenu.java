package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Category;
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

public class PetMenu {

    @Getter
    private static final String title = Language.INVENTORY_PETS_MENU.getMessage();

    @Getter
    private final Inventory inventory;

    @Getter
    private final UUID owner;

    public PetMenu(Player p, int page) {
        // Load the data from the player
        // Mainly for the pet stats
        PlayerData.get(p.getUniqueId());
        owner = p.getUniqueId();

        List<Pet> availablePets = Pet.getAvailablePets(p);

        // Count the amount of pets that are being selected at that page
        // One page is up to 53 pets, so the page P has already seen 53 * P pets
        // 53 pets because we gotta leave one spot available for the pager everytime
        ArrayList<Pet> selectedPets = new ArrayList<>();
        // Let's see if we need to add a pager to the inventory
        // Either we have more than 53 pets or we are at a page greater than 0
        boolean addPager = page > 0;
        int pageSize = 53;
        if (GlobalConfig.getInstance().getAdaptiveInventory() > 0) {
            pageSize = GlobalConfig.getInstance().getAdaptiveInventory() - 1;
        }
        for (int i = pageSize * page; i < availablePets.size(); i++)
        {
            // We can not have more than 53 pets selected at a given page
            if(selectedPets.size() >= 53)
            {
                addPager = true;
                break;
            }
            selectedPets.add(availablePets.get(i));
        }

        // We can now easily compute the inventory size in the adaptive case
        // by taking the amount of pets selected and adding one for the pager
        // then we round it up to the nearest multiple of 9
        int invSize = GlobalConfig.getInstance().getAdaptiveInventory();
        if (invSize <= 0) {
            invSize = selectedPets.size() + 1;
            while (invSize % 9 != 0) {
                invSize++;
            }
        }

        // Let's fill tbe view with the selected pets
        inventory = new PetInventoryHolder(invSize, title).getInventory();
        for (Pet pet : selectedPets) {
            inventory.addItem(pet.buildItem(pet.getIcon(), true, null, null, null, null, 0, null));
        }

        // If we need to add a pager, we do so
        if (addPager) {
            inventory.setItem(invSize - 1, Items.page(page, p));
        }
    }

    public void open(Player p) {
        if (p.getUniqueId().equals(owner) && Category.getCategories().size() > 0) {
            CategoriesMenu.open(p);
            return;
        }
        p.openInventory(inventory);
    }
}
