package fr.nocsy.mcpets.listeners;

import fr.nocsy.mcpets.MCPets;
import fr.nocsy.mcpets.data.Pet;
import fr.nocsy.mcpets.data.PetDespawnReason;
import fr.nocsy.mcpets.data.config.GlobalConfig;
import fr.nocsy.mcpets.data.livingpets.PetLevel;
import fr.nocsy.mcpets.data.livingpets.PetStats;
import fr.nocsy.mcpets.data.serializer.PetStatsSerializer;
import fr.nocsy.mcpets.events.PetDeathEvent;
import fr.nocsy.mcpets.events.PetDespawnEvent;
import fr.nocsy.mcpets.events.PetSpawnEvent;
import fr.nocsy.mcpets.events.PetSpawnedEvent;
import fr.nocsy.mcpets.utils.PetTimer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class LivingPetsListenerTest {
    private final LivingPetsListener listener = new LivingPetsListener();
    private final Map<Integer, Runnable> tasks = new LinkedHashMap<>();
    private final UUID ownerId = UUID.randomUUID();
    private MockedStatic<Bukkit> bukkit;
    private MockedStatic<MCPets> plugin;
    private GlobalConfig previousConfig;
    private Pet pet;
    private PetStats stats;
    private PetLevel level;
    private ActiveMob mob;
    private LivingEntity entity;
    private boolean present;
    private double health;
    private int healthWrites;
    private int notifications;
    private int nextTask;
    private Runnable beforeDeath = () -> { };

    @Before
    public void setUp() {
        previousConfig = GlobalConfig.instance;
        GlobalConfig.instance = mock(GlobalConfig.class);
        when(GlobalConfig.instance.getPercentHealthOnRespawn()).thenReturn(.25);
        BukkitScheduler scheduler = mock(BukkitScheduler.class);
        PluginManager manager = mock(PluginManager.class);
        MCPets instance = mock(MCPets.class);
        bukkit = mockStatic(Bukkit.class);
        plugin = mockStatic(MCPets.class);
        bukkit.when(Bukkit::getScheduler).thenReturn(scheduler);
        bukkit.when(Bukkit::getPluginManager).thenReturn(manager);
        plugin.when(MCPets::getInstance).thenReturn(instance);
        when(scheduler.scheduleSyncRepeatingTask(any(Plugin.class), any(Runnable.class), anyLong(), anyLong()))
                .thenAnswer(call -> {
                    int id = ++nextTask;
                    tasks.put(id, call.getArgument(1));
                    return id;
                });
        doAnswer(call -> {
            tasks.remove(call.getArgument(0));
            return null;
        }).when(scheduler).cancelTask(anyInt());
        doAnswer(call -> {
            if (call.getArgument(0) instanceof PetDeathEvent death) {
                assertTrue("Recursive death notification", ++notifications <= 5);
                beforeDeath.run();
                listener.respawnCooldownHandler(death);
            }
            return null;
        }).when(manager).callEvent(any(Event.class));

        level = mock(PetLevel.class);
        when(level.getMaxHealth()).thenReturn(100.0);
        when(level.getRegeneration()).thenReturn(5.0);
        when(level.getRespawnCooldown()).thenReturn(3);
        when(level.getRevokeCooldown()).thenReturn(2);
        when(level.getInventoryExtension()).thenReturn(9);
        when(level.getLevelId()).thenReturn("level-1");
        pet = newPet();
        stats = new PetStats(pet, 42, 60, level);
        stats.launchTimers();
        tick();
        spawnEntity();
    }

    private Pet newPet() {
        Pet result = mock(Pet.class);
        when(result.getPetStats()).thenAnswer(call -> stats);
        when(result.getActiveMob()).thenAnswer(call -> mob);
        when(result.isStillHere()).thenAnswer(call -> present);
        when(result.getOwner()).thenReturn(ownerId);
        when(result.getId()).thenReturn("dragon");
        when(result.getPetLevels()).thenReturn(List.of(level));
        when(result.getDefaultInventorySize()).thenReturn(9);
        return result;
    }

    @After
    public void tearDown() {
        if (plugin != null) plugin.close();
        if (bukkit != null) bukkit.close();
        GlobalConfig.instance = previousConfig;
        PetTimer.getRunningTimers().clear();
    }

    private void spawnEntity() {
        entity = mock(LivingEntity.class);
        UUID id = UUID.randomUUID();
        MetadataValue metadata = mock(MetadataValue.class);
        when(metadata.value()).thenReturn(pet);
        when(entity.hasMetadata("AlmPet")).thenReturn(true);
        when(entity.getMetadata("AlmPet")).thenReturn(List.of(metadata));
        when(entity.getUniqueId()).thenReturn(id);
        when(entity.getHealth()).thenAnswer(call -> health);
        doAnswer(call -> {
            assertTrue("Recursive health write", ++healthWrites <= 10);
            health = call.getArgument(0);
            if (health <= 0) death(entity);
            return null;
        }).when(entity).setHealth(anyDouble());
        AbstractEntity adapter = mock(AbstractEntity.class);
        when(adapter.getUniqueId()).thenReturn(id);
        when(adapter.getHealth()).thenAnswer(call -> health);
        doAnswer(call -> {
            entity.setHealth(call.getArgument(0));
            return null;
        }).when(adapter).setHealth(anyDouble());
        mob = mock(ActiveMob.class);
        when(mob.getEntity()).thenReturn(adapter);
        present = true;
        health = 100;
        listener.respawnHealth(new PetSpawnedEvent(pet));
        healthWrites = 0;
    }

    private void death(LivingEntity dying) {
        EntityDeathEvent event = mock(EntityDeathEvent.class);
        when(event.getEntity()).thenReturn(dying);
        listener.petDeathHandler(event);
    }

    private void combatDeath() {
        health = 0;
        death(entity);
    }

    private void tick() {
        for (int id : new ArrayList<>(tasks.keySet())) {
            Runnable task = tasks.get(id);
            if (task != null) task.run();
        }
    }

    @Test
    public void combatDeathDoesNotWriteEntityHealth() {
        combatDeath();
        assertEquals(0, healthWrites);
        assertEquals(1, notifications);
        assertTrue(stats.isDead());
        assertEquals(3, stats.getRespawnTimer().getRemainingTime());
    }

    @Test
    public void recordingDeathDoesNotKillTheEntity() {
        stats.setDead();
        assertTrue(stats.isDead());
        assertEquals(60, health, 0);
        assertEquals(0, healthWrites);
    }

    @Test
    public void intentionalLethalWriteStillKillsOnce() {
        stats.setHealth(0);
        assertEquals(1, healthWrites);
        assertEquals(1, notifications);
        assertTrue(stats.isDead());
        assertTrue(stats.isRespawnTimerRunning());
    }

    @Test
    public void reentryAndDuplicatesDoNotRepeatEffects() {
        beforeDeath = () -> death(entity);
        combatDeath();
        tick();
        for (int i = 0; i < 100; i++) {
            death(entity);
            listener.respawnCooldownHandler(new PetDeathEvent(pet));
        }
        assertEquals(1, notifications);
        assertEquals(0, healthWrites);
        assertEquals(2, stats.getRespawnTimer().getRemainingTime());
    }

    @Test
    public void zeroSavedHealthDoesNotSkipTheFirstDeath() {
        health = 0;
        stats.updateHealth();
        death(entity);
        assertEquals(1, notifications);
        assertEquals(3, stats.getRespawnTimer().getRemainingTime());
    }

    @Test
    public void directDeathEventStartsCooldownOnce() {
        listener.respawnCooldownHandler(new PetDeathEvent(pet));
        tick();
        listener.respawnCooldownHandler(new PetDeathEvent(pet));
        assertTrue(stats.isDead());
        assertEquals(2, stats.getRespawnTimer().getRemainingTime());
        assertEquals(0, healthWrites);
    }

    @Test
    public void deathIsRecordedAfterMythicDetachesTheMob() {
        mob = null;
        present = false;
        combatDeath();
        assertTrue(stats.isDead());
        assertEquals(1, notifications);
        assertTrue(stats.isRespawnTimerRunning());
    }

    @Test
    public void regenerationAndDelayedUpdatesCannotReviveThePet() {
        combatDeath();
        health = 60;
        stats.updateHealth();
        stats.setHealth(100);
        tick();
        assertEquals(0, stats.getCurrentHealth(), 0);
        assertEquals(0, healthWrites);
        assertFalse(stats.getRegenerationTimer().isRunning());
    }

    @Test
    public void regenerationStopsForZeroSavedHealthBeforeNotification() {
        stats.setDead();
        tick();
        assertEquals(0, healthWrites);
        assertTrue(stats.isDead());
        assertFalse(stats.getRegenerationTimer().isRunning());
    }

    @Test
    public void respawnedPetCanDieAgainAndIgnoresOldEntity() {
        combatDeath();
        LivingEntity oldEntity = entity;
        spawnEntity();
        assertEquals(25, stats.getCurrentHealth(), 0);
        death(oldEntity);
        assertFalse(stats.isDead());
        combatDeath();
        assertEquals(2, notifications);
        assertTrue(stats.isDead());
        assertEquals(0, healthWrites);
    }

    @Test
    public void duplicateSpawnNotificationCannotReviveTheBody() {
        combatDeath();
        listener.respawnHealth(new PetSpawnedEvent(pet));
        death(entity);
        assertTrue(stats.isDead());
        assertEquals(1, notifications);
        assertEquals(0, healthWrites);
    }

    @Test
    public void oldPetObjectCannotKillReplacementSharingItsStats() {
        combatDeath();
        LivingEntity oldEntity = entity;
        when(pet.getActiveMob()).thenReturn(null);
        when(pet.isStillHere()).thenReturn(false);
        pet = newPet();
        spawnEntity();
        death(oldEntity);
        assertEquals(25, stats.getCurrentHealth(), 0);
        assertEquals(1, notifications);
        combatDeath();
        assertEquals(2, notifications);
    }

    @Test
    public void regenerationRestartsAfterRespawn() {
        combatDeath();
        tick();
        spawnEntity();
        tick();
        assertEquals(30, stats.getCurrentHealth(), 0);
        assertEquals(1, healthWrites);
        combatDeath();
        assertEquals(2, notifications);
    }

    @Test
    public void ordinaryResummonPreservesHealthAndProgression() {
        stats.setHealth(37);
        listener.revokeCooldownHandler(new PetDespawnEvent(pet, PetDespawnReason.REVOKE));
        assertTrue(stats.isRevokeTimerRunning());
        assertFalse(stats.isRespawnTimerRunning());
        spawnEntity();
        assertEquals(37, stats.getCurrentHealth(), 0);
        assertEquals(42, stats.getExperience(), 0);
        assertEquals(18, stats.getExtendedInventorySize());
        tick();
        assertEquals(42, stats.getCurrentHealth(), 0);
    }

    @Test
    public void replacementDoesNotStartDeathOrRevokeCooldown() {
        listener.revokeCooldownHandler(new PetDespawnEvent(pet, PetDespawnReason.REPLACED));
        assertFalse(stats.isDead());
        assertFalse(stats.isRespawnTimerRunning());
        assertFalse(stats.isRevokeTimerRunning());
    }

    @Test
    public void deathSerializationPreservesProgression() {
        combatDeath();
        PetStatsSerializer saved = PetStatsSerializer.unserialize(stats.serialize());
        assertEquals(0, saved.getCurrentHealth(), 0);
        assertEquals(42, saved.getExperience(), 0);
        assertEquals("level-1", saved.getLevelId());
        assertFalse(saved.JSONformatted().contains("deathProcessed"));
    }

    @Test
    public void cooldownBlocksSummoningUntilItExpires() {
        combatDeath();
        PetSpawnEvent blocked = new PetSpawnEvent(pet, null);
        listener.attemptToSpawn(blocked);
        assertTrue(blocked.isCancelled());
        tick(); tick(); tick();
        PetSpawnEvent allowed = new PetSpawnEvent(pet, null);
        listener.attemptToSpawn(allowed);
        assertFalse(allowed.isCancelled());
    }

    @Test
    public void firstDeathAutomaticallyRespawnsAndNextDeathWorks() {
        when(GlobalConfig.instance.isAutoRespawn()).thenReturn(true);
        Player owner = mock(Player.class);
        Location location = mock(Location.class);
        when(owner.getLocation()).thenReturn(location);
        bukkit.when(() -> Bukkit.getPlayer(ownerId)).thenReturn(owner);
        when(pet.spawn(location, true)).thenAnswer(call -> {
            spawnEntity();
            return Pet.MOB_SPAWN;
        });
        combatDeath();
        present = false;
        mob = null;
        tick(); tick(); tick();
        verify(pet).spawn(location, true);
        assertEquals(25, stats.getCurrentHealth(), 0);
        combatDeath();
        assertEquals(2, notifications);
        assertTrue(stats.isDead());
    }

    @Test
    public void ownerDeathIsNotPetDeath() {
        death(mock(Player.class));
        assertEquals(0, notifications);
        assertEquals(60, stats.getCurrentHealth(), 0);
        assertFalse(stats.isRespawnTimerRunning());
        assertFalse(stats.isRevokeTimerRunning());
    }
}
