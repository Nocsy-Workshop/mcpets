package fr.nocsy.mcpets.data;

import java.util.*;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.utils.MenuPaginationHelper;
import fr.nocsy.mcpets.data.inventories.PetInventoryHolder;
import fr.nocsy.mcpets.utils.MenuPaginationHelper.PaginationConfig;

public class Category {

    @Getter
    private static List<Category> categories = new ArrayList<>();
    @Getter
    private static Map<UUID, Category> categoryView = new HashMap<>();

    @Getter
    private final String id;

    @Getter
    @Setter
    private String displayName;

    @Getter
    @Setter
    private String iconName;

    @Getter
    private ItemStack icon;
    @Getter
    private List<Pet> pets;

    @Getter
    private int maxPages;

    @Getter
    @Setter
    private boolean defaultCategory;

    @Getter
    @Setter
    private List<String> excludedCategoriesId;

    @Getter
    @Setter
    private CategoryType categoryType;

    public Category(final String id) {
        this.id = id;
        this.icon = null;
        this.pets = new ArrayList<>();
        this.displayName = "Unknown";
        this.iconName = "Unknown";
        this.categoryType = CategoryType.DEFAULT;
    }

    public boolean openInventory(final Player p, final int page) {
        if (page >= maxPages || page < 0) return false;

        p.closeInventory();

        final List<Pet> playerPets = new ArrayList<>();
        for (Pet pet : pets) {
            if (!pet.has(p)) continue;
            playerPets.add(pet);
        }
        
        if (playerPets.isEmpty() && page > 0) return false;
        
        PaginationConfig config = MenuPaginationHelper.calculatePagination(page, playerPets.size());

        final List<Pet> petsForPage = new ArrayList<>();
        for (int i = config.startIndex(); i < config.startIndex() + config.itemsToShow() && i < playerPets.size(); i++) {
            petsForPage.add(playerPets.get(i));
        }
        
        final Inventory inventory = new PetInventoryHolder(config.invSize(), displayName, PetInventoryHolder.Type.CATEGORY_MENU).getInventory();

        if (config.needsPreviousButton()) {
            inventory.setItem(0, Items.previousPage(this, page));
        }

        if (config.needsNextButton()) {
            inventory.setItem(config.invSize() - 1, Items.nextPage(this, page));
        }

        int slot = config.startSlot();
        for (Pet pet : petsForPage) {
            inventory.setItem(slot++, pet.buildItem(pet.getIcon(), true));
        }

        p.openInventory(inventory);
        Category.registerPlayerView(p, this);
        return true;
    }

    public void addPet(final Pet pet) {
        if (!pets.contains(pet)) pets.add(pet);
    }

    public void countMaxPages() {
        maxPages = MenuPaginationHelper.calculateMaxPages(pets.size());
    }

    public void setIcon(final ItemStack it) {
        icon = it;
        setupData();
    }


    private void setupData() {
        final ItemMeta meta = icon.getItemMeta();
        PDCTag.set(meta, "MCPetsCategory;" + this.getId());
        meta.displayName(Utils.toComponent(iconName));

        icon.setItemMeta(meta);
    }

    public static void add(final Category category) {
        categories.add(category);
    }

    /**
     * Return the category associated to the said id
     * null if none is found
     */
    public static Category getFromId(final String categoryId) {
        final Optional<Category> optional = categories.stream().filter(cat -> cat.getId().equals(categoryId)).findFirst();
        return optional.orElse(null);
    }

    /**
     * Return categories filtered by type
     * For PET type, also includes DEFAULT categories for backward compatibility
     * @param type the category type to filter by
     * @return list of categories matching the type
     */
    public static List<Category> getCategories(CategoryType type) {
        List<Category> filtered = new ArrayList<>();
        for (Category cat : categories) {
            if (cat.getCategoryType() == type) {
                filtered.add(cat);
            }
            // For PET filter, also include DEFAULT categories
            else if (type == CategoryType.PET && cat.getCategoryType() == CategoryType.DEFAULT) {
                filtered.add(cat);
            }
        }
        return filtered;
    }

    /**
     * Dynamically register the category viewed by the player
     * Recall to unregister the view when the inventory closes
     */
    public static void registerPlayerView(final Player p, final Category category) {
        categoryView.put(p.getUniqueId(), category);
    }

    /**
     * Unregister a dynamically saved player view of a category
     */
    public static void unregisterPlayerView(final Player p) {
        categoryView.remove(p.getUniqueId());
    }

    /**
     * Get the category currently viewed by the given player if dynamically registered
     */
    public static Category getCategoryView(final Player p) {
        return categoryView.get(p.getUniqueId());
    }

}
