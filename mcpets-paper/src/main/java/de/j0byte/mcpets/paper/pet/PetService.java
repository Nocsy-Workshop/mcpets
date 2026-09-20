package de.j0byte.mcpets.paper.pet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.api.document.PetSettings;
import de.j0byte.mcpets.api.message.PetSyncMessage;
import de.j0byte.mcpets.paper.config.ConfigManager;
import de.j0byte.mcpets.paper.config.GeneralConfig;
import de.j0byte.mcpets.paper.config.PetDefinition;
import de.j0byte.mcpets.paper.storage.PetDataService;
import de.j0byte.mcpets.paper.storage.PetSyncService;
import de.j0byte.shark.api.Shark;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.bukkit.platform.BukkitEntity;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.nms.ModelNametag;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.ModelScaler;
import kr.toxicity.model.api.util.function.BonePredicate;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Spawnt, bewegt und entfernt Pets.
 *
 * <p>Ein Pet besteht aus einer unsichtbaren Basis-Entity und einem
 * BetterModel-{@link EntityTracker}, der das Modell darauf rendert. MythicMobs und
 * ModelEngine werden nicht mehr gebraucht - das Modell kommt komplett von BetterModel.</p>
 */
@Singleton
public class PetService {

    private final Plugin plugin;
    private final ConfigManager configs;
    private final PetDataService data;
    private final PetSyncService sync;
    private final Shark shark;
    private final Logger logger;

    private final Map<UUID, ActivePet> active = new ConcurrentHashMap<>();

    private BukkitTask followTask;

    @Inject
    public PetService(
            @NotNull final Plugin plugin,
            @NotNull final ConfigManager configs,
            @NotNull final PetDataService data,
            @NotNull final PetSyncService sync,
            @NotNull final Shark shark,
            @PluginLogger @NotNull final Logger logger) {

        this.plugin = plugin;
        this.configs = configs;
        this.data = data;
        this.sync = sync;
        this.shark = shark;
        this.logger = logger;
    }

    // ------------------------------------------------------------------ Lebenszyklus

    /**
     * Startet die Folge-Logik. Das Intervall kommt aus der {@code config.yml}.
     */
    public void start() {
        stop();
        final long interval = Math.max(1L, this.configs.general().getFollowIntervalTicks());
        this.followTask = this.plugin.getServer().getScheduler()
                .runTaskTimer(this.plugin, this::tick, interval, interval);
    }

    public void stop() {
        if (this.followTask != null) {
            this.followTask.cancel();
            this.followTask = null;
        }
    }

    /**
     * Entfernt alle gespawnten Pets, z. B. beim Neuladen oder beim Herunterfahren.
     */
    public void despawnAll() {
        for (final UUID owner : Map.copyOf(this.active).keySet()) {
            remove(owner);
        }
    }

    // ------------------------------------------------------------------ Abfragen

    @NotNull
    public Optional<ActivePet> activePet(@NotNull final UUID owner) {
        return Optional.ofNullable(this.active.get(owner));
    }

    public boolean hasActivePet(@NotNull final UUID owner) {
        return this.active.containsKey(owner);
    }

    // ------------------------------------------------------------------ Spawnen

    /**
     * Spawnt das Pet fuer den Spieler und merkt es sich als aktives Pet.
     *
     * <p>Ein bereits aktives Pet wird vorher abgesetzt - ein Spieler hat immer
     * hoechstens ein Pet draussen.</p>
     *
     * @return das gespawnte Pet, oder leer wenn BetterModel das Modell nicht kennt
     */
    @NotNull
    public Optional<ActivePet> spawn(@NotNull final Player owner, @NotNull final PetDefinition definition) {
        final ModelRenderer renderer = BetterModel.modelOrNull(definition.model());
        if (renderer == null) {
            this.logger.warning("BetterModel does not know the model '" + definition.model()
                    + "' used by pet '" + definition.id() + "'.");
            return Optional.empty();
        }

        despawn(owner, false);

        final Location location = spawnLocation(owner);
        final Entity base = spawnBaseEntity(location, definition);
        if (base == null) {
            return Optional.empty();
        }

        final EntityTracker tracker;
        try {
            tracker = renderer.create(new BukkitEntity(base));
        } catch (final RuntimeException exception) {
            base.remove();
            this.logger.log(Level.WARNING,
                    "Failed to create the BetterModel tracker for pet '" + definition.id() + "'", exception);
            return Optional.empty();
        }

        if (definition.scale() != 1.0D) {
            tracker.scaler(ModelScaler.value((float) definition.scale()));
        }

        final ActivePet pet = new ActivePet(owner.getUniqueId(), definition, base, tracker);
        this.active.put(owner.getUniqueId(), pet);

        applyNametag(owner, pet);
        playAnimation(pet, definition.spawnAnimation());
        playAnimation(pet, definition.idleAnimation());

        this.data.setActivePet(owner.getUniqueId(), definition.id());
        this.sync.publish(PetSyncMessage.petActivated(owner.getUniqueId(), definition.id()));
        return Optional.of(pet);
    }

    /**
     * Setzt das Pet des Spielers ab.
     *
     * @param clearActive ob auch das gespeicherte "aktives Pet" geleert wird. Beim
     *                    Wechsel auf ein anderes Pet soll das nicht passieren, beim
     *                    bewussten Absetzen schon.
     * @return {@code true} wenn ueberhaupt ein Pet draussen war
     */
    public boolean despawn(@NotNull final Player owner, final boolean clearActive) {
        final boolean removed = remove(owner.getUniqueId());

        if (clearActive) {
            this.data.setActivePet(owner.getUniqueId(), null);
            this.sync.publish(PetSyncMessage.petDespawned(owner.getUniqueId()));
        }
        return removed;
    }

    /**
     * Entfernt Modell und Basis-Entity, ohne an der Datenbank etwas zu aendern.
     */
    public boolean remove(@NotNull final UUID owner) {
        final ActivePet pet = this.active.remove(owner);
        if (pet == null) {
            return false;
        }

        try {
            pet.getTracker().close();
        } catch (final RuntimeException exception) {
            this.logger.log(Level.FINE, "Failed to close the tracker of pet " + pet.petId(), exception);
        }
        pet.getBaseEntity().remove();
        return true;
    }

    // ------------------------------------------------------------------ Namen

    /**
     * Uebernimmt den Namen aus den Einstellungen auf ein bereits gespawntes Pet.
     */
    public void refreshName(@NotNull final Player owner) {
        activePet(owner.getUniqueId()).ifPresent(pet -> applyNametag(owner, pet));
    }

    /**
     * Der Name, der ueber dem Pet steht: der eigene Name des Spielers, sonst der
     * Standard aus der {@code pets.yml}.
     */
    @NotNull
    public String resolveName(@NotNull final UUID owner, @NotNull final PetDefinition definition) {
        final PetSettings settings = this.data.settings(owner, definition.id());
        return settings.displayNameOr(definition.defaultName());
    }

    private void applyNametag(@NotNull final Player owner, @NotNull final ActivePet pet) {
        final PetDefinition definition = pet.getDefinition();
        final Component name = this.shark.messages().renderItem(
                owner,
                resolveName(owner.getUniqueId(), definition),
                Placeholder.unparsed("owner", owner.getName()),
                Placeholder.unparsed("pet_id", definition.id()));

        final BonePredicate predicate = BonePredicate.name(definition.nametagBone()).withoutChildren();
        final boolean created = pet.getTracker().createNametag(predicate, (bone, tag) -> {
            tag.alwaysVisible(definition.nameVisible());
            tag.component(name);
        });

        if (created) {
            return;
        }

        // Das Nametag gab es schon (z. B. nach einem Umbenennen) - dann nur den Text tauschen.
        for (final var bone : pet.getTracker().bones()) {
            final ModelNametag tag = bone.getNametag();
            if (tag != null) {
                tag.alwaysVisible(definition.nameVisible());
                tag.component(name);
            }
        }
    }

    // ------------------------------------------------------------------ Bewegung

    private void tick() {
        final GeneralConfig general = this.configs.general();

        for (final ActivePet pet : Map.copyOf(this.active).values()) {
            final Player owner = this.plugin.getServer().getPlayer(pet.getOwner());
            if (owner == null || !owner.isOnline()) {
                remove(pet.getOwner());
                continue;
            }
            if (!pet.alive()) {
                remove(pet.getOwner());
                continue;
            }
            follow(general, owner, pet);
        }
    }

    private void follow(
            @NotNull final GeneralConfig general,
            @NotNull final Player owner,
            @NotNull final ActivePet pet) {

        final Location target = owner.getLocation();
        final Location current = pet.getBaseEntity().getLocation();

        // Weltwechsel oder zu weit weg: hinterherteleportieren statt ewig laufen.
        if (!target.getWorld().equals(current.getWorld())
                || current.distanceSquared(target) > square(general.getFollowTeleportDistance())) {
            pet.getBaseEntity().teleport(spawnLocation(owner));
            setWalking(pet, false);
            return;
        }

        final double distance = current.distance(target);
        if (distance <= general.getFollowStartDistance()) {
            lookAt(pet, target);
            setWalking(pet, false);
            return;
        }

        // Schrittweite waechst mit der Entfernung, sonst haengt das Pet beim Sprinten zurueck.
        final double remaining = distance - general.getFollowStopDistance();
        if (remaining <= 0.0D) {
            setWalking(pet, false);
            return;
        }
        final double step = Math.min(
                remaining,
                general.getFollowSpeed() * Math.max(1.0D, distance / general.getFollowStartDistance()));

        final Vector direction = target.toVector().subtract(current.toVector());
        if (direction.lengthSquared() < 1.0E-4D) {
            setWalking(pet, false);
            return;
        }

        final Location next = current.clone().add(direction.normalize().multiply(step));
        next.setDirection(target.toVector().subtract(next.toVector()));

        pet.getBaseEntity().teleport(next);
        setWalking(pet, true);
    }

    private void lookAt(@NotNull final ActivePet pet, @NotNull final Location target) {
        final Location current = pet.getBaseEntity().getLocation();
        final Vector direction = target.toVector().subtract(current.toVector());
        if (direction.lengthSquared() < 1.0E-4D) {
            return;
        }
        current.setDirection(direction);
        pet.getBaseEntity().teleport(current);
    }

    /**
     * Wechselt zwischen Lauf- und Leerlauf-Animation, aber nur beim echten Wechsel.
     */
    private void setWalking(@NotNull final ActivePet pet, final boolean walking) {
        if (!pet.walking(walking)) {
            return;
        }

        final PetDefinition definition = pet.getDefinition();
        final String starting = walking ? definition.walkAnimation() : definition.idleAnimation();
        final String stopping = walking ? definition.idleAnimation() : definition.walkAnimation();

        if (stopping != null) {
            pet.getTracker().stopAnimation(bone -> true, stopping);
        }
        playAnimation(pet, starting);
    }

    private void playAnimation(@NotNull final ActivePet pet, @Nullable final String animation) {
        if (animation == null) {
            return;
        }
        try {
            pet.getTracker().animate(animation);
        } catch (final RuntimeException exception) {
            this.logger.log(Level.FINE,
                    "Failed to play animation '" + animation + "' on pet " + pet.petId(), exception);
        }
    }

    // ------------------------------------------------------------------ Basis-Entity

    /**
     * Die Stelle, an der das Pet erscheint: leicht hinter dem Besitzer.
     */
    @NotNull
    private Location spawnLocation(@NotNull final Player owner) {
        final Location location = owner.getLocation().clone();
        final Vector direction = location.getDirection().setY(0.0D);

        // Schaut der Spieler senkrecht nach oben oder unten, ist der waagerechte
        // Anteil null - normalize() wuerde daraus NaN machen und die Entity ins
        // Nirgendwo teleportieren.
        if (direction.lengthSquared() < 1.0E-4D) {
            return location;
        }
        return location.subtract(direction.normalize().multiply(this.configs.general().getSpawnOffset()));
    }

    /**
     * Spawnt die unsichtbare Entity, die das Modell traegt.
     */
    @Nullable
    private Entity spawnBaseEntity(@NotNull final Location location, @NotNull final PetDefinition definition) {
        final Class<? extends Entity> type = definition.baseEntity().getEntityClass();
        if (type == null || location.getWorld() == null) {
            this.logger.warning("Pet '" + definition.id() + "' has an unspawnable base entity: "
                    + definition.baseEntity());
            return null;
        }

        try {
            return location.getWorld().spawn(location, type, this::configureBaseEntity);
        } catch (final IllegalArgumentException exception) {
            this.logger.log(Level.WARNING,
                    "Failed to spawn the base entity of pet '" + definition.id() + "'", exception);
            return null;
        }
    }

    /**
     * Die Basis-Entity ist reiner Traeger: unsichtbar, unverwundbar, ohne KI und
     * ohne Schwerkraft - bewegt wird sie ausschliesslich von {@link #follow}.
     */
    private void configureBaseEntity(@NotNull final Entity entity) {
        entity.setPersistent(false);
        entity.setSilent(true);
        entity.setInvulnerable(true);
        entity.setGravity(false);

        if (entity instanceof LivingEntity living) {
            living.setInvisible(true);
            living.setCollidable(false);
            living.setRemoveWhenFarAway(false);
            living.setCanPickupItems(false);
        }
        if (entity instanceof Mob mob) {
            mob.setAware(false);
        }
        if (entity instanceof org.bukkit.entity.ArmorStand stand) {
            stand.setVisible(false);
            stand.setMarker(true);
            stand.setBasePlate(false);
            stand.setArms(false);
        }
    }

    private static double square(final double value) {
        return value * value;
    }
}
