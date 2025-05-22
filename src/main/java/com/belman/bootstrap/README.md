# Package: `com.belman.bootstrap`

## 1. Purpose

* The bootstrap package is responsible for initializing and configuring the application at startup.
* It coordinates the bootstrapping process across all three layers of the application (presentation, domain, and data access).
* It represents the application's startup and lifecycle management process, handling configuration, dependency injection, and platform-specific setup.

## 2. Key Classes and Interfaces

* `Main` - The entry point of the application that extends JavaFX's Application class and manages the application lifecycle.
* `ApplicationBootstrapper` - Handles the initialization and shutdown of the application components.
* `ServiceLocator` - Implements a service locator pattern for dependency management across the application.
* `ServiceRegistry` - Manages registration and retrieval of services in the application.
* `StorageTypeConfig` - Configures the storage type (database) used by the application.
* `LifecycleManager` - Manages the application's lifecycle events and hooks.
* `GluonInternalClassesFix` - Provides fixes for Gluon-related issues, especially for mobile platforms.
* `RouteGuardInitializer` - Sets up security guards for navigation routes based on user roles.

## 3. Architectural Role

* This package is part of the infrastructure layer, serving as the foundation for the entire application.
* It acts as the glue between the three main architectural layers (presentation, domain, and data access).
* It provides cross-cutting concerns like dependency injection, configuration, and platform detection.
* It initializes the JavaFX UI framework and sets up the application's visual components.

## 4. Requirements Coverage

* Supports cross-platform functionality by detecting and adapting to different platforms (desktop vs. mobile).
* Enables secure access through route guards and authentication service initialization.
* Facilitates modular design through dependency injection and service registration.
* Ensures proper application startup and shutdown sequences, meeting reliability requirements.
* Provides error handling mechanisms to improve application robustness.

## 5. Usage and Flow

* Application startup flow:
  1. The `main()` method initializes storage configuration and launches the JavaFX application.
  2. The `init()` method sets up Gluon fixes, bootstraps the application, initializes error handling, and configures platform-specific services.
  3. The `start()` method creates the UI scene, initializes the view stack, applies styling, and shows the splash screen.
  4. The `stop()` method handles proper application shutdown.
* The bootstrap package is used at the very beginning of the application lifecycle and sets up all necessary components for the application to function.
* It creates and configures the dependency injection container that other parts of the application will use to resolve dependencies.

## 6. Patterns and Design Decisions

* **Service Locator Pattern**: Used for dependency management instead of a more modern dependency injection framework.
* **Factory Pattern**: Used in various factory classes like `DisplayServiceFactory` and `StorageServiceFactory`.
* **Singleton Pattern**: Used in classes like `ViewRegistry` and `ViewStackManager`.
* **Adapter Pattern**: Used to adapt different logging implementations.
* **Initialization-on-demand**: Many components use static initialization methods that are called in a specific sequence.

## 7. Unnecessary Complexity

* The manual dependency management through `ServiceLocator` and `ServiceRegistry` could be replaced with a more modern DI framework.
* There's a mix of static initialization and instance-based configuration which can make the code harder to follow.
* Platform-specific code is scattered throughout the package rather than being isolated in dedicated adapters.
* The initialization sequence is complex and spread across multiple classes, making it difficult to understand the complete startup flow.

## 8. Refactoring Opportunities

* Replace the custom Service Locator with a proper dependency injection framework like Spring or Dagger.
* Consolidate platform-specific code into dedicated adapter classes.
* Create a more explicit and linear initialization sequence to improve readability.
* Consider using a builder pattern for application configuration to make the setup more declarative.
* Improve error handling during initialization to provide better diagnostics for startup failures.
* Reduce the use of static methods and singletons to improve testability.
