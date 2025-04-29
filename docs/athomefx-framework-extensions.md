# AtHomeFX Framework Extensions

This document describes the extensions added to the AtHomeFX framework to support domain events, logging, and aspect-oriented programming (AOP).

## Domain Event System

The domain event system allows components to publish events that can be handled by other components without direct coupling. This is useful for implementing cross-cutting concerns like logging, auditing, and notifications.

### Core Components

- **DomainEvent**: Base interface for all domain events
- **AbstractDomainEvent**: Abstract base class that implements the DomainEvent interface
- **DomainEventHandler**: Interface for event handlers
- **DomainEventPublisher**: Singleton class for publishing events and managing handlers
- **PublishEvent**: Annotation for methods that publish domain events

### Usage

#### Creating a Domain Event

```java
public class UserLoggedInEvent extends AbstractDomainEvent {
    private final String username;
    
    public UserLoggedInEvent(String username) {
        super();
        this.username = username;
    }
    
    public String getUsername() {
        return username;
    }
}
```

#### Publishing an Event

```java
// Publish synchronously
DomainEventPublisher.getInstance().publish(new UserLoggedInEvent("john.doe"));

// Publish asynchronously
DomainEventPublisher.getInstance().publishAsync(new UserLoggedInEvent("john.doe"));
```

#### Handling an Event

```java
// Create a handler
DomainEventHandler<UserLoggedInEvent> handler = event -> {
    System.out.println("User logged in: " + event.getUsername());
};

// Register the handler
DomainEventPublisher.getInstance().register(UserLoggedInEvent.class, handler);

// Unregister the handler when no longer needed
DomainEventPublisher.getInstance().unregister(UserLoggedInEvent.class, handler);
```

#### Using AOP for Event Publishing

The framework includes an aspect for automatically publishing events from methods annotated with `@PublishEvent`. To use this feature, you need to add AspectJ to your project.

```java
@PublishEvent
public UserLoggedInEvent login(String username, String password) {
    // Authentication logic...
    return new UserLoggedInEvent(username);
}
```

To enable AspectJ, add the following dependencies to your pom.xml:

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.19</version>
</dependency>
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.19</version>
</dependency>
```

## Logging System

The framework includes a simple logging facade that can be used throughout the application. For production use, it's recommended to replace this with SLF4J.

### Core Components

- **Logger**: Simple logging facade with support for different log levels

### Usage

```java
// Get a logger for a class
private static final Logger logger = Logger.getLogger(MyClass.class);

// Log messages at different levels
logger.trace("This is a trace message");
logger.debug("This is a debug message");
logger.info("This is an info message");
logger.warn("This is a warning message");
logger.error("This is an error message");

// Log messages with parameters
logger.info("User {} logged in", username);

// Log exceptions
try {
    // Some code that might throw an exception
} catch (Exception e) {
    logger.error("An error occurred", e);
}
```

### Configuring Log Levels

```java
// Set the minimum log level
Logger.setMinimumLevel(Logger.Level.DEBUG);
```

### Using SLF4J (Recommended for Production)

For production use, it's recommended to replace the simple logging facade with SLF4J. To do this, add the following dependencies to your pom.xml:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.7</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.11</version>
</dependency>
```

Then create a new implementation of the Logger class that delegates to SLF4J.

## Modern UI Features

The framework includes several features inspired by modern UI frameworks like React, Vue, and Angular:

1. **Component-Based Architecture**: The framework encourages a component-based approach, where each view is a self-contained component with its own controller and view model.

2. **Reactive Data Binding**: The StateStore provides a centralized store for managing application state with reactive properties, similar to Vuex/Pinia in Vue.js.

3. **Declarative UI**: The FXML files allow for declarative UI definition, similar to JSX in React or templates in Vue.js.

4. **Dependency Injection**: The ServiceLocator provides a simple dependency injection mechanism, similar to Angular's DI system.

5. **Lifecycle Hooks**: The ViewLifecycle interface provides lifecycle hooks (onShow, onHide) for views and view models, similar to lifecycle hooks in React and Vue.js.

## Best Practices

1. **Use Domain Events for Cross-Cutting Concerns**: Use domain events for cross-cutting concerns like logging, auditing, and notifications, rather than adding this logic directly to your business code.

2. **Log at the Appropriate Level**: Use the appropriate log level for each message:
   - TRACE: Detailed information for debugging
   - DEBUG: Debugging information
   - INFO: General information about application progress
   - WARN: Potentially harmful situations
   - ERROR: Error events that might still allow the application to continue running

3. **Use AOP for Cross-Cutting Concerns**: Use AOP for cross-cutting concerns like logging, security, and transaction management, rather than adding this logic directly to your business code.

4. **Follow the Single Responsibility Principle**: Each class should have only one reason to change. Use domain events and AOP to separate cross-cutting concerns from business logic.

5. **Use Dependency Injection**: Use the ServiceLocator to inject dependencies rather than creating them directly, which makes your code more testable and maintainable.