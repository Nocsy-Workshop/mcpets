package fr.nocsy.mcpets.data.livingpets;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.serializer.PetStatsSerializer;
import fr.nocsy.mcpets.data.sql.Databases;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.PetGainExperienceEvent;
import fr.nocsy.mcpets.utils.PetTimer;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PetStats {

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

    // This variable is just here to make sure the timer are not ran when initializing the files
    private boolean initializingRun = true;

    /**
     * Set up the basic parameters
     * Launch the various pet stats schedulers
     * @param pet
     * @param experience
     */
    public PetStats(Pet pet,
                    double experience,
                    double currentHealth,
                    PetLevel currentLevel)
    {
        this.pet = pet;
        this.experience = experience;
        this.currentHealth = currentHealth;
        this.currentLevel = currentLevel;

        updateChangingData();
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
     * used to refresh data that is changed by the level
     */
    private void updateChangingData()
    {
        refreshMaxHealth();
        updateHealth();
        respawnTimer = new PetTimer(currentLevel.getRespawnCooldown(), 20, new Runnable() {
            @Override
            public void run() {
                // If it's an initialization run, we don't want the respawn to happen
                if(initializingRun)
                {
                    initializingRun = false;
                    return;
                }
                // Else, we check if the autorespawn could happen
                if(GlobalConfig.getInstance().isAutorespawn())
                {
                    Player p = Bukkit.getPlayer(pet.getOwner());
                    if(p != null && Pet.getActivePets().get(pet.getOwner()) == null)
                    {
                        pet.spawn(p.getLocation(), true);
                        Debugger.send("§aPet §6" + pet.getId() + "§a was autorespawned after death.");
                    }
                    else
                    {
                        Debugger.send("§cPet §6" + pet.getId() + "§c was supposed to autorespawn, but the player already has a spawned pet with him, or is disconnected.");
                    }
                }
            }
        });
        revokeTimer = new PetTimer(currentLevel.getRevokeCooldown(), 20, null);
    }


    /**
     * Launch the timers that should be triggered initially if they are not null
     */
    public void launchTimers()
    {
        launchRespawnTimer();
    }

    /**
     * Launch how much the pet should be regenerating
     * Can not be launched multiple times
     */
    public void launchRegenerationTimer()
    {
        // If the regeneration timer is already running and not null, then do not run it again
        if(regenerationTimer != null && regenerationTimer.isRunning())
            return;
        // If the regeneration is none then do not launch the scheduler coz it's useless
        if(currentLevel.getRegeneration() <= 0)
            return;
        regenerationTimer = new PetTimer(Integer.MAX_VALUE, 20, null);
        regenerationTimer.launch(new Runnable() {
            @Override
            public void run() {
                if(pet.isStillHere())
                {
                    double value = Math.min(currentHealth + currentLevel.getRegeneration(), currentLevel.getMaxHealth());
                    pet.getActiveMob().getEntity().setHealth(value);
                    updateHealth();
                }
                else
                {
                    regenerationTimer.stop(null);
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
            respawnTimer.launch(new Runnable() {
                @Override
                public void run() {
                    if(!isDead())
                    {
                        respawnTimer.stop(null);
                    }
                }
            });
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
     * Says whether the respawn timer is running
     * @return
     */
    public boolean isRespawnTimerRunning()
    {
        return respawnTimer != null && respawnTimer.isRunning();
    }

    /**
     * Says whether the revoke timer is running basically
     * @return
     */
    public boolean isRevokeTimerRunning()
    {
        return revokeTimer != null && revokeTimer.isRunning();
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
        {
            pet.getActiveMob().getEntity().setHealth(value);
            currentHealth = value;
        }
    }

    /**
     * Set the pet as dead
     */
    public void setDead()
    {
        setHealth(0);
        currentHealth = 0;
    }

    /**
     * Value of the health when the pet should be respawning
     * Minimum is 1% of pet's health
     * Maximum is 100% of pet's health
     * @return
     */
    public double getRespawnHealth()
    {
        double coef = Math.min(1, Math.max(0.01, GlobalConfig.getInstance().getPercentHealthOnRespawn()));
        return coef * currentLevel.getMaxHealth();
    }

    /**
     * Get the extended inventory size value
     * Depends of the actual pet inventory size and the current level bonuses
     * @return
     */
    public int getExtendedInventorySize()
    {
        return Math.min(pet.getDefaultInventorySize() + currentLevel.getInventoryExtension(), 54);
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
        // If there is no owner, then the pet can not gain experience
        if(pet.getOwner() == null)
            return false;

        PetGainExperienceEvent event = new PetGainExperienceEvent(pet, value);
        Utils.callEvent(event);
        if(event.isCancelled())
            return false;

        // add the experience to the pet
        experience = experience + event.getExperience();
        Debugger.send("§7adding " + experience + "xp to the pet " + pet.getId());

        // Look if there's a level up to perform
        PetLevel nextLevel = getNextLevel();
        boolean levelUp = false;
        while(!nextLevel.equals(currentLevel) && nextLevel.getExpThreshold() <= experience)
        {
            Debugger.send("§aPet §7" + pet.getId() + "§a is leveling up to §6" + nextLevel.getLevelName());
            // note that's there's been a levelup
            levelUp = true;
            // Set the current level to the next one
            currentLevel = nextLevel;
            // Play the level up skills, animations, etc...
            currentLevel.levelUp(pet.getOwner());
            // Move on the loop
            nextLevel = getNextLevel();
        }

        // If a level up happened, make sure to save it and update the actual pet
        if(levelUp)
        {
            updateChangingData();
        }

        // If there is no next level, set the experience so that it's the plateau value
        if(getNextLevel().equals(currentLevel) && experience > currentLevel.getExpThreshold())
        {
            Debugger.send("§7Pet " + pet.getId() + "is §cnot leveling up§7 as it has reached §cmaximum level§7, or that you §calready own the evolution§7.");
            experience = currentLevel.getExpThreshold();
        }

        return true;
    }

    public PetLevel getNextLevel()
    {
        if(currentLevel == null)
            return null;

        return pet.getPetLevels().stream()
                                    .filter(petLevel -> petLevel.getExpThreshold() > currentLevel.getExpThreshold() &&
                                                        petLevel.canEvolve(pet.getOwner(), Pet.getFromId(petLevel.getEvolutionId())))
                                    .findFirst().orElse(currentLevel);
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
        if(currentLevel.getResistanceModifier() == 0)
            return Integer.MAX_VALUE;
        return value / currentLevel.getResistanceModifier();
    }

    /**
     * Serialize the pet stats into a string object
     * @return
     */
    public String serialize()
    {
        PetStatsSerializer serializer = PetStatsSerializer.build(this);
        return serializer.serialize();
    }

    /**
     * Unserialize the PetStats object
     * @param base64Str
     * @return
     */
    public static PetStats unzerialize(String base64Str)
    {
        PetStatsSerializer serializer = PetStatsSerializer.unserialize(base64Str);
        if(serializer == null)
            return null;
        return serializer.buildStats();
    }

    /**
     * Save the stats in the database
     * Runs async for SQL, runs sync otherwise coz YAML doesn't support async
     */
    public void save() {
        if (GlobalConfig.getInstance().isDatabaseSupport()) {
            PlayerData.saveDB();
        } else {
            PlayerData pd = PlayerData.get(pet.getOwner());
            if (pd != null) {
                pd.save();
            }
        }
    }

    /**
     * Returns the current level index in the pile of possible levels of the pet
     * @return
     */
    public int getCurrentLevelIndex()
    {
        int i = 1;
        for(PetLevel level : pet.getPetLevels())
        {
            if(level.equals(currentLevel))
                return i;
            i++;
        }
        return -1;
    }

    //------------ Static code -------------//
    private static List<PetStats> petStatsList = new ArrayList<>();

    public static List<PetStats> getPetStats(UUID owner)
    {
        return petStatsList.stream()
                .filter(petStats -> petStats.getPet().getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    /**
     * Remove the pet stats corresponding to the given pet id
     * @param petId
     */
    public static void remove(String petId)
    {
        petStatsList.removeAll(petStatsList.stream()
                .filter(stat -> stat.getPet().getId().equals(petId))
                .collect(Collectors.toList()));
    }

    /**
     * Remove the pet stats corresponding to the given player
     * @param owner
     */
    public static void remove(UUID owner)
    {
        petStatsList.removeIf(stat -> stat.getPet().getOwner().equals(owner));
    }

    /**
     * Remove the pet stats corresponding to the given owner
     * @param petId
     * @param owner
     */
    public static void remove(String petId, UUID owner)
    {
        petStatsList.removeAll(petStatsList.stream()
                .filter(stat -> stat.getPet().getOwner().equals(owner)
                        && stat.getPet().getId().equals(petId))
                .collect(Collectors.toList()));
    }

    /**
     * Save all the pet stats in the DB
     */
    public static void saveAll()
    {
        if(GlobalConfig.getInstance().isDatabaseSupport())
        {
            PlayerData.saveDB();
        }
        else
        {
            petStatsList.forEach(PetStats::save);
        }
    }

    /**
     * Save all pet stats asynchronously on a regular time period
     */
    public static void saveStats() {
        // Get the auto save delay (in seconds) and transform it into ticks
        long delay = (long)GlobalConfig.getInstance().getAutoSave()*20;
        // If the delay is negative, disable the autosave
        if(delay <= 0)
            return;
        // Runs ASync if it's a SQL, sync if not coz YAML doesn't support ASync
        if(GlobalConfig.getInstance().isDatabaseSupport()) {
            // TODO: For now, we make the AutoSave only saving the connected players for MySQL users
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(MCPets.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for(Player p : Bukkit.getOnlinePlayers())
                    {
                        Databases.savePlayerData(p.getUniqueId());
                    }
                }
            }, delay, delay);
        }
        else {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(MCPets.getInstance(), new Runnable() {
                @Override
                public void run() {
                    petStatsList.forEach(PetStats::save);
                }
            }, delay, delay);
        }
    }


    /**
     * Find the pet stats corresponding to the pet and the defined owner if registered
     * null if nothing is found
     * @param petId
     * @param owner
     * @return
     */
    public static PetStats get(String petId, UUID owner)
    {
        return petStatsList.stream()
                .filter(stat -> stat.getPet().getId().equals(petId) &&
                        stat.getPet().getOwner().equals(owner))
                .findFirst().orElse(null);
    }

    /**
     * Register a pet stats
     * @param petStats
     * @return
     */
    public static boolean register(PetStats petStats)
    {
        // If there is no pet nor owner, we can not do the sanity check, so we don't register it
        if(petStats.getPet() == null || petStats.getPet().getOwner() == null)
            return false;

        // If the pet stats is already registered, then we overwrite the previous one
        if(get(petStats.getPet().getId(), petStats.getPet().getOwner()) != null)
        {
            petStatsList.remove(get(petStats.getPet().getId(), petStats.getPet().getOwner()));
        }

        // We register the pet stats if we found no matches for the same pet
        // and the same owner in the current registration
        petStatsList.add(petStats);
        return true;
    }

    /**
     * Get the first pet stats for which the timer is currently running
     * Useful only in one case but better put it here
     * @param uuid
     * @return
     */
    public static PetStats getPetStatsOnRespawnTimerRunning(UUID uuid)
    {
        return petStatsList.stream().filter(petStats -> petStats.getPet().getOwner().equals(uuid) && petStats.isRespawnTimerRunning()).findFirst().orElse(null);
    }

    /**
     * Set the pet's stats values.
     * @param experience
     * @param currentHealth
     * @param currentLevel
     */
    public void setStats(double experience, double currentHealth, PetLevel currentLevel) {
        this.experience = experience;
        this.currentHealth = currentHealth;
        this.currentLevel = currentLevel;

        updateChangingData();
        launchRegenerationTimer();
    }

}
