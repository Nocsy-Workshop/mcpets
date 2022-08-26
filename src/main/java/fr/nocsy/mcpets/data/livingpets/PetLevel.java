package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
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

    @Getter
    // The name of the level
    private String levelName;

    @Getter
    // The experience threshold
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
