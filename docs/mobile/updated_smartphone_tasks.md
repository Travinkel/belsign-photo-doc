# Updated Task List for Running BelSign on a Smartphone

## Completed Tasks
- [X] Fixed SplashView.fxml logo.png path issues
  - Updated path in `src\main\resources\com\belman\views\SplashView.fxml` to `@/com/belman/images/logo.png`
  - Updated path in `src\main\resources\com\belman\presentation\views\splash\SplashView.fxml` to `/com/belman/images/logo.png`
- [X] Fixed ProgressBar import in `src\main\resources\com\belman\presentation\views\splash\SplashView.fxml`
  - Changed from `javafx.scene.control.ProgressBar` to `com.gluonhq.charm.glisten.control.ProgressBar`
- [X] Fixed stylesheets path in `src\main\resources\com\belman\presentation\views\splash\SplashView.fxml`
  - Updated from `/styles/app.css` to `/com/belman/styles/app.css`
- [X] Verified GluonFX Maven plugin configuration in pom.xml
  - Confirmed target is set to "android"
  - Confirmed GraalVM home path is set correctly

## Remaining Tasks for Smartphone Deployment

### Building and Deploying

> **Important Note for Windows Users**: Building for Android on Windows requires WSL2 (Windows Subsystem for Linux). If you encounter the error "We currently can't compile to aarch64-linux-android when running on x86_64-microsoft-windows", please refer to the [WSL Setup Guide](wsl_setup_guide.md) for instructions on how to set up WSL2 for Android builds.

1. [ ] Build the application for Android
   ```bash
   # On Windows, run this command from within WSL2
   mvn gluonfx:build -Pandroid
   ```

2. [ ] Package the application
   ```bash
   # On Windows, run this command from within WSL2
   mvn gluonfx:package -Pandroid
   ```

3. [ ] Install the application on the connected device
   ```bash
   # On Windows, run this command from within WSL2
   mvn gluonfx:install -Pandroid
   ```

4. [ ] Run the application on the connected device
   ```bash
   # On Windows, run this command from within WSL2
   mvn gluonfx:nativerun -Pandroid
   ```

### Troubleshooting
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

### Testing the Application
1. [ ] Test user authentication (login/logout)
2. [ ] Test photo capture functionality
3. [ ] Test orderAggregate number entry
4. [ ] Test photo upload
5. [ ] Test photo list view
6. [ ] Test photo deletion
7. [ ] Test UI responsiveness on different screen sizes
8. [ ] Test application behavior with different network conditions

### Performance Optimization
- [ ] Monitor memory usage
- [ ] Optimize image handling for mobile devices
- [ ] Ensure smooth UI transitions
- [ ] Implement proper error handling for mobile-specific issues

### Security Considerations
- [ ] Ensure secure storage of credentials
- [ ] Implement proper permission handling
- [ ] Protect sensitive data in transit
- [ ] Consider device-specific security features

## Additional Notes
- The application should now correctly display the logo on the splash screen
- The ProgressBar component should now work correctly on mobile devices
- The stylesheets should now be properly loaded on mobile devices

If you encounter any issues during deployment or testing, refer to the troubleshooting guide in `docs\mobile\troubleshooting.md`.
