package com.belman.domain.audit;

/**
 * Interface for handling business events.
 * <p>
 * This functional interface defines the contract for classes that handle
 * business events. It is parameterized with the type of business event
 * that the handler can process.
 *
 * @param <T> the type of business event this handler can process
 */
@FunctionalInterface
public interface BusinessEventHandler<T extends BusinessEvent> {

    /**
     * Handles a business event.
     *
     * @param event the business event to handle
     */
    void handle(T event);
}