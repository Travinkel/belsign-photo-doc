    # Package: `com.belman.domain`
    
    ## 1. Purpose
    
    * The domain package contains the core business logic and entities of the application.
    * It implements the domain model using Domain-Driven Design principles.
    * It represents the heart of the application, capturing the business concepts, rules, and workflows.
    
    ## 2. Key Classes and Interfaces
    
    * `ValueObject` - Marker interface for immutable objects defined by their attributes rather than identity.
    * `Entity` - Base class for objects with distinct identity that persists through state changes.
    * `BusinessObject` - Abstract base class for primary business entities with lifecycle tracking.
    * `Repository` - Interface defining collection-like access to domain objects.
    * `UserBusiness` - Entity representing a user in the system with roles and approval workflow.
    * `OrderBusiness` - Entity representing a customer order with associated photos and status.
    * `PhotoDocument` - Entity representing a photo with metadata, approval status, and review information.
    * `ReportBusiness` - Entity representing a generated report with associated photos and metadata.
    
    ## 3. Architectural Role
    
    * This package is the domain layer in the clean architecture.
    * It contains the core business logic independent of any infrastructure or UI concerns.
    * It defines interfaces that are implemented by the infrastructure layer (repositories, services).
    * It encapsulates business rules and invariants within rich domain objects.
    
    ## 4. Requirements Coverage
    
    * Implements user management with role-based access control and approval workflows.
    * Supports order management with status tracking and photo association.
    * Provides photo documentation with templates, annotations, and approval processes.
    * Enables report generation with different formats and types.
    * Enforces business rules and validation at the domain level.
    * Maintains data integrity through rich domain objects with encapsulated behavior.
    
    ## 5. Usage and Flow
    
    * The domain layer is used by the application layer to execute business operations.
    * Domain objects encapsulate business rules and enforce invariants.
    * Repositories provide an abstraction for data access, with interfaces defined in the domain layer.
    * Typical flow:
      1. Application layer receives a request and retrieves domain objects through repositories
      2. Domain objects execute business logic and enforce rules
      3. Changes to domain objects are persisted through repositories
      4. Domain events may be raised to trigger side effects
    * Value objects are used for descriptive aspects of the domain (e.g., EmailAddress, PersonName).
    * Entities represent objects with identity and lifecycle (e.g., UserBusiness, OrderBusiness).
    
    ## 6. Patterns and Design Decisions
    
    * **Domain-Driven Design**: Rich domain model with entities, value objects, and repositories.
    * **Value Object Pattern**: Immutable objects defined by their attributes (EmailAddress, PersonName).
    * **Entity Pattern**: Objects with identity that persists through state changes (UserBusiness, OrderBusiness).
    * **Repository Pattern**: Collection-like interfaces for data access defined in the domain layer.
    * **Factory Pattern**: Factory methods and classes for creating complex domain objects.
    * **State Pattern**: ApprovalState hierarchy for managing user approval workflows.
    * **Builder Pattern**: Used for constructing complex domain objects with many attributes.
    
    ## 7. Unnecessary Complexity
    
    * Some domain objects have too many responsibilities and could be split into smaller, more focused classes.
    * The approval workflow state pattern adds complexity that might be overkill for simple approval processes.
    * Some value objects could be simplified to use Java's built-in types for simpler cases.
    * The inheritance hierarchy (Entity -> BusinessObject -> concrete classes) adds complexity that might not be necessary.
    * Some domain logic is duplicated across different domain objects.
    
    ## 8. Refactoring Opportunities
    
    * Extract complex business rules into domain services to reduce the size of domain entities.
    * Simplify the approval workflow state pattern for cases where the full state machine isn't needed.
    * Use Java records for simpler value objects to reduce boilerplate code.
    * Consider using a more lightweight approach for entity identity and equality.
    * Add more comprehensive validation for domain objects to catch errors earlier.
    * Introduce domain events for better decoupling between domain objects.
    * Improve documentation of business rules and invariants within the domain model.