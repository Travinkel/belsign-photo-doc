package com.belman.business.richbe.events;

/**
 * Base class for events related to application state changes.
 * These events are triggered by mobile lifecycle events and can be used
 * to manage application state during lifecycle transitions.
 */
public abstract class ApplicationStateEvent extends AbstractDomainEvent {
    private final ApplicationState state;

    /**
     * Creates a new ApplicationStateEvent with the specified state.
     *
     * @param state the new application state
     */
    protected ApplicationStateEvent(ApplicationState state) {
        super();
        this.state = state;
    }

    /**
     * Gets the application state associated with this event.
     *
     * @return the application state
     */
    public ApplicationState getState() {
        return state;
    }

    @Override
    public String toString() {
        return String.format("%s[state=%s, timestamp=%s, id=%s]",
                getEventType(), state, getOccurredOn(), getEventId());
    }

    /**
     * Enum representing the possible states of the application.
     */
    public enum ApplicationState {
        /**
         * The application is in the foreground and active.
         */
        ACTIVE,

        /**
         * The application is in the foreground but paused (e.g., dialog shown).
         */
        PAUSED,

        /**
         * The application is in the background but still running.
         */
        BACKGROUND,

        /**
         * The application is being stopped.
         */
        STOPPING,

        /**
         * The application is starting up.
         */
        STARTING
    }
}
