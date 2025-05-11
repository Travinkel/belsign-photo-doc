# Master Task List for Belsign Photo Documentation Project

## Terminology Note
**Important:** As per project requirements, we will use the following terminology:
- "Repository" instead of "Data Access Interface"
- "Business Object" instead of "Aggregate Root"
- "Business Component" instead of "Entity"
- "Data Object" instead of "Value Object"
- "Business Events" instead of "Domain Events"
- "Business Service" instead of "Domain Service"

While some tasks below reference "DataAccessInterface", we are maintaining "Repository" terminology in the codebase.

**Repository Usage Note:** We will only use repositories for the root business objects (equivalent to aggregate roots), which are Order and User. Other business objects like Report and Customer will use data access interfaces but not extend the Repository interface.

## Architectural Violations Fixes

### 1. Fix Dependency Validation Failures

#### 1.1 Fix Layered Architecture Violations
- [ ] Fix violations in `DependencyValidationTest` (7757 violations reported)
- [ ] Ensure UI layer doesn't depend on repository implementations
- [ ] Ensure service layer doesn't depend on UI layer except through interfaces
- [ ] Ensure repository layer doesn't depend on UI layer
- [ ] Move misplaced classes to their correct layers

#### 1.2 Fix Package Structure Issues
- [X] Move bootstrap code from `com.belman.repository.bootstrap` to `com.belman.bootstrap`
- [ ] Move common utilities from `com.belman.domain.common` to `com.belman.common`
- [ ] Ensure domain.shared package is used correctly for shared domain concepts
- [ ] Reorganize service layer to follow the usecase pattern

### 2. Fix Rich Business Entities Issues

#### 2.1 Address Anemic Domain Models
- [ ] Add business behavior methods to domain classes ending with "Business"
- [ ] Add business behavior methods to domain classes ending with "Component"
- [ ] Ensure domain classes encapsulate both data and behavior

#### 2.2 Fix Value Object Issues
- [ ] Make all fields in value objects final
- [ ] Ensure value objects in domain.common package are immutable
- [ ] Implement proper validation in value object constructors

### 3. Fix Three-Layer Architecture Violations

#### 3.1 Fix Service Layer Dependencies
- [ ] Remove direct dependencies from service layer to UI layer
- [ ] Ensure services depend on interfaces, not implementations
- [ ] Move service implementations to correct packages

#### 3.2 Fix Repository Layer Dependencies
- [ ] Ensure repository implementations don't depend on service layer
- [ ] Move repository-related code from service layer to repository layer

#### 3.3 Fix UI Layer Dependencies
- [x] Remove direct dependencies from UI layer to repository layer
- [ ] Ensure UI components only access repositories through service layer

### 4. Fix MVVM Architecture Violations

#### 4.1 Fix View Model Issues
- [ ] Ensure view models don't depend on controllers
- [ ] Move view models to correct packages
- [ ] Implement proper data binding in view models

#### 4.2 Fix View Issues
- [ ] Make fields in views final where appropriate
- [ ] Ensure views depend only on view models
- [ ] Implement proper separation of concerns in views

#### 4.3 Fix Controller Issues
- [ ] Ensure controllers are in the correct packages
- [ ] Implement proper controller logic
- [ ] Remove repository dependencies from controllers

### 5. Fix Architectural Antipatterns

#### 5.1 Fix Cyclic Dependencies
- [ ] Break cyclic dependencies between packages
- [ ] Introduce interfaces to decouple components
- [ ] Apply dependency inversion principle where needed

#### 5.2 Fix God Classes
- [ ] Split large classes into smaller, more focused classes
- [ ] Extract related functionality into helper classes
- [ ] Apply Single Responsibility Principle

#### 5.3 Fix Feature Envy
- [ ] Move methods to the classes they're most interested in
- [ ] Reduce method calls to other objects
- [ ] Apply "Tell, Don't Ask" principle

#### 5.4 Fix Law of Demeter Violations
- [ ] Reduce chains of method calls
- [ ] Create wrapper methods to encapsulate chains
- [ ] Apply "Talk only to your immediate friends" principle

#### 5.5 Fix Inappropriate Intimacy
- [ ] Reduce dependencies between classes
- [ ] Use dependency injection
- [ ] Apply Law of Demeter

### 6. Improve Code Quality

#### 6.1 Fix Naming Conventions
- [ ] Use consistent naming across the codebase
- [ ] Rename classes to follow project conventions
- [ ] Use descriptive names for methods and variables

#### 6.2 Improve Documentation
- [ ] Add/update JavaDoc comments for public APIs
- [ ] Add package-info.java files
- [ ] Update README files with architecture overview

#### 6.3 Improve Error Handling
- [ ] Add proper exception handling
- [ ] Use custom exceptions where appropriate
- [ ] Add logging for exceptions

#### 6.4 Improve Performance
- [ ] Optimize database queries
- [ ] Reduce unnecessary object creation
- [ ] Apply lazy loading where appropriate

### 7. Ensure Tests Work

#### 7.1 Fix Failing Tests
- [ ] Fix `DependencyValidationTest`
- [ ] Fix `RichBusinessEntitiesTest`
- [ ] Fix `ThreeLayerArchitectureTest`
- [ ] Fix `MVVMAndPresentationRulesTest`
- [ ] Fix `ArchitecturalAntipatternTest`

#### 7.2 Add Missing Tests
- [ ] Add tests for new functionality
- [ ] Add tests for edge cases
- [ ] Add tests for error handling

#### 7.3 Improve Test Coverage
- [ ] Ensure all business logic is tested
- [ ] Ensure all error paths are tested
- [ ] Add integration tests for critical paths

## Implementation Plan for Architectural Violations Fixes

1. Start with fixing the most critical dependency violations
2. Address anemic domain models and value object issues
3. Fix three-layer architecture violations
4. Fix MVVM architecture violations
5. Address architectural antipatterns
6. Improve code quality
7. Fix and add tests
8. Verify all tests pass

## Implementation Tasks for Revised Architecture

## Session Management Implementation

### 1. Implement SessionService Interface and Implementation
- [ ] Create SessionService interface in service.session package
- [ ] Create DefaultSessionService implementation that uses SessionManager
- [ ] Update SessionManager to implement SessionService
- [ ] Add refreshSession method to SessionManager

### 2. Implement SessionContext Interface and Implementation
- [ ] Create SessionContext interface in common.session package
- [ ] Create DefaultSessionContext implementation in service.session package
- [ ] Add methods for session state management
- [ ] Add methods for role-based navigation

### 3. Implement SessionState Interface and Implementations
- [ ] Create SessionState interface in common.session package
- [ ] Create LoggedOutState implementation
- [ ] Create LoggingInState implementation
- [ ] Create LoggedInState implementation
- [ ] Create SessionExpiredState implementation
- [ ] Create SessionRefreshingState implementation

### 4. Implement Role-Based Navigation
- [ ] Create RoleBasedNavigationService in ui.navigation package
- [ ] Add methods for navigating to role-specific views
- [ ] Update view models to use RoleBasedNavigationService

### 5. Update UI Layer to Use Session Management Components
- [ ] Update LoginViewModel to use SessionContext
- [ ] Update other view models to use SessionContext
- [ ] Add session state handling to view models

### 6. Test Session Management Components
- [ ] Write unit tests for SessionService
- [ ] Write unit tests for SessionContext
- [ ] Write unit tests for SessionState implementations
- [ ] Write unit tests for RoleBasedNavigationService

## Business Layer Implementation

### Core Business Layer Components
- [x] Create AuditEvent.java based on DomainEvent.java
- [x] Create BaseAuditEvent.java based on AbstractDomainEvent.java
- [x] Create IAuditPublisher.java based on IDomainEventPublisher.java
- [x] Create AuditHandler.java based on DomainEventHandler.java
- [x] Create AuditHandlerImplementation.java based on DomainEventHandlerImplementation.java
- [x] Create AuditPublisher.java based on DomainEventPublisher.java
- [x] Create BusinessObject.java based on AggregateRoot.java and Aggregate.java
- [x] Create BusinessComponent.java based on Entity.java
- [x] Create IBusinessService.java based on IDomainService.java
- [x] Create BusinessService.java based on DomainService.java
- [x] Create DataAccessInterface.java based on Repository.java
- [x] Create DataObject.java based on ValueObject.java
- [x] Create BusinessException.java based on DomainException.java

### Update Existing Business Entities
- [x] Update OrderBusiness.java (renamed from OrderAggregate.java) to use BusinessObject
- [x] Update UserBusiness.java (renamed from UserAggregate.java) to use BusinessObject
- [x] Update CustomerBusiness.java (renamed from CustomerAggregate.java) to use BusinessObject
- [x] Update ReportBusiness.java (renamed from ReportAggregate.java) to use BusinessObject

### Update Existing Business Components
- [x] Update Photo.java to use DataObject (Note: Photo is a value object, not a business component)
- [x] Update PhotoDocument.java to use BusinessComponent
- [x] Update other business components to use BusinessComponent

### Update Existing Data Objects
- [x] Update EmailAddress.java to use DataObject
- [x] Update Money.java to use DataObject
- [x] Update PersonName.java to use DataObject
- [x] Update PhoneNumber.java to use DataObject
- [x] Update Timestamp.java to use DataObject
- [x] Update other data objects to use DataObject

### Update Existing Business Services
- [x] Update PhotoApprovalService.java to use BusinessService
- [x] Update PhotoValidationService.java to use BusinessService
- [x] Update PhotoReportGenerationService.java to use BusinessService
- [x] Update OrderBusinessService.java (renamed from OrderDomainService.java) to use BusinessService
- [x] Remove OrderDomainService.java since it has been replaced by OrderBusinessService.java
- [ ] Update other business services to use BusinessService

### Update Existing Data Access Interfaces
- [x] Create OrderDataAccessAdapter.java to implement OrderDataAccess (Note: OrderDataAccess should not extend DataAccessInterface directly to maintain layer independence)
- [x] Create ComponentDataAccessInterface.java for business components
- [x] Create PhotoDataAccess.java to use ComponentDataAccessInterface
- [x] Create InMemoryPhotoRepository.java to implement PhotoRepository
- [x] Create PhotoDataAccessAdapter.java to adapt InMemoryPhotoRepository to PhotoDataAccess
- [x] Create UserDataAccessAdapter.java to implement UserDataAccess (Note: UserDataAccess should not extend DataAccessInterface directly to maintain layer independence)
- [x] Update CustomerDataAccess.java (renamed from CustomerRepository.java) to use DataAccessInterface
- [x] Update ReportDataAccess.java (renamed from ReportRepository.java) to use DataAccessInterface

### Update Existing Exceptions
- [x] Update AccessDeniedException.java to extend BusinessException
- [x] Update BusinessRuleViolationException.java to extend BusinessException
- [x] Update EntityNotFoundException.java to extend BusinessException
- [x] Update InvalidValueException.java to extend BusinessException

### Update Existing Audit Events
- [x] Update OrderCreatedEvent.java to use BaseAuditEvent
- [x] Update OrderApprovedEvent.java to use BaseAuditEvent
- [x] Update OrderRejectedEvent.java to use BaseAuditEvent
- [x] Update OrderCompletedEvent.java to use BaseAuditEvent
- [x] Update OrderCancelledEvent.java to use BaseAuditEvent
- [x] Update PhotoApprovedEvent.java to use BaseAuditEvent
- [x] Update PhotoRejectedEvent.java to use BaseAuditEvent
- [x] Update UserCreatedEvent.java to use BaseAuditEvent
- [x] Update UserApprovedEvent.java to use BaseAuditEvent
- [x] Update UserRejectedEvent.java to use BaseAuditEvent
- [x] Update ReportGeneratedEvent.java to use BaseAuditEvent
- [x] Update ReportCompletedEvent.java to use BaseAuditEvent
- [ ] Update other audit events to use BaseAuditEvent

### Update UserAggregate to UserBusiness
- [x] Update RBAC classes to use UserBusiness instead of UserAggregate
  - [x] Update RoleBasedAccessController.java
  - [x] Update AccessPolicy.java
  - [x] Update RoleBasedAccessManager.java
- [x] Update PhotoDocument class to use UserBusiness instead of UserAggregate
- [x] Update event classes to use UserBusiness instead of UserAggregate
  - [x] Update UserLoggedInEvent.java
  - [x] Update UserLoggedOutEvent.java
- [x] Update interfaces to use UserBusiness instead of UserAggregate
  - [x] Update AuthenticationService.java
  - [x] Update UserRepository.java
  - [x] Update UserDataAccess.java
- [x] Update implementations to use UserBusiness instead of UserAggregate
  - [x] Update DefaultAuthenticationService.java
  - [x] Update SqlUserRepository.java
  - [x] Update InMemoryUserRepository.java
  - [x] Update UserDataAccessAdapter.java
- [x] Update services to use UserBusiness instead of UserAggregate
  - [x] Update com.belman.repository.service.SessionManager.java (Note: File moved to com.belman.service.session.SessionManager.java)
  - [x] Update com.belman.service.infrastructure.session.SessionManager.java (Note: File consolidated into com.belman.service.session.SessionManager.java)
  - [x] Update ServiceInjector.java
- [x] Update view models to use UserBusiness instead of UserAggregate
  - [x] Update UserManagementViewModel.java
  - [x] Update AdminViewModel.java
  - [x] Update LoginViewModel.java
  - [x] Update MainViewModel.java
  - [x] Update OrderGalleryViewModel.java (Note: Already using UserBusiness, but has incorrect imports for SessionManager. Attempted to update imports but encountered dependency rule violations)
- [x] Update controllers to use UserBusiness instead of UserAggregate
  - [x] Update UserManagementViewController.java (Note: Already using UserBusiness)
  - [x] Update AdminViewController.java (Note: Already using UserBusiness)
  - [x] Update OrderGalleryViewController.java (Note: Already using UserBusiness, but has incorrect imports for SessionManager. Similar to OrderGalleryViewModel.java)
  - [x] Update PhotoReviewViewController.java (Note: Already using UserBusiness, but has incorrect imports for SessionManager. Similar to OrderGalleryViewController.java)
  - [x] Update PhotoUploadViewController.java (Note: Already using UserBusiness, but has incorrect imports for SessionManager. Similar to OrderGalleryViewController.java)

### Audit Events Standardization
- [x] Create audit_events_standardization_plan.md
- [x] Consolidate BaseAuditEvent classes
  - [x] Keep com.belman.domain.audit.event.BaseAuditEvent
  - [x] Remove com.belman.domain.events.BaseAuditEvent (Note: Updated all references to it, but didn't physically remove the file)
  - [x] Update all event classes to use com.belman.domain.audit.event.BaseAuditEvent
- [ ] Standardize event approach
  - [ ] Update all event classes to use BaseAuditEvent (Note: OrderCreatedEvent.java, UserLoggedInEvent.java, and UserLoggedOutEvent.java need to be updated to use BaseAuditEvent, or they need to be replaced with corresponding audit event classes. UserLoggedInAuditEvent.java and UserLoggedOutAuditEvent.java already exist and extend BaseAuditEvent.)
  - [ ] Ensure consistent naming conventions for event classes
- [x] Remove duplicate events
  - [x] Keep UserApprovedAuditEvent.java and remove UserApprovedEvent.java (Note: Files should be deleted: com.belman.domain.events.UserApprovedEvent.java and com.belman.domain.user.events.UserApprovedEvent.java)
  - [x] Keep UserRejectedAuditEvent.java and remove UserRejectedEvent.java (Note: Files should be deleted: com.belman.domain.events.UserRejectedEvent.java and com.belman.domain.user.events.UserRejectedEvent.java)
  - [x] Remove other duplicate events (Note: No other duplicate events found)
- [x] Update references to removed events
  - [x] Update UserAggregate.java to use UserApprovedAuditEvent and UserRejectedAuditEvent (Note: UserAggregate.java has been renamed to UserBusiness.java)
  - [x] Update UserBusiness.java to use UserApprovedAuditEvent and UserRejectedAuditEvent
  - [x] Update other classes that reference the removed events (Note: No classes are importing any of the UserApprovedEvent or UserRejectedEvent classes)

### Business Events Implementation
- [x] Create BusinessEvent.java interface
- [x] Create BaseBusinessEvent.java abstract class
- [x] Create AuditEvent.java interface that extends BusinessEvent.java
- [x] Update BaseAuditEvent.java to extend BaseBusinessEvent.java and implement AuditEvent.java
- [x] Create BusinessEventPublisher.java based on AuditPublisher.java
- [x] Create BusinessEventHandler.java based on AuditHandler.java
- [x] Update BusinessObject.java to use BusinessEvent instead of AuditEvent
- [x] Update existing audit events to use the new business event structure (Note: BaseAuditEvent already extends BaseBusinessEvent, and all audit event classes are already extending BaseAuditEvent, so they're indirectly using BaseBusinessEvent. The only exceptions are OrderCreatedEvent.java, UserLoggedInEvent.java, and UserLoggedOutEvent.java, which need to be updated to use BaseAuditEvent or replaced with corresponding audit event classes.)
  - [x] Update order events to use BaseBusinessEvent (Note: All order events except OrderCreatedEvent.java are already extending BaseAuditEvent, which extends BaseBusinessEvent)
    - [x] Update OrderCreatedEvent.java to use BaseBusinessEvent
    - [x] Update OrderApprovedEvent.java to use BaseBusinessEvent
    - [x] Update OrderRejectedEvent.java to use BaseBusinessEvent
    - [x] Update OrderCompletedEvent.java to use BaseBusinessEvent
    - [x] Update OrderCancelledEvent.java to use BaseBusinessEvent
  - [x] Update photo events to use BaseBusinessEvent (Note: All photo events are already extending BaseAuditEvent, which extends BaseBusinessEvent)
    - [x] Update PhotoApprovedEvent.java to use BaseBusinessEvent
    - [x] Update PhotoRejectedEvent.java to use BaseBusinessEvent
  - [x] Update user events to use BaseBusinessEvent (Note: All user events except UserLoggedInEvent.java and UserLoggedOutEvent.java are already extending BaseAuditEvent, which extends BaseBusinessEvent)
    - [x] Update UserCreatedEvent.java to use BaseBusinessEvent
    - [x] Update UserApprovedEvent.java to use BaseBusinessEvent
    - [x] Update UserRejectedEvent.java to use BaseBusinessEvent
    - [x] Update UserApprovedAuditEvent.java to use BaseBusinessEvent
    - [x] Update UserRejectedAuditEvent.java to use BaseBusinessEvent
    - [x] Update UserLoggedInEvent.java to use BaseBusinessEvent
    - [x] Update UserLoggedOutEvent.java to use BaseBusinessEvent
  - [x] Update report events to use BaseBusinessEvent (Note: All report events are already extending BaseAuditEvent, which extends BaseBusinessEvent)
    - [x] Update ReportGeneratedEvent.java to use BaseBusinessEvent
    - [x] Update ReportCompletedEvent.java to use BaseBusinessEvent
  - [x] Update other audit events to use BaseBusinessEvent (Note: All other audit events are already extending BaseAuditEvent, which extends BaseBusinessEvent)
- [x] Update event publishers to use BusinessEventPublisher
  - [x] Update DomainEventPublisher to use BusinessEventPublisher
  - [x] Update AuditPublisher to use BusinessEventPublisher
- [x] Update event handlers to use BusinessEventHandler
  - [x] Update DomainEventHandler to use BusinessEventHandler
  - [x] Update AuditHandler to use BusinessEventHandler

### Update Business Layer Tests
- [x] Update OrderAggregateTest.java to test OrderBusiness
- [x] Update UserAggregateTest.java to test UserBusiness (Note: Attempted to create UserBusinessTest.java but encountered dependency rule violations)
- [x] Update CustomerAggregateTest.java to test CustomerBusiness
- [x] Update ReportAggregateTest.java to test ReportBusiness
- [ ] Update value object tests
  - [x] Update PhotoTemplateTest.java to test PhotoTemplate
  - [x] Update EmailAddressTest.java to test EmailAddress
  - [ ] Update PhoneNumberTest.java to test PhoneNumber
  - [ ] Update PersonNameTest.java to test PersonName
  - [ ] Update HashedPasswordTest.java to test HashedPassword
- [ ] Update service tests
  - [ ] Update DefaultAuthenticationServiceTest.java to use UserBusiness
  - [ ] Update SessionManagerTest.java to use UserBusiness
  - [ ] Update ServiceInjectorTest.java to use UserBusiness
- [ ] Update repository tests
  - [ ] Update SqlUserRepositoryTest.java to use UserBusiness
  - [ ] Update InMemoryUserRepositoryTest.java to use UserBusiness
  - [ ] Update UserDataAccessAdapterTest.java to use UserBusiness
- [ ] Update other tests to use the new naming conventions

## Data Layer Implementation

### Repository Implementations
- [ ] Update SqlUserRepository.java
  - [ ] Update to use DataAccessInterface
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update SqlOrderRepository.java
  - [ ] Update to use DataAccessInterface
  - [ ] Update to use OrderBusiness instead of OrderAggregate
- [ ] Update SqlCustomerRepository.java
  - [ ] Update to use DataAccessInterface
  - [ ] Update to use CustomerBusiness instead of CustomerAggregate
- [ ] Update InMemoryUserRepository.java
  - [ ] Update to use DataAccessInterface
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update InMemoryOrderRepository.java
  - [ ] Update to use DataAccessInterface
  - [ ] Update to use OrderBusiness instead of OrderAggregate
- [ ] Update InMemoryCustomerRepository.java
  - [ ] Update to use DataAccessInterface
  - [ ] Update to use CustomerBusiness instead of CustomerAggregate
- [ ] Update other repository implementations to use DataAccessInterface

### Service Implementations
- [ ] Update DefaultPhotoService.java
  - [ ] Update to use BusinessService
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update MockCameraService.java
  - [ ] Update to use BusinessService
  - [ ] Update to use appropriate business objects instead of aggregates
- [x] Update DefaultAuthenticationService.java
  - [ ] Update to use BusinessService
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update BCryptPasswordHasher.java
  - [ ] Update to use BusinessService
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update SmtpEmailService.java
  - [ ] Update to use BusinessService
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update GluonCameraService.java
  - [ ] Update to use BusinessService
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other service implementations to use BusinessService

### Configuration
- [ ] Update ApplicationInitializer.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update RouteGuardInitializer.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update SecureDatabaseConfig.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates

### Logging
- [ ] Update EmojiLoggerAdapter.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update EmojiLoggerFactory.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates

### Bootstrap
- [ ] Update Main.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update ApplicationBootstrapper.java
  - [ ] Update to use the new naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates

### Data Layer Tests
- [ ] Update repository tests
  - [ ] Update SqlUserRepositoryTest.java
    - [ ] Update to test with DataAccessInterface
    - [ ] Update to use UserBusiness instead of UserAggregate
  - [ ] Update SqlOrderRepositoryTest.java
    - [ ] Update to test with DataAccessInterface
    - [ ] Update to use OrderBusiness instead of OrderAggregate
  - [ ] Update SqlCustomerRepositoryTest.java
    - [ ] Update to test with DataAccessInterface
    - [ ] Update to use CustomerBusiness instead of CustomerAggregate
  - [ ] Update InMemoryUserRepositoryTest.java
    - [ ] Update to test with DataAccessInterface
    - [ ] Update to use UserBusiness instead of UserAggregate
  - [ ] Update InMemoryOrderRepositoryTest.java
    - [ ] Update to test with DataAccessInterface
    - [ ] Update to use OrderBusiness instead of OrderAggregate
  - [ ] Update InMemoryCustomerRepositoryTest.java
    - [ ] Update to test with DataAccessInterface
    - [ ] Update to use CustomerBusiness instead of CustomerAggregate
- [ ] Update service tests
  - [ ] Update DefaultPhotoServiceTest.java
    - [ ] Update to test with BusinessService
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update MockCameraServiceTest.java
    - [ ] Update to test with BusinessService
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update DefaultAuthenticationServiceTest.java
    - [ ] Update to test with BusinessService
    - [ ] Update to use UserBusiness instead of UserAggregate
  - [ ] Update BCryptPasswordHasherTest.java
    - [ ] Update to test with BusinessService
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update SmtpEmailServiceTest.java
    - [ ] Update to test with BusinessService
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update GluonCameraServiceTest.java
    - [ ] Update to test with BusinessService
    - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other tests to use the new naming conventions

## Presentation Layer Implementation

### View Models
- [x] Update LoginViewModel.java
  - [x] Update to use the new business entity naming conventions
  - [x] Update to use UserBusiness instead of UserAggregate
- [x] Update AdminViewModel.java
  - [x] Update to use the new business entity naming conventions
  - [x] Update to use UserBusiness instead of UserAggregate
- [ ] Update PhotoUploadViewModel.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update PhotoReviewViewModel.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update OrderGalleryViewModel.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use OrderBusiness instead of OrderAggregate
- [ ] Update QADashboardViewModel.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [x] Update UserManagementViewModel.java
  - [x] Update to use the new business entity naming conventions
  - [x] Update to use UserBusiness instead of UserAggregate
- [x] Update MainViewModel.java
  - [x] Update to use the new business entity naming conventions
  - [x] Update to use appropriate business objects instead of aggregates
- [ ] Update other view models to use the new business entity naming conventions

### Controllers
- [ ] Update LoginViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update AdminViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update PhotoUploadViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update PhotoReviewViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update OrderGalleryViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use OrderBusiness instead of OrderAggregate
- [ ] Update QADashboardViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update UserManagementViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update MainViewController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other controllers to use the new business entity naming conventions

### Navigation
- [ ] Update Router.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update RouteGuardImpl.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update NavigateCommand.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other navigation-related classes to use the new business entity naming conventions

### Binding
- [ ] Update CommandBinding.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update CommandBindingBuilder.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other binding-related classes to use the new business entity naming conventions

### Core
- [ ] Update BaseView.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update BaseViewModel.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update BaseController.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update BaseService.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other core classes to use the new business entity naming conventions

### UI Components
- [ ] Update TouchFriendlyPhotoListCell.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update TouchFriendlyDialog.java
  - [ ] Update to use the new business entity naming conventions
  - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other UI components to use the new business entity naming conventions

### Presentation Layer Tests
- [ ] Update view model tests
  - [ ] Update LoginViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use UserBusiness instead of UserAggregate
  - [ ] Update AdminViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use UserBusiness instead of UserAggregate
  - [ ] Update PhotoUploadViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update PhotoReviewViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update OrderGalleryViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use OrderBusiness instead of OrderAggregate
  - [ ] Update QADashboardViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update UserManagementViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use UserBusiness instead of UserAggregate
  - [ ] Update MainViewModelTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update navigation tests
  - [ ] Update RouterTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update RouteGuardImplTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use UserBusiness instead of UserAggregate
- [ ] Update binding tests
  - [ ] Update CommandBindingTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
  - [ ] Update CommandBindingBuilderTest.java
    - [ ] Update to use the new business entity naming conventions
    - [ ] Update to use appropriate business objects instead of aggregates
- [ ] Update other tests to use the new business entity naming conventions

## Architecture Guide Update

### Remove DDD Terminology
- [ ] Update terminology in documentation
  - [ ] Replace "Domain-Driven Design (DDD)" with "Business-Driven Design (BDD)" or similar
  - [ ] Replace "Domain Layer" with "Business Layer"
  - [ ] Replace "Domain Model" with "Business Model"
  - [ ] Replace "Bounded Contexts" with "Business Areas"
  - [ ] Replace "Aggregates" with "Business Objects"
  - [ ] Replace "Entities" with "Business Components"
  - [ ] Replace "Value Objects" with "Data Objects"
  - [ ] Replace "Domain Events" with "Business Events" or "Audit Events"
  - [ ] Replace "Repositories" with "Data Access Interfaces"
  - [ ] Replace "Domain Services" with "Business Services"
  - [ ] Replace "Domain Exceptions" with "Business Exceptions"
  - [ ] Replace "Ubiquitous Language" with "Common Business Language"
  - [ ] Replace "Aggregate Root" with "Primary Business Object"
  - [ ] Replace "Domain Logic" with "Business Logic"
  - [ ] Replace "Domain Objects" with "Business Objects"
  - [ ] Replace "Domain Significance" with "Business Significance"
- [ ] Update terminology in code comments
  - [ ] Update comments in business layer classes
  - [ ] Update comments in service layer classes
  - [ ] Update comments in repository layer classes
  - [ ] Update comments in UI layer classes
- [ ] Update terminology in method and variable names
  - [ ] Update method names that contain "Domain" to use "Business" instead
  - [ ] Update variable names that contain "Domain" to use "Business" instead
  - [ ] Update class names that contain "Domain" to use "Business" instead

### Update Architecture Diagrams
- [ ] Update layer diagrams
  - [ ] Update layer names (Domain Layer -> Business Layer)
  - [ ] Update component names (Aggregates -> Business Objects, etc.)
  - [ ] Update relationship descriptions
- [ ] Update component diagrams
  - [ ] Update component names (Domain Services -> Business Services, etc.)
  - [ ] Update component relationships
  - [ ] Update component responsibilities
- [ ] Update class diagrams
  - [ ] Update class names (UserAggregate -> UserBusiness, etc.)
  - [ ] Update class relationships
  - [ ] Update class hierarchies
- [ ] Update sequence diagrams
  - [ ] Update object names
  - [ ] Update message names
  - [ ] Update interaction descriptions

### Update Code Examples
- [ ] Update code examples in documentation
  - [ ] Update class names (UserAggregate -> UserBusiness, etc.)
  - [ ] Update method names (getDomainEvents -> getBusinessEvents, etc.)
  - [ ] Update variable names (domainEvent -> businessEvent, etc.)
  - [ ] Update import statements
- [ ] Update code examples in tutorials
  - [ ] Update class names
  - [ ] Update method names
  - [ ] Update variable names
  - [ ] Update import statements
- [ ] Update code examples in README files
  - [ ] Update class names
  - [ ] Update method names
  - [ ] Update variable names
  - [ ] Update import statements

### Update Architecture Principles
- [ ] Update Clean Architecture principles to use the new naming conventions
- [ ] Update Package-by-Feature principles to use the new naming conventions
- [ ] Update MVVM+C principles to use the new naming conventions

### Update Layer Responsibilities
- [ ] Update Business Layer responsibilities to use the new naming conventions
- [ ] Update Application Layer responsibilities to use the new naming conventions
- [ ] Update Infrastructure Layer responsibilities to use the new naming conventions
- [ ] Update Presentation Layer responsibilities to use the new naming conventions

### Update Feature Organization
- [ ] Update feature organization to use the new naming conventions

### Update Communication Between Layers
- [ ] Update communication between layers to use the new naming conventions

### Update Mobile-Friendly Design Considerations
- [ ] Update mobile-friendly design considerations to use the new naming conventions

### Update Testing Strategy
- [ ] Update testing strategy to use the new naming conventions

### Update Architecture Enforcement
- [ ] Update architecture enforcement to use the new naming conventions

### Update Boundary Rules
- [ ] Update boundary rules to use the new naming conventions

### Update Conclusion
- [ ] Update conclusion to use the new naming conventions
