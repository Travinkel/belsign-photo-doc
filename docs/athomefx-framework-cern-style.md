# AtHomeFX Framework: CERN-Style JavaFX Architecture

## Overview

AtHomeFX has been enhanced to align with CERN's proven JavaFX best practices, focusing on simplicity, performance, and lifecycle clarity. This document outlines the key architectural principles and implementation details of the CERN-style JavaFX architecture in AtHomeFX.

## Core Principles

### Single Stage, Single Scene

- Only one JavaFX Stage and Scene exist throughout the application lifecycle.
- All navigation and UI switching is done by changing the content of a central root container (StackPane).
- This approach improves performance, reduces resource usage, and simplifies responsive scaling.

### Decoupled Logic and View

- Views are purely visual and do not contain business logic.
- All user interaction and state belongs in ViewModels.
- The UI is a function of the application state.

### Explicit Navigation and Lifecycle

- Navigation is controlled by explicitly changing the content in the root StackPane.
- This enables full lifecycle control (onShow, onHide) with zero JavaFX glitches.
- View lifecycle hooks are called manually when views are swapped.

### State-Driven UI

- The UI always reflects application state.
- State is never inferred from the scene graph.
- The StateStore provides a centralized store for managing application state.

### Framework Structure

- The framework follows a clean architecture approach with clear separation of concerns.
- The UI components (View, Controller, ViewModel) are in the core package, not in a separate UI package.
- Example implementations and utilities are kept separate from the core framework.
- AOP (Aspect-Oriented Programming) is used for cross-cutting concerns like event publishing.

## Implementation Details

### Router

The Router class has been updated to implement the single Scene model with a central root container:

```java
public class Router {
    // ...
    private static StackPane rootPane;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;

        // Initialize the root pane if it doesn't exist
        if (rootPane == null) {
            rootPane = new StackPane();
            Scene scene = new Scene(rootPane);
            primaryStage.setScene(scene);
        }
    }

    public static void navigateTo(Class<? extends BaseView<?>> viewClass, Map<String, Object> parameters) {
        // ...

        // Hide the current view
        if (currentView != null) {
            currentView.onHide();
        }

        // Set the new view as the content of the root pane
        rootPane.getChildren().setAll(view.getRoot());
        primaryStage.show();

        // Show the new view
        view.onShow();
        currentView = view;

        // ...
    }

    // ...
}
```

### View Lifecycle

The ViewLifecycle interface provides hooks for view lifecycle events:

```java
public interface ViewLifecycle {
    default void onShow() {}
    default void onHide() {}
}
```

These hooks are called by the Router when views are swapped:

1. `onHide()` is called on the current view before it is removed from the scene.
2. `onShow()` is called on the new view after it is added to the scene.

### State Management

The StateStore provides a centralized store for managing application state:

```java
public class StateStore {
    // ...

    public <T> void set(String key, T value) {
        // ...
    }

    public <T> T get(String key) {
        // ...
    }

    public <T> ObjectProperty<T> getProperty(String key) {
        // ...
    }

    public <T> void listen(String key, Object owner, Consumer<T> listener) {
        // ...
    }

    // ...
}
```

ViewModels observe the state and update the UI accordingly, ensuring that the UI is always a function of the application state.

## Benefits

### Performance

- Reduced memory usage by maintaining only one Scene.
- Smoother transitions between views.
- Better performance on mobile devices with limited resources.

### Simplicity

- Clearer navigation flow.
- Explicit lifecycle management.
- Simplified responsive scaling.

### Maintainability

- Clear separation of concerns.
- State-driven UI makes debugging easier.
- Consistent architecture across the application.

## Best Practices

1. **Use the Router for Navigation**: Always use the Router to navigate between views.
2. **Implement Lifecycle Hooks**: Implement onShow() and onHide() for proper resource management.
3. **Use StateStore for State Management**: Use the StateStore for global state management.
4. **Keep Views Simple**: Views should only contain UI elements and minimal code.
5. **Use ViewModels for Logic**: Business logic should be in the ViewModel, not the View or Controller.
6. **Run UI Operations on the JavaFX Thread**: Use Platform.runLater() for UI operations from background threads.

## Conclusion

By aligning with CERN's JavaFX best practices, AtHomeFX provides a robust, performant, and maintainable framework for building JavaFX applications. The single Scene model with a central root container, explicit lifecycle management, and state-driven UI ensure that applications built with AtHomeFX are responsive, efficient, and easy to maintain.
