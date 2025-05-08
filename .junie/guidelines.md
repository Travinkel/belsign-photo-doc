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

### Project Architecture
The project follows a 3-layered architecture:
1. **Presentation Layer**: JavaFX UI components (views, view models, controllers)
2. **Business Layer**: Domain logic, use cases, services
3. **Data Layer**: Repositories, data access, external services

### Domain-Driven Design
The project uses Domain-Driven Design (DDD) concepts:
- **Aggregates**: Root entities that ensure consistency boundaries
- **Value Objects**: Immutable objects representing concepts with no identity
- **Repositories**: Interfaces for data access
- **Services**: Domain operations that don't belong to entities

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
- **Secondary Colors**: Various grays

#### UI Components
- **Buttons**: Large, with adequate padding
- **Fonts**: Sans-serif, minimum 16pt
- **Layout**: Clear navigation, step-by-step flows

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