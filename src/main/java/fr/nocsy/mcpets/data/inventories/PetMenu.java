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

import java.util.List;
import java.util.UUID;

public class PetMenu {

    @Getter
    private static final String title = Language.INVENTORY_PETS_MENU.getMessage();

    @Getter
    private final Inventory inventory;

    @Getter
    private final UUID owner;

    public PetMenu(Player p, int page, boolean addPager) {
        // Load the data from the player
        // Mainly for the pet stats
        PlayerData.get(p.getUniqueId());
        owner = p.getUniqueId();

        List<Pet> availablePets = Pet.getAvailablePets(p);

        int invSize = GlobalConfig.getInstance().getAdaptiveInventory();

        if (invSize == -1) {

            while (availablePets.size() - 53 * page < 0 &&
                    page > 0) {
                page--;
            }

            invSize = Math.max(Math.min(availablePets.size() - 53 * page, 53), 0);
            while (invSize % 9 != 0 || invSize == 0) {
                invSize++;
            }

        }

        inventory = Bukkit.createInventory(null, invSize, title);

        for (int i = page * 53; i < invSize + page * 53; i++) {
            if (i >= availablePets.size()) {
                continue;
            }
            Pet pet = availablePets.get(i);

            if (i % 53 == 0 && i > page * 53) {
                inventory.setItem(invSize - 1, Items.page(page, p));
                break;
            }

            inventory.addItem(pet.buildItem(pet.getIcon(), true, null, null, null, null, 0, null));

        }

        if (addPager) {
            inventory.setItem(invSize - 1, Items.page(page, p));
        }

    }

    public void open(Player p) {
        if(p.getUniqueId().equals(owner) && Category.getCategories().size() > 0)
        {
            CategoriesMenu.open(p);
            return;
        }
        p.openInventory(inventory);
    }

}
