package de.j0byte.mcpets.paper.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.chameleon.api.config.ConfigFile;
import de.j0byte.chameleon.api.config.ConfigSection;
import de.j0byte.chameleon.api.config.ConfigStore;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Alle Configs des Plugins an einer Stelle - ausschliesslich ueber Chameleon.
 *
 * <p>Vier Dateien: {@code config.yml} (typisiert gebunden), {@code pets.yml},
 * {@code menus.yml} und {@code messages.yml}. Pets und Menues haben Schluessel,
 * die der Code nicht kennen kann, und werden deshalb ueber
 * {@link ConfigSection#sections(String)} in Dateireihenfolge eingelesen.</p>
 */
@Singleton
public class ConfigManager {

    private static final String PETS_FILE = "pets.yml";
    private static final String MENUS_FILE = "menus.yml";
    private static final String MESSAGES_FILE = "messages.yml";

    private final ConfigStore store;
    private final Logger logger;

    private GeneralConfig general;
    private ConfigFile petsFile;
    private ConfigFile menusFile;
    private ConfigFile messagesFile;

    private Map<String, PetDefinition> pets = Map.of();
    private Map<String, MenuConfig> menus = Map.of();

    @Inject
    public ConfigManager(
            @NotNull final ConfigStore store, @PluginLogger @NotNull final Logger logger) {
        this.store = store;
        this.logger = logger;
    }

    /**
     * Laedt alle Dateien einmalig. Danach haelt {@link #reload()} sie aktuell.
     */
    public void load() {
        this.general = this.store.bind(GeneralConfig.class);
        this.petsFile = this.store.file(PETS_FILE).resourceFromName().load();
        this.menusFile = this.store.file(MENUS_FILE).resourceFromName().load();
        this.messagesFile = this.store.file(MESSAGES_FILE).resourceFromName().load();

        readPets();
        readMenus();
    }

    /**
     * Liest alle Dateien neu von der Platte.
     *
     * <p>Das gebundene {@link GeneralConfig} behaelt dabei seine Identitaet -
     * Chameleon befuellt es neu, statt es zu ersetzen. Referenzen darauf bleiben
     * also gueltig.</p>
     */
    public void reload() {
        this.store.reloadAll();
        readPets();
        readMenus();
    }

    @NotNull
    public GeneralConfig general() {
        return this.general;
    }

    /**
     * @return alle Pets in Dateireihenfolge, Schluessel ist die Pet-ID
     */
    @NotNull
    public Map<String, PetDefinition> pets() {
        return this.pets;
    }

    @NotNull
    public Optional<PetDefinition> pet(@Nullable final String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(this.pets.get(id));
    }

    /**
     * @return alle Menues in Dateireihenfolge, Schluessel ist die Menue-ID
     */
    @NotNull
    public Map<String, MenuConfig> menus() {
        return this.menus;
    }

    @NotNull
    public Optional<MenuConfig> menu(@Nullable final String id) {
        return id == null ? Optional.empty() : Optional.ofNullable(this.menus.get(id));
    }

    /**
     * Ein Text aus der {@code messages.yml}.
     *
     * <p>Fehlt der Schluessel, kommt ein sichtbarer Hinweis zurueck statt {@code null} -
     * so faellt eine unvollstaendige Config im Spiel sofort auf.</p>
     */
    @NotNull
    public String message(@NotNull final String path) {
        return this.messagesFile.getString(path, "<error>Missing message: " + path);
    }

    /**
     * Wie {@link #message(String)}, aber fuer mehrzeilige Eintraege (z. B. Lore).
     */
    @NotNull
    public java.util.List<String> messageList(@NotNull final String path) {
        return this.messagesFile.getStringList(path);
    }

    private void readPets() {
        final Map<String, PetDefinition> result = new LinkedHashMap<>();
        for (final var entry : this.petsFile.sections("pets").entrySet()) {
            try {
                result.put(entry.getKey(), PetDefinition.read(entry.getKey(), entry.getValue()));
            } catch (final RuntimeException exception) {
                this.logger.warning("Skipping pet '" + entry.getKey() + "': " + exception.getMessage());
            }
        }
        this.pets = Map.copyOf(result);
        this.logger.info("Loaded " + this.pets.size() + " pets from " + PETS_FILE + ".");
    }

    private void readMenus() {
        final Map<String, MenuConfig> result = new LinkedHashMap<>();
        for (final var entry : this.menusFile.sections("menus").entrySet()) {
            try {
                result.put(entry.getKey(), MenuConfig.read(entry.getKey(), entry.getValue()));
            } catch (final RuntimeException exception) {
                this.logger.warning("Skipping menu '" + entry.getKey() + "': " + exception.getMessage());
            }
        }
        this.menus = Map.copyOf(result);
        this.logger.info("Loaded " + this.menus.size() + " menus from " + MENUS_FILE + ".");
    }
}
