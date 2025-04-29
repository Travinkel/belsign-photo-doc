package events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods that should publish domain events.
 * Methods annotated with this annotation will have their return values published as domain events.
 * The return value must be a DomainEvent or a subclass of DomainEvent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PublishEvent {
    
    /**
     * Whether to publish the event asynchronously.
     * If true, the event will be published in a separate thread.
     * If false, the event will be published in the same thread.
     * 
     * @return true if the event should be published asynchronously, false otherwise
     */
    boolean async() default false;
}