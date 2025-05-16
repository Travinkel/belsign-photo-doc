package com.belman.domain.audit.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that represents a change in the application state.
 * <p>
 * This event captures information about state transitions in the application lifecycle,
 * such as starting, pausing, resuming, or stopping the application.
 * <p>
 * It provides detailed information about:
 * - What happened: The state transition that occurred
 * - Who triggered it: The user or system component that initiated the transition
 * - Why it happened: The reason for the state change
 * - How it happened: The mechanism that triggered the state change
 * - When and where it occurred: Timestamp and context information
 */
public class ApplicationStateEvent extends BaseAuditEvent {
    
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
    
    private final ApplicationState previousState;
    private final ApplicationState newState;
    private final String initiator;
    private final String reason;
    private final String mechanism;
    private final String context;
    
    /**
     * Creates a new ApplicationStateEvent with the specified states and details.
     *
     * @param previousState the state before the transition
     * @param newState      the state after the transition
     * @param initiator     the entity that initiated the state change (user ID or system component)
     * @param reason        the reason for the state change
     * @param mechanism     the mechanism that triggered the state change (e.g., "user action", "system event")
     * @param context       additional context information about where the state change occurred
     */
    public ApplicationStateEvent(
            ApplicationState previousState,
            ApplicationState newState,
            String initiator,
            String reason,
            String mechanism,
            String context) {
        super();
        this.previousState = previousState;
        this.newState = newState;
        this.initiator = initiator;
        this.reason = reason;
        this.mechanism = mechanism;
        this.context = context;
    }
    
    /**
     * Creates a new ApplicationStateEvent with the specified ID, timestamp, states, and details.
     *
     * @param eventId       the unique identifier for this event
     * @param occurredOn    the timestamp when this event occurred
     * @param previousState the state before the transition
     * @param newState      the state after the transition
     * @param initiator     the entity that initiated the state change (user ID or system component)
     * @param reason        the reason for the state change
     * @param mechanism     the mechanism that triggered the state change (e.g., "user action", "system event")
     * @param context       additional context information about where the state change occurred
     */
    public ApplicationStateEvent(
            UUID eventId,
            Instant occurredOn,
            ApplicationState previousState,
            ApplicationState newState,
            String initiator,
            String reason,
            String mechanism,
            String context) {
        super(eventId, occurredOn);
        this.previousState = previousState;
        this.newState = newState;
        this.initiator = initiator;
        this.reason = reason;
        this.mechanism = mechanism;
        this.context = context;
    }
    
    /**
     * Gets the state before the transition.
     *
     * @return the previous state
     */
    public ApplicationState getPreviousState() {
        return previousState;
    }
    
    /**
     * Gets the state after the transition.
     *
     * @return the new state
     */
    public ApplicationState getNewState() {
        return newState;
    }
    
    /**
     * Gets the entity that initiated the state change.
     *
     * @return the initiator (user ID or system component)
     */
    public String getInitiator() {
        return initiator;
    }
    
    /**
     * Gets the reason for the state change.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Gets the mechanism that triggered the state change.
     *
     * @return the mechanism (e.g., "user action", "system event")
     */
    public String getMechanism() {
        return mechanism;
    }
    
    /**
     * Gets additional context information about where the state change occurred.
     *
     * @return the context
     */
    public String getContext() {
        return context;
    }
    
    @Override
    public String getEventType() {
        return "APPLICATION_STATE_CHANGED";
    }
    
    /**
     * Creates a builder for ApplicationStateEvent.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder for ApplicationStateEvent.
     */
    public static class Builder {
        private ApplicationState previousState;
        private ApplicationState newState;
        private String initiator = "system";
        private String reason = "Not specified";
        private String mechanism = "system event";
        private String context = "application";
        
        /**
         * Sets the previous state.
         *
         * @param previousState the state before the transition
         * @return this builder for method chaining
         */
        public Builder previousState(ApplicationState previousState) {
            this.previousState = previousState;
            return this;
        }
        
        /**
         * Sets the new state.
         *
         * @param newState the state after the transition
         * @return this builder for method chaining
         */
        public Builder newState(ApplicationState newState) {
            this.newState = newState;
            return this;
        }
        
        /**
         * Sets the initiator.
         *
         * @param initiator the entity that initiated the state change
         * @return this builder for method chaining
         */
        public Builder initiator(String initiator) {
            this.initiator = initiator;
            return this;
        }
        
        /**
         * Sets the reason.
         *
         * @param reason the reason for the state change
         * @return this builder for method chaining
         */
        public Builder reason(String reason) {
            this.reason = reason;
            return this;
        }
        
        /**
         * Sets the mechanism.
         *
         * @param mechanism the mechanism that triggered the state change
         * @return this builder for method chaining
         */
        public Builder mechanism(String mechanism) {
            this.mechanism = mechanism;
            return this;
        }
        
        /**
         * Sets the context.
         *
         * @param context additional context information
         * @return this builder for method chaining
         */
        public Builder context(String context) {
            this.context = context;
            return this;
        }
        
        /**
         * Builds the ApplicationStateEvent.
         *
         * @return a new ApplicationStateEvent
         * @throws IllegalStateException if previousState or newState is null
         */
        public ApplicationStateEvent build() {
            if (previousState == null) {
                throw new IllegalStateException("Previous state cannot be null");
            }
            if (newState == null) {
                throw new IllegalStateException("New state cannot be null");
            }
            
            return new ApplicationStateEvent(
                    previousState,
                    newState,
                    initiator,
                    reason,
                    mechanism,
                    context
            );
        }
    }
}