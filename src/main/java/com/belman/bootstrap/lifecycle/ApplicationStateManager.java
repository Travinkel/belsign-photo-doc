package com.belman.bootstrap.lifecycle;

import com.belman.bootstrap.event.EventManager;
import com.belman.domain.audit.event.*;
import com.belman.domain.audit.event.ApplicationStateAuditEvent.ApplicationState;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Manages application state and lifecycle transitions.
 * This class is responsible for tracking the current application state,
 * handling state transitions, and notifying listeners of state changes.
 */
public class ApplicationStateManager {
    private static final List<Consumer<ApplicationStateEvent>> stateChangeListeners = new CopyOnWriteArrayList<>();
    private static final List<Runnable> backgroundTasks = new ArrayList<>();
    private static final List<Runnable> foregroundTasks = new ArrayList<>();
    private static final List<Runnable> shutdownTasks = new ArrayList<>();
    private static Logger logger;
    private static ApplicationState currentState = ApplicationState.STARTING;

    /**
     * Gets the current application state.
     *
     * @return the current application state
     */
    public static ApplicationState getCurrentState() {
        return currentState;
    }

    /**
     * Registers a listener for state changes.
     *
     * @param listener the listener to register
     */
    public static void addStateChangeListener(Consumer<ApplicationStateEvent> listener) {
        stateChangeListeners.add(listener);
    }

    /**
     * Unregisters a listener for state changes.
     *
     * @param listener the listener to unregister
     */
    public static void removeStateChangeListener(Consumer<ApplicationStateEvent> listener) {
        stateChangeListeners.remove(listener);
    }

    /**
     * Registers a task to be executed when the application goes to the background.
     *
     * @param task the task to execute
     */
    public static void registerBackgroundTask(Runnable task) {
        backgroundTasks.add(task);
    }

    /**
     * Registers a task to be executed when the application comes to the foreground.
     *
     * @param task the task to execute
     */
    public static void registerForegroundTask(Runnable task) {
        foregroundTasks.add(task);
    }

    /**
     * Registers a task to be executed when the application is shutting down.
     *
     * @param task the task to execute
     */
    public static void registerShutdownTask(Runnable task) {
        shutdownTasks.add(task);
    }

    /**
     * Initializes the ApplicationStateManager.
     * This method should be called once during application startup.
     */
    public static void initialize() {
        if (logger != null) {
            logger.info("Initializing ApplicationStateManager");
        }
        transitionTo(ApplicationState.STARTING);
    }

    /**
     * Transitions the application to the specified state.
     * This method publishes the appropriate event and notifies all listeners.
     *
     * @param newState the new application state
     */
    public static void transitionTo(ApplicationState newState) {
        if (newState == currentState) {
            if (logger != null) {
                logger.debug("Application is already in state: {}", newState);
            }
            return;
        }

        if (logger != null) {
            logger.info("Application transitioning from {} to {}", currentState, newState);
        }
        ApplicationStateEvent event = createEventForState(newState);
        currentState = newState;

        // Execute state-specific tasks
        executeStateSpecificTasks(newState);

        // Publish the event
        EventManager.getInstance().publishEvent(event);

        // Notify listeners
        notifyStateChangeListeners(event);
    }

    /**
     * Creates an event for the specified state.
     *
     * @param state the application state
     * @return the appropriate event for the state
     */
    private static ApplicationStateEvent createEventForState(ApplicationState state) {
        switch (state) {
            case STARTING:
                return new ApplicationStartedEvent();
            case ACTIVE:
                return new ApplicationResumedEvent();
            case PAUSED:
                return new ApplicationPausedEvent();
            case BACKGROUND:
                return new ApplicationBackgroundedEvent();
            case STOPPING:
                return new ApplicationStoppedEvent();
            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    /**
     * Executes tasks specific to the new state.
     *
     * @param newState the new application state
     */
    private static void executeStateSpecificTasks(ApplicationState newState) {
        switch (newState) {
            case BACKGROUND:
                executeBackgroundTasks();
                break;
            case ACTIVE:
                executeForegroundTasks();
                break;
            case STOPPING:
                executeShutdownTasks();
                break;
            default:
                // No specific tasks for other states
                break;
        }
    }

    /**
     * Notifies all registered listeners of a state change.
     *
     * @param event the state change event
     */
    private static void notifyStateChangeListeners(ApplicationStateEvent event) {
        for (Consumer<ApplicationStateEvent> listener : stateChangeListeners) {
            try {
                listener.accept(event);
            } catch (Exception e) {
                if (logger != null) {
                    logger.error("Error notifying state change listener: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Executes tasks that should run when the application goes to the background.
     */
    private static void executeBackgroundTasks() {
        if (logger != null) {
            logger.debug("Executing background tasks");
        }
        for (Runnable task : backgroundTasks) {
            try {
                task.run();
            } catch (Exception e) {
                if (logger != null) {
                    logger.error("Error executing background task: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Executes tasks that should run when the application comes to the foreground.
     */
    private static void executeForegroundTasks() {
        if (logger != null) {
            logger.debug("Executing foreground tasks");
        }
        for (Runnable task : foregroundTasks) {
            try {
                task.run();
            } catch (Exception e) {
                if (logger != null) {
                    logger.error("Error executing foreground task: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Executes tasks that should run when the application is shutting down.
     */
    private static void executeShutdownTasks() {
        if (logger != null) {
            logger.debug("Executing shutdown tasks");
        }
        for (Runnable task : shutdownTasks) {
            try {
                task.run();
            } catch (Exception e) {
                if (logger != null) {
                    logger.error("Error executing shutdown task: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * Sets the logger for this class.
     * This method should be called before using any methods in this class.
     *
     * @param loggerFactory the factory to create loggers
     */
    public static void setLogger(LoggerFactory loggerFactory) {
        if (loggerFactory == null) {
            throw new IllegalArgumentException("LoggerFactory cannot be null");
        }
        logger = loggerFactory.getLogger(ApplicationStateManager.class);
    }
}
