# Admin View Fixes

This document describes the fixes implemented to address issues with the admin view functionality.

## Issues Fixed

### 1. System Settings Fail to Open

**Problem:** The system settings view failed to open when clicked from the admin dashboard.

**Root Cause:** The `SystemSettingsView` was not registered with the `ViewStackManager`, so the router couldn't find it when attempting to navigate to it.

**Solution:**
- Created a new `SystemSettingsViewFactory` class that implements the `ViewFactory` interface
- Registered the factory in the `ViewStackManager.registerAllViews()` method
- This allows the router to properly create and display the system settings view

### 2. Export Database Doesn't Do Anything

**Problem:** The "Backup Database" button in the admin dashboard didn't perform any action.

**Root Cause:** The `backupDatabase()` method in `AdminDashboardViewModel` was just a placeholder that displayed a message but didn't actually perform any backup.

**Solution:**
- Implemented a proper `backupDatabase()` method that:
  - Shows a directory chooser dialog to let the user select a backup location
  - Generates a filename based on the current timestamp
  - Locates the SQLite database file
  - Copies the database file to the selected location
  - Updates the UI with a success or error message

### 3. Admin View Theme Not Aligned with Rest of App

**Problem:** The admin view had a different theme than the rest of the application, and UI elements were right-aligned.

**Root Cause:** There were no admin-specific styles defined in the CSS files, so the admin view was using default styles that didn't match the rest of the app.

**Solution:**
- Added admin-specific styles to the `views.css` file, including:
  - Styles for the admin dashboard view and admin view in general
  - Styles for sections, section titles, headers, and action buttons
  - Specific styles for the system settings view
  - Tablet-friendly adjustments for admin views
- Set the alignment to center-left to fix the right-alignment issue
- Ensured consistent styling with the rest of the application

## Files Modified

1. `D:\Repo\EASV\belsign-photo-doc\src\main\resources\com\belman\assets\styles\views.css`
   - Added admin-specific styles

2. `D:\Repo\EASV\belsign-photo-doc\src\main\java\com\belman\presentation\usecases\admin\dashboard\AdminDashboardViewModel.java`
   - Implemented proper database backup functionality

3. `D:\Repo\EASV\belsign-photo-doc\src\main\java\com\belman\presentation\core\ViewStackManager.java`
   - Registered the SystemSettingsView

## Files Created

1. `D:\Repo\EASV\belsign-photo-doc\src\main\java\com\belman\presentation\usecases\admin\settings\SystemSettingsViewFactory.java`
   - Created factory class for SystemSettingsView

## Testing

The changes have been tested to ensure:
- System settings can be opened from the admin dashboard
- Database backup functionality works correctly
- Admin view styling is consistent with the rest of the application
- UI elements are properly aligned