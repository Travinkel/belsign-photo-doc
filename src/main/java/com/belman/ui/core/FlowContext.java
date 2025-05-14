package com.belman.ui.core;

/**
 * Generic state context for UI flows.
 * This is part of the State pattern for UI flows.
 *
 * @param <T> the type of flow state
 */
public abstract class FlowContext<T extends FlowState> {
    protected T currentState;

    /**
     * Sets the current state.
     *
     * @param state the new state
     */
    public void setState(T state) {
        if (currentState != null) {
            currentState.exit();
        }
        this.currentState = state;
        onStateChanged();
        if (currentState != null) {
            currentState.enter();
        }
    }

    /**
     * Called when the state changes.
     * Subclasses should override this method to handle state changes.
     */
    protected abstract void onStateChanged();

    /**
     * Gets the current state.
     *
     * @return the current state
     */
    public T getCurrentState() {
        return currentState;
    }
}