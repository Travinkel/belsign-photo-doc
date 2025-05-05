package com.belman.domain.shared;

/**
 * Event that is published when the application is started.
 * This event is triggered during application initialization.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ApplicationStartedEvent} instead.
 */
@Deprecated
public class ApplicationStartedEvent extends com.belman.domain.events.ApplicationStartedEvent {

    /**
     * Creates a new ApplicationStartedEvent.
     */
    public ApplicationStartedEvent() {
        super();
    }
}
