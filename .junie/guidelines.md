# Development Guidelines for ExamProjectBelman

This document provides essential information for developers working on the ExamProjectBelman project.

## Build/Configuration Instructions

### Prerequisites
- Java 23 or higher
- Maven 3.8 or higher

### Building the Project
1. Clone the repository
2. Navigate to the project root directory
3. Run `mvn clean install` to build the project

### Running the Application
- For local development: `mvn javafx:run`
- For mobile builds: `mvn gluonfx:build`

### Project Structure
- The project follows a domain-driven design approach
- Main packages:
  - `com.belman.belsign.domain.model`: Contains domain model classes
  - `com.belman.belsign.domain.repository`: Contains repository interfaces
  - `com.belman.belsign.domain.service`: Contains domain services
  - `com.belman.belsign.domain.specification`: Contains specifications for filtering domain objects
  - `com.belman.belsign.infrastructure.service`: Contains infrastructure services

### AtHomeFX Framework
The project uses the AtHomeFX framework, a lightweight micro-framework for JavaFX that follows Clean Architecture principles.

#### Key Features
- Automatic FXML loading
- Automatic linking between View, Controller, and ViewModel
- Simple, clean View routing (SPA-style)
- Lifecycle support (`onShow()`, `onHide()`) for Views/ViewModels
- Runs safely inside the JavaFX Application Thread

#### Framework Structure
- `athomefx/core`: Core framework classes (BaseView, BaseController, BaseViewModel)
- `athomefx/navigation`: Navigation components (Router)
- `athomefx/lifecycle`: Lifecycle management
- `athomefx/di`: Dependency injection
- `athomefx/events`: Event handling
- `athomefx/aop`: Aspect-oriented programming
- `athomefx/state`: State management
- `athomefx/util`: Utility classes

#### Using AtHomeFX CLI
The AtHomeFX CLI is a command-line tool for generating components:

1. Run the CLI: `java -jar athomefx-cli.jar`
2. Choose what to generate:
   - Feature (View + Controller + ViewModel + FXML)
   - Service
3. Enter the required information:
   - Base package (e.g., com.example.features.login)
   - Base name (e.g., Login, Dashboard, UserProfile)
   - Output directory (e.g., ./src/main/java)

## Testing Information

### Test Structure
- Tests are organized in the `src/test/java` directory
- Unit tests are in the `unit` package
- Integration tests are in the `integration` package
- Tests follow the JUnit Jupiter framework

### Running Tests
- Run all tests: `mvn test`
- Run a specific test class: `mvn test -Dtest=ClassName`
- Run a specific test method: `mvn test -Dtest=ClassName#methodName`

### Creating New Tests
1. Create a new test class in the appropriate package (e.g., `unit.domain.model.order`)
2. Follow the naming convention: `ClassNameTest`
3. Use JUnit Jupiter annotations (`@Test`, etc.)
4. Use assertions from `org.junit.jupiter.api.Assertions`

### Test Example
Here's an example of a test class for the `PhotoAngle` domain model class:

```java
package unit.domain.model.order.photodocument;

import com.belman.belsign.domain.model.order.photodocument.PhotoAngle;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhotoAngleTest {

    @Test
    void validAngleShouldBeCreated() {
        PhotoAngle angle = new PhotoAngle(90.0);
        assertEquals(90.0, angle.getDegrees());
    }

    @Test
    void negativeAngleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoAngle(-1.0));
    }

    @Test
    void tooLargeAngleShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhotoAngle(360.0));
    }
}
```

## Additional Development Information

### Code Style
- Follow Java code conventions
- Use meaningful variable and method names
- Document public APIs with JavaDoc comments
- Value objects should be immutable (final classes with final fields)
- Implement proper equals() and hashCode() methods for value objects

### Domain Model Design
- The project follows Domain-Driven Design principles
- Value objects represent concepts with no identity (e.g., `PhotoAngle`, `EmailAddress`)
- Entities have identity and are mutable (e.g., `Order`, `PhotoDocument`)
- Repositories provide access to entities
- Services implement domain logic that doesn't belong to entities

### Error Handling
- Use exceptions for exceptional conditions
- Validate input parameters in constructors
- Use `Objects.requireNonNull()` for null checks

### Testing Best Practices
- Test both valid and invalid inputs
- Test edge cases
- Test equals() and hashCode() implementations for value objects
- Use descriptive test method names that explain what is being tested
