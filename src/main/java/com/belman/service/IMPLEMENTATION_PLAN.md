# Implementation Plan for Business Layer Refactoring

## Overview

This document outlines the implementation plan for completing the transition from DDD terminology to business
terminology in the business layer. The plan follows the medium-term approach recommended in the REFACTORING_NOTES.md
file, which involves gradually migrating from DDD to business terminology one aggregate at a time.

## Current Status

The core business layer components have been created:

- AuditEvent.java, BaseAuditEvent.java, IAuditPublisher.java, AuditHandler.java, AuditHandlerImplementation.java,
  AuditPublisher.java
- BusinessObject.java, BusinessComponent.java, IBusinessService.java, BusinessService.java, DataAccessInterface.java,
  DataObject.java, BusinessException.java

However, many existing classes still need to be updated to use these new components:

- Business entities (OrderBusiness.java, UserBusiness.java, CustomerBusiness.java, ReportBusiness.java)
- Business components (Photo.java, etc.)
- Data objects (EmailAddress.java, Money.java, PersonName.java, PhoneNumber.java, Timestamp.java, etc.)
- Business services (PhotoApprovalService.java, PhotoValidationService.java, PhotoReportGenerationService.java,
  OrderBusinessService.java, etc.)
- Data access interfaces (OrderDataAccess.java, PhotoDataAccess.java, UserDataAccess.java, CustomerDataAccess.java,
  ReportDataAccess.java)
- Exceptions (AccessDeniedException.java, BusinessRuleViolationException.java, EntityNotFoundException.java,
  InvalidValueException.java)
- Audit events (OrderCreatedEvent.java, OrderApprovedEvent.java, etc.)

## Implementation Strategy

The implementation will follow these steps:

1. **Prioritize modules based on dependencies**:
    - Start with modules that have fewer dependencies on other modules
    - Prioritize modules that are more stable and less likely to change

2. **For each module**:
    - Update business entities to use BusinessObject
    - Update business components to use BusinessComponent
    - Update data objects to use DataObject
    - Update business services to use BusinessService
    - Update data access interfaces to use DataAccessInterface
    - Update exceptions to extend BusinessException
    - Update audit events to use BaseAuditEvent
    - Update tests to use the new naming conventions

3. **After each module is updated**:
    - Run tests to ensure functionality is preserved
    - Fix any issues that arise
    - Document the changes made

4. **After all modules are updated**:
    - Remove the old DDD-style classes and interfaces
    - Update the architecture guide to remove DDD terminology and use the new naming conventions

## Module Prioritization

Based on dependencies and stability, the modules will be updated in the following order:

1. **Common Module**:
    - Contains basic data objects like EmailAddress, Money, PersonName, PhoneNumber, Timestamp
    - Has few dependencies on other modules
    - Is used by many other modules

2. **Customer Module**:
    - Contains CustomerBusiness, CustomerComponent, CustomerDataAccess
    - Has dependencies on the Common module
    - Is relatively self-contained

3. **Report Module**:
    - Contains ReportBusiness, ReportDataAccess
    - Has dependencies on the Common module
    - Is relatively self-contained

4. **User Module**:
    - Contains UserBusiness, UserDataAccess
    - Has dependencies on the Common module
    - Is used by other modules

5. **Order Module**:
    - Contains OrderBusiness, OrderDataAccess
    - Has dependencies on the Common, Customer, and User modules
    - Contains the Photo submodule

## Detailed Implementation Plan

### Phase 1: Common Module

1. Update EmailAddress.java to use DataObject
2. Update Money.java to use DataObject
3. Update PersonName.java to use DataObject
4. Update PhoneNumber.java to use DataObject
5. Update Timestamp.java to use DataObject
6. Update other data objects to use DataObject
7. Update tests for these data objects

### Phase 2: Customer Module

1. Update CustomerBusiness.java to use BusinessObject
2. Update CustomerComponent.java to use BusinessComponent
3. Update CustomerDataAccess.java to use DataAccessInterface
4. Create CustomerAuditEvents to replace CustomerEvents
5. Update tests for the Customer module

### Phase 3: Report Module

1. Update ReportBusiness.java to use BusinessObject
2. Update ReportDataAccess.java to use DataAccessInterface ✓
3. Create ReportAuditEvents to replace ReportEvents
4. Update PhotoReportGenerationService.java to use BusinessService
5. Update tests for the Report module

### Phase 4: User Module

1. Update UserBusiness.java to use BusinessObject
2. Update UserDataAccess.java to use DataAccessInterface
3. Create UserAuditEvents to replace UserEvents
4. Update tests for the User module

### Phase 5: Order Module

1. Update OrderBusiness.java to use BusinessObject ✓
2. Update OrderDataAccess.java to use DataAccessInterface ✓
3. Update Photo.java to use BusinessComponent ✓
4. Create ComponentDataAccessInterface.java for business components ✓
5. Create PhotoDataAccess.java to use ComponentDataAccessInterface ✓
6. Create InMemoryPhotoRepository.java to implement PhotoRepository ✓
7. Create PhotoDataAccessAdapter.java to adapt InMemoryPhotoRepository to PhotoDataAccess ✓
8. Create OrderAuditEvents to replace OrderEvents
9. Create PhotoAuditEvents to replace PhotoEvents
10. Update PhotoApprovalService.java to use BusinessService
11. Update PhotoValidationService.java to use BusinessService
12. Update OrderBusinessService.java to use BusinessService
13. Update tests for the Order module

### Phase 6: Exceptions

1. Update AccessDeniedException.java to extend BusinessException
2. Update BusinessRuleViolationException.java to extend BusinessException
3. Update EntityNotFoundException.java to extend BusinessException
4. Update InvalidValueException.java to extend BusinessException

### Phase 7: Cleanup and Documentation

1. Remove old DDD-style classes and interfaces
2. Update the architecture guide to remove DDD terminology and use the new naming conventions
3. Document the changes made and provide guidelines for future development

## Timeline

The implementation will be completed in phases, with each phase focusing on a specific module. The estimated timeline is
as follows:

- Phase 1 (Common Module): 1 week
- Phase 2 (Customer Module): 1 week
- Phase 3 (Report Module): 1 week
- Phase 4 (User Module): 1 week
- Phase 5 (Order Module): 2 weeks
- Phase 6 (Exceptions): 1 week
- Phase 7 (Cleanup and Documentation): 1 week

Total estimated time: 8 weeks

## Conclusion

This implementation plan provides a structured approach to completing the transition from DDD terminology to business
terminology in the business layer. By following this plan, we can ensure that the transition is completed in a
systematic and controlled manner, with minimal disruption to the codebase and functionality.
