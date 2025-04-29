# AtHomeFX Framework Restructuring

## Overview

This document outlines the recommended restructuring of the AtHomeFX framework to align with CERN best practices and Gluon requirements. The restructuring focuses on simplifying the framework, improving performance, and ensuring compatibility with cross-platform deployment using Gluon.

## Current Issues

1. **UI Folder Structure**: The current structure has a separate UI folder with sealed interfaces for Scene and Stage, which adds an unnecessary layer of abstraction and complicates the framework.
2. **Multiple Scenes**: The use of multiple Scene instances can cause UI flicker and stuttering, especially on mobile devices.
3. **Gluon Compatibility**: The current structure may not be fully compatible with Gluon's approach to cross-platform deployment.
4. **AOP Location**: The current location of AOP (Aspect-Oriented Programming) components may not be optimal.

## Recommended Changes

### 1. Remove UI Folder from Core Framework

The UI folder in the core framework should be removed, and its contents should be moved to an examples package. The sealed interfaces (Scene, Stage) and their implementations are not part of the core framework but are examples of how to use JavaFX's Scene and Stage classes.

**Current Structure**:
```
framework/
  athomefx/
    ui/
      Scene.java
      Stage.java
      LoginScene.java
      PhotoScene.java
      AdminScene.java
      DesktopStage.java
      IPadStage.java
      SmartPhoneStage.java
```

**Recommended Structure**:
```
framework/
  athomefx/
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

### 2. Use Single Scene Model

The Router class already implements a single Scene model with a central root container (StackPane), which aligns with CERN's best practices. This approach should be maintained and emphasized.

```java
public class Router {
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
    
    public static void navigateTo(Class<? extends BaseView<?>> viewClass) {
        // ...
        
        // Set the new view as the content of the root pane
        rootPane.getChildren().setAll(view.getRoot());
        
        // ...
    }
}
```

### 3. Ensure Gluon Compatibility

To ensure compatibility with Gluon for cross-platform deployment, the framework should:

1. Use a single Scene model (already implemented)
2. Avoid platform-specific code in the core framework
3. Use configuration rather than runtime branching for platform-specific behavior
4. Ensure that all UI operations run on the JavaFX Application Thread

### 4. Keep AOP in the Framework

AOP is appropriate for cross-cutting concerns like event publishing and should remain in the framework. The current implementation in the `aop` package is appropriate.

```
framework/
  athomefx/
    aop/
      DomainEventPublisherAspect.java
```

## Benefits of Restructuring

1. **Simplified Framework**: Removing the unnecessary UI folder simplifies the framework and makes it easier to understand and use.
2. **Improved Performance**: Using a single Scene model improves performance, reduces resource usage, and eliminates UI flicker and stuttering.
3. **Better Gluon Compatibility**: The restructured framework will be more compatible with Gluon's approach to cross-platform deployment.
4. **Clearer Separation of Concerns**: Moving example implementations to a separate package makes it clear what is part of the core framework and what is not.

## Implementation Steps

1. Move the sealed interfaces and their implementations from the UI folder to the examples/ui folder.
2. Update any references to these classes in the examples and tests.
3. Ensure that the Router class continues to use the single Scene model.
4. Update documentation to reflect the changes.
5. Test the changes to ensure they work correctly.

## Conclusion

By restructuring the AtHomeFX framework as recommended, we can align it with CERN best practices and Gluon requirements, resulting in a simpler, more performant, and more maintainable framework that is well-suited for cross-platform deployment.