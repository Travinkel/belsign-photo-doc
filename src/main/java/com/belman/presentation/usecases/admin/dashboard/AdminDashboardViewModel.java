package com.belman.presentation.usecases.admin.dashboard;

import com.belman.application.usecase.report.ReportService;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportType;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.base.LogoutCapable;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.admin.usermanagement.UserManagementView;
import com.belman.presentation.usecases.login.LoginView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ViewModel for the Admin dashboard view.
 * Provides data and operations for admin-specific functionality.
 */
public class AdminDashboardViewModel extends BaseViewModel<AdminDashboardViewModel> implements LogoutCapable {
    private final AuthenticationService authenticationService = ServiceLocator.getService(AuthenticationService.class);
    private final StringProperty welcomeMessage = new SimpleStringProperty("Welcome to Admin Dashboard");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    @Inject
    private UserRepository userRepository;

    @Inject
    private SessionContext sessionContext;

    @Inject
    private ReportService reportService;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public AdminDashboardViewModel() {
        // Default constructor
    }

    @Override
    public void onShow() {
        // Update welcome message with user name if available
        sessionContext.getUser().ifPresent(user -> {
            welcomeMessage.set("Welcome, " + user.getUsername().value() + "!");
        });
    }

    /**
     * Navigates to the user management view.
     */
    public void navigateToUserManagement() {
        try {
            // Navigate to the user management view
            Router.navigateTo(UserManagementView.class);
        } catch (Exception e) {
            errorMessage.set("Error navigating to user management: " + e.getMessage());
        }
    }

    /**
     * Gets the welcome message property.
     *
     * @return the welcome message property
     */
    public StringProperty welcomeMessageProperty() {
        return welcomeMessage;
    }

    /**
     * Gets the error message property.
     *
     * @return the error message property
     */
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    @Override
    public void logout() {
        try {
            // Log out the user
            authenticationService.logout();

            // Clear the session context
            SessionContext.clear();

            // Navigate to the login view
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            // Log the full error for debugging
            System.err.println("Error logging out: " + e.getMessage());
            e.printStackTrace();

            // Set a user-friendly error message
            errorMessage.set("Unable to log out properly. Please close the application and restart it to ensure you are fully logged out.");

            // Even if logout fails, try to navigate to login screen anyway
            try {
                // Navigate to the login view
                Router.navigateTo(LoginView.class);
            } catch (Exception navEx) {
                System.err.println("Failed to navigate to login view after logout error: " + navEx.getMessage());
            }
        }
    }

    /**
     * Initiates a database backup operation.
     * Allows the user to select a directory to save the database backup,
     * then exports the database to that directory.
     */
    public void backupDatabase() {
        try {
            // Get the current user
            UserBusiness currentUser = sessionContext.getUser()
                    .orElseThrow(() -> new IllegalStateException("No user logged in"));

            // Create a directory chooser
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Backup Directory");

            // Show the directory chooser dialog
            Stage stage = new Stage();
            File selectedDirectory = directoryChooser.showDialog(stage);

            if (selectedDirectory != null) {
                // Generate a filename based on the current timestamp
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                String filename = "belsign_db_backup_" + timestamp + ".db";
                File outputFile = new File(selectedDirectory, filename);

                // Perform the database backup
                // This is a simplified implementation that copies the SQLite database file
                // In a real implementation, you would use a proper database backup mechanism
                try {
                    // Get the path to the SQLite database file
                    String dbPath = System.getProperty("user.dir") + "\\src\\main\\resources\\sqlitedb\\belsign.db";
                    File dbFile = new File(dbPath);

                    if (!dbFile.exists()) {
                        // Try alternative path
                        dbPath = System.getProperty("user.dir") + "\\target\\classes\\sqlitedb\\belsign.db";
                        dbFile = new File(dbPath);
                    }

                    if (!dbFile.exists()) {
                        throw new IOException("Database file not found");
                    }

                    // Copy the database file to the output file
                    java.nio.file.Files.copy(dbFile.toPath(), outputFile.toPath(), 
                                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    // Update the welcome message with the result
                    welcomeMessage.set("Database backup completed successfully. Saved to: " + outputFile.getAbsolutePath());
                } catch (IOException e) {
                    errorMessage.set("Error backing up database: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                // User cancelled the directory selection
                welcomeMessage.set("Database backup cancelled.");
            }
        } catch (Exception e) {
            errorMessage.set("Error backing up database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exports reports from the system.
     * Allows the user to select a directory to save the reports,
     * then exports all reports to PDF files in that directory.
     */
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

                if (reports.isEmpty()) {
                    welcomeMessage.set("No reports found to export.");
                    return;
                }

                int exportedCount = 0;

                // Export each report to a PDF file
                for (var report : reports) {
                    try {
                        // Generate a filename based on the report ID and current timestamp
                        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                        String filename = "report_" + report.getId().toString() + "_" + timestamp + ".pdf";
                        File outputFile = new File(selectedDirectory, filename);

                        // Get the report preview as bytes
                        byte[] reportBytes = reportService.previewReport(
                                report.getOrderId(), 
                                ReportType.QUALITY_ASSURANCE, 
                                ReportFormat.PDF);

                        // Write the bytes to the file
                        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                            fos.write(reportBytes);
                        }

                        exportedCount++;
                    } catch (Exception e) {
                        System.err.println("Error exporting report " + report.getId() + ": " + e.getMessage());
                    }
                }

                // Update the welcome message with the result
                welcomeMessage.set("Successfully exported " + exportedCount + " of " + reports.size() + " reports to " + selectedDirectory.getAbsolutePath());
            } else {
                // User cancelled the directory selection
                welcomeMessage.set("Report export cancelled.");
            }
        } catch (Exception e) {
            errorMessage.set("Error exporting reports: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Opens the system settings view.
     */
    public void openSystemSettings() {
        try {
            // Navigate to the system settings view
            Router.navigateTo(com.belman.presentation.usecases.admin.settings.SystemSettingsView.class);
        } catch (Exception e) {
            errorMessage.set("Error opening system settings: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
