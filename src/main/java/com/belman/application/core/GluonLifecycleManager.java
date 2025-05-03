package com.belman.application.core;

import com.belman.application.api.CoreAPI;
import com.belman.presentation.core.BaseController;
import com.belman.presentation.core.BaseView;
import com.belman.presentation.core.BaseViewModel;
import com.belman.domain.shared.DomainEvent;
import com.belman.domain.shared.ViewHiddenEvent;
import com.belman.domain.shared.ViewShownEvent;
import com.belman.domain.shared.ApplicationStateEvent.ApplicationState;
import com.belman.infrastructure.EmojiLogger;
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
 */
public class GluonLifecycleManager {
    private static final EmojiLogger logger = EmojiLogger.getLogger(GluonLifecycleManager.class);
    private static final Map<LifecycleEvent, DomainEvent> eventMappings = new HashMap<>();

    // Use WeakHashMap to avoid memory leaks - views can be garbage collected when no longer needed
    private static final Map<View, ChangeListener<Boolean>> viewListeners = new WeakHashMap<>();

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
     * Registers a view for lifecycle management.
     * This method sets up listeners for the view's showing property and ensures
     * that the view's lifecycle methods are called appropriately.
     *
     * @param view the view to register
     */
    public static void registerView(BaseView<?> view) {
        if (view == null) {
            logger.warn("Attempted to register null view");
            return;
        }

        logger.debug("Registering view for lifecycle management: {}", view.getClass().getSimpleName());

        // Check if this view is already registered
        if (viewListeners.containsKey(view)) {
            logger.debug("View already registered: {}", view.getClass().getSimpleName());
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
        view.showingProperty().addListener(showingListener);

        // Store the listener so we can remove it later if needed
        viewListeners.put(view, showingListener);

        logger.debug("View registered successfully: {}", view.getClass().getSimpleName());
    }

    /**
     * Unregisters a view from lifecycle management.
     * This method removes the listeners for the view's showing property.
     *
     * @param view the view to unregister
     */
    public static void unregisterView(View view) {
        if (view == null) {
            logger.warn("Attempted to unregister null view");
            return;
        }

        logger.debug("Unregistering view from lifecycle management: {}", view.getClass().getSimpleName());

        // Get the listener for this view
        ChangeListener<Boolean> listener = viewListeners.get(view);

        if (listener != null) {
            // Remove the listener from the view's showing property
            view.showingProperty().removeListener(listener);

            // Remove the view from our map
            viewListeners.remove(view);

            logger.debug("View unregistered successfully: {}", view.getClass().getSimpleName());
        } else {
            logger.debug("View was not registered: {}", view.getClass().getSimpleName());
        }
    }

    /**
     * Handles the event when a view is shown.
     * This method calls the appropriate lifecycle methods on the view, view model, and controller.
     * It also publishes a ViewShownEvent.
     *
     * @param view the view that was shown
     */
    private static void handleViewShown(BaseView<?> view) {
        if (view == null) {
            logger.warn("Attempted to handle null view being shown");
            return;
        }

        String viewName = view.getClass().getSimpleName();
        logger.debug("View shown: {}", viewName);

        // Get the view model and controller
        BaseViewModel<?> viewModel = view.getViewModel();
        BaseController<?> controller = view.getController();

        // Call lifecycle methods on the view model if it implements ViewModelLifecycle
        if (viewModel != null) {
            try {
                if (viewModel instanceof ViewModelLifecycle) {
                    ((ViewModelLifecycle) viewModel).onShow();
                    logger.debug("Called onShow() on view model: {}", viewModel.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error calling onShow() on view model: {}", e.getMessage(), e);
            }
        }

        // Call lifecycle methods on the controller if it implements ControllerLifecycle
        if (controller != null) {
            try {
                if (controller instanceof ControllerLifecycle) {
                    ((ControllerLifecycle) controller).onShow();
                    logger.debug("Called onShow() on controller: {}", controller.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error calling onShow() on controller: {}", e.getMessage(), e);
            }
        }

        // Call lifecycle methods on the view
        try {
            // BaseView no longer implements ViewLifecycle, but still has onShow method
            view.onShow();
            logger.debug("Called onShow() on view: {}", viewName);
        } catch (Exception e) {
            logger.error("Error calling onShow() on view: {}", e.getMessage(), e);
        }

        // Publish a ViewShownEvent
        try {
            ViewShownEvent event = new ViewShownEvent(viewName);
            CoreAPI.publishEvent(event);
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
    private static void handleViewHidden(BaseView<?> view) {
        if (view == null) {
            logger.warn("Attempted to handle null view being hidden");
            return;
        }

        String viewName = view.getClass().getSimpleName();
        logger.debug("View hidden: {}", viewName);

        // Get the view model and controller
        BaseViewModel<?> viewModel = view.getViewModel();
        BaseController<?> controller = view.getController();

        // Call lifecycle methods on the view model if it implements ViewModelLifecycle
        if (viewModel != null) {
            try {
                if (viewModel instanceof ViewModelLifecycle) {
                    ((ViewModelLifecycle) viewModel).onHide();
                    logger.debug("Called onHide() on view model: {}", viewModel.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error calling onHide() on view model: {}", e.getMessage(), e);
            }
        }

        // Call lifecycle methods on the controller if it implements ControllerLifecycle
        if (controller != null) {
            try {
                if (controller instanceof ControllerLifecycle) {
                    ((ControllerLifecycle) controller).onHide();
                    logger.debug("Called onHide() on controller: {}", controller.getClass().getSimpleName());
                }
            } catch (Exception e) {
                logger.error("Error calling onHide() on controller: {}", e.getMessage(), e);
            }
        }

        // Call lifecycle methods on the view
        try {
            // BaseView no longer implements ViewLifecycle, but still has onHide method
            view.onHide();
            logger.debug("Called onHide() on view: {}", viewName);
        } catch (Exception e) {
            logger.error("Error calling onHide() on view: {}", e.getMessage(), e);
        }

        // Publish a ViewHiddenEvent
        try {
            ViewHiddenEvent event = new ViewHiddenEvent(viewName);
            CoreAPI.publishEvent(event);
            logger.debug("Published ViewHiddenEvent for view: {}", viewName);
        } catch (Exception e) {
            logger.error("Error publishing ViewHiddenEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Initializes the lifecycle manager.
     * This method should be called once during application startup.
     */
    public static void initialize() {
        logger.info("Initializing GluonLifecycleManager");

        // Initialize the ApplicationStateManager
        ApplicationStateManager.initialize();

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
     */
    public static void init(MobileApplication app) {
        if (app == null) {
            logger.warn("Cannot initialize GluonLifecycleManager with null application");
            return;
        }

        logger.info("Initializing GluonLifecycleManager with application: {}", app.getClass().getSimpleName());

        // Initialize basic lifecycle handlers
        initialize();

        // Transition the application to the ACTIVE state
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);

        // Register a shutdown hook to handle application termination
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutdown hook triggered");
            ApplicationStateManager.transitionTo(ApplicationState.STOPPING);
        }));

        // Note: Views will register themselves with GluonLifecycleManager during construction
        logger.info("GluonLifecycleManager initialized. Views will register themselves when created.");
    }
}
