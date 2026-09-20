package de.j0byte.mcpets.paper.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.api.document.PetProfile;
import de.j0byte.mcpets.api.document.PetSettings;
import de.j0byte.mcpets.paper.config.PetDefinition;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Gecachter Zugriff auf Profile und Pet-Einstellungen.
 *
 * <p>Fuer online Spieler liegt alles im Speicher, damit GUIs und Commands
 * synchron arbeiten koennen. Geschrieben wird immer asynchron nach MongoDB.
 * Die verlaessliche Quelle bleibt die Datenbank - der Cache ist nur eine
 * Abkuerzung fuer die Dauer der Session.</p>
 */
@Singleton
public class PetDataService {

    private final PetStorage storage;
    private final Logger logger;

    private final Map<UUID, PetProfile> profiles = new ConcurrentHashMap<>();
    private final Map<String, PetSettings> settings = new ConcurrentHashMap<>();

    @Inject
    public PetDataService(
            @NotNull final PetStorage storage, @PluginLogger @NotNull final Logger logger) {
        this.storage = storage;
        this.logger = logger;
    }

    // ------------------------------------------------------------------ Lebenszyklus

    /**
     * Laedt Profil und alle Pet-Einstellungen des Spielers in den Cache.
     */
    @NotNull
    public CompletableFuture<PetProfile> onJoin(@NotNull final UUID uuid, @NotNull final String name) {
        return this.storage.loadOrCreateProfile(uuid, name)
                .thenCompose(profile -> this.storage.findAllSettings(uuid).thenApply(all -> {
                    cache(profile, all);
                    return profile;
                }));
    }

    /**
     * Speichert das Profil und raeumt den Cache des Spielers auf.
     */
    @NotNull
    public CompletableFuture<Void> onQuit(@NotNull final UUID uuid) {
        final PetProfile profile = this.profiles.remove(uuid);
        this.settings.keySet().removeIf(key -> key.startsWith(uuid + ":"));

        if (profile == null) {
            return CompletableFuture.completedFuture(null);
        }
        return this.storage.saveProfile(profile)
                .thenAccept(saved -> {
                })
                .exceptionally(error -> {
                    this.logger.log(Level.WARNING, "Failed to save pet profile of " + uuid, error);
                    return null;
                });
    }

    /**
     * Wirft den Spieler aus dem Cache, ohne zu speichern. Wird benutzt, wenn ein
     * anderer Server gemeldet hat, dass sich die Daten geaendert haben.
     */
    public void invalidate(@NotNull final UUID uuid) {
        this.profiles.remove(uuid);
        this.settings.keySet().removeIf(key -> key.startsWith(uuid + ":"));
    }

    public void invalidateAll() {
        this.profiles.clear();
        this.settings.clear();
    }

    private void cache(@NotNull final PetProfile profile, @NotNull final List<PetSettings> all) {
        this.profiles.put(profile.getUuid(), profile);
        for (final PetSettings value : all) {
            this.settings.put(value.getId(), value);
        }
    }

    // ------------------------------------------------------------------ Lesen

    /**
     * @return das gecachte Profil, oder leer wenn der Spieler nicht geladen ist
     */
    @NotNull
    public Optional<PetProfile> profile(@NotNull final UUID uuid) {
        return Optional.ofNullable(this.profiles.get(uuid));
    }

    /**
     * Ob das Pet in der Datenbank auf dem Spieler steht.
     *
     * <p>Beruecksichtigt keine Permissions - dafuer gibt es
     * {@link #owns(Player, PetDefinition)}.</p>
     */
    public boolean owns(@NotNull final UUID uuid, @NotNull final String petId) {
        return profile(uuid).map(profile -> profile.owns(petId)).orElse(false);
    }

    /**
     * Ob der Spieler dieses Pet nutzen darf.
     *
     * <p>Das ist die massgebliche Frage fuer Menues und Commands: ein Pet gehoert
     * einem Spieler entweder, weil es ihm gegeben wurde, oder weil seine Permission
     * es freischaltet.</p>
     */
    public boolean owns(@NotNull final Player player, @NotNull final PetDefinition definition) {
        return definition.grantedByPermission(player) || owns(player.getUniqueId(), definition.id());
    }

    @Nullable
    public String activePet(@NotNull final UUID uuid) {
        return profile(uuid).map(PetProfile::activePet).orElse(null);
    }

    /**
     * Die Einstellungen des Spielers fuer dieses Pet.
     *
     * <p>Existieren noch keine, wird ein leeres Objekt angelegt und gecacht -
     * gespeichert wird es erst, wenn der Spieler wirklich etwas aendert.</p>
     */
    @NotNull
    public PetSettings settings(@NotNull final UUID owner, @NotNull final String petId) {
        return this.settings.computeIfAbsent(
                PetSettings.documentId(owner, petId), key -> new PetSettings(owner, petId));
    }

    // ------------------------------------------------------------------ Schreiben

    /**
     * Setzt das aktive Pet. {@code null} bedeutet "kein Pet aktiv".
     */
    @NotNull
    public CompletableFuture<Void> setActivePet(@NotNull final UUID uuid, @Nullable final String petId) {
        final PetProfile profile = this.profiles.get(uuid);
        if (profile == null) {
            return CompletableFuture.completedFuture(null);
        }
        profile.setActivePet(petId);
        return persist(profile);
    }

    /**
     * @return {@code true} wenn der Spieler das Pet vorher nicht besass
     */
    @NotNull
    public CompletableFuture<Boolean> grant(@NotNull final UUID uuid, @NotNull final String petId) {
        return mutateProfile(uuid, profile -> profile.grant(petId));
    }

    /**
     * @return {@code true} wenn der Spieler das Pet vorher besass
     */
    @NotNull
    public CompletableFuture<Boolean> revoke(@NotNull final UUID uuid, @NotNull final String petId) {
        return mutateProfile(uuid, profile -> profile.revoke(petId));
    }

    /**
     * Speichert geaenderte Pet-Einstellungen.
     */
    @NotNull
    public CompletableFuture<Void> saveSettings(@NotNull final PetSettings value) {
        this.settings.put(value.getId(), value);
        return this.storage.saveSettings(value)
                .thenAccept(saved -> {
                })
                .exceptionally(error -> {
                    this.logger.log(Level.WARNING, "Failed to save pet settings " + value.getId(), error);
                    return null;
                });
    }

    /**
     * Aendert das Profil eines Spielers, auch wenn er gerade offline ist.
     *
     * <p>Ist der Spieler geladen, wird das gecachte Objekt direkt mutiert; sonst
     * wird das Dokument geladen, geaendert und zurueckgeschrieben.</p>
     */
    @NotNull
    private CompletableFuture<Boolean> mutateProfile(
            @NotNull final UUID uuid, @NotNull final java.util.function.Predicate<PetProfile> change) {

        final PetProfile cached = this.profiles.get(uuid);
        if (cached != null) {
            final boolean changed = change.test(cached);
            return changed ? persist(cached).thenApply(ignored -> true)
                    : CompletableFuture.completedFuture(false);
        }

        return this.storage.findProfile(uuid).thenCompose(found -> {
            final PetProfile profile = found.orElseGet(() -> new PetProfile(uuid, uuid.toString()));
            if (!change.test(profile)) {
                return CompletableFuture.completedFuture(false);
            }
            return this.storage.saveProfile(profile).thenApply(saved -> true);
        });
    }

    @NotNull
    private CompletableFuture<Void> persist(@NotNull final PetProfile profile) {
        return this.storage.saveProfile(profile)
                .thenAccept(saved -> {
                })
                .exceptionally(error -> {
                    this.logger.log(Level.WARNING, "Failed to save pet profile of " + profile.getUuid(), error);
                    return null;
                });
    }
}
