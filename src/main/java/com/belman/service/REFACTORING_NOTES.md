# Refactoring Notes for Business Layer

## Overview

This document outlines issues identified during the refactoring of the business layer to move away from DDD terminology
and adopt a more business-oriented approach. It highlights areas that need further attention in future refactoring
efforts.

## Parallel Class Hierarchies

We currently have two parallel class hierarchies:

1. **DDD-style hierarchy**:
    - `AggregateRoot` -> `UserAggregate`, `OrderAggregate`, etc.
    - Uses `DomainEvent` for events
    - Located in `business.module.core` and `business.module.<feature>`

2. **Business-style hierarchy**:
    - `BusinessObject` -> `UserBusiness`, `OrderBusiness`, etc.
    - Uses `AuditEvent` for events
    - Located in `business.module.core` and `business.module.<feature>`

This duplication causes compatibility issues, especially with interfaces like `ApprovalState` that are designed to work
with one hierarchy but are used by both.

## Specific Issues

### UserAggregate.java

1. `approve()` and `reject()` methods pass `UserAggregate` to `ApprovalState` methods that expect `UserBusiness`
2. Registers `UserApprovedEvent` as a `DomainEvent`, but they're not compatible with the new event system

### OrderAggregate.java

1. Registers various order events (`OrderCompletedEvent`, `OrderApprovedEvent`, etc.) as `DomainEvent`, but they're not
   compatible with the new event system

### ApprovalState.java

1. Methods are designed to work with `UserBusiness`, not `UserAggregate`

## Potential Solutions

1. **Short-term**: Keep both hierarchies but ensure they don't interact
    - Update interfaces to use generic type parameters where needed
    - Create separate event hierarchies for each class hierarchy

2. **Medium-term**: Gradually migrate from DDD to business terminology
    - Convert one aggregate at a time to use the business object hierarchy
    - Update all references to use the new classes
    - Remove the old classes once all references are updated

3. **Long-term**: Complete removal of DDD terminology
    - Eliminate all DDD-style classes and interfaces
    - Use only business-oriented terminology throughout the codebase
    - Ensure consistent naming and organization

## Recommended Approach

The recommended approach is to follow the medium-term solution:

1. Start by creating a comprehensive test suite to ensure functionality is preserved
2. Convert one aggregate at a time, starting with the least connected ones
3. Update all references to use the new classes
4. Run tests after each conversion to ensure functionality is preserved
5. Remove the old classes once all references are updated

## Next Steps

1. Create a detailed migration plan for each aggregate
2. Prioritize the aggregates based on their dependencies
3. Implement the migration plan one aggregate at a time
4. Update documentation to reflect the new architecture