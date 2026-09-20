package de.j0byte.mcpets.paper.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.api.document.PetProfile;
import de.j0byte.mcpets.paper.config.ConfigManager;
import de.j0byte.mcpets.paper.pet.PetService;
import de.j0byte.mcpets.paper.storage.PetDataService;
import de.j0byte.octopus.api.Octopus;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Laedt die Pet-Daten beim Betreten, raeumt beim Verlassen auf und spawnt das
 * zuletzt aktive Pet wieder.
 */
@Singleton
public class PetListener implements Listener {

    private final ConfigManager configs;
    private final PetDataService data;
    private final PetService pets;
    private final Octopus octopus;
    private final Logger logger;

    @Inject
    public PetListener(
            @NotNull final ConfigManager configs,
            @NotNull final PetDataService data,
            @NotNull final PetService pets,
            @NotNull final Octopus octopus,
            @PluginLogger @NotNull final Logger logger) {

        this.configs = configs;
        this.data = data;
        this.pets = pets;
        this.octopus = octopus;
        this.logger = logger;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        // Laedt asynchron und kommt fuer das Spawnen zurueck in den Tick.
        this.octopus.scheduler().callback(
                this.data.onJoin(player.getUniqueId(), player.getName()),
                profile -> respawn(player, profile),
                error -> this.logger.log(Level.WARNING,
                        "Failed to load the pet profile of " + player.getName(), error));
    }

    /**
     * Spawnt das zuletzt aktive Pet wieder - aber nur, wenn der Spieler es
     * ueberhaupt noch besitzt.
     */
    private void respawn(@NotNull final Player player, @NotNull final PetProfile profile) {
        if (!this.configs.general().isRespawnOnJoin() || !player.isOnline()) {
            return;
        }

        final String petId = profile.activePet();
        if (petId == null) {
            return;
        }
        this.configs.pet(petId)
                .filter(definition -> this.data.owns(player, definition))
                .ifPresent(definition -> this.pets.spawn(player, definition));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (this.configs.general().isDespawnOnQuit()) {
            // Ohne clearActive: das Pet verschwindet, bleibt aber das gemerkte
            // aktive Pet und ist beim naechsten Join wieder da.
            this.pets.remove(player.getUniqueId());
        }
        this.data.onQuit(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(@NotNull final PlayerDeathEvent event) {
        if (this.configs.general().isDespawnOnDeath()) {
            this.pets.despawn(event.getPlayer(), false);
        }
    }

    /**
     * Beim Weltwechsel wird das Pet neu gespawnt - die Basis-Entity kann die Welt
     * nicht einfach mitwechseln.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NotNull final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        this.pets.activePet(player.getUniqueId()).ifPresent(active -> {
            this.pets.remove(player.getUniqueId());
            this.pets.spawn(player, active.getDefinition());
        });
    }
}
