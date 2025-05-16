package com.belman.domain.audit.adapter;

import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.audit.event.AuditHandler;
import com.belman.domain.audit.BusinessEvent;
import com.belman.domain.audit.BusinessEventHandler;

/**
 * Adapter class that allows a BusinessEventHandler to be used as an AuditHandler.
 * <p>
 * This adapter implements the AuditHandler interface and wraps a BusinessEventHandler,
 * converting AuditEvent to BusinessEvent before delegating to the wrapped handler.
 * This allows BusinessEventHandlers to be registered with AuditPublisher.
 *
 * @param <T> the type of AuditEvent this handler can handle
 */
public class BusinessEventHandlerToAuditHandlerAdapter<T extends AuditEvent> implements AuditHandler<T> {
    private final BusinessEventHandler<BusinessEvent> businessEventHandler;

    /**
     * Creates a new BusinessEventHandlerToAuditHandlerAdapter that wraps the specified BusinessEventHandler.
     *
     * @param businessEventHandler the BusinessEventHandler to wrap
     */
    public BusinessEventHandlerToAuditHandlerAdapter(BusinessEventHandler<BusinessEvent> businessEventHandler) {
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
        // Convert AuditEvent to BusinessEvent using the adapter
        AuditEventAdapter adapter = new AuditEventAdapter(event);
        // Delegate to the wrapped BusinessEventHandler
        businessEventHandler.handle(adapter);
    }

    @Override
    public String toString() {
        return "BusinessEventHandlerToAuditHandlerAdapter{" +
               "businessEventHandler=" + businessEventHandler +
               '}';
    }
}