package fr.nocsy.mcpets.data.config;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import lombok.Getter;

import fr.nocsy.mcpets.data.Items;

import org.bukkit.inventory.ItemStack;

public class ItemsListConfig extends AbstractConfig {

    private static ItemsListConfig instance;

    @Getter
    private Map<String, ItemStack> items;

    private ItemsListConfig() {
        items = new HashMap<>();
    }

    public static ItemsListConfig getInstance() {
        if (instance == null) instance = new ItemsListConfig();
        return instance;
    }

    public static void reloadInstance() {
        instance = new ItemsListConfig();
        instance.init();
    }

    public void init() {
        super.init("", "menuIcons.yml");

        if (getConfig().get("mount") == null)
            getConfig().set("mount", Items.MOUNT.getItem());
        if (getConfig().get("rename") == null)
            getConfig().set("rename", Items.RENAME.getItem());
        if (getConfig().get("inventory") == null)
            getConfig().set("inventory", Items.INVENTORY.getItem());
        if (getConfig().get("petmenu") == null)
            getConfig().set("petmenu", Items.PETMENU.getItem());
        if (getConfig().get("mountmenu") == null)
            getConfig().set("mountmenu", Items.MOUNTMENU.getItem());
        if (getConfig().get("skins") == null)
            getConfig().set("skins", Items.SKINS.getItem());
        if (getConfig().get("equipment") == null)
            getConfig().set("equipment", Items.EQUIPMENT.getItem());
        if (getConfig().get("previous_page_selector") == null)
            getConfig().set("previous_page_selector", Items.PREVIOUS_PAGE_SELECTOR.getItem());
        if (getConfig().get("next_page_selector") == null)
            getConfig().set("next_page_selector", Items.NEXT_PAGE_SELECTOR.getItem());

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

        for (String key : getConfig().getKeys(false)) {
            if (getConfig().getItemStack(key) == null) continue;
            items.put(key, getConfig().getItemStack(key));
        }
    }

    /**
     * Return the item with the said key
     * null if it doesn't exist
     */
    public ItemStack getItemStack(String key) {
        return items.get(key);
    }

    public void setItemStack(String key, ItemStack itemStack) {
        items.put(key, itemStack);
        getConfig().set(key, itemStack);
        save();
    }

    public void removeItemStack(String key) {
        items.remove(key);
        getConfig().set(key, null);
        save();
    }

    public static ItemStack loadConfigItem(String itemId) {
        ItemsListConfig config = new ItemsListConfig();
        config.init();
        return config.getItems().get(itemId);
    }

    public Set<String> listKeys() {
        return items.keySet();
    }

}
