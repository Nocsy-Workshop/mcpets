package de.j0byte.mcpets.paper.item;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.paper.config.ItemConfig;
import de.j0byte.shark.api.Shark;
import de.j0byte.shark.gui.SharkInventories;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Baut alle Items des Plugins.
 *
 * <p>Normalfall ist Sharks {@code ButtonFactory}: sie kennt Bukkit-Materialien und
 * ItemsAdder und rendert Name und Lore ueber Sharks Message-System. Zeigt
 * {@code material} auf ein CraftEngine-Item, wird das Grund-Item stattdessen von
 * CraftEngine geholt und hier mit Name, Lore und CustomModelData aus der Config
 * ueberschrieben.</p>
 *
 * <p>Die Reihenfolge ist CraftEngine zuerst, dann Shark. Kennt CraftEngine die ID
 * nicht, faellt alles ganz normal an Shark zurueck - ItemsAdder-Items und
 * vanilla {@code minecraft:stone} funktionieren also unveraendert weiter.</p>
 */
@Singleton
public class ItemFactory {

    private static final String CRAFT_ENGINE_PLUGIN = "CraftEngine";

    private final Shark shark;
    private final Logger logger;

    /** {@code null}, wenn CraftEngine nicht installiert ist. */
    private final CraftEngineItemProvider craftEngine;

    @Inject
    public ItemFactory(
            @NotNull final Plugin plugin,
            @NotNull final Shark shark,
            @PluginLogger @NotNull final Logger logger) {

        this.shark = shark;
        this.logger = logger;
        this.craftEngine = resolveCraftEngine(plugin);
    }

    /**
     * Legt den CraftEngine-Zugriff nur an, wenn das Plugin auch da ist.
     *
     * <p>Erst hier wird {@link CraftEngineItemProvider} geladen - und damit auch
     * erst hier werden die CraftEngine-Klassen gebraucht. Ohne diesen Umweg wuerde
     * schon das Laden dieser Klasse auf einem Server ohne CraftEngine scheitern.</p>
     */
    @Nullable
    private CraftEngineItemProvider resolveCraftEngine(@NotNull final Plugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin(CRAFT_ENGINE_PLUGIN) == null) {
            // Ausdruecklich loggen: sonst sucht man lange, warum ein "namespace:id"
            // aus der Config im Menue als Barrier auftaucht.
            this.logger.info("CraftEngine is not installed - 'namespace:id' materials "
                    + "fall back to ItemsAdder and Bukkit materials.");
            return null;
        }

        try {
            final CraftEngineItemProvider provider = new CraftEngineItemProvider();
            this.logger.info("CraftEngine found - 'namespace:id' materials will be resolved through it.");
            return provider;
        } catch (final LinkageError error) {
            // Andere CraftEngine-Version als die, gegen die gebaut wurde.
            this.logger.log(Level.WARNING,
                    "CraftEngine is installed but its item API could not be used", error);
            return null;
        }
    }

    /**
     * Baut ein Item aus der Config.
     *
     * @param loreBlocks Marker-Name auf Zeilenliste, siehe {@code lore-blocks} in der menus.yml
     */
    @NotNull
    public ItemStack create(
            @NotNull final ItemConfig item,
            @Nullable final Player viewer,
            @NotNull final Map<String, List<String>> loreBlocks,
            final TagResolver... placeholders) {

        final ItemStack custom = craftEngineItem(item, viewer);
        if (custom != null) {
            return decorate(custom, item, viewer, loreBlocks, placeholders);
        }

        return SharkInventories.controls().buttons()
                .create(item.toButton(), viewer, loreBlocks, placeholders);
    }

    /**
     * @return das CraftEngine-Item, oder {@code null} wenn es keins ist
     */
    @Nullable
    private ItemStack craftEngineItem(@NotNull final ItemConfig item, @Nullable final Player viewer) {
        if (this.craftEngine == null || item.material().indexOf(':') <= 0) {
            return null;
        }

        try {
            return this.craftEngine.item(item.material(), viewer);
        } catch (final RuntimeException | LinkageError error) {
            // Ein kaputtes Item soll kein ganzes Menue sprengen.
            this.logger.log(Level.WARNING,
                    "Failed to build the CraftEngine item '" + item.material() + "'", error);
            return null;
        }
    }

    /**
     * Legt Name, Lore und CustomModelData aus der Config ueber das CraftEngine-Item.
     *
     * <p>Leere Felder lassen das, was CraftEngine mitbringt, bewusst stehen: wer in
     * der Config keinen Namen setzt, bekommt den Namen aus CraftEngine.</p>
     */
    @NotNull
    private ItemStack decorate(
            @NotNull final ItemStack stack,
            @NotNull final ItemConfig item,
            @Nullable final Player viewer,
            @NotNull final Map<String, List<String>> loreBlocks,
            final TagResolver... placeholders) {

        stack.setAmount(item.amount());

        final ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return stack;
        }

        if (!item.name().isEmpty()) {
            meta.displayName(this.shark.messages().renderItem(viewer, item.name(), placeholders));
        }
        if (!item.lore().isEmpty()) {
            meta.lore(renderLore(item.lore(), viewer, loreBlocks, placeholders));
        }

        applyCustomModelData(meta, item.customModelData());

        if (item.itemModel() != null && !item.itemModel().isBlank()) {
            final NamespacedKey key = NamespacedKey.fromString(item.itemModel());
            if (key != null) {
                meta.setItemModel(key);
            }
        }

        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Rendert die Lore und ersetzt dabei Marker-Zeilen durch ihren Block.
     *
     * <p>Gleiche Regel wie in Sharks {@code ButtonFactory}: eine Zeile, die nur aus
     * {@code <marker>} besteht, wird durch die Zeilen des Blocks ersetzt. Ist der
     * Marker nicht belegt, bleibt die Zeile stehen und wird normal gerendert.</p>
     */
    @NotNull
    private List<Component> renderLore(
            @NotNull final List<String> lore,
            @Nullable final Player viewer,
            @NotNull final Map<String, List<String>> loreBlocks,
            final TagResolver... placeholders) {

        final List<Component> result = new ArrayList<>(lore.size());
        for (final String line : lore) {
            final String marker = markerName(line);
            final List<String> block = marker == null ? null : loreBlocks.get(marker);

            if (block == null) {
                result.add(this.shark.messages().renderItem(viewer, line, placeholders));
                continue;
            }
            for (final String blockLine : block) {
                result.add(this.shark.messages().renderItem(viewer, blockLine, placeholders));
            }
        }
        return result;
    }

    /**
     * @return der Marker-Name einer Zeile, die nur aus {@code <name>} besteht, sonst {@code null}
     */
    @Nullable
    private static String markerName(@NotNull final String line) {
        final String trimmed = line.trim();
        if (trimmed.length() < 3 || trimmed.charAt(0) != '<' || trimmed.charAt(trimmed.length() - 1) != '>') {
            return null;
        }
        final String name = trimmed.substring(1, trimmed.length() - 1);
        return name.indexOf('<') < 0 && name.indexOf('>') < 0 ? name : null;
    }

    /**
     * {@code 0} heisst laut Shark-Konvention "nicht setzen" - so kann in jedem
     * Item-Block ein {@code custom-model-data} stehen, ohne das Modell zu ueberschreiben.
     *
     * <p>{@code setCustomModelData(Integer)} ist seit 1.21.4 veraltet, bleibt aber der
     * Weg fuer klassische Resourcepacks. Shark macht es an derselben Stelle genauso.</p>
     */
    @SuppressWarnings("deprecation")
    private static void applyCustomModelData(@NotNull final ItemMeta meta, final int customModelData) {
        if (customModelData == 0) {
            return;
        }
        meta.setCustomModelData(customModelData);
    }
}
