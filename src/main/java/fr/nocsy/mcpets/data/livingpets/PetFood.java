package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.config.PetFoodConfig;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PetFood {

    private static final HashMap<String, PetFood> petFoodHashMap = new HashMap<>();


    //----------------- Generic item section ------------------//
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


    //----------------- Evolution item section ------------------//
    @Getter
    private String evolution;

    @Getter
    private int experienceThreshold;

    @Getter
    private int delay;


    //----------------- Unlock item section ------------------//
    @Getter
    private String permission;

    @Getter
    private String unlockedPet;


    /**
     * Constructor
     * @param itemId
     * @param power
     * @param operator
     */
    public PetFood(
            String id,
            String itemId,
            double power,
            PetFoodType type,
            PetMath operator,
            String signal,
            String evolution,
            int experienceThreshold,
            int delay,
            String permission,
            String unlockedPet,
            List<String> petIds
    )
    {
        this.id = id;
        this.itemId = itemId;
        this.power = power;
        this.type = type;
        this.operator = operator;
        this.signal = signal;
        this.evolution = evolution;
        this.experienceThreshold = experienceThreshold;
        this.delay = delay;
        this.permission = permission;
        this.unlockedPet = unlockedPet;
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
                itemStack = Items.UNKNOWN.getItem().clone();
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
     * Register an owner in the waiting list so that they don't spam an item
     * and loose it unintentionally
     */
    private ArrayList<UUID> waitingListApply = new ArrayList<>();
    public void registerWaitingList(UUID owner, long delay)
    {
        if(waitingListApply.contains(owner))
            return;
        waitingListApply.add(owner);
        new BukkitRunnable() {
            @Override
            public void run() {
                waitingListApply.remove(owner);
            }
        }.runTaskLater(MCPets.getInstance(), delay);
    }

    /**
     * Give the food to the pet
     * @param pet
     * @return value stating whether or not the food could be applied
     */
    public boolean apply(Pet pet, Player p)
    {
        if(pet == null)
            return false;

        if(waitingListApply.contains(pet.getOwner()))
            return false;

        if(type == null)
            return false;

        if(!isCompatibleWithPet(pet))
            return false;

        // says whether the petfood was triggered or not
        boolean triggered = false;

        Debugger.send("§7Applying pet food §6" + this.id + "§7 to §6" + pet.getId() + "§7 with type §a" + this.type.getType());
        if (type.getType().equals(PetFoodType.HEALTH.getType()))
        {
            if(pet.getPetStats() != null && pet.getPetStats().getCurrentHealth() < pet.getPetStats().getCurrentLevel().getMaxHealth())
            {
                pet.getPetStats().setHealth(operator.get(pet.getPetStats().getCurrentHealth(), power));
                triggered = true;
            }
            else
            {
                Debugger.send("§cCould not give HEALTH to the pet because it is already at maximum value.");
            }
        }
        else if(type.getType().equals(PetFoodType.TAME.getType()))
        {
            if(pet.getTamingProgress() != 1)
            {
                pet.setTamingProgress(operator.get(pet.getTamingProgress(), power));
                triggered = true;
            }
            else
            {
                Debugger.send("§cCould not give TAMING PROGRESS to the pet because it has reached maximum value.");
            }
        }
        else if(type.getType().equals(PetFoodType.EXP.getType()))
        {
            if(pet.getPetStats() != null)
            {
                triggered = pet.getPetStats().addExperience(power);
                if(!triggered)
                {
                    Debugger.send("§cCould not give EXP to the pet because it has reached maximum value.");
                }
            }
        }
        else if(type.getType().equals(PetFoodType.EVOLUTION.getType()))
        {
            Pet evolutionPet = Pet.getFromId(evolution);
            if(pet.getPetStats() != null
                    && evolutionPet != null
                    && pet.getPetStats().getExperience() >= experienceThreshold)
            {
                PetLevel level = pet.getPetStats().getCurrentLevel();
                level.setDelayBeforeEvolution(delay);
                triggered = level.evolveTo(pet.getOwner(), false, evolutionPet);
            }
            else
            {
                Debugger.send("§cCould not evolve pet has conditions are not met or the evolution doesn't exist.");
            }
        }
        else if(type.getType().equals(PetFoodType.UNLOCK.getType()))
        {
            if(p != null)
            {
                if(permission != null && !p.hasPermission(permission))
                {
                    Language.PETUNLOCK_NOPERM.sendMessage(p);
                    return false;
                }

                Pet unlockedPetObject = Pet.getFromId(unlockedPet);
                if(unlockedPetObject == null)
                {
                    Debugger.send("§7The player §c" + p.getName() + "§7 tried to unlock a pet using an unlock item but the pet §7"+ unlockedPet +"§7 does not exist.");
                    return false;
                }
                else if(p.hasPermission(unlockedPetObject.getPermission()))
                {
                    Debugger.send("§7The player §c" + p.getName() + "§7 tried to unlock a pet using an unlock item but they already own the pet.");
                    Language.PETUNLOCKED_ALREADY.sendMessageFormated(p, new FormatArg("%petName%", unlockedPetObject.getIcon().getItemMeta().getDisplayName()));
                    return false;
                }

                Utils.givePermission(p.getUniqueId(), unlockedPetObject.getPermission());
                Language.PETUNLOCKED.sendMessageFormated(p, new FormatArg("%petName%", unlockedPetObject.getIcon().getItemMeta().getDisplayName()));
                triggered = true;
            }
        }

        if(triggered)
        {
            pet.sendSignal(signal);
        }

        registerWaitingList(pet.getOwner(), 5L);

        return triggered;
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
