package de.j0byte.mcpets.paper.menu;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.api.document.PetSettings;
import de.j0byte.mcpets.api.message.PetSyncMessage;
import de.j0byte.mcpets.paper.config.ConfigManager;
import de.j0byte.mcpets.paper.config.MenuConfig;
import de.j0byte.mcpets.paper.config.PetDefinition;
import de.j0byte.mcpets.paper.item.ItemFactory;
import de.j0byte.mcpets.paper.pet.PetService;
import de.j0byte.mcpets.paper.storage.PetDataService;
import de.j0byte.mcpets.paper.storage.PetSyncService;
import de.j0byte.shark.api.Shark;
import de.j0byte.shark.gui.SharkInventories;
import de.j0byte.shark.gui.SmartInventory;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Oeffnet die Menues und fuehrt die Aktionen aus, die in der {@code menus.yml} stehen.
 *
 * <p>Es gibt bewusst nur einen einzigen {@link ConfiguredMenu}-Provider: welches Menue
 * wie aussieht und was seine Buttons tun, steht komplett in der Config. Im Code gibt es
 * kein festes Layout, keine feste Slot-Nummer und keinen festen Text.</p>
 */
@Singleton
public class MenuService {

    private final ConfigManager configs;
    private final PetDataService data;
    private final PetService pets;
    private final PetSyncService sync;
    private final Shark shark;
    private final ItemFactory items;
    private final Provider<RenameDialog> renameDialog;
    private final Logger logger;

    @Inject
    public MenuService(
            @NotNull final ConfigManager configs,
            @NotNull final PetDataService data,
            @NotNull final PetService pets,
            @NotNull final PetSyncService sync,
            @NotNull final Shark shark,
            @NotNull final ItemFactory items,
            @NotNull final Provider<RenameDialog> renameDialog,
            @PluginLogger @NotNull final Logger logger) {

        this.configs = configs;
        this.data = data;
        this.pets = pets;
        this.sync = sync;
        this.shark = shark;
        this.items = items;
        this.renameDialog = renameDialog;
        this.logger = logger;
    }

    // ------------------------------------------------------------------ Oeffnen

    /**
     * Oeffnet das Hauptmenue, also das, was {@code /pets} zeigt.
     */
    public void openMain(@NotNull final Player player) {
        open(player, this.configs.general().getMainMenu(), null);
    }

    /**
     * Oeffnet das Einstellungsmenue fuer ein Pet - der Rechtsklick auf einen Eintrag.
     */
    public void openSettings(@NotNull final Player player, @NotNull final PetDefinition definition) {
        open(player, this.configs.general().getSettingsMenu(), definition);
    }

    /**
     * Oeffnet ein Menue anhand seines Schluessels aus der {@code menus.yml}.
     *
     * @param context das Pet, um das es geht - noetig fuer das Einstellungsmenue
     */
    public void open(
            @NotNull final Player player,
            @NotNull final String menuId,
            @Nullable final PetDefinition context) {

        final Optional<MenuConfig> found = this.configs.menu(menuId);
        if (found.isEmpty()) {
            this.shark.messages().error(player,
                    this.configs.message("menu.unknown"), Placeholder.unparsed("menu", menuId));
            return;
        }

        final MenuConfig menu = found.get();
        SmartInventory.builder()
                .manager(SharkInventories.manager())
                .id("mcpets_" + menu.id())
                .title(this.shark.messages().renderItem(player, menu.title(), placeholders(player, context)))
                .size(menu.rows(), MenuConfig.COLUMNS)
                .provider(new ConfiguredMenu(this, menu, context))
                .build()
                .open(player);
    }

    // ------------------------------------------------------------------ Aktionen

    /**
     * Aktiviert ein Pet - der Linksklick auf einen Eintrag.
     *
     * <p>Laeuft nur, wenn der Spieler das Pet auch besitzt.</p>
     */
    public void activate(@NotNull final Player player, @NotNull final PetDefinition definition) {
        if (!this.data.owns(player, definition)) {
            this.shark.messages().error(player, this.configs.message("pet.not-owned"),
                    placeholders(player, definition));
            return;
        }

        if (this.pets.spawn(player, definition).isEmpty()) {
            this.shark.messages().error(player, this.configs.message("pet.spawn-failed"),
                    placeholders(player, definition));
            return;
        }

        this.shark.messages().success(player, this.configs.message("pet.spawned"),
                placeholders(player, definition));
        player.closeInventory();
    }

    /**
     * Setzt das aktive Pet ab.
     */
    public void despawn(@NotNull final Player player) {
        if (!this.pets.despawn(player, true)) {
            this.shark.messages().error(player, this.configs.message("pet.none-active"));
            return;
        }
        this.shark.messages().success(player, this.configs.message("pet.despawned"));
        player.closeInventory();
    }

    /**
     * Oeffnet den Umbenennen-Dialog und speichert das Ergebnis.
     */
    public void rename(@NotNull final Player player, @NotNull final PetDefinition definition) {
        if (!this.data.owns(player, definition)) {
            this.shark.messages().error(player, this.configs.message("pet.not-owned"),
                    placeholders(player, definition));
            return;
        }

        final PetSettings settings = this.data.settings(player.getUniqueId(), definition.id());
        final String prefill = settings.displayNameOr(definition.defaultName());

        this.renameDialog.get().open(player, definition, prefill,
                input -> applyName(player, definition, input),
                () -> openSettings(player, definition));
    }

    /**
     * Setzt den Namen auf den Standard aus der {@code pets.yml} zurueck.
     */
    public void resetName(@NotNull final Player player, @NotNull final PetDefinition definition) {
        final PetSettings settings = this.data.settings(player.getUniqueId(), definition.id());
        settings.resetDisplayName();

        this.data.saveSettings(settings);
        this.sync.publish(PetSyncMessage.profileChanged(player.getUniqueId()));
        this.pets.refreshName(player);

        this.shark.messages().success(player, this.configs.message("pet.name-reset"),
                placeholders(player, definition));
        openSettings(player, definition);
    }

    /**
     * Prueft den eingegebenen Namen und speichert ihn.
     */
    private void applyName(
            @NotNull final Player player,
            @NotNull final PetDefinition definition,
            @NotNull final String input) {

        final String name = sanitize(input);
        if (name.isBlank()) {
            this.shark.messages().error(player, this.configs.message("pet.name-empty"));
            openSettings(player, definition);
            return;
        }
        if (name.length() > this.configs.general().getNameMaxLength()) {
            this.shark.messages().error(player, this.configs.message("pet.name-too-long"),
                    Placeholder.unparsed("max", String.valueOf(this.configs.general().getNameMaxLength())));
            openSettings(player, definition);
            return;
        }
        if (blocked(name)) {
            this.shark.messages().error(player, this.configs.message("pet.name-blocked"));
            openSettings(player, definition);
            return;
        }

        final PetSettings settings = this.data.settings(player.getUniqueId(), definition.id());
        settings.displayName(name);

        this.data.saveSettings(settings);
        this.sync.publish(PetSyncMessage.profileChanged(player.getUniqueId()));
        this.pets.refreshName(player);

        this.shark.messages().success(player, this.configs.message("pet.name-set"),
                merge(placeholders(player, definition), Placeholder.unparsed("name", name)));
        openSettings(player, definition);
    }

    /**
     * Entfernt fuehrende und folgende Leerzeichen und - wenn konfiguriert - alle
     * MiniMessage- und Legacy-Formatierungen, damit sich niemand einen farbigen
     * Fantasienamen ueber den Kopf haengt.
     */
    @NotNull
    private String sanitize(@NotNull final String input) {
        final String trimmed = input.trim();
        if (!this.configs.general().isNameStripFormatting()) {
            return trimmed;
        }
        return trimmed.replaceAll("<[^<>]*>", "").replaceAll("[§&][0-9A-Fa-fK-Ok-oRr]", "").trim();
    }

    private boolean blocked(@NotNull final String name) {
        final String lower = name.toLowerCase(Locale.ROOT);
        for (final String entry : this.configs.general().getNameBlocked()) {
            if (lower.contains(entry.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ Intern

    /**
     * Die Platzhalter, die in Titeln, Namen und Lore zur Verfuegung stehen.
     */
    @NotNull
    TagResolver[] placeholders(@NotNull final Player player, @Nullable final PetDefinition definition) {
        if (definition == null) {
            return new TagResolver[] {
                Placeholder.unparsed("owner", player.getName()),
                Placeholder.unparsed("pet_id", ""),
                Placeholder.parsed("pet_name", this.configs.message("pet.no-active-name"))
            };
        }

        return new TagResolver[] {
            Placeholder.unparsed("owner", player.getName()),
            Placeholder.unparsed("pet_id", definition.id()),
            petName(player, definition),
            Placeholder.parsed("pet_default_name", definition.defaultName())
        };
    }

    /**
     * Der Pet-Name als Platzhalter.
     *
     * <p>Der Standardname aus der {@code pets.yml} wird geparst, damit seine
     * Farb-Tags wirken. Ein selbst gesetzter Name dagegen wird bewusst <b>nicht</b>
     * geparst: sonst koennte ein Spieler ueber seinen Pet-Namen MiniMessage in
     * fremde Chat-Zeilen schreiben - inklusive Klick- und Hover-Events. Wer seinen
     * Spielern Farben erlauben will, schaltet dafuer {@code pets.name.strip-formatting}
     * aus; die Tags wirken dann im Nametag ueber dem Pet, nicht im Chat.</p>
     */
    @NotNull
    private TagResolver petName(@NotNull final Player player, @NotNull final PetDefinition definition) {
        final PetSettings settings = this.data.settings(player.getUniqueId(), definition.id());
        return settings.hasCustomName()
                ? Placeholder.unparsed("pet_name", settings.getDisplayName())
                : Placeholder.parsed("pet_name", definition.defaultName());
    }

    /**
     * Haengt weitere Platzhalter an die Standard-Platzhalter an.
     *
     * <p>Noetig, weil die Message-Methoden ein Varargs-Array erwarten und sich ein
     * fertiges Array nicht mit einzelnen Werten mischen laesst.</p>
     */
    @NotNull
    private static TagResolver[] merge(@NotNull final TagResolver[] base, @NotNull final TagResolver... extra) {
        final TagResolver[] result = java.util.Arrays.copyOf(base, base.length + extra.length);
        System.arraycopy(extra, 0, result, base.length, extra.length);
        return result;
    }

    @NotNull
    ConfigManager configs() {
        return this.configs;
    }

    @NotNull
    PetDataService data() {
        return this.data;
    }

    @NotNull
    PetService pets() {
        return this.pets;
    }

    @NotNull
    Shark shark() {
        return this.shark;
    }

    @NotNull
    ItemFactory items() {
        return this.items;
    }

    @NotNull
    Logger logger() {
        return this.logger;
    }
}
