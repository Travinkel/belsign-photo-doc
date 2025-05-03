# State Management Module

The State Management module provides a centralized store for managing application state with reactive properties. It supports both simple and complex state values, nested properties, and validation.

## Overview

The State Management module consists of the following components:

- **StateStore**: A singleton service that manages application state.
- **StateKey**: A type-safe key for the state store.
- **Property**: A platform-agnostic property class that can be used to store values and notify listeners when the value changes.
- **NestedProperty**: A property that supports nested properties for complex objects.
- **StateSchema**: A schema for validating state values.
- **ValidationResult**: Represents the result of a validation operation.
- **CommonStateKeys**: Predefined state keys for common application state.

## Usage

### Basic State Management

```java
// Set a state value
CoreAPI.setState("username", "john.doe");

// Get a state value
String username = CoreAPI.getState("username");

// Update a state value
CoreAPI.updateState("counter", (Integer count) -> count + 1);

// Listen for state changes
CoreAPI.listenToState("username", this, username -> {
    updateUI(username);
});

// Stop listening for state changes
CoreAPI.unlistenToState("username", this);
```

### Type-Safe State Management

```java
// Create a type-safe key
StateKey<User> CURRENT_USER = StateKey.of("currentUser", User.class);

// Set a state value
CoreAPI.setState(CURRENT_USER, user);

// Get a state value
User user = CoreAPI.getState(CURRENT_USER);

// Update a state value
CoreAPI.updateState(CURRENT_USER, user -> {
    user.setLastLoginTime(System.currentTimeMillis());
    return user;
});

// Listen for state changes
CoreAPI.listenToState(CURRENT_USER, this, user -> {
    updateUI(user);
});

// Stop listening for state changes
CoreAPI.unlistenToState(CURRENT_USER, this);
```

### Predefined State Keys

The `CommonStateKeys` class provides predefined state keys for common application state:

```java
// User and Authentication
CoreAPI.setState(CommonStateKeys.CURRENT_USER, user);
CoreAPI.setState(CommonStateKeys.AUTH_TOKEN, "jwt-token");
CoreAPI.setState(CommonStateKeys.IS_LOGGED_IN, true);

// Navigation
CoreAPI.setState(CommonStateKeys.CURRENT_VIEW, LoginView.class);
CoreAPI.setState(CommonStateKeys.ROUTE_PARAMETERS, parameters);

// Application State
CoreAPI.setState(CommonStateKeys.THEME, "dark");
CoreAPI.setState(CommonStateKeys.LANGUAGE, Locale.US);

// UI State
CoreAPI.setState(CommonStateKeys.IS_LOADING, true);
CoreAPI.setState(CommonStateKeys.ERROR_MESSAGE, "Invalid credentials");
CoreAPI.setState(CommonStateKeys.SUCCESS_MESSAGE, "Login successful");
```

### Nested Properties

The `NestedProperty` class allows for accessing and updating nested properties of complex objects using dot notation:

```java
// Get a nested property
NestedProperty<User> userProperty = CoreAPI.getNestedProperty(CommonStateKeys.CURRENT_USER);

// Get a nested value
String city = userProperty.getNestedValue("address.city");

// Set a nested value
userProperty.setNestedValue("address.city", "New York");

// Add a listener for a nested property
userProperty.addNestedListener("address.city", city -> {
    updateCityUI(city);
});

// Shorthand methods
String city = CoreAPI.getNestedValue(CommonStateKeys.CURRENT_USER, "address.city");
CoreAPI.setNestedValue(CommonStateKeys.CURRENT_USER, "address.city", "New York");
CoreAPI.listenToNestedState(CommonStateKeys.CURRENT_USER, "address.city", this, city -> {
    updateCityUI(city);
});
```

### Validation

The `StateSchema` class allows for defining validation rules for state values:

```java
// Create a schema for a string
StateSchema<String> usernameSchema = StateSchema.builder(String.class)
    .required()
    .addRule(s -> s.length() >= 3, "Username must be at least 3 characters long")
    .addRule(s -> s.length() <= 20, "Username must be at most 20 characters long")
    .addRule(s -> s.matches("[a-zA-Z0-9_]+"), "Username must contain only letters, numbers, and underscores")
    .build();

// Create a schema for a complex object
StateSchema<String> citySchema = StateSchema.builder(String.class)
    .required()
    .addRule(s -> s.length() >= 2, "City name must be at least 2 characters long")
    .build();

StateSchema<Address> addressSchema = StateSchema.builder(Address.class)
    .required()
    .addNestedSchema("city", citySchema)
    .build();

StateSchema<User> userSchema = StateSchema.builder(User.class)
    .required()
    .addNestedSchema("address", addressSchema)
    .addRule(u -> u.getUsername() != null, "User must have a username")
    .build();

// Register the schema
CoreAPI.registerSchema(CommonStateKeys.CURRENT_USER, userSchema);

// Validate a state value
ValidationResult result = CoreAPI.validate(CommonStateKeys.CURRENT_USER);
if (result.isValid()) {
    // State value is valid
} else {
    // State value is invalid
    for (String error : result.getErrors()) {
        System.out.println(error);
    }
}

// Validate all state values
Map<String, ValidationResult> results = CoreAPI.validateAll();
for (Map.Entry<String, ValidationResult> entry : results.entrySet()) {
    if (!entry.getValue().isValid()) {
        System.out.println("Invalid state: " + entry.getKey());
        for (String error : entry.getValue().getErrors()) {
            System.out.println("  " + error);
        }
    }
}
```

## Best Practices

- Use type-safe keys whenever possible to catch type errors at compile time.
- Use predefined keys from `CommonStateKeys` for common application state.
- Use nested properties for complex objects to avoid having to manually update the entire object.
- Define validation schemas for state values to ensure they conform to expected formats.
- Unregister listeners when they are no longer needed to avoid memory leaks.
- Use the `updateState` method for atomic updates to state values.