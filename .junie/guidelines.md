# Belsign Photo Documentation Project - Development Guidelines

## Build/Configuration Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.8 or higher
- MSSQL Server database
- GraalVM (for Android builds)

### Building the Project
The project uses Maven for build management. Here are the key commands:

```bash
# Build the project
mvn clean install

# Run the application
mvn javafx:run

# Package as executable JAR
mvn package
```

### Configuration
1. **Database Configuration**: The application connects to a MSSQL database. Configure connection settings in the appropriate configuration files.

2. **Android Build Configuration**:
   - Set the `GRAALVM_HOME` environment variable to your GraalVM installation directory
   - Building for Android on Windows requires WSL2 (Windows Subsystem for Linux)
   - Use the following Maven profiles:
     ```bash
     # Debug build for Android
     mvn -Pandroid-debug gluonfx:build

     # Release build for Android
     mvn -Pandroid-release gluonfx:build
     ```

## Testing Information

### Test Structure
The project follows a comprehensive testing approach with different types of tests:
- **Unit Tests**: Located in `src/test/java/com/belman/unit/`
- **Integration Tests**: Located in `src/test/java/com/belman/integration/`
- **Functional Tests**: Located in `src/test/java/com/belman/functional/`
- **Acceptance Tests**: Located in `src/test/java/com/belman/acceptance/`
- **Architecture Tests**: Located in `src/test/java/com/belman/architecture/`

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Run specific test method
mvn test -Dtest=ClassName#methodName
```

### Writing Tests
1. **Test Naming Convention**: Use descriptive names following the pattern `methodName_scenario_expectedBehavior`
2. **Test Structure**: Follow the Arrange-Act-Assert pattern
3. **Test Coverage**: Aim for high test coverage, especially for domain logic

### Example Test
Here's an example of a value object test:

```java
@Test
void constructor_withValidNames_shouldCreatePersonName() {
    // Act
    PersonName name = new PersonName("John", "Doe");

    // Assert
    assertEquals("John", name.firstName());
    assertEquals("Doe", name.lastName());
}
```

## Additional Development Information

### Curriculum Constraints
The project is constrained to what has been learned in the curriculum as documented in the docs/project/handouts folder. Key curriculum areas include:

1. **Software Architecture**:
   - Three-layer architecture (presentation, business, data)
   - Rich business entities in the domain package

2. **Programming Concepts**:
   - Object-oriented programming with Java
   - JavaFX for UI development
   - JDBC for database access
   - Unit testing with JUnit

3. **Design Patterns**:
   - Service-Repository pattern for data access
   - Command pattern for operations
   - Observer pattern for event handling
   - Factory pattern for object creation
   - Builder pattern for complex object construction
   - Adapter pattern for interface compatibility
   - Facade pattern for subsystem simplification
   - Iterator pattern for collection traversal

4. **Development Methodology**:
   - Scrum for project management
   - Test-driven development
   - Version control with Git

5. **Clean Code Principles**:
   - Meaningful naming conventions
   - Proper exception handling
   - Manageable code complexity
   - Elimination of code duplication
   - High-quality comments and documentation
   - Effective logging practices
   - Method and class cohesion
   - Proper null handling
   - Performance considerations
   - Security best practices
   - SOLID principles application
   - Comprehensive test coverage
   - Immutability where appropriate

All implementation must adhere to these constraints and use only technologies and patterns covered in the curriculum. It is imperative that the project is constrained to the requirements inside the projectdescription.md file.

### Project Architecture
The project follows a 3-layered architecture with "rich business entities":
1. **Presentation Layer**: JavaFX UI components (views, view models, controllers)
2. **Business Layer**: Domain logic, use cases, services
   - Contains "rich business entities" in the domain package
3. **Data Layer**: Repositories, data access, external services

This architecture is a requirement from the curriculum and must be strictly followed. The project is constrained to what has been learned in the curriculum as documented in the docs/project/handouts folder.

#### Package Structure
The project follows a specific package structure for each layer:

##### Presentation Layer (com.belman.ui)
- **views**: Contains all view classes, organized by feature
  - Each feature has its own package (e.g., login, admin, photoupload)
  - Each view package contains the View, ViewModel, and Controller classes for that feature
- **navigation**: Contains navigation-related classes
  - Router for handling navigation between views
  - Route guards for controlling access to views
- **core**: Contains base classes and interfaces
  - Base classes for views, view models, and controllers
  - Common UI components and utilities

##### Business Layer (com.belman.service)
- **domain**: Contains rich business entities, organized by bounded contexts
  - **photo**: Photo-related domain classes
  - **order**: Order-related domain classes
  - **report**: Report-related domain classes
  - **user**: User-related domain classes
  - **customer**: Customer-related domain classes
  - **common**: Shared domain classes
  - **security**: Security-related domain classes
  - **services**: Domain services
- **usecases**: Contains use case classes
  - Organized by feature or domain area
- **core**: Contains core business services and interfaces
  - Service registry and locator
  - Lifecycle management

##### Data Layer (com.belman.repository)
- **persistence**: Contains repository implementations, organized by feature
  - **customer**: Customer-related repository implementations
  - **order**: Order-related repository implementations
  - **photo**: Photo-related repository implementations
  - **report**: Report-related repository implementations
  - **user**: User-related repository implementations
  - **admin**: Admin-related repository implementations
  - **auth**: Authentication-related repository implementations
  - **qa**: QA-related repository implementations
  - **support**: Support-related repository implementations
- **service**: Contains service implementations
  - External service integrations
  - File system operations
- **config**: Contains configuration classes
  - Application configuration
  - Database configuration
- **logging**: Contains logging implementations
- **platform**: Contains platform-specific utilities
- **bootstrap**: Contains application bootstrap code
  - Application initialization
  - Main entry point

#### Bootstrap Package
The bootstrap package (com.belman.repository.bootstrap) is part of the data layer, not a separate layer. It contains code that initializes the application, including:
- The main entry point (Main.java)
- Application bootstrapping logic (ApplicationBootstrapper.java)

This package is special in that it's allowed to depend on all layers to properly initialize the application. However, it should be kept minimal and focused solely on application initialization.

### Domain Architecture
The domain layer organizes the codebase by business capabilities rather than technical concerns. The rich business entities are located in the domain package inside the business layer.

Key architectural concepts applied:

1. **Business Modules**: The domain is divided into clearly defined business modules, each representing a coherent area of business functionality.

2. **Consistent Terminology**: Each business module has its own terminology, reflected in the code through clear naming and consistent language.

3. **Business Entity Groups**: Business entities are grouped with clear boundaries and a single entry point (the primary entity).
   - Primary entities that ensure consistency boundaries
   - Each entity group has a corresponding data access interface

4. **Immutable Data Objects**: Immutable objects representing concepts with no identity.
   - Used for descriptive aspects of the domain
   - Self-validating to ensure business rules

5. **Audit Events**: Important occurrences within the system that need to be tracked for accountability.
   - Used for loose coupling between business modules
   - Recorded when entity changes are saved

6. **Data Access Interfaces**: Interfaces for data access.
   - Provide collection-like access to business entities
   - Abstract persistence details
   - Return fully reconstituted entities

7. **Business Operations**: Operations that don't belong to entities.
   - Used for operations that span multiple entities
   - Implement business processes

8. **Business Rules**: Encapsulations of business rules that can be combined and reused.

9. **Configurable Rules**: Encapsulations of varying business rules that may change over time or by context.

The domain layer has no dependencies on outer layers, following good separation of concerns.

### Design Pattern Implementation Guidelines
The project should implement the following design patterns according to these guidelines:

1. **Service-Repository Pattern**:
   - Services encapsulate business logic and orchestrate operations
   - Repositories abstract data access and persistence details
   - Services depend on repository interfaces, not implementations
   - Example: `PhotoService` uses `PhotoRepository` to manage photo documents

2. **Command Pattern**:
   - Encapsulate operations as objects
   - Support undo/redo functionality where appropriate
   - Implement common interface with `execute()` method
   - Example: `UploadPhotoCommand`, `ApprovePhotoCommand`

3. **Observer Pattern**:
   - Use for event handling and notification
   - Implement with Java's built-in Observer/Observable or custom event system
   - Example: UI components observing changes in data models

4. **Factory Pattern**:
   - Create objects without exposing creation logic
   - Use static factory methods for common object creation
   - Example: `ReportFactory.createReport(ReportType type)`

5. **Builder Pattern**:
   - Use for complex objects with many optional parameters
   - Implement with fluent interface for readability
   - Example: `ReportBuilder.withTitle().withPhotos().build()`

6. **Adapter Pattern**:
   - Convert interfaces to work with incompatible classes
   - Use when integrating with external systems or libraries
   - Example: `LegacySystemAdapter` to work with existing code

7. **Facade Pattern**:
   - Provide simplified interface to complex subsystems
   - Hide implementation details from client code
   - Example: `EmailFacade` to simplify email sending operations

8. **Iterator Pattern**:
   - Provide standard way to traverse collections
   - Implement Java's `Iterable` interface where appropriate
   - Example: Custom collections implementing `Iterable`

### UI Design Guidelines
The UI is designed for production workers, sometimes wearing gloves, so it needs:
- Large click targets (minimum 48px height, prefer 60px)
- High contrast
- Readability from a distance
- Simplicity

#### Brand Colors
- **Primary Colors**:
  - Belman Light Blue (#7fa8c5)
  - Belman Blue (#004b88)
  - Belman Flexibles India Green (#338d71)
  - Belman Dark Grey (#333535)
- **Secondary Colors**:
  - Belman 80 Grey (#575757)
  - Belman 50 Grey (#9d9d9d)
  - Belman 30 Grey (#c6c6c6)
  - Belman 20 Grey (#dadada)
  - Belman 15 Grey (#e3e3e3)
  - Belman 07 Grey (#f2f2f2)

#### UI Components
- **Buttons**:
  - Primary Buttons: Solid Belman Blue background with white text
  - Secondary Buttons: Light Blue border, white background, blue text
  - Touch Areas: Buttons must be large and padded (min height 48px, prefer 60px)
- **Fonts**: Sans-serif (Segoe UI, Roboto, Arial, Helvetica), minimum 16pt for body text, 20-24pt for headings
- **Layout**: 
  - Clear navigation with simple top bar or side navigation
  - Step-by-step flows for complex tasks
  - Progress indicators for processes like photo uploads
  - Big, clear error messages in case of failure (red text, high contrast)

#### Accessibility
- High color contrast (> 4.5:1) for text
- Clear, non-ambiguous icons
- Avoid small text and small touch targets
- Support keyboard navigation (optional but good for robustness)

#### Key Design Motto
"Make it readable, make it tappable, make it impossible to get wrong."

### Known Issues
- There are currently build errors in the codebase that need to be resolved before tests can be run successfully.
- The project contains two different PersonName classes that may cause confusion.

### Project Purpose
This is a photo documentation system for Belman A/S, a Danish company specializing in expansion joints and flexible pipe solutions. The system allows:
- Attaching images to order numbers and saving to DB
- Autogenerating QC reports
- Emailing QC documentation to customers

The system has multiple user roles:
- Production worker (Taking the pictures)
- Quality assurance employees (Approving the documentation)
- Admin (Assigns roles to employees)
