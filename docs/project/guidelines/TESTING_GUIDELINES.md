# Testing Guidelines for BelSign Photo Documentation System

## Table of Contents
1. [Introduction](#introduction)
2. [Testing Approach](#testing-approach)
3. [Test Types](#test-types)
4. [Test Organization](#test-organization)
5. [Test Naming Conventions](#test-naming-conventions)
6. [Test Data Management](#test-data-management)
7. [Mocking and Test Doubles](#mocking-and-test-doubles)
8. [Architecture Testing](#architecture-testing)
9. [Test Coverage](#test-coverage)
10. [Continuous Integration](#continuous-integration)
11. [Test Documentation](#test-documentation)

## Introduction

This document provides guidelines for testing the BelSign Photo Documentation System. It covers the testing approach, test types, organization, naming conventions, and best practices. Following these guidelines will ensure that the system is thoroughly tested and meets the quality requirements.

The project requirements specify that at least one of the core classes must be tested through an automated JUnit test. However, to ensure the quality and reliability of the system, we recommend a more comprehensive testing approach.

## Testing Approach

The BelSign Photo Documentation System follows a test-driven development (TDD) approach where appropriate, combined with a comprehensive testing strategy that includes unit tests, integration tests, architecture tests, and UI tests.

### Test-Driven Development (TDD)

For core business logic and complex algorithms, we recommend following a TDD approach:

1. Write a failing test that defines the expected behavior
2. Implement the minimum code necessary to make the test pass
3. Refactor the code while keeping the tests passing

### Testing Pyramid

The testing strategy follows the testing pyramid approach:

1. **Unit Tests**: Many small, focused tests at the bottom of the pyramid
2. **Integration Tests**: Fewer tests that verify interactions between components
3. **UI Tests**: A small number of tests that verify the user interface

This approach ensures that most issues are caught early in the development process, while still providing confidence that the system works as a whole.

## Test Types

### Unit Tests

Unit tests verify the behavior of individual components in isolation.

**Characteristics**:
- Fast execution
- No external dependencies (databases, files, network)
- Test a single unit of functionality
- Use mocks or stubs for dependencies

**Example**:
```java
@Test
public void testOrderAggregateAddPhoto() {
    // Arrange
    OrderId orderId = new OrderId("ORD-123");
    CustomerId customerId = new CustomerId("CUST-456");
    OrderAggregate order = new OrderAggregate(orderId, customerId);
    Photo photo = new Photo(new PhotoId("PHOTO-789"), "test.jpg");
    
    // Act
    order.addPhoto(photo);
    
    // Assert
    assertTrue(order.getPhotos().contains(photo));
    assertEquals(1, order.getPhotos().size());
}
```

### Integration Tests

Integration tests verify that different components work together correctly.

**Characteristics**:
- Test interactions between components
- May involve external dependencies
- Focus on boundaries between components
- Verify correct data flow

**Example**:
```java
@Test
public void testOrderRepositorySaveAndRetrieve() {
    // Arrange
    OrderRepository repository = new SqlOrderRepository(dataSource);
    OrderId orderId = new OrderId("ORD-123");
    CustomerId customerId = new CustomerId("CUST-456");
    OrderAggregate order = new OrderAggregate(orderId, customerId);
    
    // Act
    repository.save(order);
    Optional<OrderAggregate> retrievedOrder = repository.findById(orderId);
    
    // Assert
    assertTrue(retrievedOrder.isPresent());
    assertEquals(orderId, retrievedOrder.get().getId());
    assertEquals(customerId, retrievedOrder.get().getCustomerId());
}
```

### Architecture Tests

Architecture tests verify that the code adheres to the architectural constraints.

**Characteristics**:
- Verify layer dependencies
- Ensure proper package structure
- Check naming conventions
- Validate design patterns usage

**Example**:
```java
@Test
public void businessLayerShouldNotDependOnPresentationLayer() {
    ArchRule rule = noClasses().that().resideInAPackage("com.belman.business..")
        .should().dependOnClassesThat().resideInAPackage("com.belman.presentation..");
    
    rule.check(importedClasses);
}
```

### UI Tests

UI tests verify that the user interface works correctly.

**Characteristics**:
- Test user interactions
- Verify UI behavior
- Check UI state after actions
- Ensure proper navigation

**Example**:
```java
@Test
public void testLoginScreen() {
    // Arrange
    clickOn("#usernameField").write("admin");
    clickOn("#passwordField").write("password");
    
    // Act
    clickOn("#loginButton");
    
    // Assert
    verifyThat("#dashboardTitle", hasText("Dashboard"));
}
```

## Test Organization

Tests should be organized to mirror the structure of the production code, with a few key differences:

1. Test classes should be in a parallel package structure under `src/test/java`
2. Test class names should match the class they test, with a "Test" suffix
3. Test resources should be in `src/test/resources`

### Example Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── belman/
│               ├── business/
│               │   └── domain/
│               │       └── order/
│               │           └── OrderAggregate.java
│               ├── data/
│               │   └── persistence/
│               │       └── SqlOrderRepository.java
│               └── presentation/
│                   └── views/
│                       └── order/
│                           └── OrderViewModel.java
└── test/
    └── java/
        └── com/
            └── belman/
                ├── unit/
                │   └── business/
                │       └── domain/
                │           └── order/
                │               └── OrderAggregateTest.java
                ├── integration/
                │   └── data/
                │       └── persistence/
                │           └── SqlOrderRepositoryTest.java
                ├── architecture/
                │   └── rules/
                │       └── threelayer/
                │           └── ThreeLayerArchitectureTest.java
                └── ui/
                    └── presentation/
                        └── views/
                            └── order/
                                └── OrderViewTest.java
```

## Test Naming Conventions

Test names should clearly describe what is being tested and the expected outcome.

### Method Naming

Use the following pattern for test method names:

```
test[MethodUnderTest]_[Scenario]_[ExpectedBehavior]
```

Examples:
- `testAddPhoto_WithValidPhoto_PhotoIsAdded`
- `testApproveOrder_WhenStatusIsPending_OrderIsApproved`
- `testFindById_WithNonExistentId_ReturnsEmptyOptional`

### Class Naming

Test classes should be named after the class they test, with a "Test" suffix:

```
[ClassUnderTest]Test
```

Examples:
- `OrderAggregateTest`
- `SqlOrderRepositoryTest`
- `OrderViewModelTest`

## Test Data Management

### Test Data Creation

Create test data in a consistent and maintainable way:

1. Use factory methods or builders to create test data
2. Use meaningful values that represent real-world scenarios
3. Avoid hardcoding values that might change

**Example**:
```java
private OrderAggregate createTestOrder() {
    OrderId orderId = new OrderId("ORD-123");
    CustomerId customerId = new CustomerId("CUST-456");
    List<ProductDescription> products = Arrays.asList(
        new ProductDescription("Product 1", "Description 1"),
        new ProductDescription("Product 2", "Description 2")
    );
    return new OrderAggregate(orderId, customerId, products);
}
```

### Test Data Cleanup

Ensure that tests clean up after themselves:

1. Delete any created files
2. Roll back database transactions
3. Reset any static state

## Mocking and Test Doubles

Use mocking frameworks (e.g., Mockito) to create test doubles for dependencies:

1. **Stubs**: Provide predefined responses to method calls
2. **Mocks**: Verify that specific methods are called with specific arguments
3. **Spies**: Wrap real objects and allow overriding specific methods
4. **Fakes**: Simplified implementations of interfaces for testing

**Example**:
```java
@Test
public void testGenerateReport() {
    // Arrange
    OrderRepository orderRepository = mock(OrderRepository.class);
    PhotoRepository photoRepository = mock(PhotoRepository.class);
    
    OrderId orderId = new OrderId("ORD-123");
    OrderAggregate order = createTestOrder();
    List<Photo> photos = createTestPhotos();
    
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    when(photoRepository.findByOrderId(orderId)).thenReturn(photos);
    
    ReportGenerator reportGenerator = new ReportGenerator(orderRepository, photoRepository);
    
    // Act
    Report report = reportGenerator.generateReport(orderId);
    
    // Assert
    assertNotNull(report);
    assertEquals(orderId, report.getOrderId());
    assertEquals(photos.size(), report.getPhotoCount());
    
    // Verify
    verify(orderRepository).findById(orderId);
    verify(photoRepository).findByOrderId(orderId);
}
```

## Architecture Testing

Architecture tests ensure that the code adheres to the architectural constraints. These tests are implemented using ArchUnit and are located in the `com.belman.architecture.rules` package.

### Layer Dependency Rules

Tests that verify the correct dependencies between layers:

```java
@Test
public void layeredArchitectureShouldBeRespected() {
    ArchRule rule = layeredArchitecture()
            .consideringAllDependencies()
            .layer("Presentation").definedBy("com.belman.presentation..")
            .layer("Business").definedBy("com.belman.business..")
            .layer("Data").definedBy("com.belman.data..")
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Business").mayOnlyBeAccessedByLayers("Presentation")
            .whereLayer("Data").mayOnlyBeAccessedByLayers("Business");

    rule.check(importedClasses);
}
```

### Naming Convention Rules

Tests that verify naming conventions:

```java
@Test
public void viewModelsShouldResideInPresentationLayer() {
    ArchRule rule = classes().that().haveNameMatching(".*ViewModel")
        .should().resideInAPackage("com.belman.presentation.views..");

    rule.check(importedClasses);
}
```

### Package Structure Rules

Tests that verify package structure:

```java
@Test
public void repositoriesShouldResideInDataLayer() {
    ArchRule rule = classes().that().haveNameMatching(".*Repository")
        .should().resideInAPackage("com.belman.data.persistence");

    rule.check(importedClasses);
}
```

## Test Coverage

Test coverage measures how much of the code is executed during tests. While high coverage is desirable, it's more important to have meaningful tests that verify the correct behavior.

### Coverage Goals

- **Business Layer**: Aim for 80-90% coverage
- **Data Layer**: Aim for 70-80% coverage
- **Presentation Layer**: Aim for 60-70% coverage

### Coverage Measurement

Use JaCoCo to measure test coverage:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.7</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Continuous Integration

Tests should be run automatically as part of the continuous integration process:

1. Run unit tests on every commit
2. Run integration tests on every pull request
3. Run architecture tests on every pull request
4. Run UI tests nightly or on demand

### CI Configuration

Configure the CI system to:

1. Build the project
2. Run the tests
3. Generate test reports
4. Measure test coverage
5. Fail the build if tests fail or coverage is below thresholds

## Test Documentation

Tests serve as documentation for the code, but additional documentation can be helpful:

1. **Test Plan**: Document the overall testing strategy
2. **Test Cases**: Document specific test scenarios
3. **Test Reports**: Generate reports from test runs

### Test Documentation in Code

Document tests with clear comments:

```java
/**
 * Tests that an order can be approved when its status is pending approval.
 * 
 * Scenario:
 * 1. Create an order with pending approval status
 * 2. Call the approve method
 * 3. Verify that the status is changed to approved
 * 4. Verify that an OrderApprovedEvent is registered
 */
@Test
public void testApproveOrder_WhenStatusIsPending_OrderIsApproved() {
    // Test implementation
}
```

### Test Reports

Generate test reports using Maven Surefire and JaCoCo:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.0.0-M5</version>
    <configuration>
        <redirectTestOutputToFile>true</redirectTestOutputToFile>
    </configuration>
</plugin>
```

## Conclusion

Following these testing guidelines will ensure that the BelSign Photo Documentation System is thoroughly tested and meets the quality requirements. The combination of unit tests, integration tests, architecture tests, and UI tests provides confidence that the system works correctly and adheres to the architectural constraints.