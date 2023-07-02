package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.inventories.PetInventory;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.PetLevelUpEvent;
import fr.nocsy.mcpets.utils.PetAnnouncement;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PetLevel {

    // The reference pet
    @Getter
    private Pet pet;

    //---------- Level statistics and changes for the pet ----------//

    @Getter
    private String levelId;

    @Getter
    // If the pet has an evolution, specify it and it will turn into the evolution
    private String evolutionId;
    @Getter
    @Setter
    // Chose how long the evolution will be taking in ticks, 0 if instant
    // otherwise put the length of your evolution animation !
    private int delayBeforeEvolution;
    @Getter
    // Chose if the previous pet should be removed from the player's permission on evolving
    private boolean removePrevious;

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
                    String levelId,
                    String evolutionId,
                    int delayBeforeEvolution,
                    boolean removePrevious,
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

        this.levelId = levelId;

        this.evolutionId = evolutionId;
        this.delayBeforeEvolution = delayBeforeEvolution;
        this.removePrevious = removePrevious;

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
    public void announce(UUID player)
    {
        if(announcement != null && !announcement.isEmpty() &&
                player != null)
        {
            Player p = Bukkit.getPlayer(player);
            if(p != null)
                announcementType.announce(p, announcement);
        }
    }

    /**
     * Play a skill on level up if setup
     */
    public void playSkill(UUID owner)
    {
        Pet pet = Pet.fromOwner(owner);
        if(mythicSkill != null && pet.isStillHere())
        {
            Optional<Skill> opt = MCPets.getMythicMobs().getSkillManager().getSkill(mythicSkill);
            opt.ifPresent(skill -> skill.execute(new SkillMetadataImpl(SkillTriggers.CUSTOM, pet.getActiveMob(), pet.getActiveMob().getEntity())));
        }
    }

    /**
     * Says whether the player is allowed to have that evolution
     * if the evolution is null, the result will always be true
     * if the evolution is not null, then it tests whether the permission is satisfied or not
     * if the permission is satisfied, then it can't evolve : result is false
     * else it can evolve, so result is true
     * @param player
     * @return
     */
    public boolean canEvolve(UUID player, Pet evolution)
    {
        if(evolution != null)
        {
            // If the owner already has the evolution, then we say that the pet can not evolve
            // Else it can evolve
            return !Utils.hasPermission(player, evolution.getPermission());
        }
        return true;
    }

    /**
     * Makes the pet evolves if it has an evolution
     * Gives the permission to the owner to access the new pet
     * @param player
     */
    public boolean evolve(UUID player, boolean forceEvolution)
    {
        return this.evolveTo(player, forceEvolution, Pet.getFromId(evolutionId));
    }

    /**
     * Makes the pet evolves if it has an evolution
     * Gives the permission to the owner to access the new pet
     * @param player
     */
    public boolean evolveTo(UUID player, boolean forceEvolution, Pet evolution)
    {
        String evId = "null";
        if (evolution != null)
            evId = evolution.getId();
        Debugger.send("Pet §6" + this.getPet().getId() + "§7 is trying to evolve as §a" + evId);
        Debugger.send("Checking conditions: §6can evolve ? §a" + canEvolve(player, evolution) + " §7| §6forced ? §a" + forceEvolution);
        if(canEvolve(player, evolution) || forceEvolution)
        {
            if(evolution == null)
                return false;

            // Give the permission to the owner
            Utils.givePermission(player, evolution.getPermission());

            // Remove the previous permission if it's an enabled feature
            if(removePrevious)
            {
                Utils.removePermission(player, pet.getPermission());
            }

            // We disable the perm check on that one so it doesn't run into a weird synchronisation issue
            evolution.setCheckPermission(false);
            // Set the owner as the current player
            evolution.setOwner(player);

            // Load the player data for the pet
            PlayerData pd = PlayerData.get(player);
            // Fetch the saved name
            String name = pd.getMapOfRegisteredNames().get(pet.getId());
            if(name != null)
                evolution.setDisplayName(name, true);

            // Transfer the inventory to the evolution
            PetInventory petInventory = PetInventory.get(pet);
            if(petInventory != null)
            {
                evolution.setOwner(player);
                PetInventory evolutionInventory = PetInventory.get(evolution);
                // If we can not define an inventory in the evolution, then we lose the content so it doesn't make sense
                if(evolutionInventory == null)
                {
                    Bukkit.getLogger().severe("Could not load inventory of pet " + evolutionId + " for player " + player + "\nCritical issue : could not evolve the pet.");
                    return false;
                }
                evolutionInventory.setInventory(petInventory.getInventory());
            }

            // Clear the stats of the previous level since we are evolving to the next one
            PetStats.remove(pet.getId(), player);

            // Fetch the owner of the pet, it has to be there to spawn the next pet right
            Player owner = Bukkit.getPlayer(player);
            if(owner == null)
                return false;

            // Spawn the evolution
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Make sure the owner is still here
                    Player owner = Bukkit.getPlayer(player);
                    if(owner != null)
                    {
                        Pet activePet = Pet.fromOwner(player);
                        Location loc = activePet != null && activePet.isStillHere() ?
                                        activePet.getActiveMob().getEntity().getBukkitEntity().getLocation() :
                                        owner.getLocation();

                        // Despawn the previous pet
                        if(activePet != null && activePet.isStillHere())
                            activePet.despawn(PetDespawnReason.EVOLUTION);

                        // Spawn the evolution
                        evolution.spawn(loc, false);
                    }
                }
            }.runTaskLater(MCPets.getInstance(), delayBeforeEvolution);
            return true;
        }

        Player p = Bukkit.getPlayer(player);
        if(p != null)
        {
            Language.PET_COULD_NOT_EVOLVE.sendMessage(p);
            Debugger.send("§a" + pet.getId() + "§6 can not evolve into §a" + evolutionId
                    + "§6 because the §cplayer" + p.getName() + " already owns the evolution§6.");
            return false;
        }

        if(evolutionId != null)
        {
            Bukkit.getLogger().warning("The pet " + pet.getId() + " tried to evolve into " + evolutionId + " but this evolution doesn't exist in MCPets. Please provide the ID of a registered pet.");
            return false;
        }
        return false;
    }

    /**
     * Play all the skills, text, sound and everything for the level up animation
     * to the given player
     */
    public void levelUp(UUID owner)
    {
        if(owner == null)
            return;

        PetLevelUpEvent event = new PetLevelUpEvent(pet, this);
        Utils.callEvent(event);

        announce(owner);
        playSkill(owner);
        evolve(owner, false);
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
