package com.belman.backbone.core.lifecycle;

import com.gluonhq.attach.lifecycle.LifecycleEvent;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.belman.backbone.core.api.CoreAPI;
import com.belman.backbone.core.events.DomainEvent;
import com.belman.backbone.core.logging.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class for managing application lifecycle events.
 * Integrates Gluon's lifecycle events with the core framework's event system.
 */
public class GluonLifecycleManager {
    private static final Logger logger = Logger.getLogger(GluonLifecycleManager.class);
    private static final Map<LifecycleEvent, DomainEvent> eventMappings = new HashMap<>();

    /**
     * Registers a handler for a specific lifecycle event.
     *
     * @param event the lifecycle event
     * @param handler the handler to execute
     */
    public static void registerLifecycleHandler(LifecycleEvent event, Runnable handler) {
        logger.debug("Registering lifecycle handler for event: {}", event);
        LifecycleService.create().ifPresent(service -> service.addListener(event, handler));
    }

    /**
     * Registers a handler for a specific lifecycle event that publishes a domain event.
     *
     * @param lifecycleEvent the lifecycle event
     * @param domainEvent the domain event to publish
     */
    public static void mapLifecycleEventToDomainEvent(LifecycleEvent lifecycleEvent, DomainEvent domainEvent) {
        if (lifecycleEvent == null || domainEvent == null) {
            throw new IllegalArgumentException("LifecycleEvent and DomainEvent cannot be null");
        }
        logger.debug("Mapping lifecycle event {} to domain event {}", lifecycleEvent, domainEvent.getEventType());
        eventMappings.put(lifecycleEvent, domainEvent);

        registerLifecycleHandler(lifecycleEvent, () -> {
            logger.debug("Lifecycle event triggered: {}, publishing domain event: {}", 
                lifecycleEvent, domainEvent.getEventType());
            CoreAPI.publishEvent(domainEvent); // Use CoreAPI for publishing events
        });
    }

    /**
     * Registers a handler for a specific domain event that is triggered by a lifecycle event.
     *
     * @param <T> the type of the domain event
     * @param eventType the class of the domain event
     * @param handler the handler to execute
     */
    public static <T extends DomainEvent> void registerDomainEventHandler(Class<T> eventType, Consumer<T> handler) {
        logger.debug("Registering domain event handler for event type: {}", eventType.getSimpleName());
        CoreAPI.registerEventHandler(eventType, handler::accept); // Use CoreAPI for event handling
    }

    /**
     * Initializes the lifecycle manager.
     * This method should be called once during application startup.
     */
    public static void initialize() {
        logger.info("Initializing GluonLifecycleManager");

        // Register default lifecycle handlers
        registerLifecycleHandler(LifecycleEvent.PAUSE, () -> {
            logger.info("Application paused");
        });

        registerLifecycleHandler(LifecycleEvent.RESUME, () -> {
            logger.info("Application resumed");
        });
    }
}

