package com.belman.domain.shared;

/**
 * Event that is published when the application is resumed.
 * This event is triggered when the application regains focus after being paused,
 * such as when returning to the application from a dialog or another app.
 * 
 * @deprecated This class is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.ApplicationResumedEvent} instead.
 */
@Deprecated
public class ApplicationResumedEvent extends com.belman.domain.events.ApplicationResumedEvent {

    /**
     * Creates a new ApplicationResumedEvent.
     */
    public ApplicationResumedEvent() {
        super();
    }
}
