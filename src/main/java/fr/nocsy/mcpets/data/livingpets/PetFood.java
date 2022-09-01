package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PetFood {

    @Getter
    private String itemId;

    @Getter
    private double power;

    @Getter
    private PetFoodType type;

    @Getter
    private PetMath operator;

    private ItemStack itemStack;

    /**
     * Constructor
     * @param itemId
     * @param power
     * @param operator
     */
    public PetFood(String itemId, double power, PetFoodType type, PetMath operator)
    {
        this.itemId = itemId;
        this.power = power;
        this.type = type;
        this.operator = operator;

        // Setup the item stack
        this.getItemStack();
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
        if (type.equals(PetFoodType.HEALTH))
        {
            if(pet.getPetStats() != null)
            {
                pet.getPetStats().setHealth(operator.get(pet.getPetStats().getCurrentHealth(), power));
            }
        }
        else if(type.equals(PetFoodType.TAME))
        {

        }
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



}
