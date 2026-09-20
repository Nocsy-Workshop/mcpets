package de.j0byte.mcpets.paper.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.api.message.PetSyncMessage;
import de.j0byte.mcpets.paper.config.ConfigManager;
import de.j0byte.mcpets.paper.config.PetDefinition;
import de.j0byte.mcpets.paper.menu.MenuService;
import de.j0byte.mcpets.paper.pet.PetService;
import de.j0byte.mcpets.paper.storage.PetDataService;
import de.j0byte.mcpets.paper.storage.PetSyncService;
import de.j0byte.shark.api.Shark;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@code /pets} und die Admin-Unterbefehle.
 *
 * <p>Laeuft ueber CommandAPI, das als eigenes Server-Plugin installiert ist.</p>
 *
 * <p>Die Lambdas sind bewusst <b>explizit typisiert</b>: CommandAPI hat zwei
 * {@code executes}-Overloads, und ein implizit typisierter Lambda passt auf beide -
 * der Aufruf waere mehrdeutig.</p>
 */
@Singleton
public class PetsCommand {

    private static final String ROOT = "pets";
    private static final String PET_ARGUMENT = "pet";
    private static final String PLAYER_ARGUMENT = "player";

    private final ConfigManager configs;
    private final MenuService menus;
    private final PetService pets;
    private final PetDataService data;
    private final PetSyncService sync;
    private final Shark shark;
    private final Logger logger;

    @Inject
    public PetsCommand(
            @NotNull final ConfigManager configs,
            @NotNull final MenuService menus,
            @NotNull final PetService pets,
            @NotNull final PetDataService data,
            @NotNull final PetSyncService sync,
            @NotNull final Shark shark,
            @PluginLogger @NotNull final Logger logger) {

        this.configs = configs;
        this.menus = menus;
        this.pets = pets;
        this.data = data;
        this.sync = sync;
        this.shark = shark;
        this.logger = logger;
    }

    public void register(@NotNull final JavaPlugin plugin) {
        new CommandAPICommand(ROOT)
                .withPermission(this.configs.general().getUsePermission())
                .withSubcommand(adminCommand())
                .executesPlayer((Player player, CommandArguments args) -> this.menus.openMain(player))
                .register(plugin);
    }

    // ------------------------------------------------------------------ /pets admin

    @NotNull
    private CommandAPICommand adminCommand() {
        return new CommandAPICommand("admin")
                .withPermission(this.configs.general().getAdminPermission())
                .withSubcommand(reloadCommand())
                .withSubcommand(grantCommand("give", this::give))
                .withSubcommand(grantCommand("remove", this::remove))
                .executes((CommandSender sender, CommandArguments args) ->
                        this.shark.messages().send(sender, this.configs.message("command.admin-usage")));
    }

    /**
     * {@code /pets admin reload} - laedt alle Configs neu und sagt den anderen
     * Servern ueber Octopus Bescheid, dass sie dasselbe tun sollen.
     */
    @NotNull
    private CommandAPICommand reloadCommand() {
        return new CommandAPICommand("reload")
                .withPermission(this.configs.general().getAdminPermission())
                .executes((CommandSender sender, CommandArguments args) -> {
                    try {
                        this.pets.despawnAll();
                        this.configs.reload();
                        this.pets.start();
                        this.sync.publish(PetSyncMessage.reload());

                        this.shark.messages().success(sender, this.configs.message("command.reloaded"),
                                Placeholder.unparsed("pets", String.valueOf(this.configs.pets().size())),
                                Placeholder.unparsed("menus", String.valueOf(this.configs.menus().size())));
                    } catch (final RuntimeException exception) {
                        this.logger.log(Level.SEVERE, "Failed to reload the configuration", exception);
                        this.shark.messages().error(sender, this.configs.message("command.reload-failed"),
                                Placeholder.unparsed("reason", String.valueOf(exception.getMessage())));
                    }
                });
    }

    /**
     * {@code /pets admin give|remove <player> <pet>}.
     *
     * <p>Beide Unterbefehle arbeiten auf dem Datenbank-Besitz. Ein Pet, das einem
     * Spieler ueber seine Permission gehoert, laesst sich damit nicht wegnehmen -
     * das geht nur ueber die Permission selbst.</p>
     */
    @NotNull
    private CommandAPICommand grantCommand(
            @NotNull final String name, @NotNull final BiConsumer<CommandSender, Request> action) {

        return new CommandAPICommand(name)
                .withPermission(this.configs.general().getAdminPermission())
                .withArguments(new StringArgument(PLAYER_ARGUMENT)
                        .replaceSuggestions(ArgumentSuggestions.strings(info -> Bukkit.getOnlinePlayers()
                                .stream()
                                .map(Player::getName)
                                .toArray(String[]::new))))
                .withArguments(new StringArgument(PET_ARGUMENT)
                        .replaceSuggestions(ArgumentSuggestions.strings(
                                info -> this.configs.pets().keySet().toArray(new String[0]))))
                .executes((CommandSender sender, CommandArguments args) -> {
                    final String playerName = (String) args.get(PLAYER_ARGUMENT);
                    final String petId = (String) args.get(PET_ARGUMENT);

                    if (playerName == null || petId == null) {
                        return;
                    }

                    final OfflinePlayer target = resolve(playerName);
                    if (target == null) {
                        this.shark.messages().error(sender, this.configs.message("command.unknown-player"),
                                Placeholder.unparsed("player", playerName));
                        return;
                    }

                    final Optional<PetDefinition> definition = this.configs.pet(petId);
                    if (definition.isEmpty()) {
                        this.shark.messages().error(sender, this.configs.message("command.unknown-pet"),
                                Placeholder.unparsed("pet", petId));
                        return;
                    }

                    action.accept(sender, new Request(target, definition.get()));
                });
    }

    /**
     * Loest einen Spielernamen auf, ohne den Main-Thread zu blockieren.
     *
     * <p>Erst die Online-Spieler, dann der Profil-Cache des Servers. Bewusst kein
     * {@code Bukkit.getOfflinePlayer(String)}: das fragt fuer unbekannte Namen
     * Mojang an und haelt dabei den Server an.</p>
     *
     * @return der Spieler, oder {@code null} wenn der Name unbekannt ist
     */
    @Nullable
    private OfflinePlayer resolve(@NotNull final String name) {
        final Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            return online;
        }
        return Bukkit.getOfflinePlayerIfCached(name);
    }

    private void give(@NotNull final CommandSender sender, @NotNull final Request request) {
        final UUID uuid = request.target().getUniqueId();

        this.data.grant(uuid, request.definition().id()).thenAccept(granted -> {
            if (!granted) {
                message(sender, "command.give-already", request);
                return;
            }
            this.sync.publish(PetSyncMessage.profileChanged(uuid));
            success(sender, "command.give-success", request);
            notifyTarget(request, "pet.received");
        }).exceptionally(error -> fail(sender, request, error));
    }

    private void remove(@NotNull final CommandSender sender, @NotNull final Request request) {
        final UUID uuid = request.target().getUniqueId();

        this.data.revoke(uuid, request.definition().id()).thenAccept(revoked -> {
            if (!revoked) {
                message(sender, "command.remove-already", request);
                return;
            }
            this.sync.publish(PetSyncMessage.profileChanged(uuid));

            // Traegt der Spieler das Pet gerade aus, muss es sofort weg.
            final Player online = request.target().getPlayer();
            if (online != null) {
                this.pets.activePet(uuid)
                        .filter(active -> active.petId().equals(request.definition().id()))
                        .ifPresent(active -> this.pets.despawn(online, true));
            }

            success(sender, "command.remove-success", request);
            notifyTarget(request, "pet.lost");
        }).exceptionally(error -> fail(sender, request, error));
    }

    /**
     * Antworten kommen aus einem asynchronen Callback - Sharks Message-System ist
     * threadsicher, deshalb ist das hier in Ordnung.
     */
    private void message(
            @NotNull final CommandSender sender, @NotNull final String key, @NotNull final Request request) {

        this.shark.messages().send(sender, this.configs.message(key), placeholders(request));
    }

    private void success(
            @NotNull final CommandSender sender, @NotNull final String key, @NotNull final Request request) {

        this.shark.messages().success(sender, this.configs.message(key), placeholders(request));
    }

    private void notifyTarget(@NotNull final Request request, @NotNull final String key) {
        final Player online = request.target().getPlayer();
        if (online != null) {
            this.shark.messages().send(online, this.configs.message(key), placeholders(request));
        }
    }

    @NotNull
    private net.kyori.adventure.text.minimessage.tag.resolver.TagResolver[] placeholders(
            @NotNull final Request request) {

        return new net.kyori.adventure.text.minimessage.tag.resolver.TagResolver[] {
            Placeholder.unparsed("player", String.valueOf(request.target().getName())),
            Placeholder.unparsed("pet_id", request.definition().id()),
            Placeholder.parsed("pet_name", request.definition().defaultName())
        };
    }

    private Void fail(
            @NotNull final CommandSender sender,
            @NotNull final Request request,
            @NotNull final Throwable error) {

        this.logger.log(Level.WARNING,
                "Failed to change pet ownership of " + request.target().getUniqueId(), error);
        this.shark.messages().error(sender, this.configs.message("command.storage-failed"));
        return null;
    }

    /**
     * Ein aufgeloester Admin-Befehl: Zielspieler und Pet.
     */
    private record Request(@NotNull OfflinePlayer target, @NotNull PetDefinition definition) {
    }
}
