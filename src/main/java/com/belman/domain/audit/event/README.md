# Audit Event System

## Overview

The audit event system provides a way to track significant state changes within the application for accountability and
traceability purposes. It replaces the previous domain event system with a more business-oriented approach that focuses
on auditing.

## Key Components

### AuditEvent Interface

The `AuditEvent` interface defines the contract for all audit events in the system:

```java
public interface AuditEvent {
    UUID getEventId();
    Instant getOccurredOn();
    String getEventType();
}
```

### BaseAuditEvent Abstract Class

The `BaseAuditEvent` abstract class provides a base implementation of the `AuditEvent` interface:

```java
public abstract class BaseAuditEvent implements AuditEvent {
    private final UUID eventId;
    private final Instant occurredOn;
    
    // Constructor and implementation details...
}
```

### AuditPublisher

The `AuditPublisher` class is responsible for publishing audit events to registered handlers:

```java
public class AuditPublisher implements IAuditPublisher {
    // Implementation details...
    
    public void publish(AuditEvent event) {
        // Publish the event to registered handlers
    }
    
    public void publishAll(Iterable<AuditEvent> events) {
        // Publish multiple events
    }
    
    public <T extends AuditEvent> void publishAsync(T event) {
        // Publish the event asynchronously
    }
}
```

### AuditHandler Interface

The `AuditHandler` interface defines the contract for handling audit events:

```java
@FunctionalInterface
public interface AuditHandler<T extends AuditEvent> {
    void handle(T event);
}
```

## Usage

### Creating Audit Events

To create a new audit event, extend the `BaseAuditEvent` class:

```java
public class UserApprovedAuditEvent extends BaseAuditEvent {
    private final UserId userId;
    private final UserId approverId;
    private final ApprovalStatus approvalStatus;
    
    // Constructor and implementation details...
    
    @Override
    public String getEventType() {
        return "UserApproved";
    }
}
```

### Emitting Audit Events

Business objects can emit audit events using the `registerAuditEvent` method:

```java
public void approve(UserBusiness reviewer, Instant reviewedAt) {
    approvalState.approve(this, reviewer, reviewedAt);
    registerAuditEvent(new UserApprovedAuditEvent(this.getId(), reviewer.getId(), ApprovalStatus.APPROVED));
}
```

### Handling Audit Events

To handle audit events, implement the `AuditHandler` interface and register it with the `AuditPublisher`:

```java
AuditHandler<UserApprovedAuditEvent> handler = event -> {
    // Handle the event
};

AuditPublisher.getInstance().register(UserApprovedAuditEvent.class, handler);
```

## Migration Notes

The audit event system is part of a larger effort to move away from DDD terminology and adopt a more business-oriented
approach. As part of this effort:

1. The `DomainEvent` interface has been replaced with the `AuditEvent` interface
2. The `AbstractDomainEvent` class has been replaced with the `BaseAuditEvent` class
3. The `DomainEventPublisher` class has been replaced with the `AuditPublisher` class
4. The `DomainEventHandler` interface has been replaced with the `AuditHandler` interface

For more information on the migration, see the [REFACTORING_NOTES.md](../../REFACTORING_NOTES.md) file.