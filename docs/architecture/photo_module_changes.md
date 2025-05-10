# Photo Module Architecture Changes

## Overview

This document describes the changes made to implement the PhotoDataAccess interface and the ComponentDataAccessInterface. These changes are part of the ongoing architecture overhaul to ensure the module package doesn't depend on other layers.

## Changes Made

### 1. Created ComponentDataAccessInterface

The `ComponentDataAccessInterface` interface was created in the `com.belman.domain.core` package to define the contract for accessing business components. This interface is similar to `DataAccessInterface` but works with `BusinessComponent` instead of `BusinessObject`.

```java
public interface ComponentDataAccessInterface<T extends BusinessComponent<ID>, ID> {
    Optional<T> findById(ID id);
    T save(T component);
    void delete(T component);
    boolean deleteById(ID id);
    List<T> findAll();
    boolean existsById(ID id);
    long count();
}
```

### 2. Created PhotoDataAccess Interface

The `PhotoDataAccess` interface was created in the `com.belman.domain.order.photo` package to define the contract for accessing photo documents. This interface extends `ComponentDataAccessInterface<PhotoDocument, PhotoId>` and adds methods specific to photo documents.

```java
public interface PhotoDataAccess extends ComponentDataAccessInterface<PhotoDocument, PhotoId> {
    List<PhotoDocument> findByOrderId(OrderId orderId);
    List<PhotoDocument> findByStatus(ApprovalStatus status);
    List<PhotoDocument> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status);
}
```

## Architecture Benefits

These changes provide several benefits to the architecture:

1. **Separation of Concerns**: The module package defines what it needs (through interfaces) without depending on how it's implemented.

2. **Dependency Inversion**: The business layer depends on abstractions (interfaces) rather than concrete implementations.

3. **Testability**: The business layer can be tested with mock implementations of the interfaces.

4. **Flexibility**: The data layer can change its implementation details without affecting the business layer.

5. **Maintainability**: The codebase is easier to maintain because changes in one layer don't ripple through other layers.

6. **Consistency**: The architecture now has a consistent approach for both business objects and business components.

## Future Work

As part of the ongoing architecture overhaul, the following tasks should be considered:

1. Create a PhotoDataAccessAdapter to implement the PhotoDataAccess interface and adapt an existing repository implementation.

2. Update the ApplicationInitializer to register the PhotoDataAccessAdapter.

3. Update the business layer to use the PhotoDataAccess interface instead of concrete repository implementations.

4. Consider creating similar data access interfaces for other business components.