# Clean Architecture and DDD Recommendations

## Overview

This document provides recommendations for improving the alignment of the codebase with Clean Architecture and Domain-Driven Design (DDD) principles. The recommendations are based on the analysis of the current codebase and the results of architecture tests.

## Current Architecture

The current architecture consists of four main layers:

1. **Domain Layer** (`com.belman.domain`): Contains domain entities, value objects, aggregates, repositories (interfaces), and domain services.
2. **Usecase Layer** (`com.belman.application`): Contains application services, use cases, and DTOs that orchestrate the domain model.
3. **Infrastructure Layer** (`com.belman.infrastructure`): Contains implementations of interfaces defined in the domain and usecase layers, as well as integrations with external systems.
4. **Presentation Layer** (`com.belman.presentation`): Contains UI components, views, and controllers that interact with the usecase layer.

## Identified Issues

Based on the architecture tests, the following issues have been identified:

### 1. Usecase Layer Dependencies

The usecase layer has dependencies on the presentation and infrastructure layers, which violates clean architecture principles. For example:

- `NavigateCommand` in the usecase layer depends on `ViewTransition` from the presentation layer.
- `BaseService` in the usecase layer depends on `EmojiLoggerAdapter` from the infrastructure layer.

### 2. Domain Layer Dependencies

The domain layer has dependencies on external libraries, which violates the principle that the domain layer should be independent of external frameworks. For example:

- `HashedPassword` value object depends on `org.mindrot.jbcrypt.BCrypt`.

### 3. Infrastructure Layer Dependencies

The infrastructure layer has dependencies on the presentation layer, which violates clean architecture principles. For example:

- `RouteGuardInitializer` in the infrastructure layer references specific view classes from the presentation layer.

### 4. Test Classes Violating Layer Boundaries

Many test classes violate layer boundaries by referencing classes from multiple layers, which makes the architecture tests fail.

## Recommendations

### 1. Refactor Usecase Layer

1. **Move UI-related commands to the presentation layer**: The `NavigateCommand` class should be moved to the presentation layer since it depends on presentation-layer concepts like `ViewTransition`.

2. **Create interfaces for infrastructure services in the usecase layer**: Instead of directly depending on infrastructure implementations, the usecase layer should define interfaces for the services it needs, and the infrastructure layer should provide implementations of these interfaces.

3. **Use dependency injection**: Inject dependencies through constructors rather than creating them directly, to make the code more testable and to better control dependencies.

Example:

```java
// Before
public class SomeService {
    private final EmojiLogger logger = EmojiLoggerAdapter.getLogger(SomeService.class);
    
    public void doSomething() {
        logger.info("Doing something");
    }
}

// After
public class SomeService {
    private final Logger logger;
    
    public SomeService(Logger logger) {
        this.logger = logger;
    }
    
    public void doSomething() {
        logger.info("Doing something");
    }
}
```

### 2. Refactor Domain Layer

1. **Move external dependencies out of the domain layer**: The domain layer should not depend on external libraries. Instead, define interfaces in the domain layer and provide implementations in the infrastructure layer.

Example:

```java
// Before
public class HashedPassword {
    private final String value;
    
    public static HashedPassword fromPlainText(String plainText) {
        String hashed = BCrypt.hashpw(plainText, BCrypt.gensalt());
        return new HashedPassword(hashed);
    }
    
    public boolean matches(String plainText) {
        return BCrypt.checkpw(plainText, value);
    }
}

// After
public class HashedPassword {
    private final String value;
    
    public static HashedPassword fromPlainText(String plainText, PasswordHasher hasher) {
        String hashed = hasher.hash(plainText);
        return new HashedPassword(hashed);
    }
    
    public boolean matches(String plainText, PasswordHasher hasher) {
        return hasher.verify(plainText, value);
    }
}

// In domain layer
public interface PasswordHasher {
    String hash(String plainText);
    boolean verify(String plainText, String hashedPassword);
}

// In infrastructure layer
public class BCryptPasswordHasher implements PasswordHasher {
    public String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
    
    public boolean verify(String plainText, String hashedPassword) {
        return BCrypt.checkpw(plainText, hashedPassword);
    }
}
```

### 3. Refactor Infrastructure Layer

1. **Remove dependencies on the presentation layer**: The infrastructure layer should not depend on the presentation layer. Instead, use interfaces or events to communicate between these layers.

2. **Use dependency inversion**: Define interfaces in the domain or usecase layer and implement them in the infrastructure layer.

Example:

```java
// Before
public class RouteGuardInitializer {
    public void initialize(AuthenticationService authService) {
        Router.addGuard(AdminView.class, () -> authService.isUserInRole(Role.ADMIN));
        Router.addGuard(UserManagementView.class, () -> authService.isUserInRole(Role.ADMIN));
    }
}

// After
// In usecase layer
public interface RouteGuard {
    void registerGuard(String routeName, Supplier<Boolean> guardCondition);
}

// In infrastructure layer
public class RouteGuardInitializer {
    private final RouteGuard routeGuard;
    private final AuthenticationService authService;
    
    public RouteGuardInitializer(RouteGuard routeGuard, AuthenticationService authService) {
        this.routeGuard = routeGuard;
        this.authService = authService;
    }
    
    public void initialize() {
        routeGuard.registerGuard("admin", () -> authService.isUserInRole(Role.ADMIN));
        routeGuard.registerGuard("userManagement", () -> authService.isUserInRole(Role.ADMIN));
    }
}

// In presentation layer
public class RouterImpl implements RouteGuard {
    public void registerGuard(String routeName, Supplier<Boolean> guardCondition) {
        if (routeName.equals("admin")) {
            Router.addGuard(AdminView.class, guardCondition);
        } else if (routeName.equals("userManagement")) {
            Router.addGuard(UserManagementView.class, guardCondition);
        }
    }
}
```

### 4. Refactor Test Classes

1. **Create test doubles**: Instead of directly using classes from multiple layers, create test doubles (mocks, stubs, fakes) for the classes you need.

2. **Use test-specific subpackages**: Organize test classes in subpackages that mirror the main codebase, to make it clear which layer each test belongs to.

3. **Exclude test classes from architecture tests**: If necessary, exclude test classes from architecture tests to avoid false positives.

Example:

```java
// Before
public class SomeTest {
    private DomainClass domainObject;
    private PresentationClass presentationObject;
    
    @Test
    public void testSomething() {
        // Test code using both domain and presentation classes
    }
}

// After
public class SomeTest {
    private DomainClass domainObject;
    private PresentationClassStub presentationStub;
    
    @Test
    public void testSomething() {
        // Test code using domain class and presentation stub
    }
}
```

## Implementation Strategy

To implement these recommendations, follow this strategy:

1. **Start with the domain layer**: Ensure the domain layer is completely independent of external frameworks and libraries.

2. **Refactor the usecase layer**: Remove dependencies on the presentation and infrastructure layers.

3. **Refactor the infrastructure layer**: Remove dependencies on the presentation layer.

4. **Update the architecture tests**: Modify the tests to reflect the improved architecture.

5. **Refactor test classes**: Update test classes to respect layer boundaries.

By following these recommendations, the codebase will better align with Clean Architecture and DDD principles, making it more maintainable, testable, and flexible.