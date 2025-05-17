package com.belman.presentation.core;

/**
 * Interface for flow states.
 * This is part of the State pattern for UI flows.
 */
public interface FlowState {
    /**
     * Called when entering the state.
     */
    void enter();

    /**
     * Called when exiting the state.
     */
    void exit();
}