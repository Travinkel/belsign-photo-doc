# Bootstrap Architecture Changes

## Overview

This document describes the changes made to ensure that bootstrapping is placed correctly in the three-layer architecture (BLL, DAL, GUI) with the module package as a shared package. The changes involve creating a new Main class in the root package and refactoring the bootstrapping code to be distributed across the three layers.

## Changes Made

### 1. Created New Main Class

A new `Main` class was created in the `com.belman` package (root package) to replace the existing `Main` class in the `com.belman.dataaccess.bootstrap` package. The new class is not tied to any specific layer and serves as the entry point for the application, coordinating bootstrapping across the three layers.

```java
package com.belman;

// imports...

/**
 * Main application class for BelSign.
 * Handles application initialization and platform detection.
 * This class is the entry point for the application and coordinates bootstrapping
 * across the three layers (BLL, DAL, GUI).
 */
public class Main extends Application {
    // Implementation...
}
```

### 2. Refactored Bootstrapping Code

The bootstrapping code was refactored to clearly separate responsibilities across the three layers:

#### Data Layer (DAL) Bootstrapping

The Data Layer bootstrapping includes initializing the database, repositories, and services:

```
// Initialize Gluon internal classes fixes (DAL)
logger.debug("Initializing Gluon internal classes fixes");
GluonInternalClassesFix.initialize();

// Bootstrap the application (DAL)
logger.startup("Bootstrapping the application");
ApplicationBootstrapper.initialize();

// Initialize service fallbacks for desktop platforms (DAL)
if (!PlatformUtils.isRunningOnMobile()) {
    logger.file("Initializing desktop storage service fallback");
    StorageServiceFactory.getStorageService();

    logger.file("Initializing desktop display service fallback");
    DisplayServiceFactory.getDisplayService();

    logger.info("Using com.gluonhq.license.disable=true to disable Gluon licensing checks");
}
```

#### Business Layer (BLL) Bootstrapping

The Business Layer bootstrapping includes initializing the lifecycle manager and service registry:

```
// Initialize the LifecycleManager and ServiceRegistry (BLL)
logger.debug("Initializing LifecycleManager and ServiceRegistry");
LoggerFactory loggerFactory = ServiceLocator.getService(LoggerFactory.class);
ServiceRegistry.setLogger(loggerFactory);
LifecycleManager.init(this, loggerFactory);
```

#### Presentation Layer (GUI) Bootstrapping

The Presentation Layer bootstrapping includes registering views, setting up the router, and initializing the UI:

```
// Register views (GUI)
registerViews();

// Set up the Router (GUI)
logger.debug("Setting up Router");
Router.setMobileApplication(app);

// Apply platform-specific styling (GUI)
logger.debug("Applying platform-specific styling");
applyPlatformStyling(scene);

// Load CSS (GUI)
loadCss(scene);

// Show the splash view (GUI)
logger.info("Showing splash view");
app.switchView(SPLASH_VIEW);
```

#### Cross-Layer Bootstrapping

Some bootstrapping tasks involve multiple layers:

```
// Initialize route guards for role-based access control (BLL + GUI)
initializeRouteGuards();
```

### 3. Modularized the Code

The code was refactored to group related functionality into separate methods:

- `registerViews()`: Registers all views with the application (GUI layer)
- `initializeRouteGuards()`: Initializes route guards for role-based access control (BLL + GUI layers)
- `loadCss(Scene)`: Loads CSS for the application (GUI layer)
- `applyPlatformStyling(Scene)`: Applies platform-specific styling to the scene (GUI layer)

This makes the code more modular and easier to understand, with clear separation of concerns.

## Architecture Benefits

These changes provide several benefits to the architecture:

1. **Separation of Concerns**: Each layer is responsible for its own bootstrapping, with clear boundaries between layers.

2. **Maintainability**: The code is more modular and easier to understand, with related functionality grouped together.

3. **Flexibility**: The bootstrapping process is now more flexible, allowing for changes in one layer without affecting other layers.

4. **Testability**: The modular structure makes it easier to test each part of the bootstrapping process in isolation.

5. **Clarity**: The code now clearly indicates which parts of the bootstrapping process belong to which layer, making it easier to understand the architecture.

## Module Package as a Shared Package

The module package continues to be used as a shared package that can be used by all layers without creating circular dependencies. The Main class imports from the module package, but the module package does not depend on the Main class or any other layer-specific code.

## Conclusion

The changes made ensure that bootstrapping is placed correctly in the three-layer architecture (BLL, DAL, GUI) with the module package as a shared package. The new Main class in the root package coordinates bootstrapping across the three layers, with clear separation of concerns and modular code structure.