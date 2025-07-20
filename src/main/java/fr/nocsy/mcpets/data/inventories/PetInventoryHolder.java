package fr.nocsy.mcpets.data.inventories;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class PetInventoryHolder implements InventoryHolder {
    private final Inventory inventory;

    public PetInventoryHolder(int size) {
        this.inventory = Bukkit.createInventory(this, size);
    }

    public PetInventoryHolder(int size, @NotNull String title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public PetInventoryHolder(InventoryType inventoryType, @NotNull String title) {
        this.inventory = Bukkit.createInventory(this, inventoryType, title);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
