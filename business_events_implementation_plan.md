# Business Events Implementation Plan

## Overview

This document outlines the plan for implementing business events in the codebase. The goal is to create a more business-oriented event system that includes audit events as a specific type of business event.

## Current Structure

Currently, the codebase has an audit event system with the following components:

1. **AuditEvent Interface**: Defines the contract for audit events
   - `UUID getEventId()`
   - `Instant getOccurredOn()`
   - `String getEventType()`

2. **BaseAuditEvent Class**: Abstract base class that implements the AuditEvent interface
   - Provides common functionality for all audit events
   - Includes a unique identifier and timestamp

3. **AuditFacade Interface**: Central facade for all audit logging operations
   - `void logEvent(AuditEvent event)`
   - `void logBatch(List<AuditEvent> events)`
   - Convenience methods for specific events

4. **BusinessObject Class**: Base class for all business objects
   - Uses AuditEvent for tracking significant state changes
   - `void registerAuditEvent(AuditEvent event)`
   - `void registerAuditEvents(Collection<AuditEvent> events)`

## Proposed Structure

The new structure will introduce a more general business event concept, with audit events as a specific type of business event:

1. **BusinessEvent Interface**: Defines the contract for all business events
   - `UUID getEventId()`
   - `Instant getOccurredOn()`
   - `String getEventType()`

2. **BaseBusinessEvent Class**: Abstract base class that implements the BusinessEvent interface
   - Provides common functionality for all business events
   - Includes a unique identifier and timestamp

3. **AuditEvent Interface**: Extends BusinessEvent to define audit-specific functionality
   - May include additional methods specific to audit events
   - Inherits all methods from BusinessEvent

4. **BaseAuditEvent Class**: Extends BaseBusinessEvent and implements AuditEvent
   - Provides common functionality for all audit events
   - Inherits core functionality from BaseBusinessEvent

5. **BusinessEventPublisher**: Responsible for publishing business events
   - Based on the current AuditPublisher
   - Handles all types of business events, including audit events

6. **BusinessObject Class**: Updated to use BusinessEvent instead of AuditEvent
   - `void registerBusinessEvent(BusinessEvent event)`
   - `void registerBusinessEvents(Collection<BusinessEvent> events)`
   - Maintains backward compatibility with audit events

## Implementation Steps

### 1. Create Core Business Event Classes

1. Create `BusinessEvent.java` interface:
   ```java
   package com.belman.domain.event;

   import java.time.Instant;
   import java.util.UUID;

   public interface BusinessEvent {
       UUID getEventId();
       Instant getOccurredOn();
       String getEventType();
   }
   ```

2. Create `BaseBusinessEvent.java` abstract class:
   ```java
   package com.belman.domain.event;

   import java.time.Instant;
   import java.util.Objects;
   import java.util.UUID;

   public abstract class BaseBusinessEvent implements BusinessEvent {
       private final UUID eventId;
       private final Instant occurredOn;

       protected BaseBusinessEvent() {
           this(UUID.randomUUID(), Instant.now());
       }

       protected BaseBusinessEvent(UUID eventId, Instant occurredOn) {
           this.eventId = Objects.requireNonNull(eventId, "eventId must not be null");
           this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
       }

       @Override
       public UUID getEventId() {
           return eventId;
       }

       @Override
       public Instant getOccurredOn() {
           return occurredOn;
       }

       @Override
       public String getEventType() {
           return this.getClass().getSimpleName();
       }

       @Override
       public int hashCode() {
           return Objects.hash(eventId);
       }

       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
           BaseBusinessEvent that = (BaseBusinessEvent) o;
           return eventId.equals(that.eventId);
       }

       @Override
       public String toString() {
           return getClass().getSimpleName() + "{" +
                  "eventId=" + eventId +
                  ", occurredOn=" + occurredOn +
                  '}';
       }
   }
   ```

### 2. Update Audit Event Classes

1. Update `AuditEvent.java` interface to extend BusinessEvent:
   ```java
   package com.belman.domain.audit.event;

   import com.belman.domain.event.BusinessEvent;

   public interface AuditEvent extends BusinessEvent {
       // Additional audit-specific methods could be added here
   }
   ```

2. Update `BaseAuditEvent.java` to extend BaseBusinessEvent and implement AuditEvent:
   ```java
   package com.belman.domain.audit.event;

   import com.belman.domain.event.BaseBusinessEvent;

   import java.time.Instant;
   import java.util.UUID;

   public abstract class BaseAuditEvent extends BaseBusinessEvent implements AuditEvent {
       protected BaseAuditEvent() {
           super();
       }

       protected BaseAuditEvent(UUID eventId, Instant occurredOn) {
           super(eventId, occurredOn);
       }
   }
   ```

### 3. Create Business Event Publisher and Handler

1. Create `BusinessEventPublisher.java` based on AuditPublisher:
   ```java
   package com.belman.domain.event;

   import java.util.HashMap;
   import java.util.List;
   import java.util.Map;
   import java.util.concurrent.CopyOnWriteArrayList;

   public class BusinessEventPublisher {
       private static final BusinessEventPublisher INSTANCE = new BusinessEventPublisher();
       private final Map<Class<? extends BusinessEvent>, List<BusinessEventHandler<?>>> handlers = new HashMap<>();

       private BusinessEventPublisher() {
       }

       public static BusinessEventPublisher getInstance() {
           return INSTANCE;
       }

       public <T extends BusinessEvent> void register(Class<T> eventType, BusinessEventHandler<T> handler) {
           handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
       }

       public void publish(BusinessEvent event) {
           if (event == null) return;

           List<BusinessEventHandler<?>> eventHandlers = handlers.get(event.getClass());
           if (eventHandlers != null) {
               for (BusinessEventHandler handler : eventHandlers) {
                   handler.handle(event);
               }
           }
       }

       public void publishAll(Iterable<BusinessEvent> events) {
           if (events == null) return;

           for (BusinessEvent event : events) {
               publish(event);
           }
       }
   }
   ```

2. Create `BusinessEventHandler.java` interface:
   ```java
   package com.belman.domain.event;

   @FunctionalInterface
   public interface BusinessEventHandler<T extends BusinessEvent> {
       void handle(T event);
   }
   ```

### 4. Update BusinessObject Class

Update `BusinessObject.java` to use BusinessEvent instead of AuditEvent:

```java
package com.belman.domain.core;

import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.event.BusinessEvent;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

public abstract class BusinessObject<ID> implements Serializable {
   private static final long serialVersionUID = 1L;

   private static AuditFacade auditFacade;
   private Instant lastModifiedAt;

   public static void setAuditFacade(AuditFacade facade) {
      auditFacade = Objects.requireNonNull(facade, "auditFacade must not be null");
   }

   protected void registerBusinessEvent(BusinessEvent event) {
      Objects.requireNonNull(event, "event must not be null");

      // If it's an audit event, log it through the audit facade
      if (event instanceof AuditEvent) {
         if (auditFacade == null) {
            throw new IllegalStateException(
                    "AuditFacade has not been set. Call setAuditFacade during application initialization.");
         }
         auditFacade.logEvent((AuditEvent) event);
      }

      updateLastModifiedAt();
   }

   protected void registerBusinessEvents(Collection<? extends BusinessEvent> events) {
      Objects.requireNonNull(events, "events collection must not be null");

      // Filter out audit events and log them through the audit facade
      if (auditFacade != null) {
         List<AuditEvent> auditEvents = events.stream()
                 .filter(e -> e instanceof AuditEvent)
                 .map(e -> (AuditEvent) e)
                 .collect(Collectors.toList());

         if (!auditEvents.isEmpty()) {
            auditFacade.logBatch(auditEvents);
         }
      }

      updateLastModifiedAt();
   }

   // For backward compatibility
   protected void registerAuditEvent(AuditEvent event) {
      registerBusinessEvent(event);
   }

   protected void registerAuditEvents(Collection<AuditEvent> events) {
      registerBusinessEvents(new ArrayList<>(events));
   }

   protected void updateLastModifiedAt() {
      this.lastModifiedAt = Instant.now();
   }

   public Instant getLastModifiedAt() {
      return this.lastModifiedAt;
   }

   @Override
   public int hashCode() {
      return Objects.hash(getId());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      BusinessObject<?> that = (BusinessObject<?>) obj;
      return Objects.equals(getId(), that.getId());
   }

   public abstract ID getId();
}
```

### 5. Update Existing Event Classes

Update all existing audit event classes to use the new structure. For example:

```java
package com.belman.domain.order.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.UUID;

public class OrderApprovedEvent extends BaseAuditEvent {
   private final OrderId orderId;
   private final UserId approverId;

   public OrderApprovedEvent(OrderId orderId, UserId approverId) {
      super(UUID.randomUUID(), Instant.now());
      this.orderId = orderId;
      this.approverId = approverId;
   }

   @Override
   public String getEventType() {
      return "OrderApproved";
   }

   public OrderId getOrderId() {
      return orderId;
   }

   public UserId getApproverId() {
      return approverId;
   }
}
```

## Benefits

1. **Clearer Terminology**: Using "BusinessEvent" better reflects the purpose of these events in the business model.

2. **Hierarchical Structure**: The new structure creates a clear hierarchy where audit events are a specific type of business event.

3. **Extensibility**: The new structure makes it easier to add new types of business events in the future.

4. **Backward Compatibility**: The updated BusinessObject class maintains backward compatibility with existing code that uses audit events.

## Implementation Timeline

1. **Phase 1**: Create core business event classes and update audit event classes
2. **Phase 2**: Update BusinessObject class and existing event classes
3. **Phase 3**: Update tests and documentation