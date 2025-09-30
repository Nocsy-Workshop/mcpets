package fr.nocsy.mcpets.data.inventories;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class PetInventoryHolder implements InventoryHolder {
    private final Inventory inventory;
    @Getter
    private final Type type;

    public PetInventoryHolder(final int size, @NotNull final Type type) {
        this.inventory = Bukkit.createInventory(this, size);
        this.type = type;
    }

    public PetInventoryHolder(final int size, @NotNull final String title, @NotNull final Type type) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.type = type;
    }

    public PetInventoryHolder(final InventoryType inventoryType, @NotNull final String title, @NotNull final Type type) {
        this.inventory = Bukkit.createInventory(this, inventoryType, title);
        this.type = type;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public enum Type {
        CATEGORIES_MENU,
        CATEGORY_MENU,
        EDITOR_MENU,
        PET_INTERACTION_MENU,
        PET_INVENTORY_MENU,
        PET_MENU,
        PET_SKINS_MENU,
    }
}
