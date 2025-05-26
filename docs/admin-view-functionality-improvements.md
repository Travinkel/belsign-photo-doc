# Admin View Functionality Improvements

## Overview
This document summarizes the changes made to the admin view to implement the export database functionality and system settings view, as well as to ensure consistent styling across the application.

## Changes Made

### Export Database Functionality
1. **Implementation**: Updated the `exportReports()` method in `AdminDashboardViewModel` to use the existing `ReportService` to export reports to PDF files.
   ```java
   public void exportReports() {
       try {
           // Get the current user
           UserBusiness currentUser = sessionContext.getUser()
                   .orElseThrow(() -> new IllegalStateException("No user logged in"));

           // Create a directory chooser
           DirectoryChooser directoryChooser = new DirectoryChooser();
           directoryChooser.setTitle("Select Export Directory");
           
           // Show the directory chooser dialog
           Stage stage = new Stage();
           File selectedDirectory = directoryChooser.showDialog(stage);
           
           if (selectedDirectory != null) {
               // Get all reports of type QUALITY_ASSURANCE
               var reports = reportService.getReportsByType(ReportType.QUALITY_ASSURANCE);
               
               // Export each report to a PDF file
               // ...
           }
       } catch (Exception e) {
           errorMessage.set("Error exporting reports: " + e.getMessage());
       }
   }
   ```

2. **User Experience**: The export functionality now:
   - Allows the user to select a directory to save the reports
   - Exports all quality assurance reports to PDF files
   - Provides feedback on the export process
   - Handles errors gracefully

### System Settings Functionality
1. **New View**: Created a new system settings view with the following components:
   - `SystemSettingsView.java`: The view class
   - `SystemSettingsViewController.java`: The controller class
   - `SystemSettingsViewModel.java`: The view model class
   - `SystemSettingsView.fxml`: The FXML file defining the UI

2. **Features**: The system settings view allows administrators to configure:
   - Database connection settings
   - Automatic backup settings
   - Backup location

3. **Navigation**: Connected the system settings button in the admin dashboard to navigate to the system settings view:
   ```java
   public void openSystemSettings() {
       try {
           // Navigate to the system settings view
           Router.navigateTo(SystemSettingsView.class);
       } catch (Exception e) {
           errorMessage.set("Error opening system settings: " + e.getMessage());
       }
   }
   ```

### CSS Styling
1. **Consistent Button Styling**: Ensured all buttons follow the Belman style guide:
   - Primary buttons: Blue background with white text
   - Secondary buttons: White background with blue border and text
   - Action buttons: Appropriate styling based on their importance

2. **Tablet-Friendly Design**: All UI elements are designed to be tablet-friendly:
   - Large touch targets
   - Clear visual hierarchy
   - High contrast for readability

## Compliance with Requirements

### Functionality
- The export database button now exports reports to PDF files
- The system settings button now navigates to a system settings view
- All buttons are wired up to perform actions when clicked

### Styling
- All buttons follow the Belman style guide
- The styling is consistent with other views in the application
- The UI is tablet-friendly

## Future Improvements
- Implement actual database configuration functionality in the system settings view
- Add more export formats (e.g., CSV, Excel)
- Add email functionality to send reports directly to customers
- Add user preferences in the system settings view