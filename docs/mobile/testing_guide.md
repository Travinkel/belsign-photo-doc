# Mobile Testing Guide

This guide provides instructions for testing the BelSign application on mobile devices and emulators.

## Prerequisites

Before testing the application on mobile devices, ensure you have the following:

1. Development environment set up according to the [README.md](README.md)
2. Android SDK installed (for Android testing)
3. Xcode installed (for iOS testing, macOS only)
4. Physical devices or emulators/simulators for testing
5. USB cables for connecting physical devices

## Setting Up Test Devices

### Android Emulators

1. Open Android Studio
2. Go to Tools > AVD Manager
3. Click "Create Virtual Device"
4. Select a device definition (e.g., Pixel 6)
5. Select a system image (API level 26 or higher)
6. Configure the AVD and click "Finish"

### iOS Simulators (macOS only)

1. Open Xcode
2. Go to Xcode > Preferences > Components
3. Download the iOS simulator runtime
4. Go to Xcode > Open Developer Tool > Simulator
5. Select a device from Hardware > Device

### Physical Devices

#### Android

1. Enable Developer Options:
   - Go to Settings > About phone
   - Tap "Build number" 7 times
   - Go back to Settings > System > Developer options
   - Enable "USB debugging"

2. Connect the device to your computer via USB
3. Accept the "Allow USB debugging" prompt on the device

#### iOS (macOS only)

1. Connect the device to your Mac via USB
2. Trust the computer on the iOS device when prompted
3. Open Xcode and register the device for development

## Building and Deploying

### Android

1. Build the application for Android:
   ```bash
   mvn gluonfx:build -Pandroid
   ```

2. Package the application:
   ```bash
   mvn gluonfx:package -Pandroid
   ```

3. Install the application on the connected device or emulator:
   ```bash
   mvn gluonfx:install -Pandroid
   ```

4. Run the application:
   ```bash
   mvn gluonfx:run -Pandroid
   ```

### iOS (macOS only)

1. Build the application for iOS:
   ```bash
   mvn gluonfx:build -Pios
   ```

2. Package the application:
   ```bash
   mvn gluonfx:package -Pios
   ```

3. Install the application on the connected device or simulator:
   ```bash
   mvn gluonfx:install -Pios
   ```

4. Run the application:
   ```bash
   mvn gluonfx:run -Pios
   ```

## Test Cases

### Basic Functionality

1. **Login/Logout**
   - Launch the application
   - Enter valid credentials and log in
   - Verify that the main screen appears
   - Log out and verify that the login screen appears

2. **Navigation**
   - Navigate between different screens
   - Verify that the back button works correctly
   - Verify that the navigation flow is intuitive

### Camera Functionality

1. **Take Photo**
   - Go to the Photo Upload screen
   - Enter an orderAggregate number and search
   - Tap the "Take Photo" button
   - Verify that the camera opens
   - Take a photo and verify that it's captured correctly
   - Verify that the photo appears in the list

2. **Select Photo from Gallery**
   - Go to the Photo Upload screen
   - Enter an orderAggregate number and search
   - Tap the "Select Photo" button
   - Verify that the gallery opens
   - Select a photo and verify that it's loaded correctly
   - Verify that the photo appears in the list

3. **Upload Photo**
   - Take or select a photo
   - Enter an angle value
   - Tap the "Upload" button
   - Verify that the upload progress indicator appears
   - Verify that the photo is uploaded successfully
   - Verify that the photo appears in the list with the correct status

4. **Delete Photo**
   - Select a photo from the list
   - Tap the "Delete Selected" button
   - Verify that a confirmation dialog appears
   - Confirm the deletion
   - Verify that the photo is removed from the list

### Responsive Design

1. **Portrait Orientation**
   - Test the application in portrait orientation
   - Verify that all UI elements are visible and properly sized
   - Verify that text is readable
   - Verify that touch targets are large enough

2. **Landscape Orientation**
   - Rotate the device to landscape orientation
   - Verify that the UI adapts correctly
   - Verify that all UI elements are visible and properly sized
   - Verify that text is readable
   - Verify that touch targets are large enough

3. **Different Screen Sizes**
   - Test the application on devices with different screen sizes
   - Verify that the UI scales appropriately
   - Verify that all UI elements are visible and properly sized
   - Verify that text is readable
   - Verify that touch targets are large enough

### Error Handling

1. **Network Errors**
   - Turn off internet connectivity
   - Attempt to upload a photo
   - Verify that an appropriate error message is displayed
   - Turn on internet connectivity and try again
   - Verify that the upload succeeds

2. **Invalid Input**
   - Enter an invalid orderAggregate number
   - Verify that an appropriate error message is displayed
   - Enter an invalid angle value
   - Verify that an appropriate error message is displayed

3. **Permission Handling**
   - Deny camera permissions
   - Tap the "Take Photo" button
   - Verify that an appropriate error message is displayed
   - Grant camera permissions and try again
   - Verify that the camera opens

### Performance

1. **Startup Time**
   - Measure the time it takes for the application to start
   - Verify that the startup time is reasonable (< 5 seconds)

2. **UI Responsiveness**
   - Navigate between screens rapidly
   - Verify that the UI remains responsive
   - Interact with UI elements (buttons, text fields, etc.)
   - Verify that the UI responds promptly to touch events

3. **Memory Usage**
   - Monitor memory usage during testing
   - Verify that memory usage remains stable
   - Perform memory-intensive operations (e.g., taking multiple photos)
   - Verify that the application doesn't crash due to memory issues

## Test Matrix

Create a test matrix to ensure comprehensive testing across different devices and OS versions:

| Device | OS Version | Screen Size | Test Results |
|--------|------------|-------------|--------------|
| Pixel 6 (Emulator) | Android 12 | 1080 x 2400 | |
| Samsung Galaxy S10 | Android 11 | 1440 x 3040 | |
| iPhone 13 (Simulator) | iOS 15 | 1170 x 2532 | |
| iPad Pro (Simulator) | iOS 15 | 2732 x 2048 | |
| [Add your test devices] | | | |

## Reporting Issues

When reporting issues found during testing, include the following information:

1. Device make and model
2. Operating system version
3. Steps to reproduce the issue
4. Expected behavior
5. Actual behavior
6. Screenshots or videos if applicable
7. Logs if available

## Automated Testing

While manual testing is essential for UI and user experience, consider implementing automated tests:

1. **Unit Tests**
   - Write unit tests for mobile-specific components
   - Run tests on both desktop and mobile platforms

2. **UI Tests**
   - Use tools like Appium or Espresso for UI testing
   - Create test scripts for common user flows
   - Run tests on different devices and OS versions

3. **Continuous Integration**
   - Set up CI/CD pipelines for automated testing
   - Run tests on every commit or pull request
   - Generate test reports for review

## Best Practices

1. **Test Early and Often**
   - Test on mobile devices throughout development
   - Don't wait until the end to test on mobile

2. **Test on Real Devices**
   - Emulators/simulators are useful but not sufficient
   - Test on real devices for accurate results

3. **Test Different Network Conditions**
   - Test on Wi-Fi, cellular, and offline
   - Simulate slow network connections

4. **Test Battery Consumption**
   - Monitor battery usage during extended testing
   - Identify and fix battery-draining issues

5. **Test Accessibility**
   - Verify that the application works with screen readers
   - Test with different font sizes and display settings