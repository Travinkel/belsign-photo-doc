# Session Management Design

## Overview

This document outlines the design for the session management components in the Belsign Photo Documentation application. The session management system is responsible for managing user sessions, authentication, and role-based navigation.

## Components

### SessionService

The `SessionService` interface defines the methods for session management:

```java
public interface SessionService {
    Optional<UserBusiness> login(String username, String password);
    void logout();
    Optional<UserBusiness> getCurrentUser();
    boolean isLoggedIn();
    void refreshSession();
}
```

The `DefaultSessionService` implementation uses the existing `SessionManager` to provide these services:

```java
public class DefaultSessionService implements SessionService {
    private final SessionManager sessionManager;
    
    public DefaultSessionService(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }
    
    @Override
    public Optional<UserBusiness> login(String username, String password) {
        return sessionManager.login(username, password);
    }
    
    @Override
    public void logout() {
        sessionManager.logout();
    }
    
    @Override
    public Optional<UserBusiness> getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
    
    @Override
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    @Override
    public void refreshSession() {
        // Implementation depends on session management requirements
    }
}
```

### SessionContext

The `SessionContext` interface defines the methods for managing the session state and providing session-related operations:

```java
public interface SessionContext {
    void setUser(UserBusiness user);
    Optional<UserBusiness> getUser();
    void setState(SessionState state);
    SessionState getState();
    void logEvent(String message);
    void navigateToUserHome();
    void navigateToLogin();
    void refreshSession();
    boolean isSessionValid();
}
```

The `DefaultSessionContext` implementation uses the `SessionService` and provides additional functionality:

```java
public class DefaultSessionContext implements SessionContext {
    private final SessionService sessionService;
    private SessionState currentState;
    private final Logger logger;
    
    public DefaultSessionContext(SessionService sessionService) {
        this.sessionService = sessionService;
        this.logger = LoggerFactory.getLogger(DefaultSessionContext.class);
        this.currentState = new LoggedOutState();
    }
    
    @Override
    public void setUser(UserBusiness user) {
        // Implementation
    }
    
    @Override
    public Optional<UserBusiness> getUser() {
        return sessionService.getCurrentUser();
    }
    
    @Override
    public void setState(SessionState state) {
        this.currentState = state;
    }
    
    @Override
    public SessionState getState() {
        return currentState;
    }
    
    @Override
    public void logEvent(String message) {
        logger.info(message);
    }
    
    @Override
    public void navigateToUserHome() {
        // Implementation depends on navigation requirements
    }
    
    @Override
    public void navigateToLogin() {
        // Implementation depends on navigation requirements
    }
    
    @Override
    public void refreshSession() {
        sessionService.refreshSession();
    }
    
    @Override
    public boolean isSessionValid() {
        return sessionService.isLoggedIn();
    }
}
```

### SessionState

The `SessionState` interface defines the behavior of different session states:

```java
public interface SessionState {
    void handle(SessionContext context);
    String getName();
}
```

Concrete implementations of `SessionState` include:

- `LoggedOutState`: The initial state when no user is logged in
- `LoggingInState`: Transitional state during login process
- `LoggedInState`: State when user is successfully authenticated
- `SessionExpiredState`: State when session has timed out
- `SessionRefreshingState`: State during session refresh

### Role-Based Navigation

Role-based navigation is implemented in the UI layer using the `SessionContext`:

```java
public class RoleBasedNavigationService {
    private final SessionContext sessionContext;
    private final Router router;
    
    public RoleBasedNavigationService(SessionContext sessionContext, Router router) {
        this.sessionContext = sessionContext;
        this.router = router;
    }
    
    public void navigateToUserHome() {
        Optional<UserBusiness> userOpt = sessionContext.getUser();
        if (userOpt.isPresent()) {
            UserBusiness user = userOpt.get();
            if (user.getRoles().contains(UserRole.ADMIN)) {
                router.navigateTo(UserManagementView.class);
            } else if (user.getRoles().contains(UserRole.QA)) {
                router.navigateTo(QADashboardView.class);
            } else if (user.getRoles().contains(UserRole.PRODUCTION)) {
                router.navigateTo(PhotoUploadView.class);
            } else {
                // Default to photo upload view
                router.navigateTo(PhotoUploadView.class);
            }
        } else {
            // If no user is logged in, navigate to login view
            navigateToLogin();
        }
    }
    
    public void navigateToLogin() {
        router.navigateTo(LoginView.class);
    }
}
```

## Integration with UI Layer

The UI layer uses the `SessionContext` to manage user sessions and navigation:

```java
public class LoginViewModel extends BaseViewModel<LoginViewModel> {
    private final SessionContext sessionContext;
    
    public LoginViewModel(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }
    
    public void login() {
        // Implementation
    }
    
    public void logout() {
        sessionContext.logout();
        sessionContext.navigateToLogin();
    }
}
```

## Implementation Considerations

When implementing the session management components, consider the following:

1. **Dependency Rules**: Ensure that the implementation follows the project's dependency rules:
   - UI layer can only access Service, Domain, and Common layers
   - Service layer can access UI, Repository, Domain, and Common layers
   - Repository layer can access Service, Domain, and Common layers
   - Domain layer can only access Common layer
   - Bootstrap layer can access all layers

2. **Package Structure**: Place interfaces and implementations in the appropriate packages:
   - Interfaces that need to be accessed by multiple layers should be in the Common layer
   - Implementations should be in their respective layers

3. **Singleton Pattern**: The `SessionManager` is implemented as a singleton. Consider whether the `SessionContext` should also be a singleton.

4. **Thread Safety**: Ensure that the session management components are thread-safe, especially if they are singletons.

5. **Error Handling**: Implement proper error handling for session-related operations.

6. **Logging**: Use appropriate logging for session-related events.

7. **Testing**: Write unit tests for the session management components.

## Conclusion

The session management components provide a flexible and extensible way to manage user sessions and navigation in the Belsign Photo Documentation application. By following the design outlined in this document, the application can provide a consistent and reliable user experience.