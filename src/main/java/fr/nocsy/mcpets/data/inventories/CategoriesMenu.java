package fr.nocsy.mcpets.data.inventories;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.CategoryType;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import fr.nocsy.mcpets.utils.MenuPaginationHelper.PaginationConfig;

public class CategoriesMenu {

    @Getter
    private static final String title = Language.CATEGORY_MENU_TITLE.getMessage();

    public static void open(final Player p) {
        openFiltered(p, CategoryType.PET);
    }

    /**
     * Open the categories menu filtered by category type
     *
     * @param p          the player to open the menu for
     * @param filterType the type to filter by (null for all categories)
     */
    public static void openFiltered(final Player p, final CategoryType filterType) {
        openFiltered(p, filterType, 0);
    }

    public static void openFiltered(final Player p, final CategoryType filterType, final int page) {
        final List<Category> categoriesToShow;

        if (filterType == null) {
            categoriesToShow = Category.getCategories();
        } else {
            categoriesToShow = Category.getCategories(filterType);
        }

        PaginationConfig config = MenuPaginationHelper.calculatePagination(page, categoriesToShow.size());
        
        final List<Category> selectedCategories = new ArrayList<>();
        for (int i = config.startIndex(); i < config.startIndex() + config.itemsToShow() && i < categoriesToShow.size(); i++) {
            selectedCategories.add(categoriesToShow.get(i));
        }
        
        String menuTitle = title;
        if (filterType != null) {
            menuTitle = filterType.getTitle().getMessage();
        }

        final Inventory inventory = new PetInventoryHolder(config.invSize(), menuTitle, PetInventoryHolder.Type.CATEGORIES_MENU).getInventory();

        int slot = config.startSlot();
        for (Category category : selectedCategories) {
            for (Pet pet : category.getPets()) {
                if (pet.has(p)) {
                    inventory.setItem(slot++, category.getIcon());
                    break;
                }
            }
        }

        int maxPages = MenuPaginationHelper.calculateMaxPages(categoriesToShow.size());
        
        if (config.needsPreviousButton()) {
            inventory.setItem(0, Items.previousPage(page, p, maxPages, filterType));
        }

        if (config.needsNextButton()) {
            inventory.setItem(config.invSize() - 1, Items.nextPage(page, p, maxPages, filterType));
        }

        p.openInventory(inventory);
    }

    public static Category findCategory(final ItemStack icon) {
        if (icon == null) return null;
        if (!icon.hasItemMeta()) return null;

        String tagVal = PDCTag.get(icon.getItemMeta());
        if (tagVal == null) return null;
        if (!tagVal.contains("MCPetsCategory")) return null;

        final String[] data = tagVal.split(";");
        if (data.length == 2) {
            final String catId = data[1];
            return Category.getFromId(catId);
        }

        return null;
    }

    public static void openSubCategory(final Player p, final ItemStack icon) {
        final Category category = findCategory(icon);
        if (category != null) category.openInventory(p, 0);
    }

}
