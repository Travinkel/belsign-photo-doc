# Events Package Analysis

## Overview

This document analyzes the events package inside business.module and evaluates whether there is a need for a separate
events module now that we are moving away from DDD terminology and focusing on audit events for traceability.

## Current Structure

The current events package (`business.module.events`) contains:

1. **Base event interfaces and classes**:
    - `DomainEvent` interface
    - `AbstractDomainEvent` abstract class
    - `AuditEvent` interface
    - `BaseAuditEvent` abstract class

2. **Event publishing infrastructure**:
    - `DomainEventPublisher` class
    - `DomainEventHandler` interface
    - `DomainEventHandlerImplementation` class
    - `IDomainEventPublisher` interface

3. **Application lifecycle events**:
    - `ApplicationStartedEvent`
    - `ApplicationStoppedEvent`
    - `ApplicationPausedEvent`
    - `ApplicationResumedEvent`
    - `ApplicationBackgroundedEvent`
    - `ApplicationStateEvent`

4. **Command-related events**:
    - `CommandExecutedEvent`
    - `CommandUndoneEvent`
    - `CommandRedoneEvent`
    - `CommandEvent`

5. **User-related events**:
    - `UserApprovedEvent`
    - `UserRejectedEvent`
    - `UserLoggedInEvent`
    - `UserLoggedOutEvent`

6. **View-related events**:
    - `ViewShownEvent`
    - `ViewHiddenEvent`

## Analysis

1. **Duplication with Audit Module**:
    - The newly created audit module (`business.module.audit`) provides similar functionality to the events package
    - Both are used for tracking significant state changes in the system
    - The audit module is more focused on traceability and accountability

2. **Module-Specific Events**:
    - User-related events should be in the user module
    - Order-related events should be in the order module
    - Report-related events should be in the report module
    - Photo-related events should be in the order.photo module

3. **Shared Events**:
    - Application lifecycle events are shared across modules
    - Command-related events are shared across modules
    - View-related events are shared across modules

## Recommendations

Based on the analysis, here are the recommendations:

1. **Rename the Events Package**:
    - Rename `business.module.events` to `business.module.system` or `business.module.shared`
    - This better reflects its purpose as a container for shared system events

2. **Move Module-Specific Events**:
    - Move user-related events to `business.module.user.events`
    - Move order-related events to `business.module.order.events`
    - Move report-related events to `business.module.report.events`
    - Move photo-related events to `business.module.order.photo.events`

3. **Migrate to Audit Events**:
    - Gradually replace domain events with audit events
    - Update all code that uses domain events to use audit events instead
    - Eventually remove the domain event classes once they're no longer used

4. **Keep Shared Events in the Renamed Package**:
    - Keep application lifecycle events in the renamed package
    - Keep command-related events in the renamed package
    - Keep view-related events in the renamed package

## Conclusion

There is no need for a separate events module. The audit module we've created is sufficient for traceability purposes.
The current events package should be renamed to better reflect its purpose as a container for shared system events. By
moving module-specific events to their respective modules and gradually migrating from domain events to audit events, we
can achieve a more cohesive and maintainable codebase that focuses on traceability and accountability.

The audit module provides all the functionality needed for tracking significant state changes in the system, and it does
so with a more business-oriented approach that aligns with our goal of moving away from DDD terminology. By
consolidating event-related functionality in the audit module and module-specific packages, we can reduce duplication
and improve the overall organization of the codebase.
