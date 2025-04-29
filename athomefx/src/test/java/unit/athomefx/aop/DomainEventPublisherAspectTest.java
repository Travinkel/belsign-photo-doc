package unit.athomefx.aop;


import events.AbstractDomainEvent;
import events.DomainEventPublisher;
import events.PublishEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Test class for the DomainEventPublisherAspect.
 * 
 * Note: This test is a demonstration of how the aspect would be used if AspectJ were available.
 * It doesn't actually test the aspect, as AspectJ is not configured in this project.
 */
class DomainEventPublisherAspectTest {
    
    /**
     * Demonstrates how the @PublishEvent annotation would be used.
     */
    @Test
    void demonstratePublishEventAnnotation() {
        // In a real application with AspectJ configured, this would automatically publish the event
        UserService userService = new UserService();
        userService.login("john.doe", "password");
        
        // Since AspectJ is not configured, we need to manually publish the event
        DomainEventPublisher.getInstance().publish(new UserLoggedInEvent("john.doe"));
    }
    
    /**
     * Example service class that uses the @PublishEvent annotation.
     */
    static class UserService {
        
        /**
         * Logs in a user and returns a UserLoggedInEvent.
         * In a real application with AspectJ configured, the returned event would be automatically published.
         * 
         * @param username the username
         * @param password the password
         * @return a UserLoggedInEvent
         */
        @PublishEvent
        public UserLoggedInEvent login(String username, String password) {
            // Authentication logic would go here
            System.out.println("User " + username + " logged in");
            return new UserLoggedInEvent(username);
        }
        
        /**
         * Logs in a user asynchronously and returns a UserLoggedInEvent.
         * In a real application with AspectJ configured, the returned event would be automatically published asynchronously.
         * 
         * @param username the username
         * @param password the password
         * @return a UserLoggedInEvent
         */
        @PublishEvent(async = true)
        public UserLoggedInEvent loginAsync(String username, String password) {
            // Authentication logic would go here
            System.out.println("User " + username + " logged in asynchronously");
            return new UserLoggedInEvent(username);
        }
    }
    
    /**
     * Example domain event class.
     */
    static class UserLoggedInEvent extends AbstractDomainEvent {
        private final String username;
        
        public UserLoggedInEvent(String username) {
            super();
            this.username = username;
        }
        
        public String getUsername() {
            return username;
        }
    }
}