package com.belman.domain.shared;

/**
 * Event that is published when the application is paused.
 * This event is triggered when the application loses focus but is still visible,
 * such as when a dialog is shown over the application.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ApplicationPausedEvent} instead.
 */
@Deprecated
public class ApplicationPausedEvent extends com.belman.domain.events.ApplicationPausedEvent {

    /**
     * Creates a new ApplicationPausedEvent.
     */
    public ApplicationPausedEvent() {
        super();
    }
}
