package com.belman.ui.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Simple implementation of the EventBus interface.
 * This is part of the Mediator pattern for component communication.
 */
public class SimpleEventBus implements EventBus {
    private final Map<Class<?>, List<SubscriberInfo<?>>> subscribers = new HashMap<>();

    @Override
    public <T> void publish(T event) {
        if (event == null) {
            return;
        }
        publish((Class<T>) event.getClass(), event);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void publish(Class<T> eventType, T event) {
        if (event == null || eventType == null) {
            return;
        }

        List<SubscriberInfo<?>> eventSubscribers = subscribers.get(eventType);
        if (eventSubscribers == null) {
            return;
        }

        // Create a copy of the subscribers list to avoid concurrent modification
        List<SubscriberInfo<?>> copy = new ArrayList<>(eventSubscribers);
        for (SubscriberInfo<?> subscriberInfo : copy) {
            try {
                ((SubscriberInfo<T>) subscriberInfo).subscriber.accept(event);
            } catch (Exception e) {
                // Log the exception but don't let it stop other subscribers
                System.err.println("Error publishing event to subscriber: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public <T> Subscription subscribe(Class<T> eventType, Consumer<T> subscriber) {
        if (eventType == null || subscriber == null) {
            throw new IllegalArgumentException("Event type and subscriber cannot be null");
        }

        List<SubscriberInfo<?>> eventSubscribers = subscribers.computeIfAbsent(eventType, k -> new ArrayList<>());
        SubscriberInfo<T> subscriberInfo = new SubscriberInfo<>(eventType, subscriber);
        eventSubscribers.add(subscriberInfo);

        return new SubscriptionImpl<>(this, subscriberInfo);
    }

    @Override
    public void unsubscribe(Subscription subscription) {
        if (subscription instanceof SubscriptionImpl) {
            SubscriptionImpl<?> subscriptionImpl = (SubscriptionImpl<?>) subscription;
            subscriptionImpl.unsubscribeFromBus();
        }
    }

    /**
     * Unsubscribes a subscriber from the event bus.
     *
     * @param subscriberInfo the subscriber info
     * @param <T>            the type of the event
     */
    <T> void unsubscribe(SubscriberInfo<T> subscriberInfo) {
        List<SubscriberInfo<?>> eventSubscribers = subscribers.get(subscriberInfo.eventType);
        if (eventSubscribers != null) {
            eventSubscribers.remove(subscriberInfo);
            if (eventSubscribers.isEmpty()) {
                subscribers.remove(subscriberInfo.eventType);
            }
        }
    }

    /**
     * Information about a subscriber.
     *
     * @param <T> the type of the event
     */
    private static class SubscriberInfo<T> {
        final Class<T> eventType;
        final Consumer<T> subscriber;

        SubscriberInfo(Class<T> eventType, Consumer<T> subscriber) {
            this.eventType = eventType;
            this.subscriber = subscriber;
        }
    }

    /**
     * Implementation of the Subscription interface.
     *
     * @param <T> the type of the event
     */
    private static class SubscriptionImpl<T> implements Subscription {
        private final SimpleEventBus eventBus;
        private SubscriberInfo<T> subscriberInfo;

        SubscriptionImpl(SimpleEventBus eventBus, SubscriberInfo<T> subscriberInfo) {
            this.eventBus = eventBus;
            this.subscriberInfo = subscriberInfo;
        }

        void unsubscribeFromBus() {
            unsubscribe();
        }

        @Override
        public void unsubscribe() {
            if (subscriberInfo != null) {
                eventBus.unsubscribe(subscriberInfo);
                subscriberInfo = null;
            }
        }
    }
}