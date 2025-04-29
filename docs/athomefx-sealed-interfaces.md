# AtHomeFX Sealed Interfaces

This document explains the sealed interfaces used in the AtHomeFX framework and their implementations.

## What are Sealed Interfaces?

Sealed interfaces are a feature introduced in Java 17 that allows you to restrict which classes can implement an interface. This provides a form of controlled extensibility, where you can specify exactly which classes are allowed to implement an interface.

The main benefits of sealed interfaces are:

1. **Type Safety**: You can ensure that only specific classes implement an interface, which can help prevent errors.
2. **Pattern Matching**: Sealed interfaces work well with pattern matching, allowing for more concise and safer code.
3. **Documentation**: Sealed interfaces make it clear which implementations are available, improving code readability.
4. **Controlled Evolution**: You can evolve your API in a controlled way, ensuring that all implementations are known and can be updated together.

## Stage Interface

The `Stage` interface is a sealed interface that represents a window in the application. It is implemented by three classes:

- `DesktopStage`: Represents a window on a desktop platform.
- `IPadStage`: Represents a window on an iPad platform.
- `SmartPhoneStage`: Represents a window on a smartphone platform.

### Example Usage

```java
// Create a stage for the current platform
Stage stage;
if (PlatformDetector.isDesktop()) {
    stage = new DesktopStage(new javafx.stage.Stage());
} else if (PlatformDetector.isIPad()) {
    stage = new IPadStage(new javafx.stage.Stage());
} else if (PlatformDetector.isSmartPhone()) {
    stage = new SmartPhoneStage(new javafx.stage.Stage());
} else {
    throw new UnsupportedOperationException("Unsupported platform");
}

// Use the stage
stage.setTitle("My Application");
stage.show();
```

### Pattern Matching

With pattern matching, you can handle different stage types more elegantly:

```java
void configureStage(Stage stage) {
    switch (stage) {
        case DesktopStage desktopStage -> {
            // Configure desktop-specific settings
            desktopStage.setWidth(1280);
            desktopStage.setHeight(720);
        }
        case IPadStage iPadStage -> {
            // iPad-specific settings are handled automatically
            System.out.println("Using iPad stage with dimensions: " + 
                iPadStage.getDefaultWidth() + "x" + iPadStage.getDefaultHeight());
        }
        case SmartPhoneStage smartPhoneStage -> {
            // Smartphone-specific settings are handled automatically
            System.out.println("Using smartphone stage with dimensions: " + 
                smartPhoneStage.getDefaultWidth() + "x" + smartPhoneStage.getDefaultHeight());
        }
    }
}
```

## Scene Interface

The `Scene` interface is a sealed interface that represents a scene in the application. It is implemented by three classes:

- `LoginScene`: Represents a login screen.
- `PhotoScene`: Represents a photo screen.
- `AdminScene`: Represents an admin screen.

### Example Usage

```java
// Create a scene for the login screen
Parent loginRoot = FXMLLoader.load(getClass().getResource("login.fxml"));
Scene loginScene = new LoginScene(loginRoot);

// Set the scene on the stage
stage.setScene(loginScene);
```

### Pattern Matching

With pattern matching, you can handle different scene types more elegantly:

```java
void configureScene(Scene scene) {
    switch (scene) {
        case LoginScene loginScene -> {
            // Configure login-specific settings
            System.out.println("Configuring login scene");
        }
        case PhotoScene photoScene -> {
            // Configure photo-specific settings
            System.out.println("Configuring photo scene");
            // Use photo-specific methods
            photoScene.takePhoto();
        }
        case AdminScene adminScene -> {
            // Configure admin-specific settings
            System.out.println("Configuring admin scene");
            // Use admin-specific methods
            adminScene.addUser("admin", "ADMIN");
        }
    }
}
```

## Benefits of Using Sealed Interfaces in AtHomeFX

1. **Platform-Specific Behavior**: The sealed `Stage` interface allows us to provide platform-specific behavior while maintaining a common interface.
2. **Screen-Specific Functionality**: The sealed `Scene` interface allows us to provide screen-specific functionality while maintaining a common interface.
3. **Type Safety**: We can ensure that only specific implementations of `Stage` and `Scene` are used, preventing errors.
4. **Pattern Matching**: We can use pattern matching to handle different stage and scene types more elegantly.
5. **Documentation**: The sealed interfaces make it clear which implementations are available, improving code readability.
6. **Controlled Evolution**: We can evolve the framework in a controlled way, ensuring that all implementations are known and can be updated together.

## Conclusion

Sealed interfaces are a powerful feature that allows us to provide controlled extensibility in the AtHomeFX framework. By using sealed interfaces for `Stage` and `Scene`, we can provide platform-specific and screen-specific behavior while maintaining a common interface, improving type safety, and enabling pattern matching.