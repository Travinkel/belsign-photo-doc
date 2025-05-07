# Task List for Running BelSign on a Smartphone

## Prerequisites
- [X] Java Development Kit (JDK) 21 installed
- [X] Android SDK installed (for Android devices)
- [ ] Xcode installed (for iOS fdevices, macOS only)
- [ ] Gluon Mobile license (for commercial use)
- [X] USB cable for connecting the smartphone to the computer
- [X] Smartphone with developer options enabled
- [X] Visual Studio Build Tools installed (MSVC, CMake, Windows SDK)
- [X] Maven installed and configured
- [X] GluonFX Maven plugin added to the project
- [X] GluonFX dependencies added to the project
- [X] GluonFX configuration in `pom.xml` for Android and iOS targets
- [X] GluonFX configuration in `pom.xml` for JavaFX and JavaFX Mobile targets

## Android Setup Tasks
1. [X] Enable Developer Options on the Android device
   - Go to Settings > About phone
   - Tap "Build number" 7 times to enable developer options
   - Go back to Settings > System > Developer options
   - Enable "USB debugging"

2. [X] Connect the Android device to the computer via USB
   - Confirm the "Allow USB debugging" prompt on the device

3. [X] Install required Android SDK components
   - Open Android Studio
   - Go to SDK Manager
   - Install Android SDK Platform-Tools
   - Install Android SDK Build-Tools
   - Install Android SDK for target API level (e.g., 33 for modern devices)

4. [X] Configure Maven for Android deployment
   - Ensure the `gluonfx-maven-plugin` is properly configured in `pom.xml`
   - Set the target to "android" in the plugin configuration

## iOS Setup Tasks (macOS only)
1. [ ] Register for an Apple Developer account

2. [ ] Connect the iOS device to the Mac via USB
   - Trust the computer on the iOS device when prompted

3. [ ] Configure Xcode for development
   - Open Xcode
   - Go to Xcode > Preferences > Accounts
   - Add your Apple ID
   - Select your team
   - Create a development certificate if needed

4. [ ] Configure Maven for iOS deployment
   - Ensure the `gluonfx-maven-plugin` is properly configured in `pom.xml`
   - Set the target to "ios" in the plugin configuration

## Building and Deploying the Application

### Android Deployment
1. [ ] Build the application for Android
   ```bash
   mvn gluonfx:build -Pandroid
   ```

2. [ ] Package the application
   ```bash
   mvn gluonfx:package -Pandroid
   ```

3. [ ] Install the application on the connected device
   ```bash
   mvn gluonfx:install -Pandroid
   ```

4. [ ] Run the application on the connected device
   ```bash
   mvn gluonfx:nativerun -Pandroid
   ```

### iOS Deployment (macOS only)
1. [ ] Build the application for iOS
   ```bash
   mvn gluonfx:build -Pios
   ```

2. [ ] Package the application
   ```bash
   mvn gluonfx:package -Pios
   ```

3. [ ] Install the application on the connected device
   ```bash
   mvn gluonfx:install -Pios
   ```

4. [ ] Run the application on the connected device
   ```bash
   mvn gluonfx:nativerun -Pios
   ```

## Troubleshooting
- [ ] Check device connection
   ```bash
   adb devices  # For Android
   ```

- [ ] Check logs for errors
   ```bash
   adb logcat  # For Android
   ```

- [ ] Ensure the device has sufficient storage space
- [ ] Verify that the device meets the minimum API level requirements
- [ ] Check that all required permissions are granted on the device

## Testing the Application
1. [ ] Test user authentication (login/logout)
2. [ ] Test photo capture functionality
3. [ ] Test orderAggregate number entry
4. [ ] Test photo upload
5. [ ] Test photo list view
6. [ ] Test photo deletion
7. [ ] Test UI responsiveness on different screen sizes
8. [ ] Test application behavior with different network conditions

## Performance Optimization
- [ ] Monitor memory usage
- [ ] Optimize image handling for mobile devices
- [ ] Ensure smooth UI transitions
- [ ] Implement proper error handling for mobile-specific issues

## Security Considerations
- [ ] Ensure secure storage of credentials
- [ ] Implement proper permission handling
- [ ] Protect sensitive data in transit
- [ ] Consider device-specific security features