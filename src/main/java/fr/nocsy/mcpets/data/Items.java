package fr.nocsy.mcpets.data;

import java.util.List;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.ItemsListConfig;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public enum Items {

    UNKNOWN("unkown"),

    MOUNT("mount"),
    RENAME("rename"),
    PETMENU("petmenu"),
    MOUNTMENU("mountmenu"),
    INVENTORY("inventory"),
    SKINS("skins"),
    EQUIPMENT("equipment"),
    PAGE_SELECTOR("page_selector");

    @Setter
    @Getter
    private ItemStack item;

    @Getter
    private String name;

    Items(String name) {
        this.name = name;
        if (ItemsListConfig.getInstance().getItemStack(name) != null) {
            item = ItemsListConfig.getInstance().getItemStack(name);
            prepareItem();
            return;
        }

        switch (name) {
            case "mount":
                item = mount();
                break;
            case "rename":
                item = rename();
                break;
            case "petmenu":
                item = petmenu();
                break;
            case "mountmenu":
                item = mountmenu();
                break;
            case "inventory":
                item = inventory();
                break;
            case "skins":
                item = skins();
                break;
            case "equipment":
                item = equipment();
                break;
            case "page_selector":
                item = page_item();
                break;
            default:
                item = unknown();
        }

        prepareItem();
    }

    private void prepareItem() {
        ItemMeta meta = item.getItemMeta();
        PDCTag.set(meta, getLocalizedName());
        item.setItemMeta(meta);
    }

    public String getLocalizedName() {
        return "MCPetsMenu;" + name;
    }

    private static ItemStack unknown() {

        ItemStack it = new ItemStack(Material.BEDROCK);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Utils.toComponent("Unknown"));
        meta.lore(new ArrayList<>());
        PDCTag.set(meta, "AlmPet;Unknown");

        it.setItemMeta(meta);

        return it;
    }

    private static ItemStack mount() {
        ItemStack it = new ItemStack(Material.SADDLE);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.MOUNT_ITEM_NAME.getComponent());

        meta.lore(
                Utils.toComponents(
                        Language.MOUNT_ITEM_DESCRIPTION.getMessage()
                )
        );;

        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack rename() {
        ItemStack it = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.RENAME_ITEM_NAME.getComponent());

        meta.lore(
                Utils.toComponents(
                        Language.RENAME_ITEM_DESCRIPTION.getMessage()
                )
        );

        it.setItemMeta(meta);
        return it;
    }

    private static ItemStack petmenu() {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.BACK_TO_PETMENU_ITEM_NAME.getComponent());
        meta.lore(
                Utils.toComponents(
                        Language.BACK_TO_PETMENU_ITEM_DESCRIPTION.getMessage()
        ));
        PDCTag.set(meta, "AlmPet;BackToPetMenu");

        it.setItemMeta(meta);

        return it;
    }

    private static ItemStack mountmenu() {
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.BACK_TO_PETMENU_ITEM_NAME.getComponent());
        meta.lore(
                Utils.toComponents(
                        Language.BACK_TO_PETMENU_ITEM_DESCRIPTION.getMessage()
                )
        );
        PDCTag.set(meta, "AlmPet;BackToMountMenu");

        it.setItemMeta(meta);

        return it;
    }

    private static ItemStack inventory() {
        ItemStack it = new ItemStack(Material.CHEST);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.INVENTORY_ITEM_NAME.getComponent());
        PDCTag.set(meta, "AlmPet;Inventory");

        it.setItemMeta(meta);

        return it;
    }

    private static ItemStack skins() {
        ItemStack it = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.SKINS_ITEM_NAME.getComponent());
        PDCTag.set(meta, "AlmPet;Skins");

        it.setItemMeta(meta);

        return it;
    }

    private static ItemStack equipment() {
        ItemStack it = new ItemStack(Material.LEATHER_HORSE_ARMOR);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.EQUIPMENT_ITEM_NAME.getComponent());
        PDCTag.set(meta, "AlmPet;Inventory");

        it.setItemMeta(meta);

        return it;
    }

    private static ItemStack page_item(){
        ItemStack it = new ItemStack(Material.PAPER);
        ItemMeta meta = it.getItemMeta();
        meta.setCustomModelData(9660);
        it.setItemMeta(meta);
        return it;
    }

    public static ItemStack page(int index, Player p) {
        ItemStack it = ItemsListConfig.getInstance().getItemStack("page_selector");
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.TURNPAGE_ITEM_NAME.getComponentFormatted(
                new FormatArg("%currentPage%", Integer.toString(index + 1)),
                new FormatArg("%maxPage%", Integer.toString((int) ((double) Pet.getAvailablePets(p).size() / 54 + 0.5)))));

        PDCTag.set(meta, "AlmPetPage;" + index);

        meta.lore(Utils.toComponents(Language.TURNPAGE_ITEM_DESCRIPTION.getMessage()));

        it.setItemMeta(meta);
        return it;
    }

    public static ItemStack page(Category category, int index) {
        ItemStack it = ItemsListConfig.getInstance().getItemStack("page_selector");
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Language.TURNPAGE_ITEM_NAME.getComponentFormatted(new FormatArg("%currentPage%", Integer.toString(index + 1)),
                                                                            new FormatArg("%maxPage%", Integer.toString(category.getMaxPages()))));
        PDCTag.set(meta, "MCPetsPage;" + category.getId() + ";" + index);

        meta.lore(
                Utils.toComponents(
                        Language.TURNPAGE_ITEM_DESCRIPTION.getMessageFormatted(
                                new FormatArg("%currentPage%", Integer.toString(index)),
                                new FormatArg("%maxPage%", Integer.toString(category.getMaxPages()))
                        )
                )
        );

        it.setItemMeta(meta);
        return it;
    }

    public static ItemStack petInfo(Pet pet) {
        Pet objectPet = Pet.getFromId(pet.getId());

        ItemStack it = objectPet.getIcon().clone();
        ItemMeta meta = it.getItemMeta();

        List<Component> lore;
        if (!meta.hasLore()) lore = new ArrayList<>();
        else lore = new ArrayList<>(meta.lore());

        if (pet.getCurrentName() != null && GlobalConfig.getInstance().isNameable()) {
            lore.add(Component.empty());
            lore.add(Language.NICKNAME.getComponentFormatted(new FormatArg("%nickname%", pet.getCurrentName())));
            lore.add(Component.empty());
        }

        lore.addAll(Utils.toComponents(Language.NICKNAME_ITEM_LORE.getMessage()));

        meta.lore(lore);
        PDCTag.set(meta, null);
        it.setItemMeta(meta);
        return it;
    }

    public static ItemStack deco(Material mat) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Component.empty().color(NamedTextColor.BLACK));

        List<Component> lore = new ArrayList<>();
        meta.lore(lore);

        it.setItemMeta(meta);

        return it;
    }

    public static boolean isSignalStick(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return false;

        final String tag = PDCTag.get(it.getItemMeta());
        return tag != null && tag.contains(Pet.SIGNAL_STICK_TAG);
    }

    public static ItemStack turnIntoSignalStick(ItemStack it, Pet pet) {
        if (it == null || it.getType().isAir() || pet == null) return it;

        ItemMeta meta = it.getItemMeta();
        PDCTag.set(meta, buildSignalStickTag(pet));
        it.setItemMeta(meta);
        return it;
    }

    public static String buildSignalStickTag(Pet pet) {
        if (pet == null) return null;

        return Pet.SIGNAL_STICK_TAG + ";" + pet.getId();
    }

    public static String getPetTag(ItemStack it) {
        if (it != null && it.hasItemMeta()) {
            final String tag = PDCTag.get(it.getItemMeta());
            if (tag != null) {
                final String[] split = tag.split(";");
                if (split.length == 2)
                    return split[1];
            }
        }

        return null;
    }

}
