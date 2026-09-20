package de.j0byte.mcpets.paper;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Markiert den Logger des Plugins.
 *
 * <p>Guice bringt fuer {@link java.util.logging.Logger} eine eigene, eingebaute
 * Bindung mit: sie liefert jeder Klasse einen Logger, der nach der Klasse selbst
 * benannt ist. Ein zusaetzliches {@code bind(Logger.class)} kollidiert damit und
 * laesst den Injector schon beim Bauen mit {@code BindingAlreadySet} scheitern.</p>
 *
 * <p>Deshalb haengt der Plugin-Logger an dieser Annotation. Wer ihn haben will,
 * schreibt {@code @PluginLogger Logger logger} in den Konstruktor und bekommt den
 * Logger von Paper - mit {@code [MCPets]} davor. Ohne die Annotation bekaeme man
 * still und leise Guices Klassen-Logger, dessen Ausgabe im Konsolen-Log nicht mehr
 * als MCPets erkennbar waere.</p>
 */
@BindingAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, PARAMETER, METHOD})
public @interface PluginLogger {
}
