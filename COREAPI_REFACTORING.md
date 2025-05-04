# CoreAPI Refactoring

## Overview

This document describes the refactoring of the CoreAPI class in the BelSign Photo Documentation Module. The CoreAPI class was a central API for interacting with various parts of the system, but it violated clean architecture principles by allowing the application layer to access the presentation layer. This refactoring removes the CoreAPI class and replaces it with more focused classes that adhere to clean architecture principles.

## Changes Made

1. Created two new classes to replace CoreAPI:
   - `StateManager` in the application.core package - Handles state management functionality
   - `EventManager` in the application.core package - Handles event handling functionality

2. Updated references to CoreAPI in the following files:
   - `ApplicationStateManager.java` - Now uses EventManager for publishing events
   - `GluonLifecycleManager.java` - Now uses EventManager for publishing events and registering event handlers
   - `Router.java` - Now uses StateManager for storing and retrieving route parameters
   - `MainViewModel.java` - Now uses StateManager for setting the app bar title

3. Added the static initializer from CoreAPI to StateManager to maintain the functionality that updates the app bar title when the appBarTitle state changes.

## Benefits

1. **Improved Architecture**: The refactoring improves the architecture by removing a class that violated clean architecture principles.

2. **Better Separation of Concerns**: The functionality of CoreAPI is now split into two more focused classes, each with a single responsibility.

3. **Reduced Dependencies**: The application layer no longer depends on the presentation layer, which is a key principle of clean architecture.

## Remaining Issues

The ArchUnit tests for the presentation layer still show some architectural violations:

1. The presentation layer directly depends on infrastructure implementations, such as CameraServiceFactory and EmojiLogger.

2. There are test classes with names ending in "ViewModel" that are not in the presentation package.

3. BaseView has non-final fields, which violates the rule that views should be simple.

Fixing these violations would require a more extensive refactoring effort, which is beyond the scope of this task.

## Next Steps

1. Update the test files that use CoreAPI to use StateManager and EventManager instead.

2. Refactor the presentation layer to remove dependencies on infrastructure implementations.

3. Update the ArchUnit tests to better reflect the desired architecture.