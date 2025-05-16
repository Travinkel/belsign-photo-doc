# ApplicationInitializer Cleanup

## Overview

This document outlines the necessary changes to clean up the `ApplicationInitializer` class based on the following requirements:

1. `PhotoDataAccess` is now called `PhotoRepository`
2. There are no longer any `PhotoDataAccessAdapter` classes
3. We are using just two repositories, as we are using aggregates (business objects) that are responsible for everything

## Changes Required

The following changes need to be made to the `ApplicationInitializer.java` file:

### 1. Remove PhotoDataAccessAdapter Creation and Registration

There are three blocks of code that create and register `PhotoDataAccessAdapter` instances that need to be removed:

#### Block 1 (Lines 140-144)

```java
// Create and register PhotoDataAccessAdapter
logger.database("Creating PhotoDataAccessAdapter");
PhotoDataAccess photoDataAccess = new PhotoDataAccessAdapter(photoRepository);
ServiceRegistry.registerService(photoDataAccess);
logger.success("PhotoDataAccessAdapter created successfully");
```

#### Block 2 (Lines 212-216)

```java
// Create and register PhotoDataAccessAdapter
logger.database("Creating PhotoDataAccessAdapter");
PhotoDataAccess photoDataAccess = new PhotoDataAccessAdapter(photoRepository);
ServiceRegistry.registerService(photoDataAccess);
logger.success("PhotoDataAccessAdapter created successfully");
```

#### Block 3 (Lines 284-288)

```java
// Create and register PhotoDataAccessAdapter
logger.database("Creating PhotoDataAccessAdapter");
PhotoDataAccess photoDataAccess = new PhotoDataAccessAdapter(photoRepository);
ServiceRegistry.registerService(photoDataAccess);
logger.success("PhotoDataAccessAdapter created successfully");
```

### 2. Keep PhotoRepository Initialization and Registration

The following blocks of code that initialize and register `PhotoRepository` instances should be kept:

#### Block 1 (Lines 134-138)

```java
// Initialize PhotoRepository - use InMemoryPhotoRepository for now
logger.database("Creating InMemoryPhotoRepository");
photoRepository = new InMemoryPhotoRepository();
ServiceRegistry.registerService(photoRepository);
logger.success("Using InMemoryPhotoRepository");
```

#### Block 2 (Lines 206-210)

```java
// Initialize PhotoRepository as fallback
logger.database("Creating in-memory PhotoRepository as fallback");
photoRepository = new InMemoryPhotoRepository();
ServiceRegistry.registerService(photoRepository);
logger.info("Using in-memory PhotoRepository as fallback");
```

#### Block 3 (Lines 278-282)

```java
// Initialize PhotoRepository as fallback
logger.database("Creating in-memory PhotoRepository as fallback");
photoRepository = new InMemoryPhotoRepository();
ServiceRegistry.registerService(photoRepository);
logger.info("Using in-memory PhotoRepository as fallback");
```

## Implementation Notes

1. The project appears to have undergone significant refactoring, with many classes being moved or renamed.
2. The issue description states that `PhotoDataAccess` is now called `PhotoRepository`, which suggests that the `PhotoDataAccess` interface has been replaced by the `PhotoRepository` interface.
3. The issue description also states that there are no longer any `PhotoDataAccessAdapter` classes, which suggests that the adapter pattern is no longer being used for photo repositories.
4. The issue description mentions that we are using just two repositories, as we are using aggregates (business objects) that are responsible for everything. This suggests a move towards a more domain-driven design approach.

## Conclusion

By making these changes, the `ApplicationInitializer` class will be cleaned up to reflect the current architecture of the project, where `PhotoDataAccess` is now called `PhotoRepository` and there are no longer any `PhotoDataAccessAdapter` classes.