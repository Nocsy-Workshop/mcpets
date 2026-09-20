package de.j0byte.mcpets.paper.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.j0byte.mcpets.paper.PluginLogger;
import de.j0byte.mcpets.api.message.PetSyncMessage;
import de.j0byte.mcpets.paper.config.ConfigManager;
import de.j0byte.mcpets.paper.config.GeneralConfig;
import de.j0byte.octopus.api.Octopus;
import de.j0byte.octopus.api.broker.ConsumeOptions;
import de.j0byte.octopus.api.broker.ExchangeSpec;
import de.j0byte.octopus.api.broker.QueueSpec;
import de.j0byte.octopus.api.messaging.Channel;
import de.j0byte.octopus.api.messaging.Subscription;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Serveruebergreifende Benachrichtigungen ueber Octopus.
 *
 * <p>Standardweg ist Redis: schnell, ohne Zustellgarantie und genau richtig fuer
 * "deine gecachten Daten sind veraltet, lad sie neu". Geht eine Nachricht verloren,
 * holt der naechste Lookup den echten Zustand aus MongoDB.</p>
 *
 * <p>Wer einen Reload garantiert auf jedem Server sehen will, schaltet in der
 * {@code config.yml} zusaetzlich RabbitMQ dazu. Jeder Server bekommt dann seine
 * eigene Queue am selben Exchange - eine gemeinsame Queue waere eine Work-Queue,
 * bei der nur ein einzelner Server die Nachricht bekaeme.</p>
 */
@Singleton
public class PetSyncService {

    private final Octopus octopus;
    private final ConfigManager configs;
    private final Logger logger;

    private Channel channel;
    private Subscription subscription;
    private de.j0byte.octopus.api.broker.BrokerSubscription brokerSubscription;

    @Inject
    public PetSyncService(
            @NotNull final Octopus octopus,
            @NotNull final ConfigManager configs,
            @PluginLogger @NotNull final Logger logger) {

        this.octopus = octopus;
        this.configs = configs;
        this.logger = logger;
    }

    /**
     * Abonniert die Sync-Nachrichten der anderen Server.
     *
     * @param handler laeuft fuer jede fremde Nachricht; eigene werden von Octopus gefiltert
     */
    public void start(@NotNull final Consumer<PetSyncMessage> handler) {
        final GeneralConfig general = this.configs.general();

        this.channel = this.octopus.messaging().channel(general.getSyncChannel());
        this.subscription = this.channel.subscribe(
                PetSyncMessage.class, message -> handler.accept(message.payload()));
        this.logger.info("Subscribed to pet sync channel '" + general.getSyncChannel() + "'.");

        if (general.isBrokerEnabled()) {
            startBroker(general, handler);
        }
    }

    private void startBroker(@NotNull final GeneralConfig general, @NotNull final Consumer<PetSyncMessage> handler) {
        // Eigene Queue pro Server, sonst bekaeme nur einer von ihnen die Nachricht.
        final String queue = general.getBrokerQueuePrefix() + "." + this.octopus.serverId();

        // Ein nicht erreichbarer Broker darf das Plugin nicht am Starten hindern -
        // Redis alleine reicht fuer den Normalbetrieb.
        this.octopus.broker().listen(
                        ExchangeSpec.fanout(general.getBrokerExchange()),
                        QueueSpec.temporary(queue),
                        general.getBrokerRoutingKey(),
                        PetSyncMessage.class,
                        message -> handler.accept(message.payload()),
                        ConsumeOptions.create().requeueOnError(false))
                .thenAccept(subscribed -> {
                    this.brokerSubscription = subscribed;
                    this.logger.info("Subscribed to pet sync queue '" + queue + "'.");
                })
                .exceptionally(error -> {
                    this.logger.log(Level.WARNING, "Could not subscribe to the pet sync queue", error);
                    return null;
                });
    }

    /**
     * Schickt eine Nachricht an alle anderen Server.
     */
    public void publish(@NotNull final PetSyncMessage message) {
        if (this.channel == null) {
            return;
        }

        this.channel.publish(message).exceptionally(error -> {
            this.logger.log(Level.WARNING, "Failed to publish pet sync message", error);
            return null;
        });

        final GeneralConfig general = this.configs.general();
        if (general.isBrokerEnabled()) {
            publishToBroker(general, message);
        }
    }

    private void publishToBroker(@NotNull final GeneralConfig general, @NotNull final PetSyncMessage message) {
        try {
            this.octopus.broker()
                    .publish(general.getBrokerExchange(), general.getBrokerRoutingKey(), message)
                    .exceptionally(error -> {
                        this.logger.log(Level.WARNING, "Failed to publish pet sync message to the broker", error);
                        return null;
                    });
        } catch (final RuntimeException exception) {
            this.logger.log(Level.WARNING, "Failed to publish pet sync message to the broker", exception);
        }
    }

    public void stop() {
        close(this.subscription);
        this.subscription = null;

        if (this.brokerSubscription != null) {
            try {
                this.brokerSubscription.cancel();
            } catch (final RuntimeException exception) {
                this.logger.log(Level.FINE, "Failed to cancel the broker subscription", exception);
            }
            this.brokerSubscription = null;
        }

        this.channel = null;
    }

    private void close(@Nullable final Subscription value) {
        if (value == null) {
            return;
        }
        try {
            value.unsubscribe();
        } catch (final RuntimeException exception) {
            this.logger.log(Level.FINE, "Failed to unsubscribe from the pet sync channel", exception);
        }
    }
}
