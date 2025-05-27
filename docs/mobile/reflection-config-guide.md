# Reflection Configuration for Mobile Builds

This document explains the reflection configuration for GraalVM native image builds, which is essential for the application to run correctly on mobile devices.

## Overview

GraalVM native image compilation requires explicit configuration for reflection, as it performs static analysis at build time to determine which classes, methods, and fields are used. Any reflection used at runtime must be explicitly declared in the configuration.

## Configuration Methods

There are two ways to configure reflection for GraalVM native image builds in this project:

1. **GluonFX Maven Plugin Configuration**: The `reflectionList` element in the `gluonfx-maven-plugin` configuration in the `pom.xml` file.
2. **reflect-config.json**: A JSON configuration file in the `src/main/resources/META-INF/native-image` directory.

Both configurations contain the same list of classes that use reflection.

## Configured Classes

The following classes have been configured for reflection:

### View Controllers
- `com.belman.presentation.views.splash.SplashViewController`
- `com.belman.presentation.views.login.LoginViewController`
- `com.belman.presentation.views.worker.WorkerViewController`

### Classes using Class.forName()
- `com.belman.bootstrap.hacks.GluonInternalClassesFix`
- `com.belman.bootstrap.config.RepositoryInitializer`
- `com.belman.presentation.base.BaseView`
- `com.belman.presentation.core.ViewLoader`

### Classes using reflection for field access
- `com.belman.bootstrap.di.ServiceLocator`
- `com.belman.bootstrap.platform.StorageServiceOverride`

### Classes using reflection for method access
- `com.belman.dataaccess.persistence.sql.SqlUserRepository`
- `com.belman.application.usecase.photo.GluonCameraService`

### External classes using reflection
- `org.apache.pdfbox.pdmodel.font.PDType1Font`

## Maintaining the Configuration

When adding new classes that use reflection, you should update both configuration methods:

1. Add the class to the `reflectionList` element in the `pom.xml` file.
2. Add the class to the `reflect-config.json` file.

## Common Reflection Patterns to Look For

When identifying classes that use reflection, look for the following patterns:

- `Class.forName()`
- `getDeclaredFields()`, `getFields()`
- `getDeclaredMethods()`, `getMethods()`
- `getDeclaredConstructor()`, `getConstructor()`
- `newInstance()`
- `getMethod()`, `getField()`

## Testing the Configuration

To test if the reflection configuration is working correctly, build the application for Android and run it on a mobile device or emulator:

```bash
mvn -Pandroid-debug gluonfx:build
mvn -Pandroid-debug gluonfx:package
mvn -Pandroid-debug gluonfx:install
mvn -Pandroid-debug gluonfx:run
```

If the application crashes with a `ClassNotFoundException` or similar reflection-related error, you may need to add more classes to the reflection configuration.