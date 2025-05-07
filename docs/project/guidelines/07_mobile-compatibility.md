# Mobile Compatibility Guidelines

## Overview

The BelSign Photo Documentation Module is designed to work seamlessly across desktop and mobile platforms using Gluon Mobile. This document provides guidelines for ensuring mobile compatibility throughout the development process.

## Mobile-First Design Principles

### Responsive Layouts

- Design layouts that adapt to different screen sizes and orientations
- Use relative sizing (percentages) instead of fixed pixel values
- Implement responsive containers (VBox, HBox, GridPane) with appropriate constraints
- Test layouts on both small (phone) and medium (tablet) screen sizes

### Touch-Friendly UI

- Design for touch interaction rather than mouse and keyboard
- Make touch targets (buttons, controls) at least 44x44 pixels
- Provide adequate spacing between interactive elements (minimum 8px)
- Implement swipe gestures for common actions (navigation, refresh)
- Avoid hover-dependent interactions

### Performance Considerations

- Optimize image loading and caching for mobile devices
- Minimize network requests and payload sizes
- Implement lazy loading for lists and grids
- Use background threads for heavy operations
- Monitor memory usage and avoid memory leaks

## Gluon Mobile Integration

### Charm Glisten Components

Use Gluon's Charm Glisten UI components for a native mobile look and feel:

- `MobileApplication`: Base application class
- `View`: Mobile-optimized view container
- `NavigationDrawer`: Side menu for navigation
- `BottomNavigation`: Tab-based navigation
- `AppBar`: Application toolbar
- `Dialog`: Mobile-friendly dialogs
- `ProgressIndicator`: Loading indicators
- `FloatingActionButton`: Primary action button

Example:

```
public class MainView extends View {
    
    public MainView() {
        super("Main");
        
        // Set up AppBar
        AppBar appBar = getAppBar();
        appBar.setNavIcon(MaterialDesignIcon.MENU.button(e -> 
            MobileApplication.getInstance().getDrawer().open()));
        appBar.setTitleText("BelSign");
        appBar.getActionItems().add(MaterialDesignIcon.SEARCH.button());
        
        // Set up content
        VBox content = new VBox();
        content.setSpacing(10);
        content.setPadding(new Insets(10));
        
        // Add responsive components
        setCenter(content);
    }
}
```

### Platform Detection

Use platform detection to apply platform-specific styling and behavior:

```
if (PlatformUtils.isAndroid()) {
    // Android-specific code
} else if (PlatformUtils.isIOS()) {
    // iOS-specific code
} else if (PlatformUtils.isDesktop()) {
    // Desktop-specific code
}
```

### Attach Services

Utilize Gluon Attach services to access native device features:

- `StorageService`: Access device storage
- `PicturesService`: Access device camera and photo gallery
- `PositionService`: Access device location
- `BrowserService`: Open web links
- `ShareService`: Share content with other apps
- `LifecycleService`: Handle application lifecycle events

Example:

```
// Take a photo using the device camera
Services.get(PicturesService.class).ifPresent(service -> {
    service.takePhoto().ifPresent(image -> {
        // Process the captured image
        imageView.setImage(image);
        uploadPhotoTask.setImage(image);
    });
});
```

## Platform-Specific Considerations

### Android

- Support various screen densities (ldpi, mdpi, hdpi, xhdpi, xxhdpi)
- Handle Android back button properly
- Implement proper permission requests
- Consider Android navigation patterns (drawer, bottom navigation)
- Test on multiple Android versions (API level 24+)

### iOS

- Follow iOS Human Interface Guidelines
- Implement proper status bar integration
- Handle safe areas (notches, home indicator)
- Consider iOS navigation patterns (tab bar, navigation bar)
- Test on multiple iOS versions (iOS 13+)

### Permissions

Implement proper permission handling for device features:

```
Services.get(StorageService.class).ifPresent(service -> {
    service.requestPermission().thenAccept(response -> {
        if (response == PermissionStatus.GRANTED) {
            // Permission granted, proceed with storage operations
        } else {
            // Handle permission denied
            showPermissionDeniedDialog();
        }
    });
});
```

## Offline Capabilities

### Local Storage

Implement local storage for offline operation:

- Use Gluon's `StorageService` for file storage
- Implement a local database (SQLite) for structured data
- Cache frequently accessed data
- Implement data synchronization when online

### Sync Strategy

Develop a robust synchronization strategy:

- Queue operations when offline
- Sync when connection is restored
- Handle conflict resolution
- Provide sync status indicators

## Testing on Mobile Devices

### Emulators and Simulators

- Test on Android emulators with various screen sizes and API levels
- Test on iOS simulators with various device types and iOS versions
- Use Android Studio and Xcode for platform-specific debugging

### Physical Devices

- Test on real Android and iOS devices
- Test on both phones and tablets
- Test with various network conditions (WiFi, cellular, offline)
- Test battery consumption during extended use

### Automated Testing

- Use TestFX for UI testing
- Implement platform-specific test cases
- Set up CI/CD pipeline for mobile testing
- Use Firebase Test Lab or similar services for device farm testing

## Mobile-Specific Features

### Camera Integration

Implement camera integration for photo documentation:

```
private void capturePhoto() {
    Services.get(PicturesService.class).ifPresent(service -> {
        service.takePhoto().ifPresent(image -> {
            // Process the captured image
            photoDocumentService.addPhotoToCurrentOrder(image);
        });
    });
}
```

### Barcode Scanning

Implement barcode scanning for quick orderAggregate lookup:

```
private void scanBarcode() {
    Services.get(BarcodeService.class).ifPresent(service -> {
        service.scanBarcode().thenAccept(barcode -> {
            if (barcode != null) {
                // Look up orderAggregate by barcode
                orderService.findByBarcode(barcode);
            }
        });
    });
}
```

### Push Notifications

Implement push notifications for important events:

- Order status changes
- Photo approval/rejection
- Report generation completion
- New assignments

## Performance Optimization

### Image Handling

Optimize image handling for mobile devices:

- Resize images before upload
- Use efficient image formats (WebP, HEIF)
- Implement progressive loading
- Cache images locally

Example:

```
private Image resizeImageForUpload(Image original) {
    int maxDimension = 1200; // Max dimension for upload
    
    double width = original.getWidth();
    double height = original.getHeight();
    
    double scale = Math.min(maxDimension / width, maxDimension / height);
    
    if (scale < 1) {
        // Resize needed
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);
        
        WritableImage resized = new WritableImage(newWidth, newHeight);
        PixelWriter pixelWriter = resized.getPixelWriter();
        
        // Implement resizing logic
        
        return resized;
    }
    
    return original; // No resize needed
}
```

### Network Optimization

Optimize network usage:

- Implement request batching
- Use compression for API requests/responses
- Implement efficient caching strategies
- Monitor and optimize payload sizes

## Accessibility

Ensure the application is accessible on mobile devices:

- Support screen readers (TalkBack on Android, VoiceOver on iOS)
- Implement proper content descriptions
- Ensure adequate color contrast
- Support text scaling
- Test with accessibility tools

## Deployment

### App Store Guidelines

Follow platform-specific guidelines for app store submission:

- Comply with Apple App Store Review Guidelines
- Comply with Google Play Store Policies
- Prepare appropriate app store assets (icons, screenshots)
- Write compelling app descriptions

### CI/CD for Mobile

Set up continuous integration and deployment for mobile builds:

- Automate build process for Android and iOS
- Implement automated testing on mobile platforms
- Set up deployment to app stores
- Use beta testing platforms (TestFlight, Google Play Beta)

## Resources

- [Gluon Mobile Documentation](https://docs.gluonhq.com/)
- [JavaFX Mobile Documentation](https://openjfx.io/openjfx-docs/)
- [Android Developer Guidelines](https://developer.android.com/design)
- [iOS Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/)
- [Material Design Guidelines](https://material.io/design)