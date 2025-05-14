package com.belman.domain.event.adapter;

import com.belman.domain.event.BusinessEvent;
import com.belman.domain.event.BusinessEventHandler;
import com.belman.domain.events.DomainEvent;
import com.belman.domain.events.DomainEventHandler;

/**
 * Adapter class that allows a BusinessEventHandler to be used as a DomainEventHandler.
 * <p>
 * This adapter implements the DomainEventHandler interface and wraps a BusinessEventHandler,
 * converting DomainEvent to BusinessEvent before delegating to the wrapped handler.
 * This allows BusinessEventHandlers to be registered with DomainEventPublisher.
 *
 * @param <T> the type of DomainEvent this handler can handle
 */
public class BusinessEventHandlerAdapter<T extends DomainEvent> implements DomainEventHandler<T> {
    private final BusinessEventHandler<BusinessEvent> businessEventHandler;

    /**
     * Creates a new BusinessEventHandlerAdapter that wraps the specified BusinessEventHandler.
     *
     * @param businessEventHandler the BusinessEventHandler to wrap
     */
    public BusinessEventHandlerAdapter(BusinessEventHandler<BusinessEvent> businessEventHandler) {
        if (businessEventHandler == null) {
            throw new IllegalArgumentException("BusinessEventHandler cannot be null");
        }
        this.businessEventHandler = businessEventHandler;
    }

    /**
     * Gets the wrapped BusinessEventHandler.
     *
     * @return the wrapped BusinessEventHandler
     */
    public BusinessEventHandler<BusinessEvent> getBusinessEventHandler() {
        return businessEventHandler;
    }

    @Override
    public void handle(T event) {
        // Convert DomainEvent to BusinessEvent using the adapter
        DomainEventAdapter adapter = new DomainEventAdapter(event);
        // Delegate to the wrapped BusinessEventHandler
        businessEventHandler.handle(adapter);
    }

    @Override
    public String toString() {
        return "BusinessEventHandlerAdapter{" +
               "businessEventHandler=" + businessEventHandler +
               '}';
    }
}