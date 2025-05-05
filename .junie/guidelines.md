# Project Guidelines for Junie

## Project Structure
The BelSign Photo Documentation Module follows a clean architecture approach with the following structure:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── belman/
│   │           ├── application/    # Application services and use cases
│   │           ├── backbone/       # Core framework components
│   │           ├── domain/         # Domain entities, value objects, and business logic
│   │           ├── framework/      # Framework-specific code
│   │           ├── infrastructure/ # External integrations and implementations
│   │           ├── presentation/   # UI components
│   │           └── Main.java       # Application entry point
│   └── resources/                  # FXML, CSS, images, and other resources
└── test/
    ├── java/
    │   └── com/
    │       └── belman/
    │           ├── acceptance/     # Acceptance tests
    │           ├── functional/     # Functional/end-to-end tests
    │           ├── integration/    # Integration tests
    │           ├── smoke/          # Smoke tests
    │           └── unit/           # Unit tests
    └── resources/                  # Test resources
```

## Testing Guidelines
When making changes to the codebase, Junie should:

1. **Run relevant tests**: Run tests related to the modified components to ensure changes don't break existing functionality.
   - Use `run_test` command with the appropriate test path
   - Example: `run_test src\test\java\com\belman\unit\domain\SomeTest.java`

2. **Test types to consider**:
   - Unit tests: For isolated component testing
   - Integration tests: For testing component interactions
   - Functional tests: For end-to-end testing
   - Smoke tests: For basic functionality verification

3. **Test coverage**: Ensure that any new code has appropriate test coverage.

## Build Instructions
Before submitting changes, Junie should:

1. **Build the project**: Use the `build` command to ensure the project compiles successfully.

2. **Run the application**: If UI changes are made, consider running the application to verify the changes work as expected.
   - The application can be run using: `mvn javafx:run`
   - The main class is: `com.belman.infrastructure.bootstrap.Main`

## Code Style Guidelines
When making changes, Junie should follow these code style guidelines:

1. **Clean Architecture**: Maintain the separation of concerns between layers.
   - Domain layer should not depend on outer layers
   - Use interfaces for dependencies that cross layer boundaries

2. **Naming Conventions**:
   - Classes: PascalCase (e.g., `OrderRepository`)
   - Methods/Variables: camelCase (e.g., `findById`, `orderNumber`)
   - Constants: UPPER_SNAKE_CASE (e.g., `MAX_UPLOAD_SIZE`)

3. **Documentation**:
   - Add JavaDoc comments for public APIs
   - Document complex business logic
   - Update existing documentation if behavior changes

4. **Error Handling**:
   - Use appropriate exception types
   - Validate inputs early
   - Provide clear error messages

5. **Testing**:
   - Write tests for new functionality
   - Update tests when changing existing functionality
   - Follow the existing test structure and naming conventions

## Mobile Compatibility
The project uses Gluon Mobile for cross-platform development. When making UI changes, consider:

1. **Responsive Design**: Ensure layouts adapt to different screen sizes
2. **Touch-Friendly UI**: Design for touch interaction rather than mouse and keyboard
3. **Platform Detection**: Use platform detection for platform-specific behavior when necessary

## Additional Notes
- The project uses Java 21 and JavaFX 21
- Gluon Mobile is used for cross-platform mobile development
- PDFBox is used for PDF generation
- JUnit and Mockito are used for testing

# Project Overview: BelSign Photo Documentation Module

## Introduction

BelSign is a quality control system developed for Belman A/S, a Danish company specializing in the design and
manufacture of expansion joints and flexible pipe solutions. This module specifically handles photo documentation tied
to orders, with user role control, report generation, and email dispatch capabilities.

## Company Background

Belman A/S was founded in 1994 and is headquartered in Esbjerg, Denmark. The company provides custom-engineered
solutions to accommodate thermal movements, vibrations, and pressure loads in piping systems for various industries,
including:

- Energy
- Offshore
- Chemical
- Power generation

## Problem Statement

Before shipping items from production, Belman needs to document joints, weldings, and other components through photo
documentation. The current process involves:

1. Taking photos with a camera
2. Uploading them to a server in one large folder

This approach makes it difficult to:

- Find specific images upon customer requests
- Access documentation in case of quality issues
- Associate images with specific orders
- Generate quality control reports efficiently
- Time-consuming searches for specific images.
- Inefficient handling of quality documentation.
- Limited association between images and specific orders.

## Solution: BelSign Photo Documentation Module

The BelSign Photo Documentation Module addresses these challenges by providing a structured system for managing photo
documentation throughout the production and quality control process.

### Key Features

- **Order-Based Photo Management**: Attach images to specific order numbers and save them to a database
- **QC Report Generation**: Automatically generate quality control reports with approved photos
- **Customer Communication**: Send emails with QC documentation directly to customers
- **User-Friendly Interface**: Designed for non-technical production workers
- **Tablet Compatibility**: Optimized for use on tablets in production environments
- **Role-Based Access Control**: Support for multiple user roles:
    - Production Worker: Upload and manage photos
    - Quality Assurance: Review and approve photos, generate reports
    - Admin: Manage user accounts and system settings

### Benefits

- **Improved Traceability**: All photos are linked to specific orders
- **Enhanced Quality Control**: Structured approval process for documentation
- **Better Customer Service**: Quick access to documentation when needed
- **Increased Efficiency**: Streamlined workflow for photo documentation
- **Reduced Errors**: Systematic approach to documentation management

## Target Users

The system is designed for three primary user roles:

1. **Production Workers**
    - Take and upload photos
    - Associate photos with order numbers
    - Review uploaded photos

2. **Quality Assurance Personnel**
    - Review photos uploaded by production workers
    - Approve or reject photos
    - Add comments to documentation
    - Generate QC reports

3. **Administrators**
    - Manage user accounts
    - Assign roles to users
    - Configure system settings

## Project Goals

- Create a user-friendly, tablet-optimized interface
- Ensure high reliability for critical industries (naval, nuclear, offshore)
- Provide complete traceability and auditability
- Build for future scalability (API integration, multi-user support)
- Here’s exactly where and how to add these extra details into `01_project-overview.md` clearly:

**Development Methodology**

- The project strictly follows Scrum methodology, including:
    - Regular Sprint Planning, Sprint Reviews, and Sprint Retrospectives.
    - Detailed Sprint Backlog management using ScrumWise.
    - Active participation from the Product Owner and teaching staff during sprint reviews, focusing on functionality
      and usability.

**Technical Implementation Requirements**

- Implemented as a Java desktop application, delivered as an IntelliJ project.
- JavaFX used for GUI development.
- Persistent storage handled by the school's MSSQL database.
- Mandatory demonstration of at least one automated JUnit test covering a core class.

**Project Documentation**

- Comprehensive documentation required, clearly detailing:
    - Project planning and execution.
    - Architectural decisions and rationale.
    - Implementation details, including code examples across all layers.
    - Testing strategy and outcomes.
    - Installation guides and credentials provided for examiners.

**Examination and Evaluation**

- Final assessment includes:
    - A group presentation (10 minutes) demonstrating overall system functionality.
    - An individual oral examination (30 minutes) focusing on the student's specific contributions and understanding of
      curriculum-related topics.

# Architecture

## Overview
The BelSign Photo Documentation Module follows a clean, layered architecture based on Domain-Driven Design (DDD) principles. This architecture ensures separation of concerns, maintainability, and testability while providing a solid foundation for future extensions.

## Architectural Principles
- **Clean Architecture**: Dependency flows inward, with domain at the center
- **Domain-Driven Design**: Rich domain model with entities, value objects, and aggregates
- **Separation of Concerns**: Clear boundaries between layers
- **Dependency Inversion**: High-level modules don't depend on low-level modules
- **Single Responsibility**: Each component has one reason to change
- **Interface Segregation**: Clients should not be forced to depend on interfaces they do not use
- **Open/Closed Principle**: Software entities should be open for extension but closed for modification
- **Test-Driven Development**: Tests are written before code to ensure quality and correctness
- **Event-Driven Architecture**: Components communicate through events, promoting loose coupling
- **Asynchronous Processing**: Long-running tasks are handled asynchronously to keep the UI responsive
- **Modular Design**: Components are organized into modules for better maintainability and scalability
- **Microservices**: The architecture can be extended to support microservices for scalability and deployment flexibility
- **Cross-Platform Compatibility**: The architecture supports both desktop and mobile platforms, ensuring a consistent user experience across devices
- **Responsive Design**: The UI adapts to different screen sizes and orientations, providing a seamless experience on various devices
- **Accessibility**: The application is designed to be accessible to users with disabilities, following best practices for UI design
- **Security**: The architecture incorporates security measures to protect sensitive data and ensure secure communication between components
- **Performance Optimization**: The architecture is designed to optimize performance, ensuring fast response times and efficient resource usage

## Interface-Based Design

The architecture employs interface-based design to achieve high modularity, loose coupling, and ease of testing. By defining clear interfaces for services, repositories, and external integrations, the system ensures:

- **Decoupling:** Components rely only on interfaces rather than concrete implementations.
- **Flexibility:** Easy substitution or extension of implementations without modifying client code.
- **Testability:** Simplified mocking and testing through dependency injection of interfaces.

### Practical Usage in Layers:

- **Domain Layer:** Repository interfaces define contracts without exposing infrastructure details.
- **Application Layer:** Service interfaces abstract orchestration and business logic from specific implementations.
- **Infrastructure Layer:** Concrete implementations of repositories and services fulfill domain-defined interfaces.

**Example:**
```java
// Domain layer interface
public interface OrderRepository {
    Optional<Order> findById(OrderId id);
    void save(Order order);
}

// Infrastructure layer implementation
public class SqlOrderRepository implements OrderRepository {
    @Override
    public Optional<Order> findById(OrderId id) {
        // implementation details
    }

    @Override
    public void save(Order order) {
        // implementation details
    }
}
```

## Layers
The application is organized into the following layers:

### 1. Presentation Layer
- **Purpose**: User interface and user interaction
- **Components**: Views, ViewModels, Controllers
- **Technologies**: JavaFX, FXML, Backbone framework
- **Responsibilities**:
    - Display information to users
    - Capture user input
    - Validate user input
    - Delegate business logic to application services

### 2. Application Layer
- **Purpose**: Orchestration of domain objects and services
- **Components**: Application Services, DTOs, ViewModels
- **Responsibilities**:
    - Coordinate domain objects to perform use cases
    - Transaction management
    - Security and authorization
    - Mapping between domain objects and DTOs

### 3. Domain Layer
- **Purpose**: Business logic and rules
- **Components**: Entities, Value Objects, Aggregates, Domain Services, Repositories (interfaces)
- **Responsibilities**:
    - Implement business rules
    - Define domain model
    - Express business concepts and relationships
    - Define repository interfaces

### 4. Infrastructure Layer
- **Purpose**: Technical capabilities and external systems integration
- **Components**: Repository Implementations, External Services, Persistence, File I/O
- **Responsibilities**:
    - Implement repository interfaces
    - Provide technical services (email, file storage)
    - Integrate with external systems
    - Handle persistence concerns

## Key Components

### Domain Model
The domain model is at the heart of the application and includes:

- **Entities**: Objects with identity (e.g., Order, User, PhotoDocument)
- **Value Objects**: Immutable objects without identity (e.g., PhotoAngle, EmailAddress)
- **Aggregates**: Clusters of entities and value objects (e.g., Order with PhotoDocuments)
- **Domain Services**: Operations that don't belong to a specific entity (e.g., ReportBuilderService)
- **Repositories**: Interfaces for data access (e.g., OrderRepository, UserRepository)

### Backbone Framework
The application uses the Backbone framework, a Gluon Mobile-friendly, opinionated JavaFX micro-framework that follows Clean Architecture principles:

- **Base Classes**: BaseView, BaseController, BaseViewModel
- **Dependency Injection**: ServiceLocator, Inject annotation
- **Event Handling**: DomainEvent, DomainEventPublisher
- **State Management**: StateStore, Property
- **Navigation**: Router for SPA-style navigation
- **Lifecycle Management**: ViewLifecycle, onShow(), onHide()

## Design Patterns

### MVVM (Model-View-ViewModel-Controller)
- **Model**: Domain entities and services
- **View**: FXML files and view classes
- **ViewModel**: Mediates between View and Model, handles UI logic
- **Controller**: Manages user interactions and delegates to ViewModel

### Supporting Concepts
- **Data Binding**: JavaFX properties for reactive UI updates
- **Lifecycle Hooks**: onShow() and onHide() methods for view lifecycle management
- **Dependency Injection**: ServiceLocator for managing dependencies
- **Event Handling**: DomainEventPublisher for event-driven architecture
- **Navigation**: Router for SPA-style navigation

### Supporting Infrastructure
- **State Management**: StateStore for global state management
- **Asynchronous Operations**: JavaFX Task for background processing
- **Error Handling**: Centralized error handling through the ErrorHandler class
- **Logging**: SLF4J for logging and monitoring
- **Testing**: JUnit and Mockito for unit and integration testing
- **Documentation**: JavaDoc and Markdown for API documentation

### Repository Pattern
- Abstracts data access logic
- Provides collection-like interface for domain objects
- Decouples domain from persistence mechanisms

### Specification Pattern
- Encapsulates query criteria (e.g., PendingOrdersSpecification)
- Enables composable, reusable query logic
- Keeps domain logic in the domain layer

### Service Locator
- Centralized registry for services
- Enables dependency injection
- Simplifies testing through service substitution

### Observer Pattern
- Used for event handling
- Enables loose coupling between components
- Implemented through the DomainEventPublisher

## Communication Flow
1. User interacts with the View
2. View delegates to Controller
3. Controller updates ViewModel
4. ViewModel executes Application Service methods
5. Application Service orchestrates Domain objects
6. Domain objects implement business logic
7. Repositories persist changes
8. Results flow back up through the layers

## Threading Model
- UI components run on the JavaFX Application Thread
- Long-running operations run on background threads
- Services use JavaFX Task for asynchronous operations
- Results are published back to the UI thread using Platform.runLater()

## Mobile Compatibility
The architecture supports both desktop and mobile platforms:
- Platform-specific styling
- Responsive layouts
- Touch-friendly UI components
- Offline capabilities through local storage


Frameworks and Libraries
Core Technologies
JavaFX 21
The project uses Java 21 for maximum compatibility with Gluon Mobile and GraalVM Native Image builds.

Modern UI components
CSS styling
FXML for declarative UI definition
Property binding for reactive UI updates
Animation framework
Maven
Maven is used for project management and build automation:

Dependency management
Build lifecycle
Resource filtering
Test execution
Packaging and deployment
Custom Frameworks
Backbone Framework
Backbone is a Gluon Mobile-friendly, opinionated JavaFX micro-framework created in-house. It follows Clean Architecture and MVVM principles and is optimized for both desktop and mobile deployment.

Key Features
Convention-over-configuration wiring between View, Controller, and ViewModel
SPA-style navigation using a centralized Router
Declarative lifecycle support (onShow(), onHide())
Domain event publishing with DomainEventPublisher
State sharing with StateStore
Dependency injection using a service locator (constructor preferred)
Goals
Remove boilerplate in FXML/ViewModel setup
Ensure consistent architecture across views
Support both desktop JavaFX and Gluon Mobile targets (using Attach services when needed)
Mobile Development
Gluon Mobile
Gluon Mobile enables cross-platform mobile development with JavaFX:

Components
Glisten: UI toolkit for mobile applications
Attach: Access to native mobile features
Connect: Cloud services integration
Maps: Mapping and location services
CloudLink: Backend connectivity
Benefits
Single codebase for desktop and mobile
Native look and feel on each platform
Access to device features (camera, storage, etc.)
Optimized for touch interfaces
External Libraries
Database Access
JDBC: Java Database Connectivity for SQL database access
HikariCP: High-performance JDBC connection pool
PDF Generation
Apache PDFBox: Library for creating and manipulating PDF documents
iText: Advanced PDF generation and manipulation
Email
JavaMail API: Sending and receiving email
Jakarta Mail: Modern implementation of the JavaMail API
Utilities
Apache Commons IO: File and stream utilities
Apache Commons Lang: String manipulation, reflection, and other utilities
SLF4J: Simple Logging Facade for Java
Logback: Logging implementation
Testing
JUnit 5: Testing framework
Mockito: Mocking framework for unit tests
TestFX: Testing framework for JavaFX applications
Awaitility: DSL for asynchronous testing
Dependency Management
Maven Dependencies
The project's dependencies are managed in the pom.xml file:

<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21.0.1</version>
    </dependency>

    <!-- Gluon Mobile -->
    <dependency>
        <groupId>com.gluonhq</groupId>
        <artifactId>charm-glisten</artifactId>
        <version>6.2.3</version>
    </dependency>
    <dependency>
        <groupId>com.gluonhq.attach</groupId>
        <artifactId>storage</artifactId>
        <version>4.0.18</version>
    </dependency>
    <dependency>
        <groupId>com.gluonhq.attach</groupId>
        <artifactId>util</artifactId>
        <version>4.0.18</version>
    </dependency>

    <!-- PDF Generation -->
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>2.0.29</version>
    </dependency>

    <!-- Email -->
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>jakarta.mail</artifactId>
        <version>2.0.1</version>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.6.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
Version Compatibility
Component	Version	Compatibility Notes
Java	21	Required for Gluon Substrate and GraalVM compatibility
JavaFX	21.0.1	Compatible with Java 23
Gluon Mobile	6.2.3	Compatible with JavaFX 21
Maven	3.8+	Required for build process
Apache PDFBox	2.0.29	Latest stable version
Jakarta Mail	2.0.1	Compatible with Java 23
Future Considerations
Modularization: Moving towards Java Platform Module System (JPMS)
GraalVM Native Image: Exploring native compilation for improved startup time
Jakarta EE Integration: For potential server-side components
Reactive Extensions: Considering RxJava for more complex async operations


# Design Guidelines

## Code Style and Conventions

### Naming Conventions

#### Packages
- Use lowercase letters
- Use reverse domain name notation (e.g., `com.belman.domain.model`)
- Use singular nouns for packages containing classes of the same type (e.g., `model`, not `models`)
- Examples:
    - `com.belman.domain.model`
    - `com.belman.domain.service`
    - `com.belman.infrastructure.repository`

#### Classes
- Use PascalCase (UpperCamelCase)
- Use nouns or noun phrases
- Be descriptive and avoid abbreviations
- Examples:
    - `OrderRepository`
    - `PhotoDocumentService`
    - `EmailAddress`

#### Interfaces
- Use PascalCase
- Do not use "I" prefix
- Examples:
    - `Repository` (not `IRepository`)
    - `EmailService` (not `IEmailService`)

#### Methods
- Use camelCase (lowerCamelCase)
- Use verbs or verb phrases
- Be descriptive about what the method does
- Examples:
    - `findById`
    - `uploadPhoto`
    - `generateReport`

#### Variables
- Use camelCase
- Use meaningful names that describe the purpose
- Avoid single-letter names except for loop counters
- Examples:
    - `orderNumber`
    - `customerEmail`
    - `isApproved`

#### Constants
- Use UPPER_SNAKE_CASE
- Examples:
    - `MAX_UPLOAD_SIZE`
    - `DEFAULT_TIMEOUT_SECONDS`

#### Null Handling
- Never return `null` from methods — use `Optional<T>` instead.
- Validate method parameters early and fail fast.
- Use `Objects.requireNonNull()` when a value is required.

#### FXML Files and IDs
- FXML files: Use PascalCase with "View" suffix (e.g., `LoginView.fxml`)
- FXML IDs: Use camelCase (e.g., `loginButton`, `usernameField`)

### Code Organization

#### Class Structure
- Order of elements in a class:
    1. Static fields
    2. Instance fields
    3. Constructors
    4. Public methods
    5. Protected methods
    6. Private methods
    7. Inner classes/interfaces

#### Method Length
- Keep methods short and focused on a single responsibility
- Aim for methods under 30 lines
- Extract helper methods for complex operations

#### Comments
- Use JavaDoc for public APIs
- Comment complex algorithms or business rules
- Avoid obvious comments that repeat what the code already says
- Use TODO comments for temporary solutions or future improvements

## Domain-Driven Design Guidelines

### Value Objects
- Should be immutable
- Should validate their state in the constructor
- Should override equals() and hashCode()
- Should have a meaningful toString() method
- Examples:
  ```java
  public final class EmailAddress {
      private final String value;

      public EmailAddress(String email) {
          if (email == null || !isValidEmail(email)) {
              throw new IllegalArgumentException("Invalid email address: " + email);
          }
          this.value = email;
      }

      public String getValue() {
          return value;
      }

      private boolean isValidEmail(String email) {
          // Email validation logic
          return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
      }

      @Override
      public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          EmailAddress that = (EmailAddress) o;
          return value.equalsIgnoreCase(that.value);
      }

      @Override
      public int hashCode() {
          return value.toLowerCase().hashCode();
      }

      @Override
      public String toString() {
          return value;
      }
  }
  ```

### Entities
- Should have a clear identity concept
- Should encapsulate business rules
- Should validate state changes
- Example:
  ```java
  public class PhotoDocument {
      private final PhotoId id;
      private OrderId orderId;
      private ImagePath imagePath;
      private PhotoAngle angle;
      private ApprovalStatus approvalStatus;
      private String comment;
      private Timestamp createdAt;

      // Constructor and methods

      public void approve(User approver) {
          if (!approver.hasRole(Role.QA)) {
              throw new UnauthorizedOperationException("Only QA can approve photos");
          }
          this.approvalStatus = ApprovalStatus.APPROVED;
      }

      public void reject(User rejector, String reason) {
          if (!rejector.hasRole(Role.QA)) {
              throw new UnauthorizedOperationException("Only QA can reject photos");
          }
          if (reason == null || reason.trim().isEmpty()) {
              throw new IllegalArgumentException("Rejection reason is required");
          }
          this.approvalStatus = ApprovalStatus.REJECTED;
          this.comment = reason;
      }
  }
  ```

### Aggregates
- Identify clear aggregate roots
- Keep aggregates small
- Reference other aggregates by identity
- Example:
  ```java
  public class Order {
      private final OrderId id;
      private final CustomerId customerId;
      private final OrderNumber orderNumber;
      private final Set<PhotoDocument> photoDocuments;
      private OrderStatus status;

      // Constructor and methods

      public void addPhotoDocument(PhotoDocument document) {
          // Business rules for adding photos
          photoDocuments.add(document);
      }

      public boolean canGenerateReport() {
          // Business rule: need at least one approved photo
          return photoDocuments.stream()
              .anyMatch(doc -> doc.getApprovalStatus() == ApprovalStatus.APPROVED);
      }
  }
  ```

### Repositories
- Use interfaces in the domain layer
- Implement in the infrastructure layer
- Return domain objects, not DTOs
- Example:
  ```java
  // Domain layer
  public interface OrderRepository {
      Optional<Order> findById(OrderId id);
      Optional<Order> findByOrderNumber(OrderNumber orderNumber);
      List<Order> findBySpecification(Specification<Order> spec);
      void save(Order order);
  }

  // Infrastructure layer
  public class SqlOrderRepository implements OrderRepository {
      // Implementation
  }
  ```

### Services
- Use domain services for operations that don't belong to entities
- Keep services focused on a single responsibility
- Example:
  ```java
  public class ReportGenerationService {
      private final OrderRepository orderRepository;
      private final PDFGenerator pdfGenerator;

      // Constructor

      public Report generateQCReport(OrderId orderId) {
          Order order = orderRepository.findById(orderId)
              .orElseThrow(() -> new EntityNotFoundException("Order not found"));

          if (!order.canGenerateReport()) {
              throw new BusinessRuleViolationException("Cannot generate report without approved photos");
          }

          // Generate report logic
      }
  }
  ```

## UI Design Guidelines

### JavaFX/FXML Best Practices
- Separate UI structure (FXML) from behavior (Controller)
- Use CSS for styling
- Use binding for reactive UI updates
- Keep controllers thin, delegate to ViewModels

### MVVM Pattern
- Model: Domain entities and business logic
- View: FXML and minimal controller code
- ViewModel: UI state and operations

### Responsive Design
- Use layout containers that resize appropriately (VBox, HBox, GridPane)
- Use percentage-based sizing where appropriate
- Test on different screen sizes

### Mobile-Friendly Design
- Use touch-friendly controls (larger buttons, etc.)
- Consider gesture support
- Ensure adequate spacing between interactive elements

## Error Handling Guidelines

### Exception Types
- Use checked exceptions for recoverable errors
- Use unchecked exceptions for programming errors
- Create custom exceptions for domain-specific errors

### Exception Handling Strategy
- Handle exceptions at the appropriate level
- Log exceptions with context information
- Provide user-friendly error messages
- Don't catch exceptions you can't handle properly

### Validation
- Validate input at the domain level
- Use defensive programming
- Fail fast and provide clear error messages

#### Immutability
- Prefer immutability for value objects and DTOs.
- Use `final` fields and avoid setters.
- Ensure constructors fully initialize objects into a valid state.

### Interface-Based Design
- Define interfaces in the domain or application layer for:
    - Repositories
    - Services
    - External integrations
- Implement these interfaces in the infrastructure layer.
- Inject interfaces via constructor to improve testability and decoupling.

```java
// Domain Layer
public interface QCReportService {
    void generateAndSendReport(OrderId orderId);
}

// Application Layer
public class DefaultQCReportService implements QCReportService {
    private final PDFGenerator generator;
    private final MailService mailService;

    public DefaultQCReportService(PDFGenerator generator, MailService mailService) {
        this.generator = generator;
        this.mailService = mailService;
    }

    public void generateAndSendReport(OrderId orderId) {
        // Orchestration logic
    }
}
# Testing Guidelines

## Testing Philosophy

The BelSign project follows a comprehensive testing approach to ensure code quality, reliability, and maintainability. Our testing philosophy is based on the following principles:

- **Test-Driven Development (TDD)**: Write tests before implementing features
- **Continuous Testing**: Run tests frequently during development
- **Comprehensive Coverage**: Test all layers of the application
- **Isolation**: Tests should be independent and not affect each other
- **Readability**: Tests should be easy to understand and maintain

## Test Structure

### Test Organization

Tests are organized in the `src/test/java` directory, mirroring the structure of the main source code:

```
src/test/java/
└── com/
└── belman/
├── unit/             # Unit tests
│   ├── domain/       # Domain layer tests
│   ├── application/  # Application layer tests
│   └── presentation/ # Presentation layer tests
├── integration/      # Integration tests
└── functional/       # End-to-end functional tests
```

### Naming Conventions

- Test classes should be named after the class they test, with a `Test` suffix
  - Example: `OrderTest` for testing the `Order` class
- Test methods should follow the pattern: `[methodName]_[scenario]_[expectedResult]`
  - Example: `approve_withQARole_shouldChangeStatusToApproved`
- For parameterized tests, use descriptive names for the test cases

## Test Types

### Unit Tests

Unit tests focus on testing individual components in isolation:

- **Domain Layer**: Test entities, value objects, and domain services
- **Application Layer**: Test application services and use cases
- **Presentation Layer**: Test view models and controllers

#### Example Unit Test

```java
package com.belman.unit.domain.model;

import com.belman.domain.model.EmailAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailAddressTest {

    @Test
    void constructor_withValidEmail_shouldCreateInstance() {
        EmailAddress email = new EmailAddress("test@example.com");
        assertEquals("test@example.com", email.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "missing@", "@nodomain", "spaces in@email.com"})
    void constructor_withInvalidEmail_shouldThrowException(String invalidEmail) {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress(invalidEmail));
    }

    @Test
    void equals_withSameEmail_shouldReturnTrue() {
        EmailAddress email1 = new EmailAddress("test@example.com");
        EmailAddress email2 = new EmailAddress("test@example.com");
        assertEquals(email1, email2);
    }

    @Test
    void equals_withDifferentEmail_shouldReturnFalse() {
        EmailAddress email1 = new EmailAddress("test1@example.com");
        EmailAddress email2 = new EmailAddress("test2@example.com");
        assertNotEquals(email1, email2);
    }
}
```

### Integration Tests

Integration tests verify that different components work together correctly:

- **Repository Tests**: Test repository implementations with a test database
- **Service Integration**: Test services with their dependencies
- **API Integration**: Test API endpoints with the full application stack

#### Example Integration Test

```java
package com.belman.integration.infrastructure.repository;

import com.belman.domain.model.Order;
import com.belman.domain.model.OrderId;
import com.belman.domain.model.OrderNumber;
import com.belman.domain.repository.OrderRepository;
import com.belman.infrastructure.repository.SqlOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SqlOrderRepositoryTest {

    private OrderRepository orderRepository;
    private TestDatabaseSetup dbSetup;

    @BeforeEach
    void setUp() {
        dbSetup = new TestDatabaseSetup();
        dbSetup.setupTestDatabase();
        orderRepository = new SqlOrderRepository(dbSetup.getDataSource());
    }

    @Test
    void findByOrderNumber_withExistingOrder_shouldReturnOrder() {
        // Given
        OrderNumber orderNumber = new OrderNumber("ORD-12345");

        // When
        Optional<Order> result = orderRepository.findByOrderNumber(orderNumber);

        // Then
        assertTrue(result.isPresent());
        assertEquals(orderNumber, result.get().getOrderNumber());
    }

    @Test
    void findByOrderNumber_withNonExistingOrder_shouldReturnEmpty() {
        // Given
        OrderNumber orderNumber = new OrderNumber("NON-EXISTENT");

        // When
        Optional<Order> result = orderRepository.findByOrderNumber(orderNumber);

        // Then
        assertTrue(result.isEmpty());
    }
}
```

### UI Tests

UI tests verify that the user interface works correctly:

- **View Tests**: Test FXML views and their controllers
- **ViewModel Tests**: Test view model behavior
- **Navigation Tests**: Test navigation between views

#### Example UI Test

```java
package com.belman.unit.presentation.view.login;

import com.belman.presentation.view.login.LoginView;
import com.belman.presentation.view.login.LoginViewModel;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextInputControlMatchers.hasText;

class LoginViewTest extends ApplicationTest {

    private Parent root;
    private LoginViewModel viewModel;

    @Override
    public void start(javafx.stage.Stage stage) {
        LoginView loginView = new LoginView();
        root = loginView.getView();
        viewModel = loginView.getViewModel();
        stage.setScene(new javafx.scene.Scene(root));
        stage.show();
    }

    @Test
    void loginButton_whenClicked_shouldCallLoginMethod() {
        // Given
        TextField usernameField = lookup("#usernameField").query();
        PasswordField passwordField = lookup("#passwordField").query();
        Button loginButton = lookup("#loginButton").query();

        // When
        clickOn(usernameField).write("testuser");
        clickOn(passwordField).write("password");
        clickOn(loginButton);

        // Then
        verifyThat(usernameField, hasText("testuser"));
        assertEquals("testuser", viewModel.usernameProperty().get());
        assertEquals("password", viewModel.passwordProperty().get());
        assertTrue(viewModel.isLoginAttempted());
    }
}
```

### Gluon Mobile Compatibility (Manual Testing Note)

Because JavaFX mobile support via Gluon involves native builds and device-specific behavior, automated UI tests cannot cover mobile environments fully.

Instead:

- Use **manual testing** on Android devices for:
    - Camera access (photo capture)
    - Touch UI responsiveness
    - File system access
- Use **integration smoke tests** with mocked Gluon services (e.g., mocked camera or storage APIs)
- Avoid relying on `TestFX` for Gluon: it does not support mobile rendering or device interactions


## Testing Tools and Frameworks

### JUnit 5

JUnit 5 is the primary testing framework used in the project:

- **@Test**: Mark methods as test cases
- **@ParameterizedTest**: Test with multiple inputs
- **@BeforeEach/@AfterEach**: Setup and teardown for each test
- **@BeforeAll/@AfterAll**: Setup and teardown once for all tests
- **Assertions**: Verify expected outcomes

### Mockito

Mockito is used for mocking dependencies in unit tests:

```java
@Test
void generateReport_withApprovedPhotos_shouldCreateReport() {
    // Given
    OrderId orderId = new OrderId("123");
    Order order = mock(Order.class);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(order.canGenerateReport()).thenReturn(true);

    // When
    reportService.generateReport(orderId);

    // Then
    verify(pdfGenerator).generatePDF(any());
    verify(reportRepository).save(any(Report.class));
}
```

### TestFX

TestFX is used for testing JavaFX UI components:

- Simulate user interactions (click, type, etc.)
- Query UI elements
- Verify UI state

## Test Data Management

### Test Fixtures

Use test fixtures to set up test data:

```java
class OrderTestFixture {
    public static Order createSampleOrder() {
        OrderId id = new OrderId("123");
        CustomerId customerId = new CustomerId("456");
        OrderNumber orderNumber = new OrderNumber("ORD-789");
        return new Order(id, customerId, orderNumber);
    }

    public static PhotoDocument createApprovedPhoto() {
        PhotoId id = new PhotoId("photo-1");
        OrderId orderId = new OrderId("123");
        ImagePath path = new ImagePath("/path/to/image.jpg");
        return new PhotoDocument(id, orderId, path, ApprovalStatus.APPROVED);
    }
}
```

### In-Memory Repositories

Use in-memory repositories for testing:

```java
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> orders = new HashMap<>();

    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(orders.get(id));
    }

    @Override
    public void save(Order order) {
        orders.put(order.getId(), order);
    }

    // Other methods
}
```

### Mocking Gluon Attach Services

When writing application/service tests involving mobile features (e.g., file storage), mock the Gluon Attach interfaces:

```java
StorageService storage = mock(StorageService.class);
when(storage.saveFile(any(), any())).thenReturn(Path.of("mock/path/to/file.jpg"));
```



## Test Coverage

Aim for high test coverage, especially in the domain and application layers:

- **Domain Layer**: 90%+ coverage
- **Application Layer**: 80%+ coverage
- **Infrastructure Layer**: 70%+ coverage
- **Presentation Layer**: 60%+ coverage

Use JaCoCo for measuring test coverage.

## Test-Driven Development (TDD)

Follow the TDD cycle:

1. **Red**: Write a failing test
2. **Green**: Write the minimum code to make the test pass
3. **Refactor**: Improve the code while keeping tests passing

### TDD Example

```java
// Step 1: Write a failing test
@Test
void approve_withQARole_shouldChangeStatusToApproved() {
    // Given
    User qaUser = new User(new UserId("123"), Role.QA);
    PhotoDocument photo = new PhotoDocument(
        new PhotoId("456"),
        new OrderId("789"),
        new ImagePath("/path/to/image.jpg"),
        ApprovalStatus.PENDING
    );

    // When
    photo.approve(qaUser);

    // Then
    assertEquals(ApprovalStatus.APPROVED, photo.getApprovalStatus());
}

// Step 2: Implement the minimum code to make it pass
public void approve(User approver) {
    if (approver.getRole() == Role.QA) {
        this.approvalStatus = ApprovalStatus.APPROVED;
    } else {
        throw new UnauthorizedOperationException("Only QA can approve photos");
    }
}

// Step 3: Refactor if needed
public void approve(User approver) {
    if (!approver.hasRole(Role.QA)) {
        throw new UnauthorizedOperationException("Only QA can approve photos");
    }
    this.approvalStatus = ApprovalStatus.APPROVED;
}
```

## Continuous Integration

Tests are automatically run in the CI pipeline:

- All tests must pass before merging to develop or main
- Test coverage reports are generated
- Test results are published to the team


## Gluon Mobile Native Builds

Gluon Substrate builds native images for Android using GraalVM.

To manually test builds, run:

```bash
mvn gluonfx:build gluonfx:package
```

## Best Practices

1. **Write Tests First**: Follow TDD principles
2. **Keep Tests Simple**: One assertion per test when possible
3. **Use Descriptive Names**: Make test names clear and descriptive
4. **Test Edge Cases**: Include tests for boundary conditions and error cases
5. **Avoid Test Interdependence**: Tests should not depend on each other
6. **Clean Up Resources**: Close connections, files, etc. in teardown
7. **Mock External Dependencies**: Use mocks for external services
8. **Test Public API**: Focus on testing the public interface
9. **Refactor Tests**: Keep tests clean and maintainable
10. **Run Tests Frequently**: Don't wait for CI to run tests


# Build and Run Instructions

## Prerequisites

Before building and running the BelSign Photo Documentation Module, ensure you have the following prerequisites installed:

### Required Software
- **Java Development Kit (JDK) 21**
- **Maven 3.8** or higher
- **Git** for version control

### Optional Software
- **IntelliJ IDEA** or **Eclipse** IDE (recommended for development)
- **Scene Builder** for FXML editing
- **Docker** for containerized deployment (optional)
- **GraalVM** for native image generation (optional)
- **Gluon Mobile** for mobile builds (optional)

### Mobile Requirements (Optional)
To build and run the app on mobile devices, ensure:

- **GraalVM Gluon Edition** is installed and configured
- **Gluon Substrate CLI** is available (optional for manual native image control)
- **WSL2 (Windows only)** for cross-compilation via Linux


## Getting the Source Code

Clone the repository using Git:

```bash
git clone https://github.com/belman/belsign.git
cd belsign
```

## Building the Project

### Using Maven Command Line

1. Navigate to the project root directory
2. Run the Maven build command:

```bash
mvn clean install
```

This command will:
- Clean the target directory
- Compile the source code
- Run the tests
- Package the application

### Using an IDE

#### IntelliJ IDEA
1. Open IntelliJ IDEA
2. Select "Open" or "Import Project"
3. Navigate to the project directory and select the `pom.xml` file
4. Choose "Open as Project"
5. Wait for the IDE to import and index the project
6. Build the project using the Build menu or by pressing Ctrl+F9 (Windows/Linux) or Cmd+F9 (macOS)

#### Eclipse
1. Open Eclipse
2. Select "File" > "Import"
3. Choose "Maven" > "Existing Maven Projects"
4. Navigate to the project directory and select the `pom.xml` file
5. Click "Finish"
6. Wait for Eclipse to import and build the project
7. Right-click on the project and select "Run As" > "Maven build..."
8. Enter "clean install" in the Goals field and click "Run"

## Running the Application

### Desktop Mode

To run the application in desktop mode:

```bash
mvn javafx:run
```

Or using the full class path:

```bash
mvn exec:java -Dexec.mainClass="com.belman.infrastructure.bootstrap.Main"
```

### Development Mode

For development with hot reloading:

```bash
mvn javafx:run -Djavafx.run.options="--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED"
```

### Running Specific Profiles

The application supports different profiles for various environments:

#### Development Profile
```bash
mvn javafx:run -Pdev
```

#### Testing Profile
```bash
mvn javafx:run -Ptest
```

#### Production Profile
```bash
mvn javafx:run -Pprod
```

## Mobile Builds

### Prerequisites for Mobile Development
- **Gluon Mobile** license (for commercial use)
- **Android SDK** (for Android builds)
- **Xcode** and **iOS SDK** (for iOS builds, macOS only)

### Building for Android

1. Ensure Android SDK is properly configured in your environment
2. Run the Gluon build command:

```bash
mvn gluonfx:build -Pandroid
```

3. The APK file will be generated in the `target/gluonfx/aarch64-android/gvm` directory

### Building for iOS (macOS only)

1. Ensure Xcode and iOS SDK are properly installed
2. Run the Gluon build command:

```bash
mvn gluonfx:build -Pios
```

3. The iOS app will be generated in the `target/gluonfx/aarch64-ios/gvm` directory

## Configuration

### Application Properties

The application uses property files for configuration:

- `application.properties`: Default configuration
- `application-dev.properties`: Development configuration
- `application-test.properties`: Testing configuration
- `application-prod.properties`: Production configuration

These files are located in the `src/main/resources` directory.

### Database Configuration

To configure the database connection:

1. Open the appropriate properties file
2. Modify the database properties:

```properties
# Database Configuration
db.url=jdbc:sqlserver://localhost:1433;databaseName=BelSign
db.username=sa
db.password=YourPassword
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### Logging Configuration

Logging is configured in the `logback.xml` file in the `src/main/resources` directory:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/belsign.log</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

### Gluon Mobile Configuration

When targeting Android/iOS, ensure the correct profiles and attach modules are configured in `pom.xml`. Gluon requires additional flags for GraalVM native image compilation.


## Troubleshooting

### Common Build Issues

#### Maven Dependencies Not Found
If Maven cannot download dependencies, check your internet connection and Maven settings in `~/.m2/settings.xml`. You may need to configure a proxy or mirror.

#### JavaFX Not Found
Ensure that JavaFX modules are included in your dependencies. Check the `pom.xml` file for JavaFX dependencies.

#### Compilation Errors
- Ensure you're using JDK 21 (GraalVM Gluon edition for mobile builds)
- Check that all required dependencies are available
- Verify that your source code is compatible with Java 23

### Runtime Issues

#### Application Won't Start
- Check the logs for error messages
- Verify that all required resources are available
- Ensure the database is running and accessible (if applicable)

#### UI Rendering Issues
- Ensure JavaFX is properly configured
- Check for CSS errors in the logs
- Verify that FXML files are correctly formatted

#### Database Connection Issues
- Verify database credentials
- Check that the database server is running
- Ensure firewall rules allow the connection

## Deployment

### JAR Packaging

To create an executable JAR file:

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

### Native Packaging

To create a native executable:

```bash
mvn javafx:jlink
```

This will create a custom runtime image in the `target/app` directory.

### Docker Deployment

A Dockerfile is provided for containerized deployment:

```bash
# Build the Docker image
docker build -t belsign .

# Run the container
docker run -p 8080:8080 belsign
```

## Continuous Integration
> Note: Native mobile builds (`mvn gluonfx:build`) are not executed in CI by default. These are done manually on supported OS environments (Linux/macOS).
The project uses GitHub Actions for continuous integration. The workflow is defined in `.github/workflows/ci.yml`.

To run the CI pipeline locally:

```bash
# Install act (https://github.com/nektos/act)
act -j build
```

## Performance Tuning

### JVM Options

For better performance, consider the following JVM options:

```bash
mvn javafx:run -Djavafx.run.options="--add-modules=javafx.controls,javafx.fxml -Xms512m -Xmx1g"
```

### Database Connection Pool

Adjust the connection pool settings in the properties file:

```properties
# Connection Pool Configuration
db.pool.initialSize=5
db.pool.maxSize=20
db.pool.minIdle=5
db.pool.maxIdle=10
```

## Additional Resources

- [JavaFX Documentation](https://openjfx.io/javadoc/21/)
- [Gluon Mobile Documentation](https://docs.gluonhq.com/)
- [Maven Documentation](https://maven.apache.org/guides/index.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

# Mobile Compatibility Guidelines

## Overview

The BelSign Photo Documentation Module is designed to work seamlessly across desktop and mobile platforms using Gluon Mobile. This document provides guidelines for ensuring mobile compatibility throughout the development process.

## Mobile-First Design Principles

### Responsive Layouts

- Design layouts that adapt to different screen sizes and orientations
- Use relative sizing (percentages) instead of fixed pixel values
- Implement responsive containers (VBox, HBox, GridPane) with appropriate constraints
- Test layouts on both small (phone) and medium (tablet) screen sizes

### Touch-Friendly UI

- Design for touch interaction rather than mouse and keyboard
- Make touch targets (buttons, controls) at least 44x44 pixels
- Provide adequate spacing between interactive elements (minimum 8px)
- Implement swipe gestures for common actions (navigation, refresh)
- Avoid hover-dependent interactions

### Performance Considerations

- Optimize image loading and caching for mobile devices
- Minimize network requests and payload sizes
- Implement lazy loading for lists and grids
- Use background threads for heavy operations
- Monitor memory usage and avoid memory leaks

## Gluon Mobile Integration

### Charm Glisten Components

Use Gluon's Charm Glisten UI components for a native mobile look and feel:

- `MobileApplication`: Base application class
- `View`: Mobile-optimized view container
- `NavigationDrawer`: Side menu for navigation
- `BottomNavigation`: Tab-based navigation
- `AppBar`: Application toolbar
- `Dialog`: Mobile-friendly dialogs
- `ProgressIndicator`: Loading indicators
- `FloatingActionButton`: Primary action button

Example:

```
public class MainView extends View {

    public MainView() {
        super("Main");

        // Set up AppBar
        AppBar appBar = getAppBar();
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
            MobileApplication.getInstance().getDrawer().open()));
        appBar.setTitleText("BelSign");
        appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button());

        // Set up content
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));

        // Add responsive components
        setCenter(content);
    }
}
```

### Platform Detection

Use platform detection to apply platform-specific styling and behavior:

```
if (PlatformUtils.isAndroid()) {
    // Android-specific code
} else if (PlatformUtils.isIOS()) {
    // iOS-specific code
} else if (PlatformUtils.isDesktop()) {
    // Desktop-specific code
}
```

### Attach Services

Utilize Gluon Attach services to access native device features:

- `StorageService`: Access device storage
- `PicturesService`: Access device camera and photo gallery
- `PositionService`: Access device location
- `BrowserService`: Open web links
- `ShareService`: Share content with other apps
- `LifecycleService`: Handle application lifecycle events

Example:

```
// Take a photo using the device camera
Services.get(PicturesService.class).ifPresent(service -> {
    service.takePhoto().ifPresent(image -> {
        // Process the captured image
        imageView.setImage(image);
        uploadPhotoTask.setImage(image);
    });
});
```

## Platform-Specific Considerations

### Android

- Support various screen densities (ldpi, mdpi, hdpi, xhdpi, xxhdpi)
- Handle Android back button properly
- Implement proper permission requests
- Consider Android navigation patterns (drawer, bottom navigation)
- Test on multiple Android versions (API level 24+)

### iOS

- Follow iOS Human Interface Guidelines
- Implement proper status bar integration
- Handle safe areas (notches, home indicator)
- Consider iOS navigation patterns (tab bar, navigation bar)
- Test on multiple iOS versions (iOS 13+)

### Permissions

Implement proper permission handling for device features:

```
Services.get(StorageService.class).ifPresent(service -> {
    service.requestPermission().thenAccept(response -> {
        if (response == PermissionStatus.GRANTED) {
            // Permission granted, proceed with storage operations
        } else {
            // Handle permission denied
            showPermissionDeniedDialog();
        }
    });
});
```

## Offline Capabilities

### Local Storage

Implement local storage for offline operation:

- Use Gluon's `StorageService` for file storage
- Implement a local database (SQLite) for structured data
- Cache frequently accessed data
- Implement data synchronization when online

### Sync Strategy

Develop a robust synchronization strategy:

- Queue operations when offline
- Sync when connection is restored
- Handle conflict resolution
- Provide sync status indicators

## Testing on Mobile Devices

### Emulators and Simulators

- Test on Android emulators with various screen sizes and API levels
- Test on iOS simulators with various device types and iOS versions
- Use Android Studio and Xcode for platform-specific debugging

### Physical Devices

- Test on real Android and iOS devices
- Test on both phones and tablets
- Test with various network conditions (WiFi, cellular, offline)
- Test battery consumption during extended use

### Automated Testing

- Use TestFX for UI testing
- Implement platform-specific test cases
- Set up CI/CD pipeline for mobile testing
- Use Firebase Test Lab or similar services for device farm testing

## Mobile-Specific Features

### Camera Integration

Implement camera integration for photo documentation:

```
private void capturePhoto() {
    Services.get(PicturesService.class).ifPresent(service -> {
        service.takePhoto().ifPresent(image -> {
            // Process the captured image
            photoDocumentService.addPhotoToCurrentOrder(image);
        });
    });
}
```

### Barcode Scanning

Implement barcode scanning for quick order lookup:

```
private void scanBarcode() {
    Services.get(BarcodeService.class).ifPresent(service -> {
        service.scanBarcode().thenAccept(barcode -> {
            if (barcode != null) {
                // Look up order by barcode
                orderService.findByBarcode(barcode);
            }
        });
    });
}
```

### Push Notifications

Implement push notifications for important events:

- Order status changes
- Photo approval/rejection
- Report generation completion
- New assignments

## Performance Optimization

### Image Handling

Optimize image handling for mobile devices:

- Resize images before upload
- Use efficient image formats (WebP, HEIF)
- Implement progressive loading
- Cache images locally

Example:

```
private Image resizeImageForUpload(Image original) {
    int maxDimension = 1200; // Max dimension for upload

    double width = original.getWidth();
    double height = original.getHeight();

    double scale = Math.min(maxDimension / width, maxDimension / height);

    if (scale < 1) {
        // Resize needed
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        WritableImage resized = new WritableImage(newWidth, newHeight);
        PixelWriter pixelWriter = resized.getPixelWriter();

        // Implement resizing logic

        return resized;
    }

    return original; // No resize needed
}
```

### Network Optimization

Optimize network usage:

- Implement request batching
- Use compression for API requests/responses
- Implement efficient caching strategies
- Monitor and optimize payload sizes

## Accessibility

Ensure the application is accessible on mobile devices:

- Support screen readers (TalkBack on Android, VoiceOver on iOS)
- Implement proper content descriptions
- Ensure adequate color contrast
- Support text scaling
- Test with accessibility tools

## Deployment

### App Store Guidelines

Follow platform-specific guidelines for app store submission:

- Comply with Apple App Store Review Guidelines
- Comply with Google Play Store Policies
- Prepare appropriate app store assets (icons, screenshots)
- Write compelling app descriptions

### CI/CD for Mobile

Set up continuous integration and deployment for mobile builds:

- Automate build process for Android and iOS
- Implement automated testing on mobile platforms
- Set up deployment to app stores
- Use beta testing platforms (TestFlight, Google Play Beta)

## Resources

- [Gluon Mobile Documentation](https://docs.gluonhq.com/)
- [JavaFX Mobile Documentation](https://openjfx.io/openjfx-docs/)
- [Android Developer Guidelines](https://developer.android.com/design)
- [iOS Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
- [Material Design Guidelines](https://material.io/design)
