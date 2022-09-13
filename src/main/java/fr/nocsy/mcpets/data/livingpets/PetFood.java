package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.utils.PetMath;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class PetFood {

    private static HashMap<String, PetFood> petFoodHashMap = new HashMap<>();

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

    private ItemStack itemStack;

    /**
     * Constructor
     * @param itemId
     * @param power
     * @param operator
     */
    public PetFood(String id, String itemId, double power, PetFoodType type, PetMath operator, String signal)
    {
        this.id = id;
        this.itemId = itemId;
        this.power = power;
        this.type = type;
        this.operator = operator;
        this.signal = signal;

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
            itemStack = ItemsListConfig.getInstance().getItemStack(itemId);
            if(itemStack == null)
                itemStack = Items.UNKNOWN.getItem();
            ItemMeta meta = itemStack.getItemMeta();
            meta.setLocalizedName("MCPets;Food;" + itemId);
            itemStack.setItemMeta(meta);
        }

        // Item Stack already setup
        return itemStack;
    }

    /**
     * Give the food to the pet
     * @param pet
     */
    public void apply(Pet pet)
    {
        if(type == null)
            return;
        if (type.getType().equals(PetFoodType.HEALTH.getType()))
        {
            if(pet.getPetStats() != null)
            {
                pet.getPetStats().setHealth(operator.get(pet.getPetStats().getCurrentHealth(), power));
                pet.sendSignal(signal);
            }
        }
        else if(type.getType().equals(PetFoodType.TAME.getType()))
        {
            pet.setTamingProgress(operator.get(pet.getTamingProgress(), power));
            pet.sendSignal(signal);
        }
        else if(type.getType().equals(PetFoodType.EXPERIENCE.getType()))
        {
            if(pet.getPetStats() != null)
            {
                pet.getPetStats().addExperience(power);
                pet.sendSignal(signal);
            }
        }
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
        if(it == null || !it.hasItemMeta() || !it.getItemMeta().hasLocalizedName())
            return null;
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
