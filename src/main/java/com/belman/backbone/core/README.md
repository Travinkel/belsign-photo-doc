# Backbone Core Module

The Core module provides the foundation for building JavaFX applications using the Backbone framework.

Backbone is an opinionated, Gluon Mobile–ready micro-framework designed to eliminate JavaFX boilerplate and promote Clean Architecture across desktop and mobile targets. It automates wiring of views, controllers, and view models using naming conventions, and supports declarative lifecycle and state management.

## Core API

The Core API provides a centralized interface for interacting with the Backbone core module. It simplifies common tasks and provides a consistent way to use the framework's features.

### Installation

The Core module is included as a dependency in the Backbone framework. To use it in your project, add the following dependency to your Maven POM file:

```xml
<dependency>
    <groupId>com.belman.backbone</groupId>
    <artifactId>core</artifactId>
    <version>1.0.0</version>
</dependency>

```

### Usage

#### Dependency Injection

Register and retrieve services using the ServiceLocator:

```java
// Register a service
UserService userService = new UserService();
CoreAPI.registerService(UserService.class, userService);

// Retrieve a service
UserService retrievedService = CoreAPI.getService(UserService.class);

// Inject services into an object
CoreAPI.injectServices(viewModel);
```

#### Event Handling

Publish and subscribe to domain events:

```java
// Register an event handler
CoreAPI.registerEventHandler(UserLoggedInEvent.class, event -> {
    System.out.println("User logged in: " + event.getUsername());
});

// Publish an event
UserLoggedInEvent event = new UserLoggedInEvent("john.doe");
CoreAPI.publishEvent(event);

// Publish an event asynchronously
CoreAPI.publishEventAsync(event);

// Unregister an event handler
CoreAPI.unregisterEventHandler(UserLoggedInEvent.class, handler);
```

#### State Management

Manage application state using the StateStore:

```java
// Set a state value
CoreAPI.setState("currentUser", user);

// Get a state value
User user = CoreAPI.getState("currentUser");

// Update a state value
CoreAPI.updateState("counter", (Integer count) -> count + 1);

// Listen for state changes
CoreAPI.listenToState("currentUser", this, user -> {
    updateUI(user);
});

// Stop listening for state changes
CoreAPI.unlistenToState("currentUser", this);
```

#### View Management

Load views and their associated components:

```java
// Load a view
ViewLoader.LoadedComponents<LoginViewModel, Parent> components = CoreAPI.loadView(LoginView.class);
Parent view = components.parent();
LoginViewModel viewModel = components.viewModel();
BaseController<LoginViewModel> controller = components.controller();
```

## Architecture

The Core module follows a clean architecture approach with the following components:

### Base Classes

- `BaseViewModel`: Base class for all view models
- `BaseController`: Base class for all controllers
- `BaseService`: Base class for all services

### Dependency Injection

- `ServiceLocator`: Service locator pattern implementation
- `Inject`: Annotation for dependency injection

### Event Handling

- `DomainEvent`: Interface for domain events
- `AbstractDomainEvent`: Base implementation of domain events
- `DomainEventHandler`: Interface for event handlers
- `DomainEventPublisher`: Publisher for domain events

### State Management

- `StateStore`: Global state management store
- `Property`: Platform-agnostic property class

### View Loading

- `ViewLoader`: Utility for loading views and creating controllers and view models
- `NamingConventions`: Utility for enforcing naming conventions

## Examples

> Backbone is designed with Gluon Mobile compatibility in mind and avoids tight coupling to desktop-only JavaFX features. This enables native image builds via GraalVM and deployment to Android or iOS using Gluon Substrate.

### Creating a View Model

```java
public class LoginViewModel extends BaseViewModel<AuthenticationService> {
    private final Property<String> username = new Property<>("");
    private final Property<String> password = new Property<>("");
    private final Property<Boolean> isLoading = new Property<>(false);
    private final Property<String> errorMessage = new Property<>("");

    public void login() {
        isLoading.set(true);
        errorMessage.set("");

        getService().authenticate(username.get(), password.get())
            .thenAccept(result -> {
                isLoading.set(false);
                if (result.isSuccess()) {
                    CoreAPI.setState("currentUser", result.getUser());
                    CoreAPI.publishEvent(new UserLoggedInEvent(result.getUser().getUsername()));
                } else {
                    errorMessage.set(result.getErrorMessage());
                }
            });
    }

    // Getters for properties
    public Property<String> usernameProperty() {
        return username;
    }

    public Property<String> passwordProperty() {
        return password;
    }

    public Property<Boolean> isLoadingProperty() {
        return isLoading;
    }

    public Property<String> errorMessageProperty() {
        return errorMessage;
    }
}
```

### Creating a Service

```java
public class AuthenticationService extends BaseService {
    public CompletableFuture<AuthResult> authenticate(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            // Simulate network delay
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Simple authentication logic
            if ("admin".equals(username) && "password".equals(password)) {
                User user = new User(username);
                return new AuthResult(true, user, null);
            } else {
                return new AuthResult(false, null, "Invalid username or password");
            }
        });
    }
}
```

### Creating a Controller

```java
public class LoginController extends BaseController<LoginViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator loadingIndicator;

    @Override
    public void initializeBinding() {
        // Bind view model properties to UI components
        usernameField.textProperty().bindBidirectional(getViewModel().usernameProperty());
        passwordField.textProperty().bindBidirectional(getViewModel().passwordProperty());
        errorLabel.textProperty().bind(getViewModel().errorMessageProperty());
        loadingIndicator.visibleProperty().bind(getViewModel().isLoadingProperty());
        
        // Disable login button when loading
        loginButton.disableProperty().bind(getViewModel().isLoadingProperty());
        
        // Set up login button action
        loginButton.setOnAction(event -> getViewModel().login());
    }
}
```

## Contributing

Contributions to the Backbone framework are welcome. Please follow the project's coding standards and submit pull requests for review.

## License

The Backbone framework is licensed under the MIT License. See the LICENSE file for details.