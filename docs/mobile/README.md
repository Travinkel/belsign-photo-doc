# BelSign Mobile Development Guide

## Overview

This guide provides information on developing and testing the BelSign Photo Documentation Module on mobile devices. The application is built using JavaFX and Gluon Mobile, allowing it to run on both Android and iOS devices from a single codebase.

## Architecture

The BelSign application follows a clean architecture approach with the following layers:

- **Presentation Layer**: UI components, views, and controllers
- **Application Layer**: Application services and use cases
- **Domain Layer**: Business logic, entities, and value objects
- **Infrastructure Layer**: External integrations and implementations

For mobile development, we use Gluon Mobile's components and services to access native device features while maintaining a single codebase.

## Mobile-Specific Components

### Camera Service

The application uses a platform-specific camera service implementation:

- `GluonCameraService`: Used on mobile devices, leverages Gluon Attach's PicturesService
- `MockCameraService`: Used on desktop for testing and development

The appropriate implementation is selected at runtime by the `CameraServiceFactory` based on the platform.

### Responsive UI

The UI is designed to be responsive and touch-friendly:

- Larger touch targets for buttons and controls
- Simplified navigation for mobile screens
- Responsive layouts that adapt to different screen sizes
- Touch-friendly dialogs and forms

## Development Environment Setup

### Prerequisites

- Java Development Kit (JDK) 21
- Maven 3.8+
- Android SDK (for Android development)
- Xcode (for iOS development, macOS only)
- Gluon Mobile license (for commercial use)

### IDE Setup

#### IntelliJ IDEA

1. Install the Gluon Plugin from the JetBrains Marketplace
2. Configure the Android SDK path in Settings > Build, Execution, Deployment > Build Tools > Gradle
3. Configure the iOS SDK path (macOS only)

#### Eclipse

1. Install the Gluon Plugin from the Eclipse Marketplace
2. Configure the Android SDK path in Preferences > Gluon
3. Configure the iOS SDK path (macOS only)

## Building for Mobile

### Android Build

To build for Android:

```bash
mvn gluonfx:build -Pandroid
mvn gluonfx:package -Pandroid
mvn gluonfx:install -Pandroid
mvn gluonfx:run -Pandroid
```

### iOS Build (macOS only)

To build for iOS:

```bash
mvn gluonfx:build -Pios
mvn gluonfx:package -Pios
mvn gluonfx:install -Pios
mvn gluonfx:run -Pios
```

## Testing on Mobile Devices

See the [Smartphone Setup Tasks](smartphone_setup_tasks.md) document for detailed instructions on setting up and testing the application on physical devices.

## Mobile-Specific Features

### Camera Access

The application uses Gluon Attach's PicturesService to access the device camera and photo gallery:

```
Services.get(PicturesService.class).ifPresent(service -> {
    service.takePhoto().ifPresent(image -> {
        // Process the captured image
    });
});
```

### Storage Access

The application uses Gluon Attach's StorageService to access the device's file system:

```
Services.get(StorageService.class).ifPresent(service -> {
    service.getPrivateStorage().ifPresent(dir -> {
        // Use the private storage directory
    });
});
```

### Permissions

The application requires the following permissions:

- Camera access
- Storage access
- Internet access

These permissions are requested at runtime using Gluon Attach's permission APIs.

## Troubleshooting

### Common Issues

1. **Build Failures**
   - Ensure you have the correct JDK version (21)
   - Verify that the Android SDK is properly configured
   - Check that all Gluon dependencies are available

2. **Runtime Errors**
   - Check the device logs for detailed error messages
   - Verify that all required permissions are granted
   - Ensure the device meets the minimum API level requirements

3. **UI Issues**
   - Test on different screen sizes and orientations
   - Verify that touch targets are large enough for comfortable use
   - Check that text is readable on all supported devices

## Resources

- [Gluon Mobile Documentation](https://docs.gluonhq.com/)
- [JavaFX Documentation](https://openjfx.io/javadoc/21/)
- [Android Developer Guidelines](https://developer.android.com/design)
- [iOS Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
