package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.events.PetLevelUpEvent;
import fr.nocsy.mcpets.utils.Utils;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;

public class PetLevel {

    // The reference pet
    @Getter
    private Pet pet;

    //---------- Level statistics and changes for the pet ----------//

    @Getter
    // If the pet has an evolution, specify it and it will turn into the evolution
    private String evolutionId;
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

    @Getter
    // Handles the inventory extension per level
    private int inventoryExtension;

    @Getter
    // Respawn cooldown at this level
    private int respawnCooldown;

    @Getter
    // Revoke cooldown at this level
    private int revokeCooldown;

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
    // Play a skill if the pet has one setup for that level
    private String mythicSkill;

    public PetLevel(Pet pet,
                    String evolutionId,
                    int delayBeforeEvolution,
                    double maxHealth,
                    double regeneration,
                    double resistanceModifier,
                    double damageModifier,
                    double power,
                    int respawnCooldown,
                    int revokeCooldown,
                    int inventoryExtension,
                    String levelName,
                    double expThreshold,
                    String announcement,
                    PetAnnouncement announcementType,
                    String mythicSkill)
    {
        this.pet = pet;

        this.evolutionId = evolutionId;
        this.delayBeforeEvolution = delayBeforeEvolution;

        this.maxHealth = maxHealth;
        this.regeneration = regeneration;
        this.resistanceModifier = resistanceModifier;
        this.damageModifier = damageModifier;
        this.power = power;
        this.respawnCooldown = respawnCooldown;
        this.revokeCooldown = revokeCooldown;
        this.inventoryExtension = inventoryExtension;

        this.levelName = levelName;
        this.expThreshold = expThreshold;
        this.announcement = announcement;
        this.announcementType = announcementType;
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
     * Makes the pet evolves if it has an evolution
     * Gives the permission to the owner to access the new pet
     */
    public void evolve()
    {
        Pet evolution = Pet.getFromId(evolutionId);
        if(evolution != null)
        {
            // If the owner already has the evolution, then we say that the pet can not evolve
            if(Utils.hasPermission(pet.getOwner(), pet.getPermission()))
            {
                return;
            }

            // Give the permission to the owner
            Utils.givePermission(pet.getOwner(), evolution.getPermission());

            // Transfer the inventory to the evolution
            PetInventory petInventory = PetInventory.get(pet);
            if(petInventory != null)
            {
                evolution.setOwner(pet.getOwner());
                PetInventory evolutionInventory = PetInventory.get(evolution);
                // If we ca not define an inventory in the evolution, then we lose the content so it doesn't make sense
                if(evolutionInventory == null)
                {
                    Bukkit.getLogger().severe("Could not load inventory of pet " + evolutionId + " for player " + getPet().getOwner() + "\nCritical issue : could not evolve the pet.");
                    return;
                }
                evolutionInventory.setInventory(petInventory.getInventory());
            }

            // Spawn the evolution
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player owner = Bukkit.getPlayer(pet.getOwner());
                    if(owner != null)
                    {
                        Location loc = pet.isStillHere() ?
                                        pet.getActiveMob().getEntity().getBukkitEntity().getLocation() :
                                        owner.getLocation();
                        evolution.spawn(owner, loc);
                    }
                }
            }.runTaskLater(MCPets.getInstance(), delayBeforeEvolution);
            return;
        }
        if(evolutionId != null)
        {
            Bukkit.getLogger().warning("The pet " + pet.getId() + " tried to evolve into " + evolutionId + " but this evolution doesn't exist in MCPets. Please provide the ID of a registered pet.");
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
        evolve();
    }

    public int compareTo(PetLevel level)
    {
        return Double.compare(this.getExpThreshold(), level.getExpThreshold());
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
