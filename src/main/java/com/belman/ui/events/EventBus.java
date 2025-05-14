package com.belman.ui.events;

import java.util.function.Consumer;

/**
 * Interface for an event bus.
 * This is part of the Mediator pattern for component communication.
 */
public interface EventBus {
    /**
     * Publishes an event to all subscribers.
     *
     * @param <T>   the type of the event
     * @param event the event to publish
     */
    <T> void publish(T event);

    /**
     * Publishes an event of the specified type to all subscribers.
     *
     * @param <T>       the type of the event
     * @param eventType the type of the event
     * @param event     the event to publish
     */
    <T> void publish(Class<T> eventType, T event);

    /**
     * Subscribes to events of the specified type.
     *
     * @param <T>        the type of the event
     * @param eventType  the type of the event
     * @param subscriber the subscriber
     * @return a subscription that can be used to unsubscribe
     */
    <T> Subscription subscribe(Class<T> eventType, Consumer<T> subscriber);

    /**
     * Unsubscribes from events.
     *
     * @param subscription the subscription to unsubscribe
     */
    void unsubscribe(Subscription subscription);
}