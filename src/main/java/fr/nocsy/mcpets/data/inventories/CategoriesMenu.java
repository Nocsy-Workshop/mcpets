package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CategoriesMenu {

    @Getter
    private static final String title = Language.CATEGORY_MENU_TITLE.getMessage();

    public static void open(Player p)
    {
        int invSize = Category.getCategories().size();
        while(invSize == 0 || invSize%9 != 0)
            invSize++;

        Inventory inventory = Bukkit.createInventory(null, invSize, title);

        Category.getCategories()
                .forEach(category -> {
                    for(Pet pet : category.getPets())
                        if (pet.has(p))
                        {
                            inventory.addItem(category.getIcon());
                            break;
                        }
                });

        p.openInventory(inventory);
    }

    public static Category findCategory(ItemStack icon)
    {
        if(icon == null)
            return null;

        if(icon.hasItemMeta() &&
            icon.getItemMeta().hasLocalizedName() &&
            icon.getItemMeta().getLocalizedName().contains("MCPetsCategory"))
        {
            String[] data = icon.getItemMeta().getLocalizedName().split(";");
            if(data.length == 2)
            {
                String catId = data[1];
                return Category.getFromId(catId);
            }
        }
        return null;
    }

    public static void openSubCategory(Player p, ItemStack icon)
    {
        Category category = findCategory(icon);
        if(category != null)
        {
            category.openInventory(p, 0);
        }
    }

}
