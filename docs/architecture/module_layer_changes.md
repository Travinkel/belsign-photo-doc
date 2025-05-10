# Module Layer Architecture Changes

## Overview

This document describes the changes made to ensure the module package doesn't violate the three-layer architecture by having dependencies on other layers. The module package is intended to be a shared package that can be used by all layers without creating circular dependencies.

## Changes Made

### 1. Created UserDataAccess Interface

The `UserDataAccess` interface was created in the `com.belman.domain.user` package to define the contract for accessing user data. This interface is used by the business layer to access user data without depending on the data layer implementation details.

```java
public interface UserDataAccess {
    Optional<UserBusiness> findByUsername(Username username);
    Optional<UserBusiness> findByEmail(EmailAddress email);
    Optional<UserBusiness> findById(UserId id);
    List<UserBusiness> findAll();
    List<UserBusiness> findByRole(UserRole role);
    void save(UserBusiness user);
    boolean delete(UserId id);
}
```

### 2. Created UserDataAccessAdapter

The `UserDataAccessAdapter` class was created in the `com.belman.repository.persistence` package to implement the `UserDataAccess` interface and adapt the existing `InMemoryUserRepository`. This adapter follows the Adapter pattern, converting between `UserAggregate` (used by the repository) and `UserBusiness` (used by the business layer).

```java
public class UserDataAccessAdapter implements UserDataAccess {
    private final InMemoryUserRepository repository;
    
    // Constructor and method implementations
}
```

### 3. Updated ApplicationInitializer

The `ApplicationInitializer` class was updated to create and register an instance of `UserDataAccessAdapter` when the `UserRepository` is an instance of `InMemoryUserRepository`. This ensures that the business layer can access user data through the `UserDataAccess` interface without depending on the data layer implementation details.

## Architecture Benefits

These changes provide several benefits to the architecture:

1. **Separation of Concerns**: The module package defines what it needs (through interfaces) without depending on how it's implemented.

2. **Dependency Inversion**: The business layer depends on abstractions (interfaces) rather than concrete implementations.

3. **Testability**: The business layer can be tested with mock implementations of the interfaces.

4. **Flexibility**: The data layer can change its implementation details without affecting the business layer.

5. **Maintainability**: The codebase is easier to maintain because changes in one layer don't ripple through other layers.

## Future Work

As part of the ongoing architecture overhaul, similar changes should be made for other repositories and services to ensure that the module package doesn't depend on other layers. The following tasks should be considered:

1. Create data access interfaces for other business objects (OrderBusiness, CustomerBusiness, etc.).

2. Create adapter implementations for these interfaces in the data layer.

3. Update the ApplicationInitializer to register these adapters.

4. Update the business layer to use these interfaces instead of concrete repository implementations.