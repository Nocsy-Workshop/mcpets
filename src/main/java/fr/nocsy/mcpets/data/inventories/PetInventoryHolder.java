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

    public PetInventoryHolder(int size, @NotNull Type type) {
        this.inventory = Bukkit.createInventory(this, size);
        this.type = type;
    }

    public PetInventoryHolder(int size, @NotNull String title, @NotNull Type type) {
        this.inventory = Bukkit.createInventory(this, size, title);
        this.type = type;
    }

    public PetInventoryHolder(InventoryType inventoryType, @NotNull String title, @NotNull Type type) {
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
        EDITOR_MENU,
        PET_INTERACTION_MENU,
        PET_INVENTORY_MENU,
        PET_MENU
    }
}
