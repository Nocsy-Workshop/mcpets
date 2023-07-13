package fr.nocsy.mcpets.data;

import fr.nocsy.mcpets.data.config.CategoryConfig;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Category {

    @Getter
    private static ArrayList<Category> categories = new ArrayList<Category>();
    @Getter
    private static HashMap<UUID, Category> categoryView = new HashMap<>();

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
    private ArrayList<Pet> pets;

    @Getter
    private int maxPages;

    @Getter
    @Setter
    private boolean defaultCategory;

    @Getter
    @Setter
    private List<String> excludedCategoriesId;

    public Category(String id)
    {
        this.id = id;
        this.icon = null;
        this.pets = new ArrayList<>();
        this.displayName = "Unknown";
        this.iconName = "Unknown";
    }

    public boolean openInventory(Player p, int page)
    {
        if(page >= maxPages || page < 0)
            return false;

        p.closeInventory();

        int invSize = pets.size() - page*53 + 1; //Adding 1 for the page manager
        invSize = Math.min(54, invSize);
        while(invSize <= 0 || invSize%9 != 0)
        {
            invSize++;
        }

        ArrayList<Pet> showedPets = new ArrayList<>();
        for(int i = page*53; i < pets.size(); i++)
        {
            if(showedPets.size() >= invSize-1)
                break;
            Pet pet = pets.get(i);
            if(pet.has(p))
                showedPets.add(pet);
        }

        if(showedPets.isEmpty() && page > 0)
            return false;

        invSize = showedPets.size();
        if(maxPages > 1)
            invSize++;
        while(invSize <= 0 || invSize % 9 != 0)
            invSize++;

        Inventory inventory = Bukkit.createInventory(null,  invSize, displayName);

        if(maxPages > 1)
            inventory.setItem(invSize-1, Items.page(this, page));
        for(int i = 0; i < showedPets.size(); i++)
        {
            Pet pet = showedPets.get(i);
            inventory.setItem(i,  pet.buildItem(pet.getIcon(), true, null, null, null, null, 0, null));
        }
        p.openInventory(inventory);
        Category.registerPlayerView(p, this);
        return true;
    }

    public void addPet(Pet pet)
    {
        if(!pets.contains(pet))
            pets.add(pet);
    }

    public void countMaxPages()
    {
        this.maxPages = 1;
        int count = pets.size();
        while(count > 0)
        {
            if(count%53 == 0)
                maxPages++;
            count--;
        }
    }

    public void setIcon(ItemStack it)
    {
        icon = it;
        setupData();
    }


    private void setupData()
    {
        ItemMeta meta = icon.getItemMeta();
        meta.setLocalizedName("MCPetsCategory;" + this.getId());
        meta.setDisplayName(iconName);
        ArrayList<String> lore = (ArrayList<String>) meta.getLore() != null
                ? (ArrayList<String>) meta.getLore()
                : new ArrayList<>();

        lore.add(" ");
        lore.add(Language.CATEGORY_PET_AMOUNT.getMessageFormatted(new FormatArg("%petAmount%", Integer.toString(pets.size()))));
        meta.setLore(lore);
        icon.setItemMeta(meta);
    }

    /**
     * Return the page associated to the said inventory
     * -1 if no category is found
     * @param inventory
     * @return
     */
    public int getCurrentPage(Inventory inventory)
    {
        if(inventory == null)
            return -1;

        ItemStack pager = inventory.getItem(inventory.getSize()-1);
        if(pager != null
                && !pager.getType().isAir()
                && pager.hasItemMeta()
                && pager.getItemMeta().hasLocalizedName())
        {
            String[] data = pager.getItemMeta().getLocalizedName().split(";");
            if(data.length != 3)
                return -1;

            String tag = data[0];
            if(!tag.equalsIgnoreCase("MCPetsPage"))
                return -1;

            String page = data[2];
            return Integer.parseInt(page);
        }
        return -1;
    }

    public static void add(Category category)
    {
        categories.add(category);
    }

    /**
     * Return the category associated to the said id
     * null if none is found
     * @param categoryId
     * @return
     */
    public static Category getFromId(String categoryId)
    {
        Optional<Category> optional = categories.stream().filter(cat -> cat.getId().equals(categoryId)).findFirst();
        return optional.orElse(null);
    }

    /**
     * Dynamically register the category viewed by the player
     * Recall to unregister the view when the inventory closes
     * @param p
     * @param category
     */
    public static void registerPlayerView(Player p, Category category)
    {
        categoryView.put(p.getUniqueId(), category);
    }

    /**
     * Unregister a dynamically saved player view of a category
     * @param p
     */
    public static void unregisterPlayerView(Player p)
    {
        categoryView.remove(p.getUniqueId());
    }

    /**
     * Get the category currently viewed by the given player if dynamically registered
     * @param p
     * @return
     */
    public static Category getCategoryView(Player p)
    {
        return categoryView.get(p.getUniqueId());
    }

    /**
     * Return the category associated to the said inventory
     * null if none is found
     * @param inventory
     * @return
     */
    public static Category getFromInventory(Inventory inventory)
    {
        if(inventory == null)
            return null;

        ItemStack pager = inventory.getItem(inventory.getSize()-1);
        if(pager != null
                && !pager.getType().isAir()
                && pager.hasItemMeta()
                && pager.getItemMeta().hasLocalizedName())
        {
            String[] data = pager.getItemMeta().getLocalizedName().split(";");
            if(data.length != 3)
                return null;

            String tag = data[0];
            if(!tag.equalsIgnoreCase("MCPetsPage"))
                return null;

            String categoryId = data[1];
            String page = data[2];

            return getFromId(categoryId);
        }
        return null;
    }

}
