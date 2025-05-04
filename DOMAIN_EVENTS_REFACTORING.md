# Domain Events Refactoring

## Overview

This document describes the refactoring of domain events in the BelSign Photo Documentation Module. The goal was to address the architectural violation where domain events were in the wrong package, specifically in `domain.shared` instead of `domain.events`.

## Changes Made

1. Created new event classes in the `domain.events` package:
   - `DomainEvent` interface - Base interface for all domain events
   - `DomainEventHandler` interface - Interface for domain event handlers
   - `AbstractDomainEvent` class - Abstract base class for domain events
   - `ViewShownEvent` class - Event for when a view is shown
   - `ViewHiddenEvent` class - Event for when a view is hidden

2. Updated existing classes in the `domain.shared` package to extend or implement the new classes:
   - `DomainEvent` now extends `com.belman.domain.events.DomainEvent`
   - `DomainEventHandler` now extends `com.belman.domain.events.DomainEventHandler`
   - `AbstractDomainEvent` now extends `com.belman.domain.events.AbstractDomainEvent`
   - `ViewShownEvent` now extends `com.belman.domain.events.ViewShownEvent`
   - `ViewHiddenEvent` now extends `com.belman.domain.events.ViewHiddenEvent`

3. Updated references to these classes in other parts of the codebase:
   - Updated `EventManager` to use the new interfaces
   - Updated `DomainEventPublisher` to use the new interfaces
   - Updated `GluonLifecycleManager` to use the new event classes
   - Updated `DomainEvents` to use the new interfaces
   - Updated test files to use the new classes

## Benefits

1. **Improved Architecture**: The refactoring improves the architecture by placing domain events in the correct package, following clean architecture principles.

2. **Better Separation of Concerns**: Domain events are now properly separated from other shared domain concepts.

3. **Backward Compatibility**: The refactoring maintains backward compatibility by having the old classes extend or implement the new ones, allowing existing code to continue working.

## Remaining Work

1. **Move Additional Event Classes**: The following event classes still need to be moved to the `domain.events` package:
   - `ApplicationBackgroundedEvent`
   - `ApplicationPausedEvent`
   - `ApplicationResumedEvent`
   - `ApplicationStartedEvent`
   - `ApplicationStateEvent`
   - `ApplicationStoppedEvent`
   - `CommandEvent`
   - `CommandExecutedEvent`
   - `CommandRedoneEvent`
   - `CommandUndoneEvent`

2. **Make Domain Events Immutable**: Domain events should be immutable, but several event-related classes have non-final fields:
   - `DomainEventPublisher.instance`
   - `DomainEventPublisher.logger`
   - `DomainEvents.logger`

3. **Fix Test Issues**: The `DomainEventsTest` is failing with Mockito errors. This is a testing issue rather than an architectural issue, but it should be addressed to ensure all tests pass.

## Next Steps

1. Move the remaining event classes from `domain.shared` to `domain.events`.
2. Make domain events immutable by making all fields final.
3. Fix the Mockito issues in the `DomainEventsTest`.
4. Address the other architectural violations identified in the `ARCHITECTURE_VIOLATIONS.md` document.