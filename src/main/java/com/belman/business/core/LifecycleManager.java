package com.belman.business.core;

import com.belman.business.domain.events.ApplicationStateEvent.ApplicationState;
import com.belman.business.domain.events.DomainEvent;
import com.belman.business.domain.events.ViewHiddenEvent;
import com.belman.business.domain.events.ViewShownEvent;
import com.belman.business.domain.services.Logger;
import com.belman.business.domain.services.LoggerFactory;
import com.gluonhq.attach.lifecycle.LifecycleEvent;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.beans.value.ChangeListener;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * Utility class for managing application lifecycle events.
 * Integrates Gluon's lifecycle events with the core framework's event system.
 * Also manages view lifecycle events and ensures proper synchronization between
 * View, ViewModel, and Controller lifecycle methods.
 * 
 * This class is a clean architecture-friendly replacement for GluonLifecycleManager,
 * using interfaces instead of concrete presentation layer classes.
 */
public class LifecycleManager {
    private static Logger logger;
    private static final Map<LifecycleEvent, DomainEvent> eventMappings = new HashMap<>();

    // Use WeakHashMap to avoid memory leaks - views can be garbage collected when no longer needed
    private static final Map<View, ChangeListener<Boolean>> viewListeners = new WeakHashMap<>();

    /**
     * Initializes the LifecycleManager with a logger.
     * This method should be called before using any other methods in this class.
     * 
     * @param loggerFactory the factory to create loggers
     */
    public static void initialize(LoggerFactory loggerFactory) {
        if (loggerFactory == null) {
            throw new IllegalArgumentException("LoggerFactory cannot be null");
        }
        logger = loggerFactory.getLogger(LifecycleManager.class);
        logger.info("LifecycleManager initialized");
    }

    /**
     * Registers a handler for a specific lifecycle event.
     *
     * @param event the lifecycle event
     * @param handler the handler to execute
     */
    public static void registerLifecycleHandler(LifecycleEvent event, Runnable handler) {
        checkLogger();
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
        checkLogger();
        if (lifecycleEvent == null || domainEvent == null) {
            throw new IllegalArgumentException("LifecycleEvent and DomainEvent cannot be null");
        }
        logger.debug("Mapping lifecycle event {} to domain event {}", lifecycleEvent, domainEvent.getEventType());
        eventMappings.put(lifecycleEvent, domainEvent);

        registerLifecycleHandler(lifecycleEvent, () -> {
            logger.debug("Lifecycle event triggered: {}, publishing domain event: {}", 
                lifecycleEvent, domainEvent.getEventType());
            EventManager.getInstance().publishEvent(domainEvent); // Publish event using EventManager
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
        checkLogger();
        logger.debug("Registering domain event handler for event type: {}", eventType.getSimpleName());
        EventManager.getInstance().registerEventHandler(eventType, handler::accept); // Register event handler using EventManager
    }

    /**
     * Registers a view for lifecycle management.
     * This method sets up listeners for the view's showing property and ensures
     * that the view's lifecycle methods are called appropriately.
     *
     * @param view the view to register
     */
    public static void registerView(ViewLifecycle<?, ?> view) {
        checkLogger();
        if (view == null) {
            logger.warn("Attempted to register null view");
            return;
        }

        logger.debug("Registering view for lifecycle management: {}", view.getViewName());

        // Get the underlying View object
        View gluonView = view.getView();

        // Check if this view is already registered
        if (viewListeners.containsKey(gluonView)) {
            logger.debug("View already registered: {}", view.getViewName());
            return;
        }

        // Create a listener for the view's showing property
        ChangeListener<Boolean> showingListener = (observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                handleViewShown(view);
            } else {
                handleViewHidden(view);
            }
        };

        // Add the listener to the view's showing property
        gluonView.showingProperty().addListener(showingListener);

        // Store the listener so we can remove it later if needed
        viewListeners.put(gluonView, showingListener);

        logger.debug("View registered successfully: {}", view.getViewName());
    }

    /**
     * Unregisters a view from lifecycle management.
     * This method removes the listeners for the view's showing property.
     *
     * @param view the view to unregister
     */
    public static void unregisterView(ViewLifecycle<?, ?> view) {
        checkLogger();
        if (view == null) {
            logger.warn("Attempted to unregister null view");
            return;
        }

        // Get the underlying View object
        View gluonView = view.getView();

        logger.debug("Unregistering view from lifecycle management: {}", view.getViewName());

        // Get the listener for this view
        ChangeListener<Boolean> listener = viewListeners.get(gluonView);

        if (listener != null) {
            // Remove the listener from the view's showing property
            gluonView.showingProperty().removeListener(listener);

            // Remove the view from our map
            viewListeners.remove(gluonView);

            logger.debug("View unregistered successfully: {}", view.getViewName());
        } else {
            logger.debug("View was not registered: {}", view.getViewName());
        }
    }

    /**
     * Handles the event when a view is shown.
     * This method calls the appropriate lifecycle methods on the view, view model, and controller.
     * It also publishes a ViewShownEvent.
     *
     * @param view the view that was shown
     */
    private static void handleViewShown(ViewLifecycle<?, ?> view) {
        checkLogger();
        if (view == null) {
            logger.warn("Attempted to handle null view being shown");
            return;
        }

        String viewName = view.getViewName();
        logger.debug("View shown: {}", viewName);

        // Get the view model and controller
        ViewModelLifecycle viewModel = view.getViewModel();
        ControllerLifecycle controller = view.getController();

        // Call lifecycle methods on the view model if it implements ViewModelLifecycle
        if (viewModel != null) {
            try {
                viewModel.onShow();
                logger.debug("Called onShow() on view model: {}", viewModel.getViewModelName());
            } catch (Exception e) {
                logger.error("Error calling onShow() on view model: {}", e.getMessage(), e);
            }
        }

        // Call lifecycle methods on the controller if it implements ControllerLifecycle
        if (controller != null) {
            try {
                controller.onShow();
                logger.debug("Called onShow() on controller: {}", controller.getControllerName());
            } catch (Exception e) {
                logger.error("Error calling onShow() on controller: {}", e.getMessage(), e);
            }
        }

        // Call the view's onViewShown method
        try {
            view.onViewShown();
            logger.debug("Called onViewShown() on view");
        } catch (Exception e) {
            logger.error("Error calling onViewShown() on view: {}", e.getMessage(), e);
        }

        // Publish a ViewShownEvent
        try {
            ViewShownEvent event = new ViewShownEvent(viewName);
            EventManager.getInstance().publishEvent(event);
            logger.debug("Published ViewShownEvent for view: {}", viewName);
        } catch (Exception e) {
            logger.error("Error publishing ViewShownEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Handles the event when a view is hidden.
     * This method calls the appropriate lifecycle methods on the view, view model, and controller.
     * It also publishes a ViewHiddenEvent.
     *
     * @param view the view that was hidden
     */
    private static void handleViewHidden(ViewLifecycle<?, ?> view) {
        checkLogger();
        if (view == null) {
            logger.warn("Attempted to handle null view being hidden");
            return;
        }

        String viewName = view.getViewName();
        logger.debug("View hidden: {}", viewName);

        // Get the view model and controller
        ViewModelLifecycle viewModel = view.getViewModel();
        ControllerLifecycle controller = view.getController();

        // Call lifecycle methods on the view model if it implements ViewModelLifecycle
        if (viewModel != null) {
            try {
                viewModel.onHide();
                logger.debug("Called onHide() on view model: {}", viewModel.getViewModelName());
            } catch (Exception e) {
                logger.error("Error calling onHide() on view model: {}", e.getMessage(), e);
            }
        }

        // Call lifecycle methods on the controller if it implements ControllerLifecycle
        if (controller != null) {
            try {
                controller.onHide();
                logger.debug("Called onHide() on controller: {}", controller.getControllerName());
            } catch (Exception e) {
                logger.error("Error calling onHide() on controller: {}", e.getMessage(), e);
            }
        }

        // Call the view's onViewHidden method
        try {
            view.onViewHidden();
            logger.debug("Called onViewHidden() on view");
        } catch (Exception e) {
            logger.error("Error calling onViewHidden() on view: {}", e.getMessage(), e);
        }

        // Publish a ViewHiddenEvent
        try {
            ViewHiddenEvent event = new ViewHiddenEvent(viewName);
            EventManager.getInstance().publishEvent(event);
            logger.debug("Published ViewHiddenEvent for view: {}", viewName);
        } catch (Exception e) {
            logger.error("Error publishing ViewHiddenEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Checks if the logger has been initialized.
     * Throws an IllegalStateException if the logger is null.
     */
    private static void checkLogger() {
        if (logger == null) {
            throw new IllegalStateException("LifecycleManager has not been initialized. Call initialize() first.");
        }
    }

    /**
     * Initializes the lifecycle handlers.
     * This method should be called after the logger is initialized.
     */
    public static void initializeLifecycleHandlers() {
        checkLogger();
        logger.info("Initializing lifecycle handlers");

        // Register lifecycle handlers for all relevant lifecycle events
        registerLifecycleHandler(LifecycleEvent.PAUSE, () -> {
            logger.info("Application paused");
            ApplicationStateManager.transitionTo(ApplicationState.PAUSED);
        });

        registerLifecycleHandler(LifecycleEvent.RESUME, () -> {
            logger.info("Application resumed");
            ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);
        });

        // Note: Gluon Attach LifecycleService only supports PAUSE and RESUME events
        // For more comprehensive lifecycle management, we use these events to infer other states
        // When the app is paused, we also register a background task to run after a delay
        ApplicationStateManager.registerBackgroundTask(() -> {
            logger.debug("Performing background cleanup");
            // Release non-essential resources when going to background
            System.gc(); // Suggest garbage collection
        });

        // Register default foreground tasks for resource reinitialization
        ApplicationStateManager.registerForegroundTask(() -> {
            logger.debug("Performing foreground initialization");
            // Reinitialize resources when coming to foreground
        });

        // Register default shutdown tasks for final cleanup
        ApplicationStateManager.registerShutdownTask(() -> {
            logger.debug("Performing shutdown cleanup");
            // Release all resources when shutting down
        });
    }

    /**
     * Initializes the lifecycle manager with the given application.
     * This method sets up the lifecycle manager to work with the application.
     * 
     * @param app the MobileApplication instance
     * @param loggerFactory the factory to create loggers
     */
    public static void init(MobileApplication app, LoggerFactory loggerFactory) {
        // Initialize the logger first
        initialize(loggerFactory);

        if (app == null) {
            logger.warn("Cannot initialize LifecycleManager with null application");
            return;
        }

        logger.info("Initializing LifecycleManager with application: {}", app.getClass().getSimpleName());

        // Initialize the ApplicationStateManager with a logger
        ApplicationStateManager.setLogger(loggerFactory);
        ApplicationStateManager.initialize();

        // Initialize basic lifecycle handlers
        initializeLifecycleHandlers();

        // Transition the application to the ACTIVE state
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);

        // Register a shutdown hook to handle application termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutdown hook triggered");
            ApplicationStateManager.transitionTo(ApplicationState.STOPPING);
        }));

        // Note: Views will register themselves with LifecycleManager during construction
        logger.info("LifecycleManager initialized. Views will register themselves when created.");
    }
}
