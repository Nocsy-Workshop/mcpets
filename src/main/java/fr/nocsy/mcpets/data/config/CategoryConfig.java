package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Category;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.editor.EditorEditing;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CategoryConfig extends AbstractConfig {

    @Getter
    private static HashMap<String, CategoryConfig> mapping = new HashMap<>();

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
        if(getConfig().get("Id") == null)
            getConfig().set("Id", this.id);
        if (getConfig().get("DisplayName") == null)
            getConfig().set("DisplayName", "Category title");
        if (getConfig().get("IconName") == null)
            getConfig().set("IconName", "Category name");
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

        // Handle the title of the category
        if (getConfig().get("DisplayName") != null)
        {
            category.setDisplayName(getConfig().getString("DisplayName"));
        }
        // Handle the name of the category
        if (getConfig().get("IconName") != null)
        {
            category.setIconName(getConfig().getString("IconName"));
        }

        // Load the excluded categories so we can prevent those pets to be added
        List<String> excludedIds = getConfig().getStringList("ExcludedCategories");
        category.setExcludedCategoriesId(excludedIds);
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
            category.setDefaultCategory(true);
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
        ItemStack it = Items.UNKNOWN.getItem().clone();
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
                mapping.put(config.getCategory().getId(), config);
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

    public static void registerCleanCategory(Player creator)
    {

        String id = UUID.randomUUID().toString();
        String folder = AbstractConfig.getPath() + "Categories/";
        String fileName = id + ".yml";

        File file = new File(folder + fileName);
        if(!file.exists())
        {
            try
            {
                file.createNewFile();
            } catch (IOException ignored) {}
        }

        new CategoryConfig(folder, fileName);
        // reload the categories
        load(AbstractConfig.getPath() + "Categories/", true);

        // Associate the category the creator
        EditorEditing editing = EditorEditing.get(creator);
        editing.setMappedId(id);
    }

    /**
     * Loads a fresh config category and output a category object (for editor only)
     * @param id
     * @return
     */
    public static Category loadConfigCategory(String id)
    {
        CategoryConfig oldConfig = CategoryConfig.getMapping().get(id);
        CategoryConfig config = new CategoryConfig(oldConfig.getFolderName(), oldConfig.getFileName());
        return config.getCategory();
    }

    public void addPet(Pet pet)
    {
        List<String> pets = getConfig().getStringList("Pets");
        if(!pets.contains(pet.getId()))
            pets.add(pet.getId());
        getConfig().set("Pets", pets);
        save();
        reload();
    }

    public void removePet(Pet pet)
    {
        List<String> pets = getConfig().getStringList("Pets");
        pets.remove(pet.getId());
        getConfig().set("Pets", pets);
        save();
        reload();
    }

}
