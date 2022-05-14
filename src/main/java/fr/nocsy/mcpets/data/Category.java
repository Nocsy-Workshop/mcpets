package fr.nocsy.mcpets.data;

import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Category {

    @Getter
    private static ArrayList<Category> categories;


    @Getter
    private String id;

    @Getter
    @Setter
    private String displayName;
    @Getter
    private ItemStack icon;
    @Getter
    private ArrayList<Pet> pets;

    public Category(String id)
    {
        this.id = id;
        this.icon = null;
        this.pets = new ArrayList<>();
        this.displayName = "Unknown";
    }

    public static void init()
    {

    }

    public void addPet(Pet pet)
    {
        pets.add(pet);
    }

    public void setIcon(ItemStack it)
    {
        icon = it;
        setupLore();
    }


    private void setupLore()
    {
        ItemMeta meta = icon.getItemMeta();
        ArrayList<String> lore = (ArrayList<String>) meta.getLore() != null
                ? (ArrayList<String>) meta.getLore()
                : new ArrayList<>();

        lore.add(" ");
        lore.add(Language.CATEGORY_PET_AMOUNT.getMessageFormatted(new FormatArg("petamount", Integer.toString(pets.size()))));
        meta.setLore(lore);
        icon.setItemMeta(meta);
    }

    public static void add(Category category)
    {
        categories.add(category);
    }

}
