package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.BukkitSerialization;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PetInventory {

    @Getter
    private static HashMap<UUID, HashMap<String, PetInventory>> petInventories = new HashMap<>();

    @Setter
    private Inventory inventory;

    private final Pet pet;

    /**
     * Constructor
     * Inventory can either be forced or created automatically using null
     * @param pet
     * @param premadeInventory
     */
    private PetInventory(Pet pet, @Nullable Inventory premadeInventory)
    {
        if(pet == null)
            throw new NullPointerException("Pet can not be null.");
        this.pet = pet;
        String title = Language.PET_INVENTORY_TITLE.getMessageFormatted(new FormatArg("pet", pet.getIcon().getItemMeta().getDisplayName()));
        if(premadeInventory == null)
            this.inventory = Bukkit.createInventory(null, pet.getInventorySize(), title);
        else
            this.inventory = premadeInventory;

        HashMap<String, PetInventory> builtIn = petInventories.get(pet.getOwner());
        if(builtIn == null)
        {
            HashMap<String, PetInventory> map = new HashMap<>();
            map.put(pet.getId(), this);
            petInventories.put(pet.getOwner(), map);
        }
        else
        {
            builtIn.put(pet.getId(), this);
            petInventories.put(pet.getOwner(), builtIn);
        }
    }

    /**
     * Get the corresponding pet inventory of the said pet
     * null if the pet has no owner
     * @param pet
     * @return
     */
    public static PetInventory get(Pet pet)
    {
        if(pet.getOwner() == null)
            return null;
        HashMap<String, PetInventory> registeredMap = petInventories.get(pet.getOwner());
        if(registeredMap != null)
        {
            PetInventory instance = registeredMap.get(pet.getId());
            return instance;
        }
        return new PetInventory(pet, null);
    }

    /**
     * Unserialize the pet inventory from the DB
     * associate it to the said owner
     * @param serialized
     * @param owner
     * @return
     */
    public static PetInventory unserialize(String serialized, @NotNull UUID owner) {
        String[] data = serialized.split(";");
        if(data.length != 2)
            throw new IllegalArgumentException("Serialized doesn't match the data format : " + serialized);
        String petId = data[0];

        Pet pet = Pet.getFromId(petId);
        if(pet == null)
            return null;

        pet.setOwner(owner);
        String serializedInventory = data[1];
        try
        {
            Inventory inventory = unserializeInventory(serializedInventory);
            return new PetInventory(pet, inventory);
        }
        catch(IOException ex)
        {
            return null;
        }

    }

    /**
     * Serialize the pet inventory formatted for the DB
     * @return
     */
    public String serialize()
    {
        return serializeInventory();
    }

    /**
     * Open the inventory to the said player
     * and add the tracing metadata
     * @param p
     */
    public void open(Player p)
    {
        p.openInventory(this.inventory);
        p.setMetadata("MCPets;petInventory", new FixedMetadataValue(MCPets.getInstance(), this.pet.getId()));
    }

    /**
     * Close the inventory of the said player, removes the metadata
     * and save the inventory in the DB
     * @param p
     */
    public void close(Player p)
    {
        p.closeInventory();
        p.setMetadata("MCPets;petInventory", new FixedMetadataValue(MCPets.getInstance(), null));
        PlayerData.saveDB();
    }

    /**
     * Get the PetInventory related to the player looking at
     * a certain inventory
     * @param p
     * @return
     */
    public static PetInventory fromCurrentView(Player p)
    {
        if(p.hasMetadata("MCPets;petInventory"))
        {
            if(p.getMetadata("MCPets;petInventory").size() > 0 &&
                p.getMetadata("MCPets;petInventory").get(0) != null &&
                p.getMetadata("MCPets;petInventory").get(0).value() instanceof String)
            {
                String petId = (String)p.getMetadata("MCPets;petInventory").get(0).value();
                UUID owner = p.getUniqueId();
                HashMap<String, PetInventory> map = petInventories.get(owner);
                if(map != null)
                {
                    return map.get(petId);
                }
            }
        }
        return null;
    }

    /**
     * Serialize the inventory only
     * @return
     */
    private String serializeInventory()
    {
        return BukkitSerialization.toBase64(this.inventory);
    }

    /**
     * Unserialize the inventory only
     * @param serialized
     * @return
     * @throws IOException
     */
    private static Inventory unserializeInventory(String serialized) throws IOException {
        return BukkitSerialization.fromBase64(serialized);
    }

}
