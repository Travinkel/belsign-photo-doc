package com.belman.common.session;

/**
 * Interface for session states.
 * This interface defines the behavior of different session states.
 */
public interface SessionState {

    /**
     * Handles the current state.
     *
     * @param context the session context
     */
    void handle(Object context);

    /**
     * Gets the name of the state.
     *
     * @return the name of the state
     */
    String getName();
}
