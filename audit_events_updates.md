# Audit Events Updates

## Overview

This document outlines the changes made to update the audit events in the codebase to use the new business terminology. The goal is to ensure that all audit events use `BaseAuditEvent` instead of `AbstractDomainEvent` or other approaches.

## Changes Made

### Photo Events

The following photo events have been updated to use `BaseAuditEvent` instead of `AbstractDomainEvent`:

1. **PhotoApprovedEvent.java**
   - Changed to extend `BaseAuditEvent` instead of `AbstractDomainEvent`
   - Updated class Javadoc to use "Audit event" instead of "Domain event"
   - Removed reference to "bounded context" in the Javadoc

2. **PhotoRejectedEvent.java**
   - Changed to extend `BaseAuditEvent` instead of `AbstractDomainEvent`
   - Updated class Javadoc to use "Audit event" instead of "Domain event"
   - Removed reference to "bounded context" in the Javadoc

### User Events

The following user events have been updated to use `BaseAuditEvent`:

1. **UserCreatedEvent.java**
   - Changed to extend `BaseAuditEvent`
   - Removed eventId and timestamp fields since they're inherited from `BaseAuditEvent`
   - Updated constructor to call super() to initialize the inherited fields
   - Added a second constructor for event deserialization/reconstitution
   - Removed the getEventId() and getTimestamp() methods since they're inherited from `BaseAuditEvent`
   - Updated class Javadoc to use "Audit event" instead of "Domain event"

2. **UserApprovedEvent.java**
   - Changed to extend `BaseAuditEvent` instead of implementing `AuditEvent` directly
   - Changed class visibility from package-private to public
   - Updated constructor to call super() to initialize the inherited fields
   - Added null checks for the constructor parameters
   - Added a second constructor for event deserialization/reconstitution
   - Added getter methods for the fields
   - Removed the overridden methods getEventId(), getOccurredOn(), and getEventType() since they're inherited from `BaseAuditEvent`

3. **UserRejectedEvent.java**
   - Changed to extend `BaseAuditEvent` instead of implementing `DomainEvent`
   - Changed class visibility from package-private to public
   - Removed eventId and occurredOn fields since they're inherited from `BaseAuditEvent`
   - Updated constructor to call super() to initialize the inherited fields
   - Added null checks for the constructor parameters
   - Added a second constructor for event deserialization/reconstitution
   - Added getter methods for the fields
   - Removed the overridden methods getEventId(), getOccurredOn(), and getEventType() since they're inherited from `BaseAuditEvent`
   - Added proper Javadoc comments

### Report Events

The following report events have been updated to use `BaseAuditEvent`:

1. **ReportGeneratedEvent.java**
   - Changed to extend `BaseAuditEvent` instead of `AbstractDomainEvent`
   - Updated class Javadoc to use "Audit event" instead of "Domain event"

2. **ReportCompletedEvent.java**
   - Changed to extend `BaseAuditEvent`
   - Removed eventId and timestamp fields since they're inherited from `BaseAuditEvent`
   - Updated constructor to call super() to initialize the inherited fields
   - Added a second constructor for event deserialization/reconstitution
   - Removed the getEventId() and getTimestamp() methods since they're inherited from `BaseAuditEvent`
   - Updated class Javadoc to use "Audit event" instead of "Domain event" and removed the reference to "bounded context"

## Inconsistencies Found

During the update process, several inconsistencies were found in the codebase regarding events:

1. **Multiple BaseAuditEvent Classes**
   - `com.belman.domain.events.BaseAuditEvent` (used by order events)
   - `com.belman.domain.audit.event.BaseAuditEvent` (used by user events with "Audit" in the name)

2. **Different Approaches to Events**
   - Order events (e.g., `OrderApprovedEvent.java`) extend `BaseAuditEvent`
   - Photo events (e.g., `PhotoApprovedEvent.java`) were extending `AbstractDomainEvent` (now updated to extend `BaseAuditEvent`)
   - Some user events (e.g., `UserApprovedEvent.java`) implement `AuditEvent` directly
   - Some user events (e.g., `UserApprovedAuditEvent.java`) extend `BaseAuditEvent` from a different package
   - Some user events (e.g., `UserCreatedEvent.java`) don't implement any interface or extend any class

3. **Duplicate Events**
   - There are both old-style events (e.g., `UserApprovedEvent.java`) and new-style events with "Audit" in the name (e.g., `UserApprovedAuditEvent.java`)

## Recommendations

To resolve these inconsistencies, the following steps are recommended:

1. **Consolidate BaseAuditEvent Classes**
   - Choose one `BaseAuditEvent` class to use throughout the codebase
   - Update all events to use the chosen `BaseAuditEvent` class

2. **Standardize Event Approach**
   - Choose one approach to events (e.g., extending `BaseAuditEvent`)
   - Update all events to use the chosen approach

3. **Remove Duplicate Events**
   - Choose one set of events to keep (e.g., the new-style events with "Audit" in the name)
   - Update all references to the removed events to use the kept events

## Next Steps

The next steps in the architecture overhaul are:

1. **Update Remaining Audit Events**
   - Update the remaining audit events to use the chosen approach

2. **Update Business Layer Tests**
   - Update tests to use the new naming conventions

3. **Data Layer Implementation**
   - Update repository implementations to use `DataAccessInterface`
   - Update service implementations to use `BusinessService`

4. **Presentation Layer Implementation**
   - Update view models to use the new business entity naming conventions
   - Update controllers to use the new business entity naming conventions
   - Update navigation-related classes to use the new business entity naming conventions
   - Update binding-related classes to use the new business entity naming conventions
   - Update core classes to use the new business entity naming conventions
   - Update UI components to use the new business entity naming conventions
