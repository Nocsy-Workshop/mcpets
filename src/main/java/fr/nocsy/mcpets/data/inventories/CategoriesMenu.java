package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CategoriesMenu {

    @Getter
    private static final String title = Language.CATEGORY_MENU_TITLE.getMessage();

    public static void open(Player p) {
        openFiltered(p, CategoryType.PET);
    }

    /**
     * Open the categories menu filtered by category type
     * @param p the player to open the menu for
     * @param filterType the type to filter by (null for all categories)
     */
    public static void openFiltered(Player p, CategoryType filterType) {
        ArrayList<Category> categoriesToShow;

        if (filterType == null) {
            categoriesToShow = Category.getCategories();
        } else {
            categoriesToShow = Category.getCategories(filterType);
        }

        int invSize = categoriesToShow.size();
        while (invSize == 0 || invSize % 9 != 0)
            invSize++;

        // Choose the appropriate title based on the filter type
        String menuTitle = title; // Default to category menu title
        if (filterType != null) {
            menuTitle = filterType.getTitle().getMessage();
        }

        Inventory inventory = new PetInventoryHolder(invSize, title, PetInventoryHolder.Type.CATEGORIES_MENU).getInventory();

        categoriesToShow
                .forEach(category -> {
                    for (Pet pet : category.getPets())
                        if (pet.has(p)) {
                            inventory.addItem(category.getIcon());
                            break;
                        }
                });

        p.openInventory(inventory);
    }

    public static Category findCategory(ItemStack icon) {
        if (icon == null) return null;

        if (icon.hasItemMeta() &&
                icon.getItemMeta().hasItemName() &&
                icon.getItemMeta().getItemName().contains("MCPetsCategory")) {

            String[] data = icon.getItemMeta().getItemName().split(";");
            if (data.length == 2) {
                String catId = data[1];
                return Category.getFromId(catId);
            }
        }

        return null;
    }

    public static void openSubCategory(Player p, ItemStack icon) {
        Category category = findCategory(icon);
        if (category != null) {
            category.openInventory(p, 0);
        }
    }
}
