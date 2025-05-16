package com.belman.ui.usecases.authentication.logout;

import com.belman.common.logging.EmojiLogger;
import com.belman.ui.session.DefaultSessionContext;
import com.belman.ui.session.DefaultSessionService;
import com.belman.ui.session.SessionContext;
import com.belman.ui.session.SessionManager;
import com.belman.ui.session.SessionService;
import com.belman.ui.base.BaseViewModel;
import com.belman.ui.navigation.RoleBasedNavigationService;
import com.belman.ui.navigation.Router;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the logout view.
 */
public class LogoutViewModel extends BaseViewModel<LogoutViewModel> {
    private final EmojiLogger logger = EmojiLogger.getLogger(LogoutViewModel.class);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty logoutInProgress = new SimpleBooleanProperty(false);
    private final SessionManager sessionManager;
    private final RoleBasedNavigationService navigationService;

    /**
     * Creates a new LogoutViewModel with the default SessionManager and a new RoleBasedNavigationService.
     */
    public LogoutViewModel() {
        // Get the SessionManager instance
        sessionManager = SessionManager.getInstance();

        // Create a SessionService
        SessionService sessionService = new DefaultSessionService(sessionManager);

        // Create a SessionContext
        SessionContext sessionContext = new DefaultSessionContext(sessionService);

        // Create a RoleBasedNavigationService with the SessionContext
        navigationService = new RoleBasedNavigationService(sessionContext);
    }

    /**
     * Creates a new LogoutViewModel with the specified SessionManager and RoleBasedNavigationService.
     *
     * @param sessionManager    the session manager
     * @param navigationService the role-based navigation service
     */
    public LogoutViewModel(SessionManager sessionManager, RoleBasedNavigationService navigationService) {
        this.sessionManager = sessionManager;
        this.navigationService = navigationService;
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
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage.get();
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the error message to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage.set(errorMessage);
    }

    /**
     * Gets the logout in progress property.
     *
     * @return the logout in progress property
     */
    public BooleanProperty logoutInProgressProperty() {
        return logoutInProgress;
    }

    /**
     * Checks if logout is in progress.
     *
     * @return true if logout is in progress, false otherwise
     */
    public boolean isLogoutInProgress() {
        return logoutInProgress.get();
    }

    /**
     * Sets whether logout is in progress.
     *
     * @param logoutInProgress true if logout is in progress, false otherwise
     */
    public void setLogoutInProgress(boolean logoutInProgress) {
        this.logoutInProgress.set(logoutInProgress);
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        // Clear any previous error message
        errorMessage.set("");

        logger.debug("Logout attempt started");

        // Set logout in progress
        logoutInProgress.set(true);

        try {
            // Attempt to log out
            logger.debug("Calling sessionManager.logout");
            sessionManager.logout();

            // Navigate to the login view
            logger.debug("Navigating to login view");
            navigationService.navigateToLogin();
            logger.success("Logout successful");
        } catch (Exception e) {
            // Handle any exceptions
            logger.error("Exception during logout", e);
            errorMessage.set("An error occurred during logout. Please try again later.");
        } finally {
            // Clear logout in progress
            logoutInProgress.set(false);
            logger.debug("Logout process completed");
        }
    }

    /**
     * Cancels the logout process and navigates back to the previous view.
     */
    public void cancel() {
        logger.debug("Logout cancelled");

        // Clear any error message
        errorMessage.set("");

        // Ensure logout is not in progress
        logoutInProgress.set(false);

        // Navigate back to the previous view
        logger.debug("Navigating back to previous view");
        Router.navigateBack();
    }
}
