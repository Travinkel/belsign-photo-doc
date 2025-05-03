# Session Management Module

The Session Management module provides functionality for managing user sessions in the Backbone framework. It is designed to work seamlessly with the rest of the Backbone framework and provides a centralized way to manage user authentication and session state.

## Overview

The Session Management module consists of the following components:

- **SessionManager**: A singleton service that manages user sessions and provides access to the current user.

## Usage

### Initializing the SessionManager

The SessionManager should be initialized during application startup, typically in the application's main class or initialization code:

```java
// Get the AuthenticationService
AuthenticationService authService = new AuthenticationService();

// Initialize the SessionManager
SessionManager sessionManager = CoreAPI.initializeSessionManager(authService);
```

### Using the SessionManager

Once initialized, the SessionManager can be accessed through the CoreAPI:

```java
// Check if a user is logged in
if (CoreAPI.isLoggedIn()) {
    // Get the current user
    Optional<User> user = CoreAPI.getCurrentUser();
    user.ifPresent(u -> {
        System.out.println("User logged in: " + u.getUsername());
    });
} else {
    System.out.println("No user logged in");
}
```

### Logging In and Out

The SessionManager provides methods for logging in and out:

```java
// Log in
Optional<User> user = CoreAPI.login("username", "password");
if (user.isPresent()) {
    System.out.println("Login successful");
} else {
    System.out.println("Login failed");
}

// Log out
CoreAPI.logout();
```

## Integration with State Management

The Session Management module integrates with the State Management module to provide a reactive way to track the current user:

```java
// Define a type-safe key for the current user
StateKey<User> CURRENT_USER_KEY = StateKey.of("currentUser", User.class);

// Listen for changes to the current user
CoreAPI.listenToState(CURRENT_USER_KEY, this, user -> {
    if (user != null) {
        System.out.println("User logged in: " + user.getUsername());
    } else {
        System.out.println("User logged out");
    }
});

// Update the current user in the state store when logging in
CoreAPI.login("username", "password").ifPresent(user -> {
    CoreAPI.setState(CURRENT_USER_KEY, user);
});

// Clear the current user from the state store when logging out
CoreAPI.logout();
CoreAPI.setState(CURRENT_USER_KEY, null);
```

## Error Handling

The SessionManager provides robust error handling:

- If the SessionManager hasn't been initialized, methods that require it will throw an `IllegalStateException`.
- If authentication fails, the `login` method will return an empty `Optional`.

## Thread Safety

The SessionManager is thread-safe and can be accessed from multiple threads simultaneously.

## Testing

The SessionManager can be tested using the provided test classes:

- `CoreAPISessionTest`: Tests the session management functionality in the CoreAPI class.
- `SessionManagerTest`: Tests the SessionManager class directly.

## Best Practices

- Always initialize the SessionManager during application startup.
- Use the CoreAPI to access the SessionManager rather than accessing it directly.
- Handle authentication failures gracefully by checking if the Optional returned by `login` is empty.
- Use the State Management module to track the current user and react to changes.