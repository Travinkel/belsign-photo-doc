package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Base class for application state audit events.
 * <p>
 * This class provides common functionality for all application state events,
 * which represent changes in the application's lifecycle state.
 */
public abstract class ApplicationStateAuditEvent extends BaseAuditEvent {

    /**
     * The possible states of the application.
     */
    public enum ApplicationState {
        /**
         * The application is starting up.
         */
        STARTING,

        /**
         * The application is active and in the foreground.
         */
        ACTIVE,

        /**
         * The application is paused but still visible.
         */
        PAUSED,

        /**
         * The application is in the background.
         */
        BACKGROUND,

        /**
         * The application is shutting down.
         */
        STOPPING
    }

    private final ApplicationState state;

    /**
     * Creates a new application state event with the specified state.
     *
     * @param state the application state
     */
    protected ApplicationStateAuditEvent(ApplicationState state) {
        super();
        this.state = state;
    }

    /**
     * Creates a new application state event with the specified ID, timestamp, and state.
     *
     * @param eventId    the unique identifier for this event
     * @param occurredOn the timestamp when this event occurred
     * @param state      the application state
     */
    protected ApplicationStateAuditEvent(UUID eventId, Instant occurredOn, ApplicationState state) {
        super(eventId, occurredOn);
        this.state = state;
    }

    /**
     * Gets the application state.
     *
     * @return the application state
     */
    public ApplicationState getState() {
        return state;
    }

    @Override
    public String getEventType() {
        return "APPLICATION_STATE_CHANGED";
    }
}