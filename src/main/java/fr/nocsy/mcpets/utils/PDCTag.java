package fr.nocsy.mcpets.utils;

import fr.nocsy.mcpets.MCPets;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public final class PDCTag {

    private static NamespacedKey key;

    private PDCTag() {}

    public static NamespacedKey key() {
        if (key == null)
            key = new NamespacedKey(MCPets.getInstance(), "mcpets_tag");
        return key;
    }

    /**
     * Write a tag value into the item's PDC.
     * Pass null to remove the tag.
     */
    public static void set(ItemMeta meta, @Nullable String value) {
        if (value == null) {
            meta.getPersistentDataContainer().remove(key());
        } else {
            meta.getPersistentDataContainer().set(key(), PersistentDataType.STRING, value);
        }
    }

    /**
     * Read the tag from PDC first; if absent, fall back to
     * legacy getItemName() for backward compatibility.
     */
    @Nullable
    public static String get(ItemMeta meta) {
        String val = meta.getPersistentDataContainer().get(key(), PersistentDataType.STRING);
        if (val != null)
            return val;
        if (meta.hasItemName())
            return meta.getItemName();
        return null;
    }

    /**
     * Returns true if the item carries an MCPets tag
     * (either in PDC or in the legacy itemName).
     */
    public static boolean has(ItemMeta meta) {
        return meta.getPersistentDataContainer().has(key(), PersistentDataType.STRING)
                || meta.hasItemName();
    }
}
