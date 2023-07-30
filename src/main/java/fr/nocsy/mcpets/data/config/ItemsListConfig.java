package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Set;

public class ItemsListConfig extends AbstractConfig {

    private static ItemsListConfig instance;

    @Getter
    private HashMap<String, ItemStack> items;

    private ItemsListConfig()
    {
        items = new HashMap<>();
    }

    public static ItemsListConfig getInstance() {
        if (instance == null)
            instance = new ItemsListConfig();
        return instance;
    }

    public static void reloadInstance()
    {
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
        if (getConfig().get("skins") == null)
            getConfig().set("skins", Items.SKINS.getItem());
        if (getConfig().get("equipment") == null)
            getConfig().set("equipment", Items.EQUIPMENT.getItem());
        if (getConfig().get("page_selector") == null)
            getConfig().set("page_selector", Items.PAGE_SELECTOR.getItem());

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

        for(String key : getConfig().getKeys(false))
        {
            if(getConfig().getItemStack(key) != null)
                items.put(key, getConfig().getItemStack(key));
        }

    }

    /**
     * Return the item with the said key
     * null if it doesn't exist
     * @param key
     * @return
     */
    public ItemStack getItemStack(String key)
    {
        return items.get(key);
    }

    public void setItemStack(String key, ItemStack itemStack)
    {
        items.put(key, itemStack);
        getConfig().set(key, itemStack);
        save();
    }

    public void removeItemStack(String key)
    {
        items.remove(key);
        getConfig().set(key, null);
        save();
    }

    public static ItemStack loadConfigItem(String itemId)
    {
        ItemsListConfig config = new ItemsListConfig();
        config.init();
        return config.getItems().get(itemId);
    }

    public Set<String> listKeys()
    {
        return items.keySet();
    }

}
