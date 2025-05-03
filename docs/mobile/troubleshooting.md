# Mobile Troubleshooting Guide

This guide provides solutions for common issues when running the BelSign application on mobile devices.

## Camera Issues

### Camera Not Available

**Symptoms:**
- "Camera is not available on this device" error message
- Take Photo button doesn't work

**Possible Causes:**
1. Device doesn't have a camera
2. App doesn't have camera permissions
3. Another app is using the camera
4. Gluon Attach PicturesService is not working correctly

**Solutions:**
1. Check if the device has a camera
2. Ensure the app has camera permissions:
   - Android: Settings > Apps > BelSign > Permissions > Camera
   - iOS: Settings > Privacy > Camera > BelSign
3. Close other apps that might be using the camera
4. Restart the application
5. Check the logs for specific error messages

### Photo Capture Fails

**Symptoms:**
- "Error taking photo" error message
- Camera opens but fails to save the photo

**Possible Causes:**
1. Insufficient storage space
2. App doesn't have storage permissions
3. Temporary directory is not accessible

**Solutions:**
1. Free up storage space on the device
2. Ensure the app has storage permissions:
   - Android: Settings > Apps > BelSign > Permissions > Storage
   - iOS: Settings > Privacy > Files and Folders > BelSign
3. Restart the application
4. Check the logs for specific error messages

## Gallery Issues

### Gallery Not Available

**Symptoms:**
- "Photo gallery is not available on this device" error message
- Select Photo button doesn't work

**Possible Causes:**
1. App doesn't have storage permissions
2. Gluon Attach PicturesService is not working correctly

**Solutions:**
1. Ensure the app has storage permissions:
   - Android: Settings > Apps > BelSign > Permissions > Storage
   - iOS: Settings > Privacy > Photos > BelSign
2. Restart the application
3. Check the logs for specific error messages

### Photo Selection Fails

**Symptoms:**
- "Error selecting photo" error message
- Gallery opens but fails to return the selected photo

**Possible Causes:**
1. Selected file is too large
2. File format is not supported
3. App doesn't have storage permissions

**Solutions:**
1. Select a smaller photo
2. Select a photo in a supported format (JPEG, PNG)
3. Ensure the app has storage permissions
4. Check the logs for specific error messages

## UI Issues

### UI Elements Too Small

**Symptoms:**
- Buttons, text fields, and other UI elements are too small to interact with
- Difficult to tap on specific elements

**Possible Causes:**
1. Device has a high-resolution screen
2. UI scaling is not working correctly

**Solutions:**
1. Adjust the display settings on the device:
   - Android: Settings > Display > Display Size
   - iOS: Settings > Display & Brightness > Text Size
2. Use the device in landscape orientation for a larger view
3. Report the issue to the development team with device details

### UI Elements Overlap

**Symptoms:**
- UI elements overlap each other
- Text is cut off or not fully visible

**Possible Causes:**
1. Device has an unusual screen aspect ratio
2. Responsive layout is not handling the screen size correctly

**Solutions:**
1. Try rotating the device to a different orientation
2. Restart the application
3. Report the issue to the development team with device details and screenshots

## Network Issues

### Unable to Upload Photos

**Symptoms:**
- "Error uploading photo" error message
- Upload progress indicator spins indefinitely

**Possible Causes:**
1. No internet connection
2. Weak or unstable internet connection
3. Server is not responding

**Solutions:**
1. Check if the device has an active internet connection
2. Connect to a stronger Wi-Fi network
3. Try again later
4. Check the logs for specific error messages

### Slow Performance

**Symptoms:**
- Application is slow to respond
- UI animations are choppy
- Loading indicators take a long time

**Possible Causes:**
1. Weak internet connection
2. Device has limited resources (CPU, memory)
3. Too many photos in the list

**Solutions:**
1. Connect to a stronger Wi-Fi network
2. Close other apps running in the background
3. Restart the application
4. Delete unnecessary photos from the list

## Build Issues

### Android Build Fails on Windows

**Symptoms:**
- Error message: "We currently can't compile to aarch64-linux-android when running on x86_64-microsoft-windows"
- Build process fails when running `mvn gluonfx:build -Pandroid`

**Possible Causes:**
1. Attempting to build for Android directly on Windows without WSL2
2. WSL2 is not properly installed or configured
3. Required tools are not installed in WSL2

**Solutions:**
1. Install and configure WSL2 following the instructions in [WSL Setup Guide](wsl_setup_guide.md)
2. Run the PowerShell script `check_wsl.ps1` to verify your WSL2 installation:
   ```powershell
   .\docs\mobile\check_wsl.ps1
   ```
3. Build the Android application from within WSL2:
   ```bash
   # In WSL2 Ubuntu terminal
   cd /mnt/c/path/to/your/project
   mvn gluonfx:build -Pandroid
   ```

## Installation Issues

### App Won't Install

**Symptoms:**
- Installation fails with an error message
- App doesn't appear on the home screen after installation

**Possible Causes:**
1. Insufficient storage space
2. Incompatible device or OS version
3. Corrupted installation file

**Solutions:**
1. Free up storage space on the device
2. Check if the device meets the minimum requirements:
   - Android: Version 8.0 (API level 26) or higher
   - iOS: Version 13.0 or higher
3. Download the installation file again
4. Restart the device and try again

### App Crashes on Startup

**Symptoms:**
- App opens briefly and then closes
- "App has stopped working" error message

**Possible Causes:**
1. Corrupted installation
2. Incompatible device or OS version
3. Missing required permissions

**Solutions:**
1. Uninstall and reinstall the application
2. Ensure the device meets the minimum requirements
3. Grant all required permissions during installation
4. Restart the device and try again
5. Check the logs for specific error messages

## Logging and Debugging

To help diagnose issues, you can enable detailed logging:

1. Connect the device to a computer with USB debugging enabled
2. Run the following command to view the logs:
   ```
   adb logcat | grep "com.belman"
   ```
3. Reproduce the issue and check the logs for error messages
4. Share the logs with the development team when reporting issues

## Reporting Issues

When reporting issues, please include the following information:

1. Device make and model
2. Operating system version
3. App version
4. Steps to reproduce the issue
5. Screenshots or videos if applicable
6. Logs if available

Send this information to the development team at support@belman.com or create an issue in the project's issue tracker.
