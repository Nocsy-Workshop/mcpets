package fr.nocsy.mcpets.data;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.PPermission;
import fr.nocsy.mcpets.data.config.FormatArg;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.config.Language;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.sql.PlayerData;
import fr.nocsy.mcpets.events.EntityMountPetEvent;
import fr.nocsy.mcpets.events.PetCastSkillEvent;
import fr.nocsy.mcpets.events.PetDespawnEvent;
import fr.nocsy.mcpets.events.PetSpawnEvent;
import fr.nocsy.mcpets.events.PetSpawnedEvent;
import fr.nocsy.mcpets.events.PetTamingEvent;
import fr.nocsy.mcpets.modeler.bone.AbstractNameTag;
import fr.nocsy.mcpets.utils.PathFindingUtils;
import fr.nocsy.mcpets.utils.Utils;
import fr.nocsy.mcpets.utils.debug.Debugger;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.exceptions.InvalidMobTypeException;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class Pet {

    //---------------------------------------------------------------------
    public static final String SIGNAL_STICK_TAG = "&MCPets-SignalSticks&";

    //---------------------------------------------------------------------
    public static final int BLOCKED = 2;
    public static final int MOB_SPAWN = 0;
    public static final int DESPAWNED_PREVIOUS = 1;
    public static final int OWNER_NULL = -1;
    public static final int MYTHIC_MOB_NULL = -2;
    public static final int NO_MOB_MATCH = -3;
    public static final int NOT_ALLOWED = -4;
    public static final int MAX_ACTIVE_PETS_REACHED = -5;
    //---------------------------------------------------------------------

    //********** Static values **********

    @Getter
    private static HashMap<UUID, List<Pet>> activePets = new HashMap<>();
    @Getter
    private static ArrayList<Pet> objectPets = new ArrayList<Pet>();
    @Getter
    private static HashMap<UUID, HashMap<String, PetSkin>> activeSkinsMap = new HashMap<>();

    // Prevent race conditions during spawn
    private static Set<String> spawningPets = java.util.concurrent.ConcurrentHashMap.newKeySet();

    //********** Global Pet **********

    @Getter
    private final Pet instance;

    @Getter
    private final String id;

    @Getter
    @Setter
    private PetStats petStats;

    @Getter
    @Setter
    private List<PetLevel> petLevels;

    @Setter
    @Getter
    private String mythicMobName;

    @Setter
    @Getter
    private String permission;
    @Setter
    @Getter
    private String mountPermission;

    @Setter
    @Getter
    private boolean mountable;

    @Setter
    @Getter
    private boolean despawnOnDismount;

    @Getter
    @Setter
    private int distance;

    @Getter
    @Setter
    private int spawnRange;

    @Getter
    @Setter
    private int comingBackRange;

    @Setter
    @Getter
    private ItemStack icon;

    @Setter
    @Getter
    private ItemStack signalStick;

    @Getter
    @Setter
    private String currentName;

    @Getter
    @Setter
    private String despawnSkill;

    @Getter
    @Setter
    private String tamingProgressSkill;

    @Getter
    @Setter
    private String tamingOverSkill;

    @Getter
    @Setter
    private boolean autoRide;

    @Setter
    @Getter
    private String mountType;

    @Getter
    @Setter
    private int defaultInventorySize;

    @Getter
    @Setter
    private List<String> signals;

    @Getter
    @Setter
    private boolean enableSignalStickFromMenu;

    private Map<String, Long> petFoodEatenTimestamps;

    //********** Entity features **********

    @Setter
    @Getter
    // Who is the owner ?
    private UUID owner;

    @Getter
    // Indicates the taming progress (between 0 and 1)
    private double tamingProgress = 0;

    @Getter
    // The active mob representing the pet instance
    private ActiveMob activeMob;

    @Getter
    // Is the pet invulnerable ?
    private boolean invulnerable;

    @Getter
    @Setter
    // Was the pet removed ?
    private boolean removed;

    @Getter
    @Setter
    // Should we check the permission when spawning ?
    private boolean checkPermission;

    @Getter
    @Setter
    // Is it the first spawn of the pet or is it being teleported for instance ?
    private boolean firstSpawn;

    @Getter
    @Setter
    // Should it follow the owner ?
    private boolean followOwner;

    // Debug variables
    @Setter
    private boolean recurrent_spawn = false;

    // AI variable
    private int task = 0;
    private boolean taskRunning = false;

    /**
     * Constructor only used to create a fundamental Pet. If you wish to use a pet instance, please refer to copy()
     */
    public Pet(final String id) {
        this.id = id;
        this.instance = this;
        this.checkPermission = true;
        this.firstSpawn = true;
        this.petFoodEatenTimestamps = new HashMap<>();
    }

    /**
     * Set the active skin of the pet and register it
     */
    public void setActiveSkin(final PetSkin skin) {
        if (owner != null) {
            HashMap<String, PetSkin> ownerPetSkins = activeSkinsMap.get(owner);
            if (ownerPetSkins == null)
                ownerPetSkins = new HashMap<String, PetSkin>();

            ownerPetSkins.put(id, skin);
            activeSkinsMap.put(owner, ownerPetSkins);
        }
    }

    /**
     * Fetch the active skin of the pet relatively to its owner
     */
    public PetSkin getActiveSkin() {
        if (owner != null) {
            final HashMap<String, PetSkin> ownerPetSkins = activeSkinsMap.get(owner);
            if (ownerPetSkins != null) {
                return ownerPetSkins.get(id);
            }
        }
        return null;
    }

    /**
     * Determine if this pet is a mount based on its category type
     */
    public boolean isMount() {
        Debugger.send("§6Checking if pet " + this.id + " is a mount...");
        // Find the category that contains this pet
        for (final Category cat : Category.getCategories()) {
            Debugger.send("§7Checking category: " + cat.getId() + " (type: " + cat.getCategoryType() + ")");
            // Check if any pet in this category has the same ID as this pet
            for (final Pet pet : cat.getPets()) {
                if (pet.getId().equals(this.id)) {
                    final CategoryType type = cat.getCategoryType();
                    final boolean result = type == CategoryType.MOUNT;
                    Debugger.send("§aFound pet in category! Type: " + type + " -> isMount: " + result);
                    return result;
                }
            }
        }
        // If no category found or category is DEFAULT/PET, return false
        Debugger.send("§cPet not found in any category, defaulting to PET");
        return false;
    }

    /**
     * Remove all signal sticks belonging to the given pet from the player's inventory.
     * Used on despawn to ensure the item does not persist in the player's hands.
     */
    public static void clearStickSignals(final Player p, final String petId) {
        if (p == null)
            return;
        for (int i = 0; i < p.getInventory().getSize(); i++) {
            final ItemStack item = p.getInventory().getItem(i);
            if (Items.isSignalStick(item)
                    && Pet.getFromSignalStick(item) != null
                    && Pet.getFromSignalStick(item).getId().equals(petId)) {
                p.getInventory().setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    /**
     * Get the pet from a serialized toString version
     */
    public static Pet fromString(final String seria) {
        if (seria.startsWith("AlmPet;")) {
            final String id = seria.split(";")[1];
            return getFromId(id);
        }
        return null;
    }

    /**
     * Get pet object from the id of the pet
     */
    public static Pet getFromId(final String id) {
        for (final Pet pet : objectPets) {
            if (pet.getId().equals(id)) {
                return pet.copy();
            }
        }
        return null;
    }

    /**
     * Get the pet from the ItemStack icon
     */
    public static Pet getFromIcon(final ItemStack icon) {
        if (icon.hasItemMeta() && icon.getItemMeta().hasItemName()) {
            return fromString(icon.getItemMeta().getItemName());
        }
        return null;
    }

    /**
     * Get the pet from the specified entity
     */
    public static Pet getFromEntity(final Entity ent) {
        if (ent != null &&
                ent.hasMetadata("AlmPet") &&
                !ent.getMetadata("AlmPet").isEmpty() &&
                ent.getMetadata("AlmPet").getFirst() != null &&
                ent.getMetadata("AlmPet").getFirst().value() != null) {
            return (Pet) ent.getMetadata("AlmPet").getFirst().value();
        }
        return null;
    }

    /**
     * Get the pet of the specified owner if it exists
     *
     * @deprecated Use getActivePetsForOwner() instead. Returns first pet only for backward compatibility.
     */
    @Deprecated
    public static Pet fromOwner(final UUID owner) {
        final List<Pet> pets = Pet.getActivePets().get(owner);
        return (pets != null && !pets.isEmpty()) ? pets.getFirst() : null;
    }

    /**
     * Get all active pets for a player
     */
    public static List<Pet> getActivePetsForOwner(final UUID owner) {
        return Pet.getActivePets().getOrDefault(owner, new ArrayList<>());
    }

    /**
     * Add a pet to a player's active pets list
     */
    public static void addActivePet(final UUID owner, final Pet pet) {
        activePets.computeIfAbsent(owner, k -> new ArrayList<>()).add(pet);
    }

    /**
     * Remove a pet from a player's active pets list
     */
    public static void removeActivePet(final UUID owner, final Pet pet) {
        final List<Pet> pets = activePets.get(owner);
        if (pets != null) {
            pets.remove(pet);
            if (pets.isEmpty()) {
                activePets.remove(owner);
            }
        }
    }

    /**
     * Get the pet from the last one that the player interacted with
     */
    public static Pet getFromLastInteractedWith(final Player p) {
        if (p != null &&
                p.hasMetadata("AlmPetInteracted") &&
                !p.getMetadata("AlmPetInteracted").isEmpty() &&
                p.getMetadata("AlmPetInteracted").getFirst() != null &&
                p.getMetadata("AlmPetInteracted").getFirst().value() != null) {
            return (Pet) p.getMetadata("AlmPetInteracted").getFirst().value();
        }
        return null;
    }

    /**
     * Get the pet from the last one that the player interacted with
     */
    public static Pet getFromLastOpInteractedWith(final Player p) {
        if (p != null && p.hasPermission(PPermission.ADMIN.getPermission()) &&
                p.hasMetadata("AlmPetOp") &&
                !p.getMetadata("AlmPetOp").isEmpty() &&
                p.getMetadata("AlmPetOp").getFirst() != null &&
                p.getMetadata("AlmPetOp").getFirst().value() != null) {
            return (Pet) p.getMetadata("AlmPetOp").getFirst().value();
        }
        return null;
    }

    /**
     * Associate the said player to the pet as last interacted with
     */
    public void setLastInteractedWith(final Player p) {
        p.setMetadata("AlmPetInteracted", new FixedMetadataValue(MCPets.getInstance(), this));
    }

    /**
     * Associate the said op player to the pet as last interacted with
     */
    public void setLastOpInteracted(final Player p) {
        if (p.hasPermission(PPermission.ADMIN.getPermission()))
            p.setMetadata("AlmPetOp", new FixedMetadataValue(MCPets.getInstance(), this));
    }

    /**
     * Return the pet from the signal stick item
     * null if none is found matching the id
     */
    public static Pet getFromSignalStick(final ItemStack signalStick) {
        final String petId = Items.getPetTag(signalStick);
        if (petId != null)
            return Pet.getFromId(petId);
        return null;
    }

    /**
     * List of pets available for the specified player (using permissions)
     */
    public static List<Pet> getAvailablePets(final Player p) {
        final ArrayList<Pet> pets = new ArrayList<>();

        for (final Pet pet : objectPets) {
            if (pet.isCheckPermission()) {
                if (p.hasPermission(pet.getPermission())) {
                    final Pet updatedPet = pet.copy();
                    updatedPet.setOwner(p.getUniqueId());
                    updatedPet.setPetStats();

                    pets.add(updatedPet);
                }
            } else {
                pets.add(pet);
            }
        }
        return pets;
    }

    /**
     * Clear the list of pets
     */
    public static void clearPets() {
        final List<Pet> allPets = new ArrayList<>();
        for (final List<Pet> petList : Pet.getActivePets().values()) {
            allPets.addAll(petList);
        }
        for (final Pet pet : allPets) {
            pet.despawn(PetDespawnReason.RELOAD);
        }
    }

    /**
     * Do not use this function except if you're just spawning a pet
     * Set the value of the taming progress default value
     */
    public void setDefaultTamingValue(final double value) {
        tamingProgress = Math.min(1, Math.max(value, 0));
    }

    /**
     * Set the taming progress to the given value
     */
    public void setTamingProgress(double value) {
        value = Math.min(1, Math.max(value, 0));

        final PetTamingEvent event = new PetTamingEvent(this, value);
        Utils.callEvent(event);

        if (!event.isCancelled()) {
            // Starts following the tamer
            AI();

            tamingProgress = event.getTamingProgress();

            // If taming is complete, then give the access to the owner
            if (event.isTamingComplete()) {
                // Setup the pet stats
                setPetStats();
                // Give the access
                Utils.givePermission(owner, permission);
                // Activate the pet in MCPets, coz so far it was just following the owner
                changeActiveMobTo(activeMob, owner, true, PetDespawnReason.REPLACED);

                // Set the health at the top after taming
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        petStats.refreshMaxHealth();
                        petStats.setHealth(petStats.getCurrentLevel().getMaxHealth());
                    }
                }.runTaskLater(MCPets.getInstance(), 2L);
                final Skill tamingOverSkillMM = Utils.getSkill(tamingOverSkill);
                if (tamingOverSkillMM != null) {
                    try {
                        tamingOverSkillMM.execute(new SkillMetadataImpl(SkillTriggers.CUSTOM, activeMob, activeMob.getEntity()));
                    } catch (final Exception ignored) {
                    }
                }
            } else {
                final Skill tamingProgressSkillMM = Utils.getSkill(tamingProgressSkill);
                if (tamingProgressSkillMM != null) {
                    try {
                        tamingProgressSkillMM.execute(new SkillMetadataImpl(SkillTriggers.CUSTOM, activeMob, activeMob.getEntity()));
                    } catch (final Exception ignored) {
                    }
                }
            }
        }
    }

    public long getFoodEatenTimestamp(final String petFoodId) {
        return petFoodEatenTimestamps.getOrDefault(petFoodId, 0L);
    }

    public void applyFoodCooldown(final String petFoodId) {
        petFoodEatenTimestamps.put(petFoodId, System.currentTimeMillis());
    }

    /**
     * Setup the pet stats if possible
     */
    private void setPetStats() {
        // We do not setup pet stats if :
        // - The pet already has stats
        // - The pet has no registered levels (it's not a living pet then)
        if (petStats != null || petLevels == null || petLevels.isEmpty())
            return;

        // If it already has registered pet stats, then we just read them from the loaded ones
        // Else we create default pet stats that will server as the base
        petStats = Optional.ofNullable(PetStats.get(id, owner)).orElseGet(() -> {
            final PetStats start = new PetStats(this, 0, petLevels.getFirst().getMaxHealth(), petLevels.getFirst());
            // We register the pet stats if we have new ones created
            PetStats.register(start);
            return start;
        });
    }

    /**
     * Spawn the pet if possible. Return values are indicated in this class.
     */
    public int spawn(final Location loc, final boolean bruise) {
        Debugger.send("§aSpawning pet " + id + "...");
        // if the pet has no pet stats, then we try to set one
        if (petStats == null) {
            setPetStats();
        }

        // Trigger the PetSpawnEvent
        final PetSpawnEvent event = new PetSpawnEvent(this, loc);
        Utils.callEvent(event);

        // Set the pet to follow the owner by default
        followOwner = true;

        // If no location is given
        if (loc == null)
            return BLOCKED;

        // If the event is cancelled trigger a despawn
        if (event.isCancelled()) {
            Debugger.send("§cThe spawn event was cancelled.");
            despawn(PetDespawnReason.CANCELLED);
            return BLOCKED;
        }

        // If we have a looping issue trigger a despawn
        if (recurrent_spawn) {
            despawn(PetDespawnReason.LOOP_SPAWN);
            if (Bukkit.getPlayer(owner) != null)
                Language.LOOP_SPAWN.sendMessage(Bukkit.getPlayer(owner));
            Debugger.send("§cPet was despawned coz it was stuck in a spawn loop.");
            return BLOCKED;
        } else {
            recurrent_spawn = true;
            // LOOP SPAWN issue
            new BukkitRunnable() {
                @Override
                public void run() {
                    recurrent_spawn = false;
                }
            }.runTaskLater(MCPets.getInstance(), 10L);
        }

        // If we should check the permission
        if (checkPermission && owner != null &&
                Bukkit.getPlayer(owner) != null &&
                !Bukkit.getPlayer(owner).hasPermission(permission)) {
            Debugger.send("§cUser is not allowed to spawn that pet.");
            despawn(PetDespawnReason.DONT_HAVE_PERM);
            return NOT_ALLOWED;
        }

        // Check if this exact pet ID is already active for this player
        // (excluding this instance if it's somehow already in the list)
        // Check if this exact pet ID is already active for this player
        // (excluding this instance if it's somehow already in the list)
        String spawnKey = null;
        if (owner != null) {
            spawnKey = owner.toString() + ":" + this.id;
            if (spawningPets.contains(spawnKey)) {
                Debugger.send("§cPet spawn already in progress for: " + this.id);
                return BLOCKED;
            }

            final List<Pet> activePets = Pet.getActivePetsForOwner(owner);
            for (final Pet activePet : activePets) {
                // Skip if it's the same instance (same object reference)
                if (activePet == this) continue;

                if (activePet.getId().equals(this.id)) {
                    Debugger.send("§cPlayer already has this pet active: " + this.id);
                    // Don't spawn, just return blocked
                    return BLOCKED;
                }
            }
            // Add to spawning set
            spawningPets.add(spawnKey);
        }

        try {
            // Max limit check is now done in changeActiveMobTo() after the pet is added to active list
            // This prevents race conditions when alternating between mounts and pets

            // Get the active skin (which is also a MythicMobs)
            // Adapt the mythicMob to despawn depending on the skin
            if (getActiveSkin() != null)
                mythicMobName = getActiveSkin().getMythicMobId();

            // Any issue with the mythicmobs definition ?
            // Any issue with the owner definition ?
            if (mythicMobName == null) {
                Debugger.send("§cMythicMob name is null, check out your pet config.");
                return MYTHIC_MOB_NULL;
            } else if (owner == null) {
                Debugger.send("§cOwner was not found.");
                return OWNER_NULL;
            }

            if (MCPets.getMythicMobs().getMobManager().getMythicMob(mythicMobName).isEmpty()) {
                Debugger.send("§cThe MythicMob §6" + mythicMobName + "§c doesn't exist in MythicMobs. §7Check your pet config to make sure the MythicMob you chose actually exists.");
                return MYTHIC_MOB_NULL;
            }

            try {
                // Initialize the entity
                final Entity ent;
                try {
                    // Spawn the mythicMobs
                    // if it's autoride then we spawn it at the player's location so he can climb on it directly
                    // Otherwise we spawn the pet around according to the noise
                    if (autoRide) {
                        ent = MCPets.getMythicMobs().getAPIHelper().spawnMythicMob(mythicMobName, loc);
                    } else {
                        Location spawnLoc = loc;
                        if (bruise)
                            spawnLoc = Utils.bruised(loc, getSpawnRange());
                        ent = MCPets.getMythicMobs().getAPIHelper().spawnMythicMob(mythicMobName, spawnLoc);
                    }
                } catch (final NullPointerException | NoSuchElementException ex) {
                    // if there's been a problem, trigger a despawn
                    Debugger.send("§cMythicMob " + mythicMobName + " was not found.");
                    despawn(PetDespawnReason.SPAWN_ISSUE);
                    return MYTHIC_MOB_NULL;
                }

                // If the pet is not here, trigger a despawn
                if (ent == null) {
                    Debugger.send("§cMythicMob was found but the entity was not able to spawn.");
                    despawn(PetDespawnReason.SPAWN_ISSUE);
                    return MYTHIC_MOB_NULL;
                }

                // We try to fetch the mob within the MythicMobs registry
                final Optional<ActiveMob> maybeHere = MCPets.getMythicMobs().getMobManager().getActiveMob(ent.getUniqueId());
                maybeHere.ifPresent(this::setActiveMob);

                // Sometimes it can happen that the mob isn't registered, so we try to register it manually
                if (activeMob == null) {
                    Debugger.send("§6Warn: §7MythicMobs didn't have the mob in the registry, let's try to register it manually.");
                    final ActiveMob mob = MCPets.getMythicMobs().getMobManager().registerActiveMob(
                            BukkitAdapter.adapt(ent),
                            MCPets.getMythicMobs().getMobManager().getMythicMob(mythicMobName).get(),
                            0
                    );
                    if (mob != null)
                        setActiveMob(mob);
                }

                // If any weird thing happened and the activeMob couldn't be registered, then we cancel everything
                if (activeMob == null) {
                    Debugger.send("§cMythicMob was spawned but MCPets couldn't link it to an active mob. Trying again in 0.5s automatically...");
                    // We remove the entity coz that'll not be done by the despawn since the activeMob is null
                    ent.remove();
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            spawn(loc, bruise);
                        }
                    }.runTaskLater(MCPets.getInstance(), 10L);
                    return MYTHIC_MOB_NULL;
                }

                final boolean returnDespawned = changeActiveMobTo(activeMob, owner, true, PetDespawnReason.REPLACED);

                // Handles the first spawn situation
                if (firstSpawn) {
                    // It won't be a first spawn anymore
                    firstSpawn = false;
                    // Handles the mount on pet on first spawn
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            final Player p = Bukkit.getPlayer(owner);
                            if (p != null && autoRide) {
                                final boolean mounted = setMount(p);
                                if (!mounted)
                                    Language.NOT_MOUNTABLE.sendMessage(p);
                            }
                        }
                    }.runTaskLater(MCPets.getInstance(), 5L);
                }

                // Call the spawned event
                final PetSpawnedEvent petSpawnedEvent = new PetSpawnedEvent(this);
                Utils.callEvent(petSpawnedEvent);

                // Either we despawned a previous pet or not
                if (returnDespawned) {
                    Debugger.send("§aSpawn successfuly happened. Previous pet is going to be despawned.");
                    return DESPAWNED_PREVIOUS;
                }
                return MOB_SPAWN;

            } catch (final InvalidMobTypeException e) {
                // If there's a mob bug, despawn the current pet
                Debugger.send("§cImpossible to spawn the pet: MythicMob was not found.");
                despawn(PetDespawnReason.SPAWN_ISSUE);
                return NO_MOB_MATCH;
            }
        } finally {
            if (spawnKey != null) {
                spawningPets.remove(spawnKey);
            }
        }
    }

    /**
     * Spawn the pet and send the corresponding message on execution
     */
    public void spawnWithMessage(final Player p) {
        final int executed = this.spawn(p, p.getLocation());
        if (isStillHere())
            switch (executed) {
                case Pet.DESPAWNED_PREVIOUS:
                    Language.REVOKED_FOR_NEW_ONE.sendMessage(p);
                    break;
                case Pet.MOB_SPAWN:
                    Language.SUMMONED.sendMessage(p);
                    break;
                case Pet.MYTHIC_MOB_NULL:
                    Language.MYTHICMOB_NULL.sendMessage(p);
                    break;
                case Pet.NO_MOB_MATCH:
                    Language.NO_MOB_MATCH.sendMessage(p);
                    break;
                case Pet.NOT_ALLOWED:
                    Language.NOT_ALLOWED.sendMessage(p);
                    break;
                case Pet.OWNER_NULL:
                    Language.OWNER_NOT_FOUND.sendMessage(p);
                    break;
                case Pet.MAX_ACTIVE_PETS_REACHED:
                    Language.MAX_ACTIVE_PETS_REACHED.sendMessageFormated(p,
                            new FormatArg("%max%", String.valueOf(GlobalConfig.getInstance().getMaxActivePets())));
                    break;
            }
    }

    /**
     * Set the pet's instance active mob to the given new ActiveMob
     * Returns the value if the mob has revoked a previous one
     */
    public boolean changeActiveMobTo(final ActiveMob mob, final UUID owner, final boolean followOwner, final PetDespawnReason reason) {
        boolean replaced = false;
        // No longer auto-despawn previous pets - allow multiple pets
        // The max limit check in spawn() will handle this

        // Then we set the active mob to the new active mob
        // And we setup the default pet parameters
        setActiveMob(mob);
        // Inform that the pet is not removed
        setRemoved(false);

        // Set the owner
        this.owner = owner;
        activeMob.setOwnerUUID(owner);

        // Follow up the owner ?
        this.followOwner = followOwner;
        this.AI();

        // Add the pet to the active list of pets for the given owner
        Pet.addActivePet(owner, this);
        Debugger.send("§b[LIMIT CHECK] Pet " + this.id + " added to active list for owner " + owner);

        if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
            final PlayerData pd = PlayerData.get(owner);
            pd.setLastActivePet(this.getId());
            pd.save();
        }

        // Check max active pets/mounts limit AFTER adding this pet to the list
        // This ensures accurate counting and prevents race conditions
        final boolean isMount = this.isMount();
        Debugger.send("§b[LIMIT CHECK] Pet " + this.id + " isMount: " + isMount);

        final int maxAllowed = isMount ?
                GlobalConfig.getInstance().getMaxActiveMounts() :
                GlobalConfig.getInstance().getMaxActivePets();
        Debugger.send("§b[LIMIT CHECK] Max allowed for " + (isMount ? "mounts" : "pets") + ": " + maxAllowed);

        if (maxAllowed > 0) {
            // Get all active pets/mounts of the same type for this owner
            // Get all active pets/mounts of the same type for this owner
            final List<Pet> sameLevelTypePets = Pet.getActivePetsForOwner(owner).stream()
                    .filter(p -> p.isMount() == isMount)  // Filter by same type
                    .collect(java.util.stream.Collectors.toCollection(ArrayList::new));

            long currentCount = sameLevelTypePets.size();
            Debugger.send("§b[LIMIT CHECK] Current count of " + (isMount ? "mounts" : "pets") + ": " + currentCount);
            Debugger.send("§b[LIMIT CHECK] List of same type pets: " + sameLevelTypePets.stream().map(Pet::getId).collect(java.util.stream.Collectors.joining(", ")));

            // If we// If we exceeded the limit, despawn oldest ones (that are not this one) until back under the limit
            if (currentCount > maxAllowed) {
                final String type = isMount ? "mount" : "pet";
                Debugger.send("§eMax " + type + " limit exceeded (" + currentCount + "/" + maxAllowed + "), despawning oldest...");

                // Continue despawning until the count no longer exceeds the limit
                while (currentCount > maxAllowed) {
                    Pet petToDespawn = null;

                    // Find the oldest pet/mount (not this one)
                    for (final Pet pet : sameLevelTypePets) {
                        if (pet != this) {
                            petToDespawn = pet;
                            break;
                        }
                    }

                    // Safety check to avoid infinite loops if no eligible pet is found
                    if (petToDespawn == null) {
                        Debugger.send("§c[LIMIT CHECK] No eligible " + type + " found to despawn while enforcing limit for " + this.id + ". Breaking out to avoid infinite loop.");
                        break;
                    }

                    Debugger.send("§eDespawning " + petToDespawn.getId() + " to enforce limit for " + this.id);
                    petToDespawn.despawn(PetDespawnReason.REPLACED);
                    sameLevelTypePets.remove(petToDespawn);
                    currentCount--;
                    replaced = true;
                }
            } else {
                Debugger.send("§b[LIMIT CHECK] No despawn needed. Count (" + currentCount + ") <= max (" + maxAllowed + ")");
            }
        } else {
            Debugger.send("§b[LIMIT CHECK] No limit configured (maxAllowed = 0)");
        }

        // Load the player data for the pet
        final PlayerData pd = PlayerData.get(owner);
        // Fetch the saved name
        final String name = pd.getMapOfRegisteredNames().get(this.id);

        // Set the display name of the pet
        if (name != null) {
            setDisplayName(name, false);
        } else {
            setDisplayName(Language.TAG_TO_REMOVE_NAME.getMessage(), false);
        }

        // Setup the default signal
        PlayerSignal.setDefaultSignal(owner, this);

        // If we change the mob, then we're going to consider it to be fully tamed as well
        tamingProgress = 1;

        return replaced;
    }

    /**
     * Set the active mob of the pet instance
     * This will not synchronize with the pet's owner, so be extra careful with using this method
     * You'd rather use "changeActiveMobTo" instead
     */
    public void setActiveMob(final ActiveMob mob) {
        if (mob == null) {
            Debugger.send("§cCould not set the active pet to the new one: mob instance is null");
            despawn(PetDespawnReason.CHANGING_TO_NULL_ACTIVEMOB);
            return;
        }
        // Then we set the active mob to the new active mob
        // And we setup the default pet parameters
        activeMob = mob;
        final Entity ent = mob.getEntity().getBukkitEntity();

        // Put the Metadata on the pet that characterizes it so we can identify it later
        ent.setMetadata("AlmPet", new FixedMetadataValue(MCPets.getInstance(), this));
    }

    /**
     * Stop the AI scheduler if running
     */
    public void stopAI() {
        if (!taskRunning)
            return;
        Bukkit.getScheduler().cancelTask(task);
        taskRunning = false;
    }

    /**
     * Activate the following AI of the mob
     */
    public void AI() {
        if (taskRunning)
            return;

        taskRunning = true;
        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(MCPets.getInstance(), new Runnable() {

            private int teleportTick = 0;

            @Override
            public void run() {

                final Player p = Bukkit.getPlayer(owner);
                if (p == null) {
                    getInstance().despawn(PetDespawnReason.OWNER_NOT_HERE);
                    stopAI();
                    return;
                }

                if (p.isDead())
                    return;

                if (!getInstance().isStillHere()) {
                    Debugger.send("§6[AiManager] : §cPet " + getId() + " is not here, so it gets despawned.");
                    getInstance().despawn(PetDespawnReason.AI_TRACK_DESPAWN);
                    stopAI();
                    return;
                }

                final String permission = getInstance().getPermission();
                if (getInstance().isCheckPermission() && (permission == null || !p.hasPermission(permission))) {
                    Debugger.send("§6[AiManager] : §cPet " + getId() + " despawned because the owner doesn't have permission");
                    getInstance().despawn(PetDespawnReason.DONT_HAVE_PERM);
                    stopAI();
                    return;
                }

                final Location petLocation = p.getLocation();
                final Location ownerLoc = petLocation;
                final Location petLoc = getInstance().getActiveMob().getEntity().getBukkitEntity().getLocation();

                // If the owner is not in the same world as the pet and that the pet is fully tamed, we move it
                // to the owner
                if (!ownerLoc.getWorld().getName().equals(petLoc.getWorld().getName()) && tamingProgress == 1) {
                    getInstance().despawn(PetDespawnReason.TELEPORT);
                    getInstance().spawn(p, petLocation);
                    return;
                }

                final double distance = Utils.distance(ownerLoc, petLoc);

                // Following AI System
                if (distance < getInstance().getComingBackRange()) {
                    // If the pet is too close then it stops
                    PathFindingUtils.stop(activeMob.getEntity(), owner);
                } else if (distance > getInstance().getDistance() &&
                        (distance < GlobalConfig.getInstance().getDistanceTeleport() || tamingProgress < 1)) {
                    // If the pet is too far but not far enough to be teleported, then it follows up the owner
                    // Except if the following is disabled
                    // * Note : if the taming is not completed then the pet can not be teleported to the owner
                    if (!followOwner)
                        return;
                    final AbstractLocation aloc = new AbstractLocation(activeMob.getEntity().getWorld(), petLocation.getX(), petLocation.getY(), petLocation.getZ());
                    PathFindingUtils.moveTo(activeMob.getEntity(), aloc);
                } else if (distance > GlobalConfig.getInstance().getDistanceTeleport()
                        && !p.isFlying() && !p.isGliding()
                        && p.isOnGround()
                        && teleportTick == 0) {
                    // If the pet is really too far, and that the owner is not flying
                    // And that we didn't teleport the pet a few ticks before
                    // Then we teleport the pet to the owner
                    // * Note that if the taming of the pet is not fully complete, then the pet won't be teleported
                    // * but instead the pet will try to come closer to the owner according to the previous "if"
                    getInstance().teleportToPlayer(p);
                    teleportTick = 4;
                }
                if (teleportTick > 0)
                    teleportTick--;

            }
        }, 0L, 10L);
    }

    /**
     * Spawn the pet at specified location and attributing player as the owner of the pet
     */
    public int spawn(@NotNull final Player owner, final Location loc) {
        this.owner = owner.getUniqueId();
        setLastInteractedWith(owner);
        return spawn(loc, true);
    }

    /**
     * Despawn the pet
     */
    public boolean despawn(final PetDespawnReason reason) {

        final PetDespawnEvent event = new PetDespawnEvent(this, reason);
        Utils.callEvent(event);

        Debugger.send("§6Pet §7" + id + "§6 has §cdespawned§6. Reason: §a" + reason.getReason());

        stopAI();
        removed = true;
        if (owner != null) {
            final Player ownerPlayer = Bukkit.getPlayer(owner);
            if (ownerPlayer != null) {
                if (reason.equals(PetDespawnReason.UNKNOWN) ||
                        reason.equals(PetDespawnReason.SPAWN_ISSUE)) {
                    Language.REVOKED_UNKNOWN.sendMessage(ownerPlayer);
                }
                if (enableSignalStickFromMenu)
                    clearStickSignals(ownerPlayer, this.id);
            }
        }

        if (activeMob != null) {

            MCPets.getModeler().dismountAll(activeMob.getEntity().getUniqueId());

            // If it's not a death, we don't let the death animation happen
            if (reason != PetDespawnReason.DEATH) {
                // Do we have a despawn skill to trigger or a skin swap?
                final Skill despawnSkillMM = Utils.getSkill(despawnSkill);
                if (despawnSkillMM != null
                        && reason != PetDespawnReason.SKIN) {
                    try {
                        despawnSkillMM.execute(new SkillMetadataImpl(SkillTriggers.CUSTOM, activeMob, activeMob.getEntity()));
                    } catch (final Exception ex) {
                        if (activeMob.getEntity() != null && activeMob.getEntity().getBukkitEntity() != null) {
                            activeMob.getEntity().getBukkitEntity().remove();
                            activeMob.despawn();
                            activeMob.remove();
                        }
                    }
                } else {
                    MCPets.getModeler().removeModel(activeMob.getEntity().getUniqueId());
                    activeMob.despawn();
                    activeMob.remove();
                    if (activeMob.getEntity() != null)
                        activeMob.getEntity().remove();
                    if (activeMob.getEntity() != null && activeMob.getEntity().getBukkitEntity() != null)
                        activeMob.getEntity().getBukkitEntity().remove();
                }
            }

            Pet.removeActivePet(owner, this);
            Debugger.send("§b[DESPAWN] Removed pet " + id + " from active list");
            if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
                if (reason == PetDespawnReason.REVOKE || reason == PetDespawnReason.DISMOUNT || reason == PetDespawnReason.UNKNOWN) {
                    final PlayerData pd = PlayerData.get(owner);
                    pd.setLastActivePet("");
                    pd.save();
                }
            }
            return true;
        }

        Debugger.send("§cActive mob was not found, so it could not be despawned.");
        Pet.removeActivePet(owner, this);
        Debugger.send("§b[DESPAWN] Removed pet " + id + " from active list (no active mob)");
        if (GlobalConfig.getInstance().isSpawnPetAfterServerRestart()) {
            if (reason == PetDespawnReason.REVOKE || reason == PetDespawnReason.DISMOUNT || reason == PetDespawnReason.UNKNOWN) {
                final PlayerData pd = PlayerData.get(owner);
                pd.setLastActivePet("");
                pd.save();
            }
        }
        return false;
    }

    /**
     * Teleport the pet to the specific location
     */
    public void teleport(final Location loc) {
        if (isStillHere()) {
            this.activeMob.remove();
            this.despawn(PetDespawnReason.TELEPORT);
            this.spawn(loc, true);
        }
    }

    /**
     * Teleport the pet to the player
     */
    public void teleportToPlayer(final Player p) {
        final Location loc = Utils.bruised(p.getLocation(), Math.min(getSpawnRange(), getDistance()));
        Debugger.send("§7teleporting pet " + id + " to player " + p.getName());
        if (isStillHere())
            this.teleport(loc);
    }

    /**
     * Say whether or not the entity is still present
     */
    public boolean isStillHere() {
        return activeMob != null &&
                activeMob.getEntity() != null &&
                activeMob.getEntity().getBukkitEntity() != null &&
                //!activeMob.getEntity().getBukkitEntity().isDead() &&     THIS ONE APPARENTLY DOESN'T WORK AS INTENDED
                !activeMob.isDead() &&
                !removed;
    }

    /**
     * Does the player have the access to the given pet ?
     */
    public boolean has(final Player p) {
        return Utils.hasPermission(p.getUniqueId(), this.getPermission());
    }

    /**
     * Set the display name of the pet
     */
    public void setDisplayName(String name, final boolean save) {

        boolean isDefaultName = false;
        if (name == null)
            name = Language.TAG_TO_REMOVE_NAME.getMessage();

        if (name.equalsIgnoreCase(Language.TAG_TO_REMOVE_NAME.getMessage()) && !GlobalConfig.getInstance().isOverrideDefaultName()) {
            isDefaultName = true;
            if (GlobalConfig.getInstance().isUseDefaultMythicMobNames())
                name = activeMob.getDisplayName();
            else
                name = GlobalConfig.getInstance().getDefaultName()
                        .replace("%player%", Optional.ofNullable(Bukkit.getOfflinePlayer(owner).getName()).orElse("Unknown"))
                        .replace("%pet_id%", id)
                        .replace("%pet_name%", icon.getItemMeta().getDisplayName());
        }

        try {
            if (name != null && ChatColor.stripColor(name).length() > GlobalConfig.instance.getMaxNameLength()) {
                setDisplayName(name.substring(0, GlobalConfig.instance.getMaxNameLength()), save);
                return;
            }
            if (name != null)
                name = name.replace("'", " ");

            currentName = name;
            if (isStillHere()) {
                if (currentName == null || currentName.equalsIgnoreCase(Language.TAG_TO_REMOVE_NAME.getMessage())) {
                    activeMob.getEntity().getBukkitEntity().setCustomName(GlobalConfig.getInstance().getDefaultName()
                            .replace("%player%", Optional.ofNullable(Bukkit.getOfflinePlayer(owner).getName()).orElse("Unknown"))
                            .replace("%pet_id%", id)
                            .replace("%pet_name%", icon.getItemMeta().getDisplayName()));

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            setNameTag(currentName, false);
                        }
                    }.runTaskLater(MCPets.getInstance(), 10L);

                    if (save) {
                        final PlayerData pd = PlayerData.get(owner);
                        pd.getMapOfRegisteredNames().remove(getId());
                        pd.save();
                    }

                    return;
                }

                activeMob.getEntity().getBukkitEntity().setCustomName(currentName);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        setNameTag(currentName, true);
                    }
                }.runTaskLater(MCPets.getInstance(), 10L);

                Debugger.send("§7Applying name " + name + " to pet " + id);
                if (save) {
                    String savedName = currentName;
                    if (isDefaultName)
                        savedName = Language.TAG_TO_REMOVE_NAME.getMessage();
                    final PlayerData pd = PlayerData.get(owner);
                    pd.getMapOfRegisteredNames().put(getId(), savedName);
                    pd.save();
                }
            }

        } catch (final Exception ex) {
            MCPets.getLog().log(Level.SEVERE, "Exception raised while naming the pet | setDisplayName(" + Language.TAG_TO_REMOVE_NAME.getMessage() + ") for the pet " + this.id, ex);
        }
    }

    /**
     * Return a copy of the current pet. Used to implement a player pet in game
     */
    public Pet copy() {
        final Pet pet = new Pet(id);
        pet.setPetStats(petStats);
        pet.setPetLevels(petLevels);
        pet.setMythicMobName(mythicMobName);
        pet.setPermission(permission);
        pet.setDistance(distance);
        pet.setSpawnRange(spawnRange);
        pet.setComingBackRange(comingBackRange);
        pet.setDespawnSkill(despawnSkill);
        pet.setTamingProgressSkill(tamingProgressSkill);
        pet.setTamingOverSkill(tamingOverSkill);
        pet.setMountable(mountable);
        pet.setMountPermission(mountPermission);
        pet.setDespawnOnDismount(despawnOnDismount);
        pet.setMountType(mountType);
        pet.setDefaultInventorySize(defaultInventorySize);
        pet.setAutoRide(autoRide);
        pet.setIcon(icon);
        pet.setSignalStick(signalStick);
        pet.setOwner(owner);
        if (activeMob != null)
            pet.setActiveMob(activeMob);
        pet.setSignals(signals);
        pet.setEnableSignalStickFromMenu(enableSignalStickFromMenu);
        return pet;
    }

    /**
     * Set the specified entity riding on the pet
     */
    public boolean setMount(final Entity ent) {
        if (ent == null)
            return false;

        final EntityMountPetEvent event = new EntityMountPetEvent(ent, this);
        final EntityMountEvent vanillaMountEvent = new EntityMountEvent(ent, activeMob.getEntity().getBukkitEntity());
        Utils.callEvent(vanillaMountEvent);
        Utils.callEvent(event);

        // We still return true as it's a normal situation, not linked to mounting point issue
        if (event.isCancelled() || vanillaMountEvent.isCancelled())
            return true;

        if (isStillHere()) {
            final UUID petUUID = activeMob.getEntity().getUniqueId();
            try {
                if (!MCPets.getModeler().mountDriver(petUUID, ent, mountType)) {
                    activeMob.getEntity().getBukkitEntity().addPassenger(ent);
                    return false;
                }
            } catch (final IllegalStateException ex) {
                Language.ALREADY_MOUNTING.sendMessageFormated(ent);
            }
            return true;
        }
        return false;
    }

    /**
     * Say if the specified entity is riding on the pet
     */
    public boolean hasMount(final Entity ent) {
        if (isStillHere()) {
            final UUID petUUID = activeMob.getEntity().getUniqueId();
            return MCPets.getModeler().hasMount(petUUID, ent);
        }
        return false;
    }

    /**
     * Unset the specified entity riding on the pet
     */
    public void dismount(final Entity ent) {
        if (ent == null)
            return;

        if (isStillHere()) {
            final UUID localUUID = activeMob.getEntity().getUniqueId();
            MCPets.getModeler().dismountRider(localUUID, ent);
        }
    }

    /**
     * Set the name of the pet to the specified name
     * If the global config states we should use MM default naming, then it won't change the name, but you can turn off the visibility
     */
    public void setNameTag(String name, final boolean visible) {
        if (isStillHere()) {
            if (name != null) {
                name = name.replace("'", " ");
                Utils.hex(name);
            }

            final AbstractNameTag tag = getNameBone();
            if (tag == null)
                return;
            tag.setString(name);
            tag.setVisible(visible);
        }
    }

    /**
     * Returns the name bone
     * Null if it's null or invisible
     */
    public AbstractNameTag getNameBone() {
        if (isStillHere()) {
            final UUID localUUID = activeMob.getEntity().getUniqueId();
            return MCPets.getModeler().getNameTag(localUUID);
        }
        return null;
    }

    /**
     * Give a signal stick to the player linked to this pet.
     * Does nothing if the signal stick feature is disabled globally or per-pet configuration.
     */
    public void giveStickSignals(final Player p) {
        if (getOwner() == null || getSignalStick() == null)
            return;

        if (p == null)
            return;

        // Respect per-pet configuration flag
        if (!enableSignalStickFromMenu)
            return;

        clearStickSignals(p, this.id);

        if (!p.getInventory().contains(signalStick))
            p.getInventory().addItem(signalStick);
    }

    /**
     * Get the pet to cast a skill by sending it a signal
     */
    public boolean sendSignal(final String signal) {
        if (signal == null || signal.isEmpty())
            return false;

        final PetCastSkillEvent event = new PetCastSkillEvent(this, signal);
        Utils.callEvent(event);

        if (event.isCancelled())
            return false;

        if (this.isStillHere()) {
            final ActiveMob mob = this.getActiveMob();
            try {
                String evolutionId = null;
                if (petStats != null && petStats.getNextLevel() != null)
                    evolutionId = petStats.getNextLevel().getEvolutionId();

                if (evolutionId != null) {
                    // Iterate through all active pets
                    for (final List<Pet> petList : Pet.getActivePets().values()) {
                        for (final Pet pet : petList) {
                            if (pet.getId().equals(evolutionId) && pet.getOwner() != null && pet.getOwner().equals(owner)) {
                                Language.PET_COULD_NOT_EVOLVE.sendMessage(Bukkit.getPlayer(owner));
                                return false;
                            }
                        }
                    }
                }
                Debugger.send("§aSending signal §6" + signal + "§a to pet " + id);
                mob.signalMob(mob.getEntity(), signal);
                return true;
            } catch (final Exception ex) {
                return false;
            }
        }
        return false;
    }

    /**
     * Says whether or not the pet has skins
     */
    public boolean hasSkins() {
        return PetSkin.getSkins(this) != null && !PetSkin.getSkins(this).isEmpty();
    }

    /**
     * Setup the item with requirements
     * Show stats to make the item show the pet stats if it has some
     */
    public ItemStack buildItem(ItemStack item, final boolean showStats, final String localizedName, String iconName, final List<String> description, final String materialType, final int customModelData, final String textureBase64) {
        final Material mat = materialType != null ? Material.getMaterial(materialType) : null;
        if (iconName == null)
            iconName = "§cUndefined";
        iconName = Utils.translateHexColorCodes("#", "", iconName);
        iconName = Utils.applyPlaceholders(owner, iconName);
        final ArrayList<String> desc = new ArrayList<>();
        if (description != null) {
            for (final String s : description) {
                desc.add(Utils.applyPlaceholders(owner, Utils.translateHexColorCodes("#", "", s)));
            }
        }
        if (mat == null && textureBase64 != null) {
            item = Utils.createHead(iconName, desc, textureBase64);
            final ItemMeta meta = item.getItemMeta();
            meta.setItemName(localizedName);
            item.setItemMeta(meta);
        } else if (mat != null) {
            item = new ItemStack(mat);
            final ItemMeta meta = item.getItemMeta();
            meta.setItemName(localizedName);
            meta.setCustomModelData(customModelData);
            meta.setDisplayName(iconName);
            meta.setLore(desc);
            item.setItemMeta(meta);
        } else if (item == null) {
            item = Utils.createHead(iconName, desc, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ5Y2M1OGFkMjVhMWFiMTZkMzZiYjVkNmQ0OTNjOGY1ODk4YzJiZjMwMmI2NGUzMjU5MjFjNDFjMzU4NjcifX19");
            final ItemMeta meta = item.getItemMeta();
            meta.setItemName(localizedName);
            item.setItemMeta(meta);
        }

        if (showStats)
            return applyStats(item);

        return item;
    }

    public ItemStack applyStats(final ItemStack item) {
        // If we show the stats then we should not modify the actual item, but just its instance in that function
        final ItemStack it = item.clone();
        // Handles the statistics being showed on the icon
        if (petStats != null) {
            final ItemMeta meta = it.getItemMeta();
            // Recover the existing lores
            ArrayList<String> lores = (ArrayList<String>) meta.getLore();
            if (lores == null)
                lores = new ArrayList<>();
            // Add a space
            lores.add(" ");

            // Implement the progress bar
            final StringBuilder progressBar = new StringBuilder();
            final PetLevel nextLevel = petStats.getNextLevel();
            if (nextLevel != null) {
                if (nextLevel.equals(petStats.getCurrentLevel())) {
                    progressBar.append(Language.PET_STATS_MAX_LEVEL.getMessage());
                } else {
                    // Size of the progress bar in the hovering
                    final int progressBarSize = GlobalConfig.instance.getExperienceBarSize();

                    final double experienceRatio = (petStats.getExperience() - petStats.getCurrentLevel().getExpThreshold()) / (nextLevel.getExpThreshold() - petStats.getCurrentLevel().getExpThreshold());
                    final int indexProgress = Math.min(progressBarSize, (int) (experienceRatio * progressBarSize + 0.5));

                    for (int i = 0; i < progressBarSize; i++) {
                        if (i < indexProgress)
                            progressBar.append(GlobalConfig.getInstance().getExperienceColorDone() +
                                    GlobalConfig.getInstance().getExperienceSymbol() +
                                    GlobalConfig.getInstance().getExperienceColorLeft());
                        else
                            progressBar.append(GlobalConfig.getInstance().getExperienceColorLeft() +
                                    GlobalConfig.getInstance().getExperienceSymbol() +
                                    GlobalConfig.getInstance().getExperienceColorLeft());
                    }
                }
                if (nextLevel.getEvolutionId() != null &&
                        !nextLevel.canEvolve(owner, Pet.getFromId(nextLevel.getEvolutionId()))) {
                    progressBar.append('\n').append(Language.PET_STATS_EVOLUTION_ALREADY_OWNED.getMessage());
                }
            }

            // Get the positive or negative sign symbol of the bonus
            final String signSymbol_damageModifer = Utils.getSignSymbol(petStats.getDamageModifier() - 1);
            final String signSymbol_resistanceModifer = Utils.getSignSymbol(petStats.getResistanceModifier() - 1);
            final String signSymbol_power = Utils.getSignSymbol(petStats.getPower() - 1);

            String currentHealthStr = Integer.toString((int) petStats.getCurrentHealth());
            if (petStats.getCurrentHealth() == 0 &&
                    petStats.getRespawnTimer() != null && !petStats.getRespawnTimer().isRunning())
                currentHealthStr = Integer.toString((int) petStats.getRespawnHealth());

            // Handles the status of the pet
            String status = Language.PET_STATUS_ALIVE.getMessage();
            if (petStats.isRespawnTimerRunning()) {
                status = Language.PET_STATUS_DEAD.getMessageFormatted(new FormatArg("%timeleft%",
                        Integer.toString((int) petStats.getRespawnTimer().getRemainingTime())));
            } else if (petStats.isRevokeTimerRunning())
                status = Language.PET_STATUS_REVOKED.getMessageFormatted(new FormatArg("%timeleft%",
                        Integer.toString((int) petStats.getRevokeTimer().getRemainingTime())));

            final String statsLore = Language.PET_STATS.getMessageFormatted(
                    new FormatArg("%status%", status),
                    new FormatArg("%levelname%", petStats.getCurrentLevel().getLevelName()),
                    new FormatArg("%health%", currentHealthStr),
                    new FormatArg("%maxhealth%", Integer.toString((int) petStats.getCurrentLevel().getMaxHealth())),
                    new FormatArg("%regeneration%", Double.toString(petStats.getCurrentLevel().getRegeneration())),
                    new FormatArg("%damagemodifier%", signSymbol_damageModifer + (int) (100 * (petStats.getDamageModifier() - 1))),
                    new FormatArg("%resistancemodifier%", signSymbol_resistanceModifer + (int) (100 * (petStats.getResistanceModifier() - 1))),
                    new FormatArg("%power%", signSymbol_power + (int) (100 * (petStats.getPower() - 1))),
                    new FormatArg("%experience%", Integer.toString((int) petStats.getExperience())),
                    new FormatArg("%threshold%", Integer.toString((int) petStats.getNextLevel().getExpThreshold())),
                    new FormatArg("%progressbar%", progressBar.toString()));

            // add the formatted statistics
            lores.addAll(Arrays.asList(statsLore.split("\n")));

            meta.setLore(lores);
            it.setItemMeta(meta);
        }
        return it;
    }

    /**
     * Format : "AlmPet;petId"
     */
    public String toString() {
        return "AlmPet;" + id;
    }

    /**
     * Value of the inventory size, taking into account the pet stats
     */
    public int getInventorySize() {
        // setup the pet stats so we can tell if we should extend the inventory or not
        setPetStats();

        int inventorySize = 0;
        if (petStats == null)
            inventorySize = defaultInventorySize;
        else
            inventorySize = petStats.getExtendedInventorySize();

        while (inventorySize % 9 != 0)
            inventorySize++;

        return Math.min(54, inventorySize);
    }

    /**
     * Compare using mythicmobs name
     */
    public boolean equals(final Pet other) {
        return this.id.equals(other.getId());
    }

}
