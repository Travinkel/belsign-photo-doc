# UI Structure Implementation

## Overview

This document describes the implementation of the new UI structure for the BelSign Photo Documentation application. The new structure is organized by business use case, making it easier to understand the application's functionality and find related code.

## Directory Structure

The new UI structure is organized as follows:

```
src/main/java/com/belman/ui/
├── base/                      # Base classes
│   ├── BaseController.java
│   ├── BaseView.java
│   ├── BaseViewModel.java
│   ├── ErrorBoundaryView.java
│   └── ObservableView.java
├── components/                # Shared UI components
│   ├── dialog/                # Dialog components
│   ├── form/                  # Form components
│   ├── list/                  # List components
│   └── media/                 # Media components
├── core/                      # Core UI infrastructure
│   ├── FlowContext.java
│   ├── FlowState.java
│   ├── ViewEvent.java
│   ├── ViewFactory.java
│   ├── ViewLoader.java
│   ├── ViewObserver.java
│   └── ViewRegistry.java
├── di/                        # Dependency injection
│   ├── Container.java
│   ├── Provider.java
│   └── SimpleContainer.java
├── events/                    # Event bus system
│   ├── EventBus.java
│   ├── SimpleEventBus.java
│   └── Subscription.java
├── lifecycle/                 # Lifecycle interfaces
│   ├── ControllerLifecycle.java
│   ├── ViewLifecycle.java
│   └── ViewModelLifecycle.java
├── navigation/                # Navigation services
│   └── RoleBasedNavigationService.java
├── utils/                     # UI utilities
│   ├── DialogUtils.java
│   ├── LayoutUtils.java
│   └── StyleUtils.java
├── validation/                # Input validation
│   ├── ValidationResult.java
│   └── Validator.java
└── usecases/                  # Organized by business use case
    ├── admin/                 # Admin use case
    │   ├── components/        # Components specific to admin
    │   ├── dashboard/         # Admin dashboard feature
    │   └── usermanagement/    # User management feature
    ├── authentication/        # Authentication use case
    │   ├── components/        # Components specific to authentication
    │   ├── login/             # Login feature
    │   │   ├── flow/          # Login flow states
    │   │   ├── LoginView.java
    │   │   ├── LoginViewController.java
    │   │   └── LoginViewModel.java
    │   └── logout/            # Logout feature
    ├── common/                # Common use case components
    │   ├── main/              # Main application shell
    │   └── splash/            # Splash screen
    ├── order/                 # Order management use case
    │   ├── components/        # Components specific to orders
    │   ├── gallery/           # Order gallery feature
    │   └── details/           # Order details feature
    ├── photo/                 # Photo management use case
    │   ├── components/        # Components specific to photos
    │   ├── upload/            # Photo upload feature
    │   └── review/            # Photo review feature
    ├── qa/                    # Quality assurance use case
    │   ├── components/        # Components specific to QA
    │   └── dashboard/         # QA dashboard feature
    └── report/                # Report management use case
        ├── components/        # Components specific to reports
        ├── preview/           # Report preview feature
        └── export/            # Report export feature
```

## Implementation Details

### Design Patterns

The following design patterns have been implemented:

1. **Factory Method Pattern**: The `ViewFactory` interface and its implementations provide a way to create views without specifying their concrete classes.

2. **State Pattern**: The `FlowContext` and `FlowState` interfaces provide a way to manage UI flows, such as the login flow.

3. **Observer Pattern**: The `ViewObserver` interface and `ObservableView` class provide a way for views to notify observers of changes.

4. **Command Pattern**: The `UICommand` interface provides a way to encapsulate UI actions and support undo/redo functionality.

5. **Dependency Injection**: The `Container` interface and `SimpleContainer` class provide a way to manage dependencies.

6. **Mediator Pattern**: The `EventBus` interface and `SimpleEventBus` class provide a way for components to communicate without direct dependencies.

7. **Error Boundary Pattern**: The `ErrorBoundaryView` class provides a way to handle errors in views and display fallback UIs.

### Navigation

The `RoleBasedNavigationService` has been updated to work with the new structure. It now navigates to views in the new structure based on user roles.

### View Classes

The following view classes have been moved to the new structure:

1. **LoginView**: Moved from `com.belman.presentation.views.login` to `com.belman.presentation.usecases.authentication.login`.

2. **MainView**: Moved from `com.belman.presentation.views.main` to `com.belman.presentation.usecases.common.main`.

3. **PhotoUploadView**: Moved from `com.belman.presentation.views.photoupload` to `com.belman.presentation.usecases.photo.upload`.

4. **QADashboardView**: Moved from `com.belman.presentation.views.qadashboard` to `com.belman.presentation.usecases.qa.dashboard`.

5. **UserManagementView**: Moved from `com.belman.presentation.views.usermanagement` to `com.belman.presentation.usecases.admin.usermanagement`.

## Benefits of the New Structure

1. **Organized by Business Use Case**: The new structure organizes the UI by business use case, making it easier to understand the application's functionality and find related code.

2. **Shared Components**: Common UI components are extracted into a shared `components` directory, promoting reuse and consistency across the application.

3. **Use Case-Specific Components**: Components that are specific to a use case are kept within that use case's directory, making it clear which components are used where.

4. **Improved Flow**: The new structure makes it easier to implement and understand UI flows within each use case, as related features are grouped together.

5. **Scalability**: The structure is more scalable, as new use cases can be added without affecting existing ones, and new features can be added to existing use cases without cluttering the codebase.

6. **Maintainability**: The clear separation of concerns makes the codebase more maintainable, as changes to one use case are less likely to affect others.

## Conclusion

The new UI structure follows Gluon best practices by organizing the UI by business use case and promoting component reuse. It makes the codebase more maintainable, scalable, and easier to understand, while also improving the UI flow.