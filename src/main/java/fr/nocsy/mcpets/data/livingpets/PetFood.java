package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.utils.PetMath;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PetFood {

    private static final HashMap<String, PetFood> petFoodHashMap = new HashMap<>();

    @Getter
    private String id;

    @Getter
    private String itemId;

    @Getter
    private double power;

    @Getter
    private PetFoodType type;

    @Getter
    private PetMath operator;

    @Getter
    private String signal;

    @Getter
    private List<String> petIds;

    @Getter
    private boolean defaultMCItem;

    private ItemStack itemStack;

    /**
     * Constructor
     * @param itemId
     * @param power
     * @param operator
     */
    public PetFood(String id, String itemId, double power, PetFoodType type, PetMath operator, String signal, List<String> petIds)
    {
        this.id = id;
        this.itemId = itemId;
        this.power = power;
        this.type = type;
        this.operator = operator;
        this.signal = signal;
        this.petIds = petIds;

        // Setup the item stack
        this.getItemStack();

        petFoodHashMap.put(id, this);
    }

    /**
     * Get the item stack of the pet food
     * @return
     */
    public ItemStack getItemStack()
    {
        // Setup the item stack
        if(itemStack == null)
        {
            // Fetch the item stack within the registered ones
            itemStack = ItemsListConfig.getInstance().getItemStack(itemId);

            // If no item stack matches, then we'll see if it's a default MC material
            if(itemStack == null)
            {
                // Checking MC materials
                Material material = Arrays.stream(Material.values()).filter(mat -> mat.name().equalsIgnoreCase(itemId)).findFirst().orElse(null);
                if(material != null && !material.isAir())
                {
                    itemStack = new ItemStack(material);
                    defaultMCItem = true;
                    // return the itemstack without adding localized information to it, coz it's a default item
                    return itemStack;
                }
                // We found no match, so we set the item to unknown
                itemStack = Items.UNKNOWN.getItem();
            }
            ItemMeta meta = itemStack.getItemMeta();
            meta.setLocalizedName("MCPets;Food;" + itemId);
            itemStack.setItemMeta(meta);
        }

        // Item Stack already setup
        return itemStack;
    }

    /**
     * Say whether this food is compatible with the given pet
     * If the food doesn't state any pet ids, then the food is compatible with any pet
     * @param pet
     * @return
     */
    public boolean isCompatibleWithPet(Pet pet)
    {
        if(pet == null)
            return false;
        if(petIds == null || petIds.isEmpty())
            return true;
        return petIds.contains(pet.getId());
    }

    /**
     * Give the food to the pet
     * @param pet
     * @return value stating whether or not the food could be applied
     */
    public boolean apply(Pet pet)
    {
        if(type == null)
            return false;

        if(!isCompatibleWithPet(pet))
            return false;

        if (type.getType().equals(PetFoodType.HEALTH.getType()))
        {
            if(pet.getPetStats() != null)
            {
                pet.getPetStats().setHealth(operator.get(pet.getPetStats().getCurrentHealth(), power));
                pet.sendSignal(signal);
                return true;
            }
        }
        else if(type.getType().equals(PetFoodType.TAME.getType()))
        {
            pet.setTamingProgress(operator.get(pet.getTamingProgress(), power));
            pet.sendSignal(signal);
            return true;
        }
        else if(type.getType().equals(PetFoodType.EXPERIENCE.getType()))
        {
            if(pet.getPetStats() != null)
            {
                pet.getPetStats().addExperience(power);
                pet.sendSignal(signal);
                return true;
            }
        }
        return false;
    }

    /**
     * Consume the pet food in the main hand of the player
     * @param p
     */
    public void consume(Player p)
    {
        if(p == null)
            return;

        ItemStack it = itemStack.clone();
        it.setAmount(p.getInventory().getItemInMainHand().getAmount()-1);
        if(it.getAmount() == 0)
            it = new ItemStack(Material.AIR);
        p.getInventory().setItemInMainHand(it);
    }

    /**
     * Get the pet food object from the item stack if it represents one
     * @param it
     * @return
     */
    public static PetFood getFromItem(ItemStack it)
    {
        if(it == null)
            return null;
        // if the item is a default MC item, then look for possible matches
        if(!it.hasItemMeta() || !it.getItemMeta().hasLocalizedName())
        {
            return PetFoodConfig.getInstance().list().stream()
                                .filter(petFood -> petFood.isDefaultMCItem()
                                                && petFood.getItemStack() != null
                                                && petFood.getItemStack().getType().equals(it.getType()))
                                .findFirst()
                                .orElse(null);
        }
        // if the item isn't a default MCItem, go through the localized informations
        return PetFoodConfig.getInstance().list().stream()
                                .filter(petFood -> petFood.getItemStack() != null
                                        && petFood.getItemStack().getItemMeta().getLocalizedName().equals(it.getItemMeta().getLocalizedName()))
                                .findFirst()
                                .orElse(null);
    }

    /**
     * Get the PetFood from the id
     * @param id
     * @return
     */
    public static PetFood getFromId(String id)
    {
        return petFoodHashMap.get(id);
    }


}
