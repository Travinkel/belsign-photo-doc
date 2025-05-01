# Backbone Framework Use Cases

This document demonstrates how a Gluon mobile app loads a view using Backbone's architecture, showcasing the framework's key features and design principles.

## Loading a View: ProfileView Example

The following example demonstrates how to load a `ProfileView` in a Gluon mobile application using the Backbone framework.

### 1. View Structure

```java
// ProfileView.java
package com.example.app.views.profile;

import com.belman.backbone.core.base.BaseView;
import com.belman.backbone.core.events.DomainEventPublisher;
import com.belman.backbone.core.events.ViewShownEvent;

public class ProfileView extends BaseView<ProfileViewModel> {
    
    public ProfileView() {
        super(); // This loads the FXML and creates the controller and view model
        setTitle("User Profile");
        setShowBackButton(true);
    }
    
    @Override
    public void onShow() {
        super.onShow();
        // Publish a domain event when the view is shown
        DomainEventPublisher.getInstance().publish(new ViewShownEvent("ProfileView"));
    }
}
```

```xml
<!-- ProfileView.fxml -->
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<View xmlns="http://javafx.com/javafx"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="com.example.app.views.profile.ProfileController">
    <VBox spacing="10" padding="20">
        <Label text="Profile Information" style="-fx-font-size: 18px; -fx-font-weight: bold;"/>
        <HBox spacing="10">
            <Label text="Username:"/>
            <Label fx:id="usernameLabel"/>
        </HBox>
        <HBox spacing="10">
            <Label text="Email:"/>
            <Label fx:id="emailLabel"/>
        </HBox>
        <Button fx:id="editButton" text="Edit Profile"/>
        <Button fx:id="logoutButton" text="Logout"/>
    </VBox>
</View>
```

### 2. Controller Implementation

```java
// ProfileController.java
package com.example.app.views.profile;

import com.belman.backbone.core.base.BaseController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ProfileController extends BaseController<ProfileViewModel> {
    
    @FXML
    private Label usernameLabel;
    
    @FXML
    private Label emailLabel;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button logoutButton;
    
    @Override
    public void initializeBinding() {
        // Bind UI elements to ViewModel properties
        usernameLabel.textProperty().bind(getViewModel().usernameProperty());
        emailLabel.textProperty().bind(getViewModel().emailProperty());
        
        // Bind button actions to ViewModel commands
        editButton.setOnAction(e -> getViewModel().editProfile());
        logoutButton.setOnAction(e -> getViewModel().logout());
    }
}
```

### 3. ViewModel with Service Injection

```java
// ProfileViewModel.java
package com.example.app.views.profile;

import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.di.Inject;
import com.belman.backbone.core.di.ServiceLocator;
import com.belman.backbone.core.navigation.Router;
import com.example.app.services.UserService;
import com.example.app.views.login.LoginView;
import com.example.app.views.profile.edit.EditProfileView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProfileViewModel extends BaseViewModel<ProfileViewModel> {
    
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    
    @Inject
    private UserService userService;
    
    public ProfileViewModel() {
        // ServiceLocator will inject the UserService
        ServiceLocator.injectServices(this);
    }
    
    @Override
    public void onShow() {
        super.onShow();
        // Load user data when the view is shown
        loadUserData();
    }
    
    private void loadUserData() {
        // Use the injected service to load user data
        userService.getCurrentUser().ifPresent(user -> {
            username.set(user.getUsername());
            email.set(user.getEmail());
        });
    }
    
    public void editProfile() {
        // Navigate to the edit profile view
        Router.navigateTo(EditProfileView.class);
    }
    
    public void logout() {
        // Log out the user and navigate to the login view
        userService.logout();
        Router.navigateTo(LoginView.class);
    }
    
    public StringProperty usernameProperty() {
        return username;
    }
    
    public StringProperty emailProperty() {
        return email;
    }
}
```

### 4. Service Implementation

```java
// UserService.java
package com.example.app.services;

import com.belman.backbone.core.base.BaseService;
import com.example.app.domain.User;

import java.util.Optional;

public class UserService extends BaseService {
    
    private User currentUser;
    
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Publish a domain event when the user changes
        publishEvent(new UserChangedEvent(user));
    }
    
    public void logout() {
        this.currentUser = null;
        // Publish a domain event when the user logs out
        publishEvent(new UserLoggedOutEvent());
    }
}
```

### 5. Navigating to the ProfileView

```java
// From another view or component
import com.belman.backbone.core.navigation.Router;
import com.example.app.views.profile.ProfileView;

// Navigate to the ProfileView
Router.navigateTo(ProfileView.class);

// Navigate with parameters
Map<String, Object> params = new HashMap<>();
params.put("userId", "12345");
Router.navigateTo(ProfileView.class, params);
```

## Key Framework Features Demonstrated

### Auto-wiring Views and ViewModels

The Backbone framework automatically wires Views and ViewModels using the ViewLoader:

1. When a `ProfileView` is instantiated, the `super()` call in the constructor triggers the loading process.
2. The ViewLoader uses naming conventions to find the corresponding FXML file (`ProfileView.fxml`).
3. The FXML loader creates the controller (`ProfileController`) specified in the FXML file.
4. The ViewLoader creates the ViewModel (`ProfileViewModel`) based on naming conventions.
5. The ViewLoader injects services into both the controller and ViewModel.
6. The controller's `initializeBinding()` method is called to set up bindings between the UI and ViewModel.

### Service Injection

Services are injected using the `@Inject` annotation and the ServiceLocator:

1. Services are registered with the ServiceLocator during application startup:
   ```java
   ServiceLocator.registerService(UserService.class, new UserService());
   ```

2. The `@Inject` annotation marks fields that should be injected:
   ```java
   @Inject
   private UserService userService;
   ```

3. The ServiceLocator injects services when requested:
   ```java
   ServiceLocator.injectServices(this);
   ```

### Domain Event Publishing

Domain events are published when significant events occur:

1. When a view is shown:
   ```java
   DomainEventPublisher.getInstance().publish(new ViewShownEvent("ProfileView"));
   ```

2. When domain state changes:
   ```java
   publishEvent(new UserChangedEvent(user));
   ```

3. Event handlers can be registered to respond to events:
   ```java
   DomainEventPublisher.getInstance().register(ViewShownEvent.class, event -> {
       System.out.println("View shown: " + event.getViewName());
   });
   ```

### Navigation

Navigation is handled using the Router:

1. Simple navigation:
   ```java
   Router.navigateTo(ProfileView.class);
   ```

2. Navigation with parameters:
   ```java
   Map<String, Object> params = new HashMap<>();
   params.put("userId", "12345");
   Router.navigateTo(ProfileView.class, params);
   ```

3. Back navigation:
   ```java
   Router.navigateBack();
   ```

## Alignment with Best Practices

### Gluon Mobile Best Practices

The Backbone framework aligns with Gluon Mobile best practices by:

1. **Using Gluon's View class**: Backbone's BaseView extends Gluon's View class, integrating seamlessly with Gluon Mobile.
2. **Supporting mobile-first design**: The framework is designed to work well on mobile devices, with appropriate UI components and navigation patterns.
3. **Providing lifecycle management**: The framework includes lifecycle hooks (onShow, onHide) that align with Gluon's view lifecycle.
4. **Integrating with Gluon's navigation**: The Router works with Gluon's MobileApplication to handle view switching.
5. **Supporting cross-platform development**: The framework works on all platforms supported by Gluon Mobile.

### Afterburner.fx Conventions

The Backbone framework follows Afterburner.fx conventions:

1. **Zero-configuration approach**: Components are auto-wired based on naming conventions, requiring minimal configuration.
2. **Naming-based component discovery**: Views, Controllers, and ViewModels are discovered based on consistent naming patterns.
3. **Dependency injection**: Services are injected into components using a simple, annotation-based approach.
4. **Lightweight design**: The framework is minimal and focused, avoiding unnecessary complexity.
5. **FXML integration**: The framework seamlessly integrates with FXML for UI definition.

### CERN's Clean Layering and Decoupling Principles

The Backbone framework implements CERN's clean layering and decoupling principles:

1. **Clear separation of concerns**:
   - Views are responsible for UI presentation
   - Controllers handle UI interaction
   - ViewModels manage application state and business logic
   - Services handle data access and external communication

2. **Explicit dependencies**: Dependencies are explicitly declared and injected, making them clear and testable.

3. **Domain-driven design**: The framework encourages a domain-driven approach with domain events and a clean domain model.

4. **Unidirectional data flow**: Data flows from the domain model through ViewModels to Views, creating a predictable application state.

5. **Testability**: Components are designed to be testable in isolation, with clear interfaces and dependencies.

## Conclusion

The Backbone framework provides a clean, structured approach to building Gluon Mobile applications. By following the patterns demonstrated in this document, developers can create maintainable, testable, and scalable applications that adhere to best practices from Gluon, Afterburner.fx, and CERN's clean architecture principles.