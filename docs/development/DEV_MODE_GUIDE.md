# Development Mode Guide

## Overview

The BelSign Photo Documentation application supports a special development mode that enables additional features and tools for development purposes. This guide explains how to use development mode and the features it provides.

## Enabling Development Mode

Development mode can be enabled in two ways:

1. **Command-line argument**: Start the application with the `--dev` flag:
   ```
   java -jar belsign-photo-doc.jar --dev
   ```

2. **System property**: Set the `belsign.devMode` system property to `true`:
   ```
   java -Dbelsign.devMode=true -jar belsign-photo-doc.jar
   ```

## Features

When development mode is enabled, the following features are available:

### 1. Hybrid Repository Configuration

Development mode uses a hybrid repository configuration:

- **SQL repositories** for persistent data (users, orders)
- **In-memory repositories** for transient data or development features

This allows you to work with real data from the database while still having the flexibility of in-memory repositories for development.

### 2. Enhanced Logging

Development mode enables more detailed logging:

- Detailed service registration and injection logging
- Logging of view model service injections
- State of critical services during application startup
- More verbose error messages

### 3. Test Data Seeding

Development mode automatically seeds test data if repositories are empty, making it easier to test the application without having to manually create data.

## Repository Configuration

The application supports three repository configurations:

1. **Production Mode (SQL)**: Uses SQL repositories for all data. This is the default mode for production.

2. **Development Mode (Hybrid)**: Uses SQL repositories for persistent data (users, orders) and in-memory repositories for transient data. This is enabled with the `--dev` flag.

3. **Fallback Mode (In-Memory)**: Uses in-memory repositories for all data. This is used when the database is not available.

## Troubleshooting

### Authentication Issues

1. **Navigation Service Null**: If you see an error like "Cannot invoke navigateToUserHome() because navigationService is null", make sure the `RoleBasedNavigationService` is properly registered with the `ServiceRegistry`.

2. **Session Context Issues**: If user sessions are not being maintained, check that the `SimpleSessionContext` is properly storing the user and not just relying on the `AuthenticationService`.

### Navigation Issues

1. **Role-Based Navigation**: If navigation to role-specific views is not working, check that the `RoleBasedNavigationService` is properly configured and that the user has the correct roles.

2. **Session Context Null**: If you see an error related to null `SessionContext`, make sure the `SessionContext` is properly registered with the `ServiceRegistry`.

### Repository Issues

1. **Database Connection**: If the application falls back to in-memory repositories, check that the database connection is properly configured.

2. **Dev Mode Repository Configuration**: If you're using development mode but not seeing the expected repository configuration, check that development mode is properly enabled.

## Best Practices

1. **Use Dev Mode for Development**: Always use development mode during development to take advantage of the enhanced logging and hybrid repository configuration.

2. **Check Logs**: When troubleshooting, check the logs for detailed information about service registration, injection, and errors.

3. **Test with Real Data**: Use the hybrid repository configuration in development mode to test with real data from the database.