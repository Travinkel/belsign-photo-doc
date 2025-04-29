# AtHomeFX Framework Documentation

## Overview

AtHomeFX is a lightweight, Clean Architecture-compliant micro-framework for JavaFX, designed to automate and standardize the View–Controller–ViewModel relationship in modular JavaFX applications. Inspired by Afterburner.fx but rebuilt from scratch, AtHomeFX is modernized for JavaFX 21+ and follows strict layering and Domain-Driven Design principles.

AtHomeFX now implements CERN's proven JavaFX best practices, focusing on a single Scene model with a central root container, explicit lifecycle management, and state-driven UI. For more information, see [AtHomeFX Framework: CERN-Style JavaFX Architecture](athomefx-framework-cern-style.md).

## Core Features

- **Automatic FXML loading**
- **Automatic linking:** View ↔ Controller ↔ ViewModel
- **Simple, clean View routing (SPA-style)**
- **Lifecycle support** (`onShow()`, `onHide()`) for Views/ViewModels
- **No magic threads:** runs safely inside the JavaFX Application Thread
- **Built-in** KISS, DRY, SOLID principles
- **State management** with reactive properties
- **Dependency injection** for services
- **Naming convention enforcement**
- **Component generation utilities**

## Architecture

AtHomeFX follows a clean architecture approach with clear separation of concerns:

- **Views** are responsible for displaying the UI and handling user input
- **Controllers** are responsible for binding the UI to the ViewModel
- **ViewModels** are responsible for managing application state and business logic
- **Services** are responsible for data access and external communication

### Package Structure

```
framework/
  athomefx/
    core/
      BaseView.java
      BaseViewModel.java
      BaseController.java
    navigation/
      Router.java
    lifecycle/
      ViewLifecycle.java
    util/
      ViewLoader.java
      NamingConventions.java
      ComponentGenerator.java
    state/
      StateStore.java
    di/
      ServiceLocator.java
      Inject.java
    exceptions/
      ServiceInjectionException.java
    aop/
      DomainEventPublisherAspect.java
    examples/
      ui/
        Scene.java
        Stage.java
        LoginScene.java
        PhotoScene.java
        AdminScene.java
        DesktopStage.java
        IPadStage.java
        SmartPhoneStage.java
        SealedInterfacesExample.java
```

Note that the `examples` package contains example implementations that demonstrate various features of the framework but are not part of the core framework itself. The core UI components (View, Controller, ViewModel) are in the `core` package, not in a separate UI package.

## Getting Started

### Creating a New Feature

The easiest way to create a new feature is to use the ComponentGenerator utility:

```java
// Generate a complete feature (view, controller, view model, FXML)
ComponentGenerator.generateFeature(
    "com.example.myapp.features.login", // Base package
    "Login",                           // Base name
    "src/main/java"                    // Output directory
);

// Generate a service
ComponentGenerator.generateService(
    "com.example.myapp.services",      // Base package
    "Auth",                           // Base name
    "src/main/java"                    // Output directory
);
```

### Manual Setup

If you prefer to create components manually, follow these steps:

1. Create a ViewModel that extends `BaseViewModel`
2. Create a Controller that extends `BaseController`
3. Create a View that extends `BaseView`
4. Create an FXML file with the same name as the View

### Navigation

To navigate between views, use the Router:

```java
// Navigate to a view
Router.navigateTo(LoginView.class);

// If called from a background thread, use Platform.runLater
Platform.runLater(() -> Router.navigateTo(LoginView.class));
```

## Core Components

### BaseView

The `BaseView` class is responsible for loading the FXML file and creating the controller and view model. It also implements the `ViewLifecycle` interface to provide lifecycle hooks.

```java
public class LoginView extends BaseView<LoginViewModel> {
    public LoginView() {
        super(); // This loads the FXML and creates the controller and view model
    }
}
```

### BaseController

The `BaseController` class is responsible for binding the UI to the ViewModel. It provides access to the ViewModel and a method for initializing bindings.

```java
public class LoginController extends BaseController<LoginViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @Override
    public void initializeBinding() {
        // Bind UI elements to ViewModel properties
        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());

        // Bind button action to ViewModel command
        loginButton.setOnAction(e -> getViewModel().login());
    }
}
```

### BaseViewModel

The `BaseViewModel` class is responsible for managing application state and business logic. It implements the `ViewLifecycle` interface to provide lifecycle hooks.

```java
public class LoginViewModel extends BaseViewModel<LoginViewModel> {
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();

    @Inject
    private AuthService authService;

    public LoginViewModel() {
        ServiceLocator.injectServices(this);
    }

    public void login() {
        if (authService.login(username.get(), password.get())) {
            // Navigate to the main view
            Router.navigateTo(MainView.class);
        }
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    @Override
    public void onShow() {
        super.onShow();
        // Initialize the view model when shown
    }

    @Override
    public void onHide() {
        super.onHide();
        // Clean up resources when hidden
    }
}
```

### Services and Dependency Injection

Services are plain Java classes that can be registered with the `ServiceLocator` and injected into ViewModels:

```java
// Define a service
public class AuthService {
    public boolean login(String username, String password) {
        // Implement login logic
        return true;
    }
}

// Register the service
ServiceLocator.registerService(AuthService.class, new AuthService());

// Inject the service into a ViewModel
public class LoginViewModel extends BaseViewModel<LoginViewModel> {
    @Inject
    private AuthService authService;

    public LoginViewModel() {
        ServiceLocator.injectServices(this);
    }
}
```

### State Management

The `StateStore` provides a centralized store for managing application state:

```java
// Set a value in the state store
StateStore.getInstance().set("currentUser", user);

// Get a value from the state store
User user = StateStore.getInstance().get("currentUser");

// Get a property that can be bound to UI elements
ObjectProperty<User> userProperty = StateStore.getInstance().getProperty("currentUser");

// Listen for changes to a value
StateStore.getInstance().listen("currentUser", this, (User user) -> {
    // Handle user change
});

// Update a value using a function
StateStore.getInstance().update("counter", (Integer count) -> count + 1);
```

## Naming Conventions

AtHomeFX enforces naming conventions for components:

- **Views** must end with "View" (e.g., `LoginView`)
- **Controllers** must end with "Controller" (e.g., `LoginController`)
- **ViewModels** must end with "ViewModel" or "Model" (e.g., `LoginViewModel` or `LoginModel`)
- **Services** must end with "Service" (e.g., `AuthService`)

The `NamingConventions` utility provides methods for validating and generating names:

```java
// Validate names
boolean isValid = NamingConventions.isValidViewName("LoginView");

// Generate names
String viewName = NamingConventions.getViewName("Login"); // "LoginView"
String controllerName = NamingConventions.getControllerName("Login"); // "LoginController"
String viewModelName = NamingConventions.getViewModelName("Login"); // "LoginViewModel"
```

## Best Practices

1. **Keep Views Simple**: Views should only contain UI elements and minimal code.
2. **Use ViewModels for Logic**: Business logic should be in the ViewModel, not the View or Controller.
3. **Use Services for Data Access**: Data access and external communication should be in Services.
4. **Follow Naming Conventions**: Use the naming conventions enforced by the framework.
5. **Use Dependency Injection**: Register services with the ServiceLocator and inject them into ViewModels.
6. **Use State Management**: Use the StateStore for global state management.
7. **Use Lifecycle Hooks**: Implement onShow() and onHide() for proper resource management.
8. **Run UI Operations on the JavaFX Thread**: Use Platform.runLater() for UI operations from background threads.

## Cross-Platform Support

AtHomeFX is designed to work on desktop, smartphone, and tablet platforms. It uses JavaFX and Gluon Mobile for cross-platform support.

## CERN-Style JavaFX Architecture

AtHomeFX now implements CERN's proven JavaFX best practices, focusing on a single Scene model with a central root container, explicit lifecycle management, and state-driven UI. For more information, see the [AtHomeFX Framework: CERN-Style JavaFX Architecture](athomefx-framework-cern-style.md) documentation.

## Conclusion

AtHomeFX provides a lightweight, clean, and standardized way to build JavaFX applications. By following the patterns and practices outlined in this documentation, you can create modular, maintainable, and testable JavaFX applications.
