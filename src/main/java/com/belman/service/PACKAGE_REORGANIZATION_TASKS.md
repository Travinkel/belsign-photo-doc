# Business Layer Package Reorganization Tasks

This document outlines the specific tasks needed to reorganize the business layer according to the package structure guidelines defined in [PACKAGE_STRUCTURE_GUIDELINES.md](PACKAGE_STRUCTURE_GUIDELINES.md).

## Task List

### 1. Preparation

- [ ] Back up the current codebase
- [ ] Create a new branch for the reorganization
- [ ] Run all tests to ensure the current codebase is working correctly

### 2. Create Missing Packages

- [ ] Create any missing packages according to the package structure guidelines
- [ ] Ensure all packages have appropriate README.md files explaining their purpose

### 3. Move Classes by Responsibility

#### Business Objects

- [ ] Move all classes ending with "Business" to the appropriate module package
  - [ ] Move OrderBusiness to com.belman.service.module.order
  - [ ] Move UserBusiness to com.belman.service.module.user
  - [ ] Move CustomerBusiness to com.belman.service.module.customer
  - [ ] Move ReportBusiness to com.belman.service.module.report

#### Business Components

- [ ] Move all classes ending with "Component" to the appropriate module package
  - [ ] Move CustomerComponent to com.belman.service.module.customer
  - [ ] Move any other component classes to their respective packages

#### Data Access Interfaces

- [ ] Move all interfaces ending with "DataAccess" or "Repository" to the appropriate module package
  - [ ] Move OrderRepository to com.belman.service.module.order
  - [ ] Move UserRepository to com.belman.service.module.user
  - [ ] Move CustomerRepository to com.belman.service.module.customer
  - [ ] Move ReportRepository to com.belman.service.module.report
  - [ ] Move PhotoRepository to com.belman.service.module.order.photo

#### Business Services

- [ ] Move all classes ending with "BusinessService" or "DomainService" to the appropriate module.services package
  - [ ] Move OrderBusinessService to com.belman.service.module.order.services
  - [ ] Move PhotoApprovalService to com.belman.service.module.order.photo.services
  - [ ] Move PhotoValidationService to com.belman.service.module.order.photo.services
  - [ ] Move PhotoReportGenerationService to com.belman.service.module.report.service

#### Data Objects

- [ ] Move all classes ending with "DataObject" or "ValueObject" to the module.common.base package
  - [ ] Move any data objects to com.belman.service.module.common.base

#### Audit Events

- [ ] Move all classes ending with "Event" to the appropriate module.events package
  - [ ] Move OrderCreatedEvent to com.belman.service.module.order.events
  - [ ] Move OrderApprovedEvent to com.belman.service.module.order.events
  - [ ] Move OrderRejectedEvent to com.belman.service.module.order.events
  - [ ] Move OrderCompletedEvent to com.belman.service.module.order.events
  - [ ] Move OrderCancelledEvent to com.belman.service.module.order.events
  - [ ] Move PhotoApprovedEvent to com.belman.service.module.order.photo.events
  - [ ] Move PhotoRejectedEvent to com.belman.service.module.order.photo.events
  - [ ] Move UserCreatedEvent to com.belman.service.module.user.events
  - [ ] Move UserApprovedEvent to com.belman.service.module.user.events
  - [ ] Move UserRejectedEvent to com.belman.service.module.user.events
  - [ ] Move ReportGeneratedEvent to com.belman.service.module.report.events
  - [ ] Move ReportCompletedEvent to com.belman.service.module.report.events
  - [ ] Move other events to the appropriate packages

#### Specifications

- [ ] Move all classes ending with "Specification" to the appropriate module.specification package
  - [ ] Move OrderCompletionSpecification to com.belman.service.module.order.specification
  - [ ] Move MinPhotosSpecification to com.belman.service.module.specification
  - [ ] Move PendingOrdersSpecification to com.belman.service.module.specification
  - [ ] Move PhotoQualitySpecification to com.belman.service.module.specification

#### Exceptions

- [ ] Move all classes ending with "Exception" to the module.exceptions package
  - [ ] Move BusinessException to com.belman.service.module.exceptions
  - [ ] Move AccessDeniedException to com.belman.service.module.exceptions
  - [ ] Move BusinessRuleViolationException to com.belman.service.module.exceptions
  - [ ] Move EntityNotFoundException to com.belman.service.module.exceptions
  - [ ] Move InvalidValueException to com.belman.service.module.exceptions

#### Service Interfaces

- [ ] Move all interfaces ending with "Service" to the appropriate module.services package
  - [ ] Move CameraService to com.belman.service.module.services
  - [ ] Move EmailService to com.belman.service.module.services
  - [ ] Move PhotoService to com.belman.service.module.services
  - [ ] Move ReportBuilderService to com.belman.service.module.services
  - [ ] Move AuthenticationService to com.belman.service.module.security

### 4. Move Classes by Feature

#### Order-related Classes

- [ ] Move all classes containing "Order" to the module.order package
  - [ ] Move OrderId to com.belman.service.module.order
  - [ ] Move OrderNumber to com.belman.service.module.order
  - [ ] Move OrderStatus to com.belman.service.module.order
  - [ ] Move ProductDescription to com.belman.service.module.order
  - [ ] Move DeliveryInformation to com.belman.service.module.order

#### User-related Classes

- [ ] Move all classes containing "User" to the module.user package
  - [ ] Move UserId to com.belman.service.module.user
  - [ ] Move Username to com.belman.service.module.user
  - [ ] Move UserReference to com.belman.service.module.user
  - [ ] Move UserStatus to com.belman.service.module.user
  - [ ] Move UserRole to com.belman.service.module.user
  - [ ] Move ApprovalState to com.belman.service.module.user
  - [ ] Move ApprovalStatus to com.belman.service.module.user
  - [ ] Move ApprovedState to com.belman.service.module.user
  - [ ] Move PendingApprovalState to com.belman.service.module.user
  - [ ] Move RejectedState to com.belman.service.module.user

#### Customer-related Classes

- [ ] Move all classes containing "Customer" to the module.customer package
  - [ ] Move CustomerId to com.belman.service.module.customer
  - [ ] Move CustomerType to com.belman.service.module.customer
  - [ ] Move Company to com.belman.service.module.customer

#### Report-related Classes

- [ ] Move all classes containing "Report" to the module.report package
  - [ ] Move ReportId to com.belman.service.module.report
  - [ ] Move ReportStatus to com.belman.service.module.report
  - [ ] Move ReportType to com.belman.service.module.report
  - [ ] Move ReportFormat to com.belman.service.module.report

#### Photo-related Classes

- [ ] Move all classes containing "Photo" to the module.order.photo package
  - [ ] Move Photo to com.belman.service.module.order.photo
  - [ ] Move PhotoId to com.belman.service.module.order.photo
  - [ ] Move PhotoTemplate to com.belman.service.module.order.photo
  - [ ] Move PhotoDocument to com.belman.service.module.order.photo
  - [ ] Move PhotoDocumentFactory to com.belman.service.module.order.photo
  - [ ] Move PhotoAnnotation to com.belman.service.module.order.photo
  - [ ] Move PhotoQualityPolicy to com.belman.service.module.order.photo.policy
  - [ ] Move ProductPhotoRequirementPolicy to com.belman.service.module.order.photo.policy
  - [ ] Move IPhotoQualityService to com.belman.service.module.order.photo.policy

#### Security-related Classes

- [ ] Move all security-related classes to the module.security package
  - [ ] Move HashedPassword to com.belman.service.module.security
  - [ ] Move PasswordHasher to com.belman.service.module.security

#### Common Value Objects

- [ ] Move all common value objects to the module.common package
  - [ ] Move EmailAddress to com.belman.service.module.common
  - [ ] Move Money to com.belman.service.module.common
  - [ ] Move PersonName to com.belman.service.module.common
  - [ ] Move PhoneNumber to com.belman.service.module.common
  - [ ] Move Timestamp to com.belman.service.module.common

### 5. Update Import Statements

- [ ] Update import statements in all affected classes
- [ ] Fix any compilation errors resulting from the reorganization

### 6. Testing

- [ ] Run all unit tests to ensure they still pass
- [ ] Run all integration tests to ensure they still pass
- [ ] Run all acceptance tests to ensure they still pass
- [ ] Run the application to ensure it still works correctly

### 7. Documentation

- [ ] Update any documentation that references the old package structure
- [ ] Document any issues encountered during the reorganization and their solutions
- [ ] Create a summary of the changes made

### 8. Finalization

- [ ] Commit the changes
- [ ] Create a pull request
- [ ] Have the changes reviewed by other team members
- [ ] Merge the changes into the main branch

## Progress Tracking

Use this section to track the progress of the reorganization:

- [ ] Preparation: 0%
- [ ] Create Missing Packages: 0%
- [ ] Move Classes by Responsibility: 0%
- [ ] Move Classes by Feature: 0%
- [ ] Update Import Statements: 0%
- [ ] Testing: 0%
- [ ] Documentation: 0%
- [ ] Finalization: 0%

## Notes

- The reorganization should be done in small, incremental steps to minimize the risk of breaking the codebase.
- Each step should be tested before moving on to the next step.
- If any issues are encountered, they should be documented and addressed before continuing.
- The reorganization should be done in a separate branch to avoid disrupting the main development workflow.
- The reorganization should be reviewed by other team members before being merged into the main branch.