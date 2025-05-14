package com.belman.ui.base;

import com.belman.ui.core.ViewEvent;
import com.belman.ui.core.ViewObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for views that can be observed.
 * This is part of the Observer pattern for view updates.
 *
 * @param <T> the type of view model
 */
public abstract class ObservableView<T extends BaseViewModel<T>> extends BaseView<T> {
    private final List<ViewObserver> observers = new ArrayList<>();

    /**
     * Adds an observer to the view.
     *
     * @param observer the observer to add
     */
    public void addObserver(ViewObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the view.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(ViewObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers of a view event with the specified type and data.
     *
     * @param type the event type
     * @param data the event data
     */
    protected void notifyObservers(String type, Object data) {
        notifyObservers(new ViewEvent(type, data, this));
    }

    /**
     * Notifies all observers of a view event.
     *
     * @param event the view event
     */
    protected void notifyObservers(ViewEvent event) {
        observers.forEach(observer -> observer.onViewUpdated(event));
    }
}