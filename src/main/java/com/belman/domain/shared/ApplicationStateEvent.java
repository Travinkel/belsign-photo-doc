package com.belman.domain.shared;

/**
 * Base class for events related to application state changes.
 * These events are triggered by mobile lifecycle events and can be used
 * to manage application state during lifecycle transitions.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ApplicationStateEvent} instead.
 */
@Deprecated
public abstract class ApplicationStateEvent extends com.belman.domain.events.ApplicationStateEvent {

    /**
     * Creates a new ApplicationStateEvent with the specified state.
     *
     * @param state the new application state
     */
    protected ApplicationStateEvent(ApplicationState state) {
        super(state);
    }
}
