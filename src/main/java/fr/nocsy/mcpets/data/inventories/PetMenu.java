package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class PetMenu {

    @Getter
    private static final String title = Language.INVENTORY_PETS_MENU.getMessage();

    @Getter
    private final Inventory inventory;

    public PetMenu(Player p, int page, boolean addPager) {
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
                inventory.setItem(invSize - 1, Items.page(page));
                break;
            }
            inventory.addItem(pet.getIcon());

        }

        if (addPager) {
            inventory.setItem(invSize - 1, Items.page(page));
        }

    }

    public void open(Player p) {
        p.openInventory(inventory);
    }

}
