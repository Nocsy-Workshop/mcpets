package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.events.PetLevelUpEvent;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Optional;

public class PetLevel {

    // The reference pet
    @Getter
    private Pet pet;

    //---------- Level statistics and changes for the pet ----------//

    @Getter
    // If the pet has an evolution, specify it and it will turn into the evolution
    private Pet evolution;
    @Getter
    // Chose how long the evolution will be taking in ticks, 0 if instant
    // otherwise put the length of your evolution animation !
    private int delayBeforeEvolution;

    @Getter
    // Handles the health of the pet
    private double maxHealth;
    @Getter
    private double regeneration;

    @Getter
    // Handles the damage resistance of the pet
    private double resistanceModifier;

    @Getter
    // Handles the damage of the pet
    private double damageModifier;

    @Getter
    // Handles the power of the pet
    // Used for the spells for instance
    private double power;

    //---------- Everything Handling the level transition ----------//

    @Getter
    // The name of the level
    private String levelName;

    @Getter
    // The experience threshold before it gets to another level
    // It's the maximum value of the level actually
    // ex: lvl 1 is between 0 and 100, so threshold is 100
    private double expThreshold;

    @Getter
    // The announced title
    private String announcement;
    @Getter
    private PetAnnouncement announcementType;

    @Getter
    // The sound for the announcement
    private Sound sound;
    @Getter
    private float volume;
    @Getter
    private float pitch;

    @Getter
    // Play a skill if the pet has one setup for that level
    private String mythicSkill;

    public PetLevel(Pet pet,
                    Pet evolution,
                    int delayBeforeEvolution,
                    double maxHealth,
                    double regeneration,
                    double resistanceModifier,
                    double damageModifier,
                    double power,
                    String levelName,
                    double expThreshold,
                    String announcement,
                    PetAnnouncement announcementType,
                    Sound sound,
                    float volume,
                    float pitch,
                    String mythicSkill)
    {
        this.pet = pet;

        this.evolution = evolution;
        this.delayBeforeEvolution = delayBeforeEvolution;

        this.maxHealth = maxHealth;
        this.regeneration = regeneration;
        this.resistanceModifier = resistanceModifier;
        this.damageModifier = damageModifier;
        this.power = power;

        this.levelName = levelName;
        this.expThreshold = expThreshold;
        this.announcement = announcement;
        this.announcementType = announcementType;
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
        this.mythicSkill = mythicSkill;

        assert(pet != null);
        assert(levelName != null);
    }

    /**
     * Throw the level up announcement if setup
     */
    public void announce()
    {
        if(announcement != null && !announcement.isEmpty() &&
            pet.getOwner() != null)
        {
            Player p = Bukkit.getPlayer(pet.getOwner());
            if(p != null)
                announcementType.announce(p, announcement);
        }
    }

    /**
     * Play the level up sound if setup
     */
    public void playSound()
    {
        if(sound != null && pet.getOwner() != null)
        {
            Player p = Bukkit.getPlayer(pet.getOwner());
            if(p != null)
                p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Play a skill on level up if setup
     */
    public void playSkill()
    {
        if(mythicSkill != null && pet.isStillHere())
        {
            Optional<Skill> opt = MCPets.getMythicMobs().getSkillManager().getSkill(mythicSkill);
            opt.ifPresent(skill -> skill.execute(new SkillMetadataImpl(SkillTriggers.CUSTOM, pet.getActiveMob(), pet.getActiveMob().getEntity())));
        }
    }

    /**
     * Play all the skills, text, sound and everything for the level up animation
     */
    public void levelUp()
    {
        PetLevelUpEvent event = new PetLevelUpEvent(pet, this);
        Utils.callEvent(event);

        announce();
        playSkill();
        playSound();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PetLevel petLevel = (PetLevel) o;
        return Double.compare(petLevel.expThreshold, expThreshold) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(levelName, expThreshold, announcement);
    }
}
