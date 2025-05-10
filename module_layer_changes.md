# Module Layer Changes

## Overview

This document outlines the changes made to transition from Domain-Driven Design (DDD) terminology to business terminology in the business layer. The changes are part of the architecture overhaul to ensure that the module package doesn't depend on other layers and follows the three-layer architecture (GUI, DAL, BLL + BE).

## Changes Made

### Business Services

The following services have been updated to use BusinessService instead of implementing IDomainService or using other service patterns:

1. **PhotoApprovalService.java**
   - Changed to extend BusinessService
   - Updated to use LoggerFactory for logging
   - Updated to use logInfo() and other logging methods from BusinessService
   - Updated class Javadoc to use "Business service" instead of "Domain service"

2. **PhotoValidationService.java**
   - Changed to extend BusinessService instead of implementing IDomainService
   - Added super() call in constructor
   - Implemented getLoggerFactory() method
   - Updated class Javadoc to use "Business service" instead of "Domain service"

3. **OrderBusinessService.java** (renamed from OrderDomainService.java)
   - Created new file that extends BusinessService
   - Updated parameter names from orderAggregate to order
   - Added logging statements using BusinessService logging methods
   - Updated class Javadoc to use "Business service" instead of "Domain service"
   - Updated to work with OrderBusiness instead of OrderAggregate

### Exceptions

The following exceptions have been updated to extend BusinessException instead of DomainException:

1. **AccessDeniedException.java**
   - Changed to extend BusinessException
   - Updated class Javadoc to use "business exception" and "business logic"

2. **BusinessRuleViolationException.java**
   - Changed to extend BusinessException
   - Updated class Javadoc to use "business exception" and "business logic"

3. **EntityNotFoundException.java**
   - Changed to extend BusinessException
   - Updated class Javadoc to use "business exception" and "business logic"

4. **InvalidValueException.java**
   - Changed to extend BusinessException
   - Updated class Javadoc to use "business rules", "business exception", and "business logic"

## Benefits

These changes provide several benefits to the architecture:

1. **Consistent Terminology**: By using business terminology consistently throughout the codebase, we make it easier for developers to understand the purpose and responsibility of each class.

2. **Reduced Coupling**: The module package now depends on fewer external components, making it more self-contained and easier to maintain.

3. **Improved Testability**: BusinessService provides common functionality like logging, which makes services more testable and reduces code duplication.

4. **Better Error Handling**: By using BusinessException as the base class for all business-related exceptions, we create a more consistent error handling approach.

## Future Work

While significant progress has been made, there are still several tasks to complete:

1. **Update Audit Events**: The audit events need to be updated to use BaseAuditEvent instead of AbstractDomainEvent.

2. **Fix Build Errors**: The current build has errors due to incompatibilities between old and new classes. These need to be resolved by updating the remaining classes to use the new terminology.

3. **Update Tests**: The tests need to be updated to use the new naming conventions and class hierarchies.

4. **Remove Old DDD-style Classes**: Once all classes have been updated, the old DDD-style classes and interfaces can be removed.

5. **Update Documentation**: The architecture guide and other documentation need to be updated to reflect the new terminology and class hierarchies.