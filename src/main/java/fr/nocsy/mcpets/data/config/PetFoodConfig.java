package fr.nocsy.mcpets.data.config;

import fr.nocsy.mcpets.data.livingpets.PetFood;
import fr.nocsy.mcpets.data.livingpets.PetFoodType;
import fr.nocsy.mcpets.data.livingpets.PetMath;

import java.util.*;

public class PetFoodConfig extends AbstractConfig {

    private static PetFoodConfig instance;

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

        for(String key : getConfig().getKeys(false))
        {
            String id = getConfig().getString(key + ".ItemId");
            PetFoodType foodType = PetFoodType.get(getConfig().getString(key + ".Type"));
            double power = getConfig().getDouble(key + ".Power");
            PetMath operator = PetMath.get(getConfig().getString(key + ".Operator"));

            PetFood petFood = new PetFood(id, power, foodType, operator);
            petFoods.put(id, petFood);
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

    public Collection<PetFood> list()
    {
        return petFoods.values();
    }
}
