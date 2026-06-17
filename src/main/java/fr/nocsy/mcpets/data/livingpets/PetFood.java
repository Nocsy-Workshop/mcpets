package fr.nocsy.mcpets.data.livingpets;

import java.util.*;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.Items;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.PDCTag;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.utils.debug.Debugger;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.ItemsListConfig;
import fr.nocsy.mcpets.data.config.PetFoodConfig;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;

import dev.lone.itemsadder.api.CustomStack;

public class PetFood {

    private static final Map<String, PetFood> petFoodHashMap = new HashMap<>();


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
    private long cooldown;

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


    //----------------- Buffs item section ------------------//
    @Getter
    private long duration;

    /**
     * Constructor
     */
    public PetFood(
            String id,
            String itemId,
            double power,
            PetFoodType type,
            PetMath operator,
            String signal,
            long cooldown,
            String evolution,
            int experienceThreshold,
            int delay,
            String permission,
            String unlockedPet,
            List<String> petIds,
            long duration) {
        this.id = id;
        this.itemId = itemId;
        this.power = power;
        this.type = type;
        this.operator = operator;
        this.signal = signal;
        this.cooldown = cooldown;
        this.evolution = evolution;
        this.experienceThreshold = experienceThreshold;
        this.delay = delay;
        this.permission = permission;
        this.unlockedPet = unlockedPet;
        this.petIds = petIds;
        this.duration = duration;

        // Setup the item stack
        getItemStack();

        petFoodHashMap.put(id, this);
    }

    /**
     * Get the item stack of the pet food
     */
    public ItemStack getItemStack() {
        if (itemStack != null) return itemStack;

        // Fetch the item stack within the registered ones
        itemStack = ItemsListConfig.getInstance().getItemStack(itemId);

        // If no item stack matches, then we'll see if it's a default MC material
        if (itemStack == null) {
            // Checking MC materials
            Material material = Material.matchMaterial(itemId);
            if (material != null && !material.isAir()) {
                itemStack = new ItemStack(material);
                defaultMCItem = true;
                // return the itemStack without adding localized information to it, coz it's a default item
                return itemStack;
            }
            // We found no match.
            // so, trying to find items from ItemsAdder if ItemsAdder is enabled.
            // if ItemsAdder is not enabled or found no match in ItemsAdder, set the item to unknown.
            if (MCPets.isItemsAdderLoaded()) {
                CustomStack customStack = CustomStack.getInstance(itemId);
                itemStack = (customStack == null) ? Items.UNKNOWN.getItem().clone() : customStack.getItemStack();
            }

            if (itemStack == null && MCPets.checkNexo()) {
                ItemBuilder builder = NexoItems.itemFromId(itemId);
                if (builder != null) {
                    itemStack = builder.build(); // Output the Nexo Item.
                }
            }

            if (itemStack == null) {
                itemStack = Items.UNKNOWN.getItem().clone();
            }

        }
        ItemMeta meta = itemStack.getItemMeta();
        PDCTag.set(meta, "MCPets;Food;" + itemId);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    /**
     * Say whether this food is compatible with the given pet
     * If the food doesn't state any pet ids, then the food is compatible with any pet
     */
    public boolean isCompatibleWithPet(Pet pet) {
        if (pet == null) return false;
        if (petIds == null || petIds.isEmpty()) return true;

        return petIds.contains(pet.getId());
    }


    /**
     * Register an owner in the waiting list so that they don't spam an item
     * and lose it unintentionally
     */
    private final Set<UUID> waitingListApply = new HashSet<>();
    public void registerWaitingList(UUID owner, long delay) {
        if (!waitingListApply.add(owner)) return;

        Bukkit.getScheduler().runTaskLater(MCPets.getInstance(), () -> waitingListApply.remove(owner), delay);
    }

    private int getRemainingCooldownInSeconds(Pet pet) {
        long foodEatenTimestamp = pet.getFoodEatenTimestamp(id);
        long remainingCooldown = foodEatenTimestamp + cooldown - System.currentTimeMillis();
        return remainingCooldown < 0 ? 0 : (int) (remainingCooldown / 1000L);
    }

    /**
     * Give the food to the pet
     * @return value stating whether the food could be applied
     */
    public boolean apply(Pet pet, Player p) {
        if (pet == null) return false;

        if (waitingListApply.contains(pet.getOwner())) return false;

        if (type == null) return false;

        if (!isCompatibleWithPet(pet)) return false;

        int foodCooldown = getRemainingCooldownInSeconds(pet);
        if (foodCooldown > 0) {
            Debugger.send("§7NOT applying pet food §6" + id + "§7 to §6" + pet.getId() + "§7 because it's on cooldown for " + foodCooldown + "s more");
            Language.PET_FOOD_ON_COOLDOWN.sendMessageFormatted(p, new FormatArg("%timeleft%", foodCooldown));
            return false;
        }

        // says whether the petfood was triggered or not
        boolean triggered = false;

        Debugger.send("§7Applying pet food §6" + id + "§7 to §6" + pet.getId() + "§7 with type §a" + type.getType());

        PetStats stats = pet.getPetStats();
        switch (type) {
            case HEALTH -> {
                if (stats == null || stats.getCurrentHealth() >= stats.getCurrentLevel().getMaxHealth()) {
                    Debugger.send("§cCould not give HEALTH to the pet because it is already at maximum value.");
                    break;
                }

                stats.setHealth(operator.get(stats.getCurrentHealth(), power));
                triggered = true;
            }
            case TAME -> {
                if (pet.getTamingProgress() >= 1) {
                    Debugger.send("§cCould not give TAMING PROGRESS to the pet because it has reached maximum value.");
                    break;
                }

                pet.setTamingProgress(operator.get(pet.getTamingProgress(), power));
                triggered = true;
            }
            case EXP -> {
                if (stats == null) {
                    break;
                }

                triggered = stats.addExperience(power);

                if (!triggered) {
                    Debugger.send("§cCould not give EXP to the pet because it has reached maximum value.");
                }
            }
            case EVOLUTION -> {
                Pet evolutionPet = Pet.getFromId(evolution);
                if (stats == null
                        || evolutionPet == null
                        || stats.getExperience() < experienceThreshold) {
                    Debugger.send("§cCould not evolve pet has conditions are not met or the evolution doesn't exist.");
                    break;
                }

                PetLevel level = stats.getCurrentLevel();
                level.setDelayBeforeEvolution(delay);
                triggered = level.evolveTo(pet.getOwner(), false, evolutionPet);
            }
            case UNLOCK -> {
                if (p == null) {
                    break;
                }

                if (permission != null && !p.hasPermission(permission)) {
                    Language.PETUNLOCK_NOPERM.sendMessage(p);
                    return false;
                }

                Pet unlockedPetObject = Pet.getFromId(unlockedPet);
                if (unlockedPetObject == null) {
                    Debugger.send("§7The player §c" + p.getName() + "§7 tried to unlock a pet using an unlock item but the pet §7"+ unlockedPet +"§7 does not exist.");
                    return false;
                }

                if (p.hasPermission(unlockedPetObject.getPermission())) {
                    Debugger.send("§7The player §c" + p.getName() + "§7 tried to unlock a pet using an unlock item but they already own the pet.");
                    Language.PETUNLOCKED_ALREADY.sendMessageFormatted(p, new FormatArg("%petName%", unlockedPetObject.getIcon().getItemMeta().getDisplayName()));
                    return false;
                }

                Utils.givePermission(p.getUniqueId(), unlockedPetObject.getPermission());
                Language.PETUNLOCKED.sendMessageFormatted(p, new FormatArg("%petName%", unlockedPetObject.getIcon().getItemMeta().getDisplayName()));
                triggered = true;
            }
            case BUFF_DAMAGE, BUFF_RESISTANCE, BUFF_POWER -> {
                PetFoodBuff buff = new PetFoodBuff(pet, type, (float) power, operator, duration);
                triggered = buff.apply();
            }
        }

        if (triggered) {
            pet.sendSignal(signal);
            pet.applyFoodCooldown(id);
        }

        registerWaitingList(pet.getOwner(), 5L);

        return triggered;
    }

    /**
     * Consume the pet food in the main hand of the player
     */
    public void consume(Player p) {
        if (p == null) return;
    
        ItemStack mainHandItem = p.getInventory().getItemInMainHand();
        if (mainHandItem.getType().isAir()) return;
    
        int currentAmount = mainHandItem.getAmount();
        if (currentAmount <= 1) {
            p.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            mainHandItem.setAmount(currentAmount - 1);
        }
    }

    /**
     * Get the pet food object from the item stack if it represents one
     */
    public static PetFood getFromItem(ItemStack handItem) {
        if (handItem == null || handItem.getItemMeta() == null) return null;

        PetFood resultFood = null;
        for (PetFood petFoods : PetFoodConfig.getInstance().list()) {

            if (petFoods == null || petFoods.getItemStack() == null || petFoods.getItemStack().getItemMeta() == null) {
                continue;
            }

            if (petFoods.getItemStack().isSimilar(handItem)) {
                resultFood = petFoods;
                break;
            }

            // find ItemsAdder Items
            if (MCPets.isItemsAdderLoaded()) {
                CustomStack handCustomStack = CustomStack.byItemStack(handItem);
                CustomStack foodCustomStack = CustomStack.byItemStack(petFoods.getItemStack());

                if (handCustomStack == null || foodCustomStack == null) continue;

                // get <namespace>:<id>
                String handId = handCustomStack.getNamespacedID();
                String foodId = foodCustomStack.getNamespacedID();

                // check their id is same
                if (handId.equals(foodId)) {
                    resultFood = petFoods;
                    break;
                }
            }

            if (MCPets.checkNexo() && NexoItems.isSameId(handItem, petFoods.getItemStack())) {
                resultFood = petFoods;
                break;
            }
        }

        return resultFood;
    }

    /**
     * Get the PetFood from the id
     */
    public static PetFood getFromId(String id) {
        return petFoodHashMap.get(id);
    }

}
