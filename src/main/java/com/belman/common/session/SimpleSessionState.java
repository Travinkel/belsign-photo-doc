package com.belman.common.session;

/**
 * A simple implementation of the SessionState interface.
 * This class represents a basic session state with a name.
 */
public class SimpleSessionState implements SessionState {
    private final String name;

    /**
     * Creates a new SimpleSessionState with the specified name.
     *
     * @param name the name of the state
     */
    public SimpleSessionState(String name) {
        this.name = name;
    }

    @Override
    public void handle(Object context) {
        // Simple implementation does nothing
    }

    @Override
    public String getName() {
        return name;
    }
}