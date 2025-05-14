package com.belman.ui.core;

/**
 * Event class for view updates.
 * This is part of the Observer pattern for view updates.
 */
public class ViewEvent {
    private final String type;
    private final Object data;
    private final Object source;

    /**
     * Creates a new ViewEvent.
     *
     * @param type   the event type
     * @param data   the event data
     * @param source the event source
     */
    public ViewEvent(String type, Object data, Object source) {
        this.type = type;
        this.data = data;
        this.source = source;
    }

    /**
     * Gets the event type.
     *
     * @return the event type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the event data.
     *
     * @return the event data
     */
    public Object getData() {
        return data;
    }

    /**
     * Gets the event source.
     *
     * @return the event source
     */
    public Object getSource() {
        return source;
    }
}