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
    Order orderAggregate = mock(Order.class);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderAggregate));
    when(orderAggregate.canGenerateReport()).thenReturn(true);
    
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
    private final Map<OrderId, Order> orderAggregates = new HashMap<>();
    
    @Override
    public Optional<Order> findById(OrderId id) {
        return Optional.ofNullable(orderAggregates.get(id));
    }
    
    @Override
    public void save(Order orderAggregate) {
        orderAggregates.put(orderAggregate.getId(), orderAggregate);
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