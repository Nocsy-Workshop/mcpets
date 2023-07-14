package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.utils.PetMath;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;

import java.util.*;

public class PetFoodConfig extends AbstractConfig {

    private static PetFoodConfig instance;

    @Getter
    private HashMap<String, PetFood> petFoods;

    private PetFoodConfig()
    {
        petFoods = new HashMap<>();
    }

    public static PetFoodConfig getInstance() {
        if (instance == null)
            instance = new PetFoodConfig();
        return instance;
    }

    public void init() {
        super.init("", "petfoods.yml");

        save();
        reload();
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void reload() {

        loadConfig();
        petFoods = new HashMap<>();

        for(String key : getConfig().getKeys(false))
        {
            String id = Optional.ofNullable(getConfig().getString(key + ".ItemId")).orElse("None set");
            PetFoodType foodType = Optional.ofNullable(PetFoodType.get(getConfig().getString(key + ".Type"))).orElse(PetFoodType.HEALTH);
            double power = getConfig().getDouble(key + ".Power");
            PetMath operator = Optional.ofNullable(PetMath.get(getConfig().getString(key + ".Operator"))).orElse(PetMath.ADDITION);
            String signal = getConfig().getString(key + ".Signal");
            String evolution = getConfig().getString(key + ".Evolution");
            int experienceThreshold = getConfig().getInt(key + ".ExperienceThreshold");
            int delay = getConfig().getInt(key + ".DelayBeforeEvolution");
            String permission = getConfig().getString(key + ".Permission");
            String unlockedPet = getConfig().getString(key + ".UnlockPet");

            List<String> petIds = getConfig().getStringList(key + ".Pets");

            PetFood petFood = new PetFood(key, id, power, foodType, operator, signal, evolution, experienceThreshold, delay, permission, unlockedPet, petIds);
            petFoods.put(key, petFood);
        }

    }

    /**
     * Return the pet food with the said id
     * @param id
     * @return
     */
    public PetFood getPetFood(String id)
    {
        return petFoods.get(id);
    }

    /**
     * List the registered pet foods
     * @return
     */
    public Collection<PetFood> list()
    {
        return petFoods.values();
    }

    public void addPet(String key, String id)
    {
        ArrayList<String> list = (ArrayList<String>) getConfig().getStringList(key + ".Pets");
        if(list.contains(id))
            return;
        list.add(id);
        getConfig().set(key + ".Pets", list);
        save();
        reload();
    }

    public void removePet(String key, String id)
    {
        ArrayList<String> list = (ArrayList<String>) getConfig().getStringList(key + ".Pets");
        if(!list.contains(id))
            return;
        list.remove(id);
        getConfig().set(key + ".Pets", list);
        save();
        reload();
    }

    /**
     * Register a new pet food with default values in entry
     */
    public String registerCleanPetfood()
    {
        String key = UUID.randomUUID().toString();
        getConfig().set(key + ".ItemId", "None set");
        save();
        reload();
        return key;
    }

    /**
     * Swap the petfood to a new key in the config
     * @param food
     * @param key
     */
    public void changePetFoodKey(PetFood food, String key)
    {
        getConfig().set(food.getId(), null);

        getConfig().set(key + ".ItemId", food.getItemId());
        getConfig().set(key + ".Type", food.getType().name());
        getConfig().set(key + ".Power", food.getPower());
        getConfig().set(key + ".Operator", food.getOperator().name());
        getConfig().set(key + ".Signal", food.getSignal());
        getConfig().set(key + ".Evolution", food.getEvolution());
        getConfig().set(key + ".ExperienceThreshold", food.getExperienceThreshold());
        getConfig().set(key + ".DelayBeforeEvolution", food.getDelay());
        getConfig().set(key + ".Permission", food.getPermission());
        getConfig().set(key + ".UnlockPet", food.getUnlockedPet());
        getConfig().set(key + ".Pets", food.getPetIds());

        save();
        reload();
    }

    /**
     * Remove a pet food
     */
    public void removePetFood(String key)
    {
        getConfig().set(key, null);
        save();
        reload();
    }

    /**
     * Load a petfood directly from the config (only for editor)
     * @return
     */
    public static PetFood loadConfigPetFood(String id)
    {
        PetFoodConfig config = new PetFoodConfig();
        config.init();
        return config.getPetFood(id);
    }
}
