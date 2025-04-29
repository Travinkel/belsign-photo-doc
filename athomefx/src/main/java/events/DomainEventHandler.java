package events;

/**
 * Interface for domain event handlers.
 * Implementations of this interface can handle events of a specific type.
 * 
 * @param <T> the type of event this handler can handle
 */
@FunctionalInterface
public interface DomainEventHandler<T extends DomainEvent> {
    
    /**
     * Handles the specified event.
     * 
     * @param event the event to handle
     */
    void handle(T event);
}