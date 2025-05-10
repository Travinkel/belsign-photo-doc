# Audit Events Standardization Implementation

## Overview

This document outlines the specific changes needed to implement the audit events standardization plan. The goal is to ensure consistency throughout the codebase and eliminate the need for casting when registering events.

## Changes Needed

### 1. Consolidate BaseAuditEvent Classes

Keep `com.belman.domain.audit.event.BaseAuditEvent` and remove `com.belman.domain.events.BaseAuditEvent`.

#### Classes to Update

The following classes need to be updated to use `com.belman.domain.audit.event.BaseAuditEvent` instead of `com.belman.domain.events.BaseAuditEvent`:

1. `com.belman.domain.order.events.OrderApprovedEvent`
2. `com.belman.domain.order.events.OrderCancelledEvent`
3. `com.belman.domain.order.events.OrderCompletedEvent`
4. `com.belman.domain.order.events.OrderRejectedEvent`
5. `com.belman.domain.order.photo.events.PhotoApprovedEvent`
6. `com.belman.domain.order.photo.events.PhotoRejectedEvent`
7. `com.belman.domain.report.events.ReportCompletedEvent`
8. `com.belman.domain.report.events.ReportGeneratedEvent`
9. `com.belman.domain.user.events.UserApprovedEvent`
10. `com.belman.domain.user.events.UserCreatedEvent`
11. `com.belman.domain.user.events.UserRejectedEvent`

### 2. Standardize Event Approach

Update all event classes to use `com.belman.domain.audit.event.BaseAuditEvent`.

#### Classes to Update

1. `com.belman.domain.user.events.UserApprovedEvent` - Update to use `com.belman.domain.audit.event.BaseAuditEvent`
2. `com.belman.domain.user.events.UserRejectedEvent` - Update to use `com.belman.domain.audit.event.BaseAuditEvent`

### 3. Remove Duplicate Events

Keep the "Audit" versions of events and remove the non-Audit versions.

#### Events to Remove

1. `com.belman.domain.events.UserApprovedEvent` - Remove and use `com.belman.domain.user.events.UserApprovedAuditEvent` instead
2. `com.belman.domain.events.UserRejectedEvent` - Remove and use `com.belman.domain.user.events.UserRejectedAuditEvent` instead
3. `com.belman.domain.user.events.UserApprovedEvent` - Remove and use `com.belman.domain.user.events.UserApprovedAuditEvent` instead
4. `com.belman.domain.user.events.UserRejectedEvent` - Remove and use `com.belman.domain.user.events.UserRejectedAuditEvent` instead

### 4. Update References

Update all references to the removed events to use the kept events.

#### Classes to Update

1. `com.belman.domain.user.UserAggregate` - Update to use `com.belman.domain.user.events.UserApprovedAuditEvent` and `com.belman.domain.user.events.UserRejectedAuditEvent` instead of `com.belman.domain.events.UserApprovedEvent`

## Implementation Steps

1. Update import statements in the classes listed above to use `com.belman.domain.audit.event.BaseAuditEvent` instead of `com.belman.domain.events.BaseAuditEvent`
2. Update `UserAggregate.java` to use `UserApprovedAuditEvent` and `UserRejectedAuditEvent` instead of `UserApprovedEvent`
3. Remove the duplicate event classes
4. Run tests to ensure everything still works

## Benefits

1. **Consistent Terminology**: By using a single set of event classes, we make it easier for developers to understand the purpose and responsibility of each class.
2. **Reduced Coupling**: The module package will depend on fewer external components, making it more self-contained and easier to maintain.
3. **Improved Testability**: Using a single approach to events makes them more testable and reduces code duplication.
4. **Better Error Handling**: By using a consistent approach to events, we create a more consistent error handling approach.