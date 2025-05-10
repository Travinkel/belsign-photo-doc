# Audit Events Standardization Plan

## Overview

This document outlines the plan for standardizing audit events in the codebase. The goal is to ensure consistency throughout the codebase and eliminate the need for casting when registering events.

## Current Issues

1. **Multiple BaseAuditEvent Classes**:
   - `com.belman.domain.events.BaseAuditEvent`
   - `com.belman.domain.audit.event.BaseAuditEvent`

2. **Different Approaches to Events**:
   - Order events (e.g., `OrderApprovedEvent.java`) extend `BaseAuditEvent`
   - Photo events (e.g., `PhotoApprovedEvent.java`) extend `BaseAuditEvent`
   - Some user events (e.g., `UserApprovedEvent.java`) implement `AuditEvent` directly
   - Some user events (e.g., `UserApprovedAuditEvent.java`) extend `BaseAuditEvent` from a different package
   - Some user events (e.g., `UserCreatedEvent.java`) don't implement any interface or extend any class

3. **Duplicate Events**:
   - There are both old-style events (e.g., `UserApprovedEvent.java`) and new-style events with "Audit" in the name (e.g., `UserApprovedAuditEvent.java`)

4. **Inconsistent Usage**:
   - `UserAggregate.java` uses `com.belman.domain.events.UserApprovedEvent` for both approval and rejection
   - `UserBusiness.java` uses `com.belman.domain.user.events.UserApprovedAuditEvent` for approval and `com.belman.domain.user.events.UserRejectedAuditEvent` for rejection
   - `OrderBusiness.java` casts events to `AuditEvent` when registering them

## Standardization Plan

### 1. Consolidate BaseAuditEvent Classes

Keep `com.belman.domain.audit.event.BaseAuditEvent` and remove `com.belman.domain.events.BaseAuditEvent`.

Rationale:
- `com.belman.domain.audit.event.AuditEvent` is used by core components of the system, including:
  - The audit infrastructure (AuditFacade, AuditRepository, DefaultAuditFacade)
  - The business layer core classes (BusinessObject, BusinessService)
  - Business objects (OrderBusiness, ReportBusiness)
- `com.belman.domain.events.AuditEvent` is used by only one test file

### 2. Standardize Event Approach

Update all event classes to use `com.belman.domain.audit.event.BaseAuditEvent`.

Specific changes:
1. Update `com.belman.domain.user.events.UserApprovedEvent` to use `com.belman.domain.audit.event.BaseAuditEvent`
2. Update `com.belman.domain.user.events.UserRejectedEvent` to use `com.belman.domain.audit.event.BaseAuditEvent`
3. Update any other events that don't currently use `com.belman.domain.audit.event.BaseAuditEvent`

### 3. Remove Duplicate Events

Keep the "Audit" versions of events and remove the non-Audit versions.

Specific changes:
1. Keep `com.belman.domain.user.events.UserApprovedAuditEvent` and remove `com.belman.domain.user.events.UserApprovedEvent`
2. Keep `com.belman.domain.user.events.UserRejectedAuditEvent` and remove `com.belman.domain.user.events.UserRejectedEvent`
3. Remove `com.belman.domain.events.UserApprovedEvent` and `com.belman.domain.events.UserRejectedEvent`

### 4. Update References

Update all references to the removed events to use the kept events.

Specific changes:
1. Update `UserAggregate.java` to use `com.belman.domain.user.events.UserApprovedAuditEvent` and `com.belman.domain.user.events.UserRejectedAuditEvent`
2. Update any other classes that reference the removed events

## Implementation Steps

1. Create a new branch for the changes
2. Make the changes outlined above
3. Run tests to ensure everything still works
4. Submit a pull request for review

## Benefits

1. **Consistent Terminology**: By using a single set of event classes, we make it easier for developers to understand the purpose and responsibility of each class.
2. **Reduced Coupling**: The module package will depend on fewer external components, making it more self-contained and easier to maintain.
3. **Improved Testability**: Using a single approach to events makes them more testable and reduces code duplication.
4. **Better Error Handling**: By using a consistent approach to events, we create a more consistent error handling approach.