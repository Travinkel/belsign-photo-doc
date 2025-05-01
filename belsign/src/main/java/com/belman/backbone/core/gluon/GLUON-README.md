# AtHomeFX Gluon Module

The Gluon module extends the core framework for multi-platform support using the Gluon SDK. It provides adapters and utilities for building mobile applications with the Gluon Mobile framework.

## Overview

The Gluon module integrates with Gluon Glisten and adapts core abstractions to Gluon's lifecycle and UI model. It provides Gluon-specific navigation, lifecycle hooks, and view rendering while maintaining full compatibility with the core module.

## Components

### GluonView

`GluonView` is the base class for all Gluon-based views. It extends Gluon's `View` class and integrates with the core framework's `BaseViewModel`. It provides the following features:

- FXML loading and controller/viewModel initialization
- Lifecycle hooks (onShow, onHide) that delegate to the ViewModel
- Integration with Gluon's AppBar
- Loading indicator
- Binding utilities for connecting core Properties to JavaFX UI components

Example usage:

```java
public class LoginView extends GluonView<LoginViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @Override
    protected void setUpAppBar() {
        setTitle("Login");
        setShowBackButton(false);
    }

    @Override
    public void initialize() {
        // Bind ViewModel properties to UI components
        bindProperty(getViewModel().usernameProperty(), usernameField.textProperty());
        bindProperty(getViewModel().passwordProperty(), passwordField.textProperty());
        bindText(getViewModel().errorMessageProperty(), errorLabel);
        bindAction(getViewModel().isLoadingProperty(), loginButton, getViewModel()::login);

        // Listen for loading state changes
        getViewModel().isLoadingProperty().addListener(isLoading -> {
            if (isLoading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
    }
}
```

### GluonRouter

`GluonRouter` provides navigation capabilities for Gluon applications. It integrates with Gluon's `MobileApplication` and provides the following features:

- Navigation between views
- Route parameters
- Navigation history
- Route guards

Example usage:

```java
// Navigate to a view
GluonRouter.navigateTo(LoginView.class);

// Navigate to a view with parameters
Map<String, Object> params = new HashMap<>();
params.put("userId", "12345");
GluonRouter.navigateTo(UserProfileView.class, params);

// Navigate back
GluonRouter.navigateBack();

// Add a route guard
GluonRouter.addGuard(SecureView.class, () -> {
    return AuthService.isAuthenticated();
});
```

### GluonLifecycleManager

`GluonLifecycleManager` manages application lifecycle events in Gluon applications. It integrates with Gluon's `LifecycleService` and provides the following features:

- Registration of lifecycle event handlers
- Mapping of lifecycle events to domain events
- Integration with the core framework's event system

Example usage:

```java
// Initialize the lifecycle manager
GluonLifecycleManager.initialize();

// Register a lifecycle event handler
GluonLifecycleManager.registerLifecycleHandler(LifecycleEvent.PAUSE, () -> {
    // Save application state
    StateStore.getInstance().set("appState", appState);
});

// Map a lifecycle event to a domain event
GluonLifecycleManager.mapLifecycleEventToDomainEvent(
    LifecycleEvent.RESUME, 
    new ApplicationResumedEvent()
);

// Register a domain event handler
GluonLifecycleManager.registerDomainEventHandler(
    ApplicationResumedEvent.class,
    event -> {
        // Refresh data
        dataService.refreshData();
    }
);
```

### GluonAPI

`GluonAPI` provides a centralized interface for interacting with the Gluon module. It simplifies common tasks and provides a consistent way to use the framework's features.

Example usage:

```java
// Initialize the Gluon module
GluonAPI.initialize(mobileApplication);

// Navigate to a view
GluonAPI.navigateTo(LoginView.class);

// Register an event listener
GluonAPI.registerEventListener(UserLoggedInEvent.class, event -> {
    // Update UI
    welcomeLabel.setText("Welcome, " + event.getUsername());
});

// Register a lifecycle handler
GluonAPI.registerLifecycleHandler(LifecycleEvent.PAUSE, () -> {
    // Save application state
    StateStore.getInstance().set("appState", appState);
});
```

## CLI Integration

The AtHomeFX CLI tool provides commands for generating Gluon components:

```bash
# Generate a complete Gluon feature
athomefx-cli generate gluon-feature com.example.login Login ./src/main/java
```

This will generate:
- LoginView.java
- LoginController.java
- LoginViewModel.java
- LoginView.fxml

## Testing

The Gluon module is fully unit and integration tested to ensure its reliability and correctness. The testing approach includes:

### Mock Implementations

The module includes mock implementations for testing without requiring the actual Gluon libraries:

- `MockView`: A mock implementation of Gluon's View class
- `MockGluonView`: A mock implementation of the GluonView class

### Unit Tests

Each component in the Gluon module has comprehensive unit tests:

- **GluonView**: Tests for view lifecycle, event publishing, and service injection
- **GluonRouter**: Tests for navigation, route guards, and history management
- **GluonLifecycleManager**: Tests for lifecycle event handling and domain event mapping
- **GluonEventBus**: Tests for event registration and publishing
- **GluonAppBarManager**: Tests for AppBar configuration
- **GluonViewFactory**: Tests for view registration and creation
- **GluonAPI**: Tests for all API methods

Example unit test:

```java
@Test
void shouldCallViewModelOnShowWhenViewIsShown() {
    // Arrange
    TestViewModel viewModel = mock(TestViewModel.class);
    TestGluonView view = new TestGluonView(viewModel);

    // Act
    view.onShown();

    // Assert
    verify(viewModel).onShow();
}
```

### Integration Tests

Integration tests verify the interaction between multiple components:

- **GluonViewIntegrationTest**: Tests the interaction between GluonView, GluonRouter, and GluonAPI
  - Service injection into view models
  - Lifecycle method calls (onShow, onHide)
  - Initialization of the Gluon module

Example integration test:

```java
@Test
void shouldInjectServicesIntoViewModel() {
    // Arrange
    TestViewModel viewModel = new TestViewModel();

    // Act
    ServiceLocator.injectServices(viewModel);

    // Assert
    assertSame(mockService, viewModel.getService());
}
```

### Testing Approach

The testing approach follows these principles:

1. **Isolation**: Each unit test focuses on a single component, mocking all dependencies
2. **Comprehensive Coverage**: Tests cover all public methods and edge cases
3. **Integration**: Integration tests verify the interaction between components
4. **Mock Dependencies**: External dependencies (like Gluon libraries) are mocked to enable testing without the actual libraries

### Running Tests

To run all tests for the Gluon module:

```bash
mvn test -pl gluon
```

To run a specific test class:

```bash
mvn test -pl gluon -Dtest=GluonViewTest
```

## Integration with Core Module

The Gluon module is designed to work seamlessly with the core module. It reuses core services, state management, and event handling while providing Gluon-specific adaptations.
