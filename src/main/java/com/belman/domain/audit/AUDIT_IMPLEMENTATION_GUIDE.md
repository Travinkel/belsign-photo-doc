# Audit System Implementation Guide

## Overview

This guide explains how to implement the audit system in the Belsign Photo Documentation project. The audit system
provides a centralized mechanism for logging audit events across the application, ensuring accountability and
traceability.

## Key Components

### 1. Auditable Interface

The `Auditable` interface defines the contract for classes that can be audited:

```java
public interface Auditable {
    String getAuditEntityType();
    String getAuditEntityId();
    String getAuditUserId();
    String getAuditAction();
    String getAuditDetails();
}
```

### 2. AuditableBusinessEvent Class

The `AuditableBusinessEvent` class extends `BaseBusinessEvent` and implements the `Auditable` interface, providing a
bridge between the business event system and the audit system:

```java
public abstract class AuditableBusinessEvent extends BaseBusinessEvent implements Auditable {
    private final String entityType;
    private final String entityId;
    private final String userId;
    private final String action;
    private final String details;
    
    protected AuditableBusinessEvent(String entityType, String entityId, String userId, String action, String details) {
        super();
        this.entityType = entityType;
        this.entityId = entityId;
        this.userId = userId;
        this.action = action;
        this.details = details;
    }
    
    // Implementations of Auditable methods
    @Override
    public String getAuditEntityType() {
        return entityType;
    }
    
    @Override
    public String getAuditEntityId() {
        return entityId;
    }
    
    @Override
    public String getAuditUserId() {
        return userId;
    }
    
    @Override
    public String getAuditAction() {
        return action;
    }
    
    @Override
    public String getAuditDetails() {
        return details;
    }
}
```

### 3. AuditFacade Interface

The `AuditFacade` interface defines the contract for logging audit events:

```java
public interface AuditFacade {
    void logEvent(AuditEvent event);
    void logBatch(List<AuditEvent> events);
    void logBusinessEvent(AuditableBusinessEvent event);
    void logBusinessEvents(List<AuditableBusinessEvent> events);
    // Other methods...
}
```

### 4. Database Schema

The audit system uses two tables to store audit events:

- `audit_events`: Stores the main audit event information
- `audit_event_properties`: Stores additional properties of audit events

## Implementing Auditable in Service Classes

To implement the `Auditable` interface in a service class, follow these steps:

1. Make your service class implement the `Auditable` interface:

```java
public class YourService implements YourServiceInterface, Auditable {
    // ...
}
```

2. Add fields to track audit context:

```java
private UserBusiness currentUser;
private String currentAction;
private String currentEntityType;
private String currentEntityId;
private String currentDetails;
```

3. Implement the `Auditable` methods:

```java
@Override
public String getAuditEntityType() {
    return currentEntityType;
}

@Override
public String getAuditEntityId() {
    return currentEntityId;
}

@Override
public String getAuditUserId() {
    return currentUser != null ? currentUser.getId().toString() : null;
}

@Override
public String getAuditAction() {
    return currentAction;
}

@Override
public String getAuditDetails() {
    return currentDetails;
}
```

4. Set the audit context before performing operations:

```java
public void someOperation(EntityId entityId, UserBusiness user) {
    // Set audit context
    this.currentUser = user;
    this.currentAction = "SOME_OPERATION";
    this.currentEntityType = "Entity";
    this.currentEntityId = entityId.toString();
    this.currentDetails = "Performed some operation on entity " + entityId;
    
    // Perform the operation
    // ...
    
    // Log the audit event
    auditFacade.logBusinessEvent(new SomeOperationEvent(
            entityId,
            user.getId().toString()
    ));
}
```

5. Create event classes for your operations:

```java
private static class SomeOperationEvent extends AuditableBusinessEvent {
    public SomeOperationEvent(EntityId entityId, String userId) {
        super("Entity", entityId.toString(), userId, "SOME_OPERATION", "Performed some operation on entity " + entityId);
    }
}
```

## Example: Implementing Auditable in PhotoService

Here's an example of how to implement the `Auditable` interface in the `PhotoService`:

```java
public class AuditablePhotoService implements PhotoService, Auditable {
    private final PhotoService delegateService;
    private final AuditFacade auditFacade;
    private UserBusiness currentUser;
    private String currentAction;
    private String currentEntityType;
    private String currentEntityId;
    private String currentDetails;
    
    // Constructor
    
    @Override
    public PhotoDocument uploadPhoto(OrderId orderId, Photo photo, UserBusiness uploadedBy) {
        // Set audit context
        this.currentUser = uploadedBy;
        this.currentAction = "UPLOAD_PHOTO";
        this.currentEntityType = "Photo";
        this.currentEntityId = photo.getId().toString();
        this.currentDetails = "Uploaded photo for order " + orderId.toString();
        
        // Delegate to the actual service
        PhotoDocument result = delegateService.uploadPhoto(orderId, photo, uploadedBy);
        
        // Log the audit event
        auditFacade.logBusinessEvent(new PhotoUploadedEvent(
                result.getId(),
                orderId,
                uploadedBy.getId().toString()
        ));
        
        return result;
    }
    
    // Other methods...
    
    // Auditable implementation
    
    @Override
    public String getAuditEntityType() {
        return currentEntityType;
    }
    
    @Override
    public String getAuditEntityId() {
        return currentEntityId;
    }
    
    @Override
    public String getAuditUserId() {
        return currentUser != null ? currentUser.getId().toString() : null;
    }
    
    @Override
    public String getAuditAction() {
        return currentAction;
    }
    
    @Override
    public String getAuditDetails() {
        return currentDetails;
    }
    
    // Event classes
    
    private static class PhotoUploadedEvent extends AuditableBusinessEvent {
        public PhotoUploadedEvent(PhotoId photoId, OrderId orderId, String userId) {
            super("Photo", photoId.toString(), userId, "UPLOAD_PHOTO", "Uploaded photo for order " + orderId.toString());
        }
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