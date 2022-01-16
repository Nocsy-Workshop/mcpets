package fr.nocsy.mcpets.data;

import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public enum Items {

    MOUNT("mount"),
    RENAME("rename"),
    PETMENU("petmenu");

    @Getter
    private ItemStack item;

    Items(String name)
    {
        switch (name)
        {
            case "mount":
                item = mount();
                break;
            case "rename":
                item = rename();
                break;
            case "petmenu":
                item = backToPets();
                break;
        }
    }

    private static ItemStack mount()
    {
        ItemStack it = new ItemStack(Material.SADDLE);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(Language.MOUNT_ITEM_NAME.getMessage());

        ArrayList<String> lore = new ArrayList<>(Arrays.asList(Language.MOUNT_ITEM_DESCRIPTION.getMessage().split("\n")));
        meta.setLore(lore);

        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack rename()
    {
        ItemStack it = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(Language.RENAME_ITEM_NAME.getMessage());

        ArrayList<String> lore = new ArrayList<>(Arrays.asList(Language.RENAME_ITEM_DESCRIPTION.getMessage().split("\n")));
        meta.setLore(lore);

        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack backToPets()
    {
        ArrayList<String> lore = new ArrayList<>(Arrays.asList(Language.BACK_TO_PETMENU_ITEM_DESCRIPTION.getMessage().split("\n")));

        ItemStack it = Utils.createHead(Language.BACK_TO_PETMENU_ITEM_NAME.getMessage(), lore, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTI5M2E2MDcwNTAzMTcyMDcxZjM1ZjU4YzgyMjA0ZTgxOGNkMDY1MTg2OTAxY2ExOWY3ZGFkYmRhYzE2NWU0NCJ9fX0=");
        ItemMeta meta = it.getItemMeta();
        meta.setLocalizedName("AlmPet;BackToPetMenu");

        it.setItemMeta(meta);

        return it;
    }

    public static ItemStack page(int index)
    {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(Language.TURNPAGE_ITEM_NAME.getMessage());

        meta.setLocalizedName("AlmPetPage;" + index);

        ArrayList<String> lore = new ArrayList<>(Arrays.asList(Language.TURNPAGE_ITEM_DESCRIPTION.getMessage().split("\n")));
        meta.setLore(lore);

        it.setItemMeta(meta);
        return it;
    }

    public static ItemStack petInfo(Pet pet)
    {
        Pet objectPet = Pet.getFromId(pet.getId());

        ItemStack it = objectPet.getIcon().clone();
        ItemMeta meta = it.getItemMeta();

        ArrayList<String> lore = new ArrayList<>(meta.getLore());

        if(pet.getCurrentName() != null)
        {
            lore.add(" ");
            lore.add(Language.NICKNAME.getMessageFormatted(new FormatArg("%nickname%", pet.getCurrentName())));
            lore.add(" ");
        }

        lore.addAll(Arrays.asList(Language.NICKNAME_ITEM_LORE.getMessage().split("\n")));

        meta.setLore(lore);
        meta.setLocalizedName(null);
        it.setItemMeta(meta);
        return it;
    }

    public static ItemStack deco(Material mat)
    {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName("§0");

        ArrayList<String> lore = new ArrayList<>();
        meta.setLore(lore);

        it.setItemMeta(meta);

        return it;
    }

    public static boolean isSignalStick(ItemStack it)
    {
        return it != null && it.hasItemMeta() && it.getItemMeta().hasLocalizedName() && it.getItemMeta().getLocalizedName().equals(Pet.SIGNAL_STICK_TAG);
    }

}
