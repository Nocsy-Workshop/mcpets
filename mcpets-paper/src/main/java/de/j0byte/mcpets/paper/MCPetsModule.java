package de.j0byte.mcpets.paper;

import com.google.inject.AbstractModule;
import de.j0byte.chameleon.api.config.ConfigStore;
import de.j0byte.shark.api.Shark;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Bindet alles, was Octopus nicht selbst mitbringt.
 *
 * <p>{@code Octopus}, {@code Mongo}, {@code Messaging}, {@code Broker}, {@code Cache}
 * und {@code OctopusScheduler} kommen aus {@code OctopusModule} - hier stehen nur die
 * Dinge, die es dort nicht gibt: die Plugin-Instanz, der Logger, Shark und Chameleons
 * {@link ConfigStore}.</p>
 *
 * <p>Alle Werte werden im Konstruktor gesetzt, nicht erst spaeter: Guice lehnt
 * {@code toInstance(null)} ab.</p>
 */
public final class MCPetsModule extends AbstractModule {

    private final JavaPlugin plugin;
    private final Shark shark;
    private final ConfigStore store;

    public MCPetsModule(
            @NotNull final JavaPlugin plugin,
            @NotNull final Shark shark,
            @NotNull final ConfigStore store) {

        this.plugin = plugin;
        this.shark = shark;
        this.store = store;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(this.plugin);
        bind(Plugin.class).toInstance(this.plugin);
        // Nicht bind(Logger.class): Guice hat fuer java.util.logging.Logger schon eine
        // eingebaute Bindung, ein zweite laesst den Injector scheitern. Siehe PluginLogger.
        bind(Logger.class).annotatedWith(PluginLogger.class).toInstance(this.plugin.getLogger());
        bind(Shark.class).toInstance(this.shark);
        bind(ConfigStore.class).toInstance(this.store);
    }
}
