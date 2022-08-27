package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.events.PetDamageEvent;
import fr.nocsy.mcpets.events.PetGainExperienceEvent;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.skills.SkillResult;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class PetStats {

    @Getter
    @Setter
    // Reference to the actual pet
    private Pet pet;

    @Getter
    // Handles the health of the Pet
    private double currentHealth;

    @Getter
    // Handles the experience of the pet
    private double experience;

    @Getter
    // Handles the levels
    private PetLevel currentLevel;
    private ArrayList<PetLevel> levels;

    @Getter
    // How long before the pet can be respawned after being dead
    // -1 Indicating permanent death
    private float respawnCooldown;
    @Getter
    // How long before the pet can be respawned after being revoked
    // -1 Indicating deletion of the pet
    private float revokeCooldown;

    /**
     * Add the given amount of experience to the pet
     * @param value
     * @return
     */
    public boolean addExperience(double value)
    {
        // That's the case for which the pet has already reached the maximum level, so it doesn't need to exp anymore
        if(currentLevel.equals(levels.get(levels.size()-1)))
            return false;

        PetGainExperienceEvent event = new PetGainExperienceEvent(pet, value);
        Utils.callEvent(event);
        if(event.isCancelled())
            return false;

        experience = event.getExperience();

        for(PetLevel petLevel : levels)
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

}
