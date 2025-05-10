# Audit System Implementation

## Overview

The audit system provides a centralized mechanism for logging audit events across the application. It follows the Facade
pattern to simplify the interface for audit logging and to decouple business objects from the details of how audit
events are stored and processed.

## Key Components

### AuditFacade

The `AuditFacade` interface defines the contract for logging audit events:

```java
public interface AuditFacade {
    void logEvent(AuditEvent event);

    void logBatch(List<AuditEvent> events);

    void logPhotoApproved(PhotoId photoId, UserId approverId);

    void logPhotoRejected(PhotoId photoId, UserId rejecterId, String reason);

    List<AuditEvent> getEventsByEntity(String entityType, String entityId);
}
```

### AuditRepository

The `AuditRepository` interface defines the contract for storing and retrieving audit events:

```java
public interface AuditRepository {
    void store(AuditEvent event);

    void storeAll(List<AuditEvent> events);

    List<AuditEvent> getEventsByEntity(String entityType, String entityId);

    List<AuditEvent> getEventsByType(String eventType);

    List<AuditEvent> getEventsByUser(String userId);
}
```

### DefaultAuditFacade

The `DefaultAuditFacade` class is the default implementation of the `AuditFacade` interface. It delegates the storage of
audit events to an `AuditRepository` implementation.

### InMemoryAuditRepository

The `InMemoryAuditRepository` class is an implementation of the `AuditRepository` interface that stores audit events in
memory. It's useful for testing and development purposes.

### AuditConfig

The `AuditConfig` class is responsible for initializing the audit system. It provides methods for setting up the
`AuditFacade` with different repository implementations.

### BusinessObject Integration

The `BusinessObject` class has been updated to use the `AuditFacade` for logging audit events. It provides methods for
registering single and multiple audit events:

```java
private void registerAuditEvent(AuditEvent event) {
    Objects.requireNonNull(event, "event must not be null");
    if (auditFacade == null) {
        throw new IllegalStateException(
                "AuditFacade has not been set. Call setAuditFacade during application initialization.");
    }
    auditFacade.logEvent(event);
    updateLastModifiedAt();
}

protected void registerAuditEvents(Collection<AuditEvent> events) {
    Objects.requireNonNull(events, "events collection must not be null");
    if (auditFacade == null) {
        throw new IllegalStateException(
                "AuditFacade has not been set. Call setAuditFacade during application initialization.");
    }
    auditFacade.logBatch(events.stream().toList());
    updateLastModifiedAt();
```

## Audit Event Types

The system includes several audit event types for common operations:

- `CustomerCreatedEvent`: Published when a new customer is created
- `CustomerUpdatedEvent`: Published when a customer is updated
- `OrderCompletedEvent`: Published when an order is completed
- `OrderApprovedEvent`: Published when an order is approved
- `OrderRejectedEvent`: Published when an order is rejected
- `OrderCancelledEvent`: Published when an order is cancelled
- `PhotoApprovedEvent`: Published when a photo is approved
- `PhotoRejectedEvent`: Published when a photo is rejected

## Usage

### Initialization

The audit system should be initialized during application startup:

```java
// Create the AuditConfig with a logger factory
LoggerFactory loggerFactory = new YourLoggerFactory();
AuditConfig auditConfig = new AuditConfig(loggerFactory);

// Initialize with an in-memory repository for development/testing
auditConfig.initializeWithInMemoryRepository();

// Or initialize with a custom repository for production
AuditRepository auditRepository = new YourAuditRepository();
auditConfig.initialize(auditRepository);
```

### Logging Audit Events

Business objects can log audit events using the `registerAuditEvent` and `registerAuditEvents` methods:

```java
// Log a single audit event
registerAuditEvent(new CustomerCreatedEvent(customerId, customerType, userId));

// Log multiple audit events
List<AuditEvent> events = Arrays.asList(
    new CustomerCreatedEvent(customerId, customerType, userId),
    new CustomerUpdatedEvent(customerId, userId, changedFields)
);
registerAuditEvents(events);
```

### Creating Custom Audit Events

Custom audit events can be created by extending the `BaseAuditEvent` class:

```java
public class YourAuditEvent extends BaseAuditEvent {
    private final YourEntityId entityId;
    private final UserId userId;
    
    public YourAuditEvent(YourEntityId entityId, UserId userId) {
        super(UUID.randomUUID(), Instant.now());
        this.entityId = entityId;
        this.userId = userId;
    }
    
    @Override
    public String getEventType() {
        return "YourEventType";
    }
    
    public YourEntityId getEntityId() {
        return entityId;
    }
    
    public UserId getUserId() {
        return userId;
    }
    
    // Methods for filtering events by entity and user
    public String getEntityType() {
        return "YourEntity";
    }
    
    public String getEntityId() {
        return entityId.toString();
    }
    
    public String getUserId() {
        return userId.toString();
    }
}
```

## Benefits

1. **Centralization**: All audit logging logic is centralized in one place, making it easier to maintain and update.

2. **Decoupling**: Business objects don't need to know how audit events are stored or processed; they just emit them.

3. **Consistency**: All audit events are logged in a consistent manner, ensuring that the audit trail is complete and
   accurate.

4. **Flexibility**: The implementation of how audit events are stored and processed can be changed without affecting the
   business objects.

5. **Testability**: The audit logging functionality can be easily tested in isolation from the business objects.