# Architectural Violations in BelSign Photo Documentation Module

## Overview

This document identifies architectural violations in the BelSign Photo Documentation Module and proposes solutions for fixing them. The violations were identified using ArchUnit tests that check for adherence to clean architecture principles, Domain-Driven Design (DDD) principles, and proper layer separation.

## Clean Architecture Principles

The BelSign Photo Documentation Module follows a clean architecture approach, also known as onion architecture. The key principles are:

1. **Dependency Rule**: Dependencies should only point inward. Inner layers should not know about outer layers.
2. **Domain Independence**: The domain layer should be independent of frameworks, UI, and infrastructure concerns.
3. **Separation of Concerns**: Each layer has a specific responsibility and should not take on responsibilities of other layers.

## Identified Violations

### 1. Application Layer Accessing Presentation Layer

The application layer should not depend on the presentation layer, as this violates the dependency rule of clean architecture. However, several classes in the application layer are directly accessing classes in the presentation layer:

#### NavigateCommand (com.belman.application.commands.ui)

This class imports and uses:
- `com.belman.presentation.navigation.Router`
- `com.belman.presentation.core.TransitionPresets`
- `com.belman.presentation.core.ViewTransition`

#### GluonLifecycleManager (com.belman.application.core)

This class imports and uses:
- `com.belman.presentation.core.BaseController`
- `com.belman.presentation.core.BaseView`
- `com.belman.presentation.core.BaseViewModel`

### 2. Presentation Layer Depending on Infrastructure Implementations

The presentation layer should depend on abstractions (interfaces) defined in the domain or application layer, not on concrete implementations in the infrastructure layer. However, several classes in the presentation layer directly depend on infrastructure implementations:

#### PhotoUploadViewController (com.belman.presentation.views.photoupload)

This class directly calls:
- `com.belman.infrastructure.camera.CameraServiceFactory.getCameraService()`

#### Multiple View Models and Services

Many classes in the presentation layer depend on:
- `com.belman.infrastructure.logging.EmojiLogger`
- `com.belman.infrastructure.service.SessionManager`
- `com.belman.infrastructure.platform.PlatformUtils`

### 3. Domain Events in Wrong Package (FIXED)

Domain events should be in the domain.events package, but many event classes were in the domain.shared package. This has been fixed by:

1. Creating new event classes in the domain.events package:
   - `com.belman.domain.events.DomainEvent` (interface)
   - `com.belman.domain.events.DomainEventHandler` (interface)
   - `com.belman.domain.events.AbstractDomainEvent` (class)
   - `com.belman.domain.events.ViewShownEvent` (class)
   - `com.belman.domain.events.ViewHiddenEvent` (class)
   - `com.belman.domain.events.ApplicationStateEvent` (class)
   - `com.belman.domain.events.ApplicationStartedEvent` (class)
   - `com.belman.domain.events.ApplicationPausedEvent` (class)
   - `com.belman.domain.events.ApplicationResumedEvent` (class)
   - `com.belman.domain.events.ApplicationBackgroundedEvent` (class)
   - `com.belman.domain.events.ApplicationStoppedEvent` (class)
   - `com.belman.domain.events.CommandEvent` (class)
   - `com.belman.domain.events.CommandExecutedEvent` (class)
   - `com.belman.domain.events.CommandRedoneEvent` (class)
   - `com.belman.domain.events.CommandUndoneEvent` (class)

2. Updating the existing classes in the domain.shared package to extend or implement the new classes:
   - `com.belman.domain.shared.DomainEvent` now extends `com.belman.domain.events.DomainEvent`
   - `com.belman.domain.shared.DomainEventHandler` now extends `com.belman.domain.events.DomainEventHandler`
   - `com.belman.domain.shared.AbstractDomainEvent` now extends `com.belman.domain.events.AbstractDomainEvent`
   - `com.belman.domain.shared.ViewShownEvent` now extends `com.belman.domain.events.ViewShownEvent`
   - `com.belman.domain.shared.ViewHiddenEvent` now extends `com.belman.domain.events.ViewHiddenEvent`
   - `com.belman.domain.shared.ApplicationStateEvent` now extends `com.belman.domain.events.ApplicationStateEvent`
   - `com.belman.domain.shared.ApplicationStartedEvent` now extends `com.belman.domain.events.ApplicationStartedEvent`
   - `com.belman.domain.shared.ApplicationPausedEvent` now extends `com.belman.domain.events.ApplicationPausedEvent`
   - `com.belman.domain.shared.ApplicationResumedEvent` now extends `com.belman.domain.events.ApplicationResumedEvent`
   - `com.belman.domain.shared.ApplicationBackgroundedEvent` now extends `com.belman.domain.events.ApplicationBackgroundedEvent`
   - `com.belman.domain.shared.ApplicationStoppedEvent` now extends `com.belman.domain.events.ApplicationStoppedEvent`
   - `com.belman.domain.shared.CommandEvent` now extends `com.belman.domain.events.CommandEvent`
   - `com.belman.domain.shared.CommandExecutedEvent` now extends `com.belman.domain.events.CommandExecutedEvent`
   - `com.belman.domain.shared.CommandRedoneEvent` now extends `com.belman.domain.events.CommandRedoneEvent`
   - `com.belman.domain.shared.CommandUndoneEvent` now extends `com.belman.domain.events.CommandUndoneEvent`

3. Marking all classes in the domain.shared package as deprecated with `@Deprecated` annotation and adding Javadoc comments to direct users to the new classes.

4. Updating references to these classes in other parts of the codebase to use the new classes from domain.events package.

### 4. Domain Events Not Immutable

Domain events should be immutable, but several event-related classes have non-final fields:

- `com.belman.domain.events.DomainEventPublisher.instance`
- `com.belman.domain.events.DomainEventPublisher.logger`
- `com.belman.domain.events.DomainEvents.logger`

### 5. Missing Aggregate Roots

The codebase doesn't have any classes with names ending in 'Aggregate' or 'Root', which suggests that the DDD concept of aggregate roots is not being properly implemented.

### 6. Views with Non-Final Fields

Views should be simple and have only final fields, but BaseView has non-final fields:

- `com.belman.presentation.core.BaseView.loadingIndicator`
- `com.belman.presentation.core.BaseView.loadingIndicatorAdded`

## Proposed Solutions

### 1. Fix Application Layer Accessing Presentation Layer

#### Create Interfaces in the Application Layer

Create interfaces in the application layer that define the functionality needed from the presentation layer:

```java
// In application layer
public interface NavigationService {
    void navigateTo(Class<?> viewClass);
    void navigateTo(Class<?> viewClass, Map<String, Object> parameters);
    Stack<Class<?>> getNavigationHistory();
    void clearNavigationHistory();
}

public interface ViewLifecycleService {
    void registerView(Object view);
    void unregisterView(Object view);
}
```

#### Implement Interfaces in the Presentation Layer

Implement these interfaces in the presentation layer:

```java
// In presentation layer
public class RouterNavigationService implements NavigationService {
    @Override
    public void navigateTo(Class<?> viewClass) {
        Router.navigateTo(viewClass);
    }

    // Other methods...
}

public class GluonViewLifecycleService implements ViewLifecycleService {
    @Override
    public void registerView(Object view) {
        // Implementation...
    }

    // Other methods...
}
```

#### Use Dependency Injection

Use dependency injection to provide the presentation layer implementations to the application layer:

```java
// In application layer
public class NavigateCommand implements Command<Void> {
    private final NavigationService navigationService;

    public NavigateCommand(Class<?> targetViewClass, NavigationService navigationService) {
        this.targetViewClass = targetViewClass;
        this.navigationService = navigationService;
    }

    @Override
    public CompletableFuture<Void> execute() {
        navigationService.navigateTo(targetViewClass);
        return CompletableFuture.completedFuture(null);
    }
}
```

### 2. Fix Presentation Layer Depending on Infrastructure Implementations

#### Create Interfaces in the Domain Layer

Create interfaces in the domain layer that define the functionality needed from the infrastructure layer:

```java
// In domain layer
public interface LoggerFactory {
    Logger getLogger(Class<?> clazz);
}

public interface CameraServiceProvider {
    CameraService getCameraService();
}
```

#### Use Dependency Injection

Use dependency injection to provide the infrastructure implementations to the presentation layer:

```java
// In presentation layer
public class PhotoUploadViewController {
    private final CameraServiceProvider cameraServiceProvider;

    public PhotoUploadViewController(CameraServiceProvider cameraServiceProvider) {
        this.cameraServiceProvider = cameraServiceProvider;
    }

    private CameraService getCameraService() {
        return cameraServiceProvider.getCameraService();
    }
}
```

### 3. Fix Domain Events Package Structure

Move all domain event classes from the domain.shared package to the domain.events package:

```java
// Move from domain.shared to domain.events
package com.belman.domain.events;

public interface DomainEvent {
    // ...
}
```

### 4. Make Domain Events Immutable

Make all fields in domain event classes final:

```java
public class DomainEventPublisher {
    private static final DomainEventPublisher instance = new DomainEventPublisher();
    private final Logger logger;

    // ...
}
```

### 5. Implement Proper Aggregate Roots

Identify the aggregates in the domain and implement them as proper aggregate roots:

```java
package com.belman.domain.aggregates;

public class OrderAggregate {
    private final OrderId id;
    private final List<PhotoDocument> photoDocuments;

    // ...

    public void addPhotoDocument(PhotoDocument document) {
        // Business rules for adding photos
        photoDocuments.add(document);
    }
}
```

### 6. Make Views Simpler

Make all fields in view classes final:

```java
public class BaseView<T extends BaseViewModel<T>> extends View {
    private final ProgressIndicator loadingIndicator;
    private final boolean loadingIndicatorAdded;

    // ...
}
```

## Conclusion

The BelSign Photo Documentation Module has several architectural violations that need to be addressed to fully adhere to clean architecture and DDD principles. The most critical violations are in the application layer, which should not depend on the presentation layer, and in the domain layer, where events are not properly organized and not immutable.

Fixing these violations will require a significant refactoring effort, but the result will be a more maintainable, testable, and flexible codebase.
