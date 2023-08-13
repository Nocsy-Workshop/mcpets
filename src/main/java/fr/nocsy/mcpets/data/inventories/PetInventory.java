package fr.nocsy.mcpets.data.inventories;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.data.sql.PlayerDataNoDatabase;
import fr.nocsy.mcpets.utils.BukkitSerialization;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PetInventory {

    @Getter
    private static HashMap<UUID, HashMap<String, PetInventory>> petInventories = new HashMap<>();

    @Getter
    private Inventory inventory;

    private final Pet pet;

    /**
     * Constructor
     * Inventory can either be forced or created automatically using null
     * @param pet
     * @param premadeInventory
     */
    private PetInventory(Pet pet, @Nullable Inventory premadeInventory, UUID owner)
    {
        if(pet == null)
            throw new NullPointerException("Pet can not be null.");
        this.pet = pet;
        this.pet.setOwner(owner);

        String title = Language.PET_INVENTORY_TITLE.getMessageFormatted(new FormatArg("%pet%", pet.getIcon().getItemMeta().getDisplayName()));

        this.inventory = Bukkit.createInventory(null, pet.getInventorySize(), title);
        if(premadeInventory != null)
        {
            if(premadeInventory.getContents().length <= inventory.getContents().length)
                inventory.setContents(premadeInventory.getContents());
            else
            {
                for(ItemStack it : premadeInventory.getContents())
                {
                    if(it != null)
                        inventory.addItem(it);
                }
            }
        }

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
        if(pet.getInventorySize() <= 0)
            return null;
        HashMap<String, PetInventory> registeredMap = petInventories.get(pet.getOwner());
        if(registeredMap != null
                && registeredMap.get(pet.getId()) != null
                && registeredMap.get(pet.getId()).getInventory().getSize() == pet.getInventorySize())
        {
            return registeredMap.get(pet.getId());
        }

        Inventory inv = null;
        if(registeredMap != null && registeredMap.get(pet.getId()) != null)
        {
            inv = registeredMap.get(pet.getId()).getInventory();
        }
        return new PetInventory(pet, inv, pet.getOwner());
    }

    public void setInventory(Inventory inventory)
    {
        this.inventory = inventory;
        PlayerData pd = PlayerData.get(pet.getOwner());
        pd.setPetInventory(this);
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

        if(pet.getInventorySize() <= 0)
            return null;

        pet.setOwner(owner);
        String serializedInventory = data[1];
        try
        {
            Inventory inventory = unserializeInventory(serializedInventory);
            return new PetInventory(pet, inventory, owner);
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
        new BukkitRunnable() {
            @Override
            public void run() {
                p.openInventory(inventory);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        p.setMetadata("MCPets;petInventory", new FixedMetadataValue(MCPets.getInstance(), pet.getId()));
                    }
                }.runTaskLater(MCPets.getInstance(), 2L);
            }
        }.runTaskLater(MCPets.getInstance(), 2L);
    }

    /**
     * Close the inventory of the said player, removes the metadata
     * and save the inventory in the DB
     * @param p
     */
    public void close(Player p)
    {
        p.setMetadata("MCPets;petInventory", new FixedMetadataValue(MCPets.getInstance(), null));
        new Thread(new Runnable() {
            public void run() {
                if(!GlobalConfig.getInstance().isDatabaseSupport())
                    PlayerDataNoDatabase.get(p.getUniqueId()).save();
                else
                    PlayerData.saveDB();
            }
        }).start();
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

    /**
     * Get the pet ID
     * @return
     */
    public String getPetId()
    {
        return pet.getId();
    }

}
