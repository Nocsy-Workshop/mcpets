package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryConfig extends AbstractConfig {

    @Getter
    private String id;

    @Getter
    private Category category;

    public CategoryConfig(String id)
    {
        super();
        if(id != null && !id.isEmpty())
            this.id = id;
    }
    /**
     * Base constructor of a pet configuration (one to one)
     * It will initialize the variables while loading the data
     *
     * @param fileName
     */
    public CategoryConfig(String folderName, String fileName) {
        init(folderName, fileName);
        reload();
    }

    @Override
    public void init(String folderName, String fileName) {
        super.init(folderName, fileName);

        this.id = fileName.replace(".yml", "");
        this.category = new Category(id);

        if (getConfig().get("DisplayName") == null)
            getConfig().set("DisplayName", "Category");
        if (getConfig().get("Icon") == null)
            getConfig().set("Icon", setupUnkownIcon());
        if (getConfig().get("Pets") == null)
            getConfig().set("Pets", new ArrayList<>());
        if (getConfig().get("DefaultCategory") == null)
            getConfig().set("DefaultCategory", false);
        if (getConfig().get("ExcludedCategories") == null)
            getConfig().set("ExcludedCategories", new ArrayList<String>());

        save();
        reload();
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void reload() {

        loadConfig();

        category.getPets().clear();

        // Handle the name of the category
        if (getConfig().get("DisplayName") != null)
        {
            category.setDisplayName(getConfig().getString("DisplayName"));
        }

        // Load the excluded categories so we can prevent those pets to be added
        List<String> excludedIds = getConfig().getStringList("ExcludedCategories");
        List<Category> excludedCategories = Category.getCategories().stream()
                                                                    .filter(cat -> excludedIds.contains(cat.getId()))
                                                                    .collect(Collectors.toList());

        // Handle the pets within that category
        // if it's a default category, all pets will be added
        if(getConfig().getBoolean("DefaultCategory"))
        {
            for(Pet pet : Pet.getObjectPets())
            {
                // Exclude the pets that are present in the excluded categories
                if(excludedCategories.stream().noneMatch(cat -> cat.getPets().stream().anyMatch(p -> p.getId().equals(pet.getId()))))
                    category.addPet(pet);
            }
            category.countMaxPages();
        }
        // Else if it's not default category, look for the specified pets
        else if (getConfig().get("Pets") != null)
        {
            for(String id : getConfig().getStringList("Pets"))
            {
                Pet pet = Pet.getFromId(id);
                if(pet != null &&
                        excludedCategories.stream().noneMatch(cat -> cat.getPets().stream().anyMatch(p -> p.getId().equals(pet.getId()))))
                    category.addPet(pet);
            }
            category.countMaxPages();
        }
        if (getConfig().get("Icon") != null)
        {
            category.setIcon((getConfig().getItemStack("Icon")));
        }

    }

    private ItemStack setupUnkownIcon()
    {
        ItemStack it = Items.UNKNOWN.getItem();
        ItemMeta meta = it.getItemMeta();
        meta.setLocalizedName("MCPets;" + id);
        meta.setDisplayName("§6" + id);
        it.setItemMeta(meta);
        return it;
    }

    /**
     * Load all the existing categories
     *
     * @param folderPath : folder where to seek for the pets
     * @param clear  : whether or not the loaded ones should be cleared (only first call should do that)
     */
    public static void load(String folderPath, boolean clear) {
        if (clear) {
            Category.getCategories().clear();
        }

        File folder = new File(folderPath);
        if (!folder.exists())
            folder.mkdirs();

        // First hand load to load up the categories content
        // Then we perform a secondary load to filter out the excluded categories
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                load(file.getPath().replace("\\", "/"), false);
                continue;
            }

            CategoryConfig config = new CategoryConfig(folder.getPath().replace("\\", "/").replace(AbstractConfig.getPath(), ""), file.getName());

            if (config.getCategory() != null)
                Category.add(config.getCategory());
        }

        // Secondary load to filter out the excluded categories content
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                load(file.getPath().replace("\\", "/"), false);
                continue;
            }

            CategoryConfig config = new CategoryConfig(folder.getPath().replace("\\", "/").replace(AbstractConfig.getPath(), ""), file.getName());

            if (config.getCategory() != null)
            {
                Category.getCategories().removeAll(Category.getCategories().stream().filter(cat -> config.getCategory().getId().equals(cat.getId())).collect(Collectors.toList()));
                Category.add(config.getCategory());
            }
        }


        Category.getCategories().sort(new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        if (clear)
            MCPets.getLog().info(MCPets.getLogName() + Category.getCategories().size() + " categories registered successfully !");
    }

}
