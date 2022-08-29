package fr.nocsy.mcpets.data.livingpets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.events.PetGainExperienceEvent;
import fr.nocsy.mcpets.utils.PetTimer;
import fr.nocsy.mcpets.utils.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class PetStats {

    //------------ Static code -------------//
    @Getter
    @Setter
    private static List<PetStats> petStatsList = new ArrayList<>();

    //------------ Object code -------------//
    @Getter
    @Setter
    // Reference to the actual pet
    private Pet pet;

    @Getter
    // Handles the health of the Pet
    private double currentHealth;
    @Getter
    private PetTimer regenerationTimer;

    @Getter
    // Handles the experience of the pet
    private double experience;

    @Getter
    // Handles the levels
    private PetLevel currentLevel;

    @Getter
    // How long before the pet can be respawned after being dead
    // In seconds
    // -1 Indicating permanent death
    private PetTimer respawnTimer;

    @Getter
    // How long before the pet can be respawned after being revoked
    // In seconds
    // -1 Indicating deletion of the pet
    private PetTimer revokeTimer;

    /**
     * Set up the basic parameters
     * Launch the various pet stats schedulers
     * @param pet
     * @param experience
     */
    public PetStats(Pet pet,
                    double experience,
                    PetLevel currentLevel)
    {
        this.pet = pet;
        this.experience = experience;
        this.currentLevel = currentLevel;

        this.respawnTimer = new PetTimer(currentLevel.getRespawnCooldown(), 1);
        this.revokeTimer = new PetTimer(currentLevel.getRevokeCooldown(), 1);

        updateHealth();
        launchRegenerationTimer();
    }

    /**
     * Update the pet's health for the stats
     */
    public void updateHealth()
    {
        if(pet.isStillHere())
        {
            this.currentHealth = pet.getActiveMob().getEntity().getHealth();
        }
    }

    /**
     * Launch how much the pet should be regenerating
     * Can not be launched multiple times
     */
    private void launchRegenerationTimer()
    {
        if(regenerationTimer != null && regenerationTimer.isRunning())
            return;
        regenerationTimer = new PetTimer(Integer.MAX_VALUE, 1);
        regenerationTimer.launch(new Runnable() {
            @Override
            public void run() {
                if(pet.isStillHere())
                {
                    double value = Math.max(currentHealth + currentLevel.getRegeneration(), currentLevel.getMaxHealth());
                    pet.getActiveMob().getEntity().setHealth(value);
                    updateHealth();
                }
            }
        });
    }

    /**
     * Launch the respawn timer
     */
    public void launchRespawnTimer()
    {
        if(respawnTimer != null)
            respawnTimer.launch(null);
    }

    /**
     * Launch the revoke timer
     */
    public void launchRevokeTimer()
    {
        if(revokeTimer != null)
            revokeTimer.launch(null);
    }

    /**
     * Says if the pet is dead according to the saved health
     * Useful when the pet is not spawned yet or doesn't have its stats applied on spawn
     * @return
     */
    public boolean isDead()
    {
        return currentHealth <= 0;
    }

    /**
     * Reset the Max Health of the pet to the given value
     */
    public void refreshMaxHealth()
    {
        if(pet.isStillHere())
            pet.getActiveMob().getEntity().setMaxHealth(currentLevel.getMaxHealth());
    }

    /**
     * Set health to a given value
     */
    public void setHealth(double value)
    {
        if(value >= currentLevel.getMaxHealth())
            value = currentLevel.getMaxHealth();
        if(pet.isStillHere())
            pet.getActiveMob().getEntity().setHealth(value);
            currentHealth = value;
    }

    /**
     * Value of the health when the pet should be respawning
     * Minimum is 1% of pet's health
     * Maximum is 100% of pet's health
     * @return
     */
    public double getRespawnHealth()
    {
        return Math.min(1, Math.max(0.01, GlobalConfig.getInstance().getPercentHealthOnRespawn())) * currentLevel.getMaxHealth();
    }

    /**
     * Get the extended inventory size value
     * Depends of the actual pet inventory size and the current level bonuses
     * @return
     */
    public int getExtendedInventorySize()
    {
        return pet.getDefaultInventorySize() + currentLevel.getInventoryExtension();
    }

    /**
     * Add the given amount of experience to the pet
     * @param value
     * @return
     */
    public boolean addExperience(double value)
    {
        // That's the case for which the pet has already reached the maximum level, so it doesn't need to exp anymore
        if(currentLevel.equals(pet.getPetLevels().get(pet.getPetLevels().size()-1)))
            return false;

        PetGainExperienceEvent event = new PetGainExperienceEvent(pet, value);
        Utils.callEvent(event);
        if(event.isCancelled())
            return false;

        experience = event.getExperience();

        for(PetLevel petLevel : pet.getPetLevels())
        {
            if(experience < petLevel.getExpThreshold())
            {
                if(!petLevel.equals(currentLevel))
                {
                    petLevel.levelUp();
                    currentLevel = petLevel;
                }
            }
        }

        return true;
    }

    /**
     * Apply the modified attack damages to the given amount of damages, depending of the damage modifer of the stats
     * @param value
     * @return
     */
    public double getModifiedAttackDamages(double value)
    {
        return value * currentLevel.getDamageModifier();
    }

    /**
     * Apply the modified resistance to damages to the given amount of damages, depending of the damage modifer of the stats
     * @param value
     * @return
     */
    public double getModifiedResistanceDamages(double value)
    {
        return value * currentLevel.getResistanceModifier();
    }

    /**
     * Serialize the pet stats into a string object
     * @return
     */
    public String serialize()
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            String jsonStr = mapper.writeValueAsString(this);
            jsonStr = Base64.getEncoder().encodeToString(jsonStr.getBytes());
            return jsonStr;
        }
        catch(JsonProcessingException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Unserialize the PetStats object
     * @param base64Str
     * @return
     */
    public static PetStats unzerialize(String base64Str)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
           String decoded = new String(Base64.getDecoder().decode(base64Str.getBytes()));
            return mapper.readValue(decoded, PetStats.class);
        }
        catch(JsonProcessingException ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
