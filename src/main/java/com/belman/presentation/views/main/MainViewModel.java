package com.belman.presentation.views.main;

import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.common.logging.EmojiLogger;
import com.belman.service.session.DefaultSessionContext;
import com.belman.service.session.DefaultSessionService;
import com.belman.service.session.SessionContext;
import com.belman.service.session.SessionManager;
import com.belman.service.session.SessionService;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.core.StateManager;
import com.belman.presentation.navigation.RoleBasedNavigationService;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.views.login.LoginView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Optional;
import java.util.Set;

/**
 * View model for the main view.
 */
public class MainViewModel extends BaseViewModel<MainViewModel> {
    private final EmojiLogger logger = EmojiLogger.getLogger(MainViewModel.class);
    private final StringProperty welcomeMessage = new SimpleStringProperty("Welcome to BelSign!");
    private final StringProperty username = new SimpleStringProperty("");
    private final BooleanProperty adminRoleAvailable = new SimpleBooleanProperty(false);
    private final BooleanProperty qaRoleAvailable = new SimpleBooleanProperty(false);
    private final BooleanProperty productionRoleAvailable = new SimpleBooleanProperty(false);
    private final SessionManager sessionManager;
    private final SessionContext sessionContext;
    private final RoleBasedNavigationService navigationService;

    /**
     * Creates a new MainViewModel.
     */
    public MainViewModel() {
        // Get the SessionManager instance
        sessionManager = SessionManager.getInstance();

        // Create a SessionService
        SessionService sessionService = new DefaultSessionService(sessionManager);

        // Create a SessionContext
        sessionContext = new DefaultSessionContext(sessionService);

        // Create a RoleBasedNavigationService with the SessionContext
        navigationService = new RoleBasedNavigationService(sessionContext);
    }

    @Override
    public void onShow() {
        logger.debug("MainViewModel.onShow() called");
        try {
            // Update the app bar title using StateManager
            logger.debug("Setting app bar title to 'Role Selection'");
            StateManager.getInstance().setState("appBarTitle", "Role Selection");

            logger.debug("Calling updateWelcomeMessage()");
            updateWelcomeMessage();
            logger.debug("updateWelcomeMessage() completed");
            
            // Update role availability
            updateRoleAvailability();
        } catch (Exception e) {
            logger.error("Exception in MainViewModel.onShow()", e);
        }
    }

    /**
     * Updates the welcome message based on the current user.
     */
    private void updateWelcomeMessage() {
        logger.debug("Updating welcome message");
        try {
            logger.debug("Getting current user from sessionContext");
            Optional<UserBusiness> currentUser = sessionContext.getUser();

            if (currentUser.isPresent()) {
                String name = currentUser.get().getUsername().value();
                logger.debug("Current user found: {}", name);
                welcomeMessage.set("Welcome, " + name + "!");
                username.set(name);
                logger.debug("Welcome message updated for user: {}", name);
            } else {
                logger.warn("No current user found, redirecting to login view");
                welcomeMessage.set("Welcome to BelSign!");
                username.set("");

                // If no user is logged in, redirect to login view
                try {
                    logger.debug("Navigating to LoginView");
                    navigationService.navigateToLogin();
                    logger.debug("Navigation to LoginView completed");
                } catch (Exception e) {
                    logger.error("Failed to navigate to LoginView", e);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while updating welcome message", e);
            // Set a default welcome message in case of error
            welcomeMessage.set("Welcome to BelSign!");
            username.set("");
        }
    }
    
    /**
     * Updates the role availability based on the current user's roles.
     */
    private void updateRoleAvailability() {
        logger.debug("Updating role availability");
        try {
            logger.debug("Getting current user from sessionContext");
            Optional<UserBusiness> currentUser = sessionContext.getUser();

            if (currentUser.isPresent()) {
                Set<UserRole> roles = currentUser.get().getRoles();
                logger.debug("Current user has {} roles", roles.size());
                
                // Update role availability properties
                adminRoleAvailable.set(roles.contains(UserRole.ADMIN));
                qaRoleAvailable.set(roles.contains(UserRole.QA));
                productionRoleAvailable.set(roles.contains(UserRole.PRODUCTION));
                
                logger.debug("Role availability updated: Admin={}, QA={}, Production={}", 
                        adminRoleAvailable.get(), qaRoleAvailable.get(), productionRoleAvailable.get());
            } else {
                logger.warn("No current user found, all roles unavailable");
                adminRoleAvailable.set(false);
                qaRoleAvailable.set(false);
                productionRoleAvailable.set(false);
            }
        } catch (Exception e) {
            logger.error("Exception while updating role availability", e);
            // Set all roles unavailable in case of error
            adminRoleAvailable.set(false);
            qaRoleAvailable.set(false);
            productionRoleAvailable.set(false);
        }
    }

    /**
     * Navigates to the admin view.
     */
    public void navigateToAdminView() {
        logger.debug("Navigating to admin view");
        try {
            navigationService.navigateToRoleSpecificView(UserRole.ADMIN);
            logger.debug("Navigation to admin view completed");
        } catch (Exception e) {
            logger.error("Failed to navigate to admin view", e);
        }
    }

    /**
     * Navigates to the QA view.
     */
    public void navigateToQAView() {
        logger.debug("Navigating to QA view");
        try {
            navigationService.navigateToRoleSpecificView(UserRole.QA);
            logger.debug("Navigation to QA view completed");
        } catch (Exception e) {
            logger.error("Failed to navigate to QA view", e);
        }
    }

    /**
     * Navigates to the production view.
     */
    public void navigateToProductionView() {
        logger.debug("Navigating to production view");
        try {
            navigationService.navigateToRoleSpecificView(UserRole.PRODUCTION);
            logger.debug("Navigation to production view completed");
        } catch (Exception e) {
            logger.error("Failed to navigate to production view", e);
        }
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        logger.debug("Logging out user");
        try {
            logger.debug("Calling sessionContext.getSessionService().logout()");
            ((DefaultSessionContext)sessionContext).getSessionService().logout();
            logger.debug("User logged out successfully");

            logger.debug("Navigating to LoginView");
            try {
                navigationService.navigateToLogin();
                logger.debug("Navigation to LoginView completed");
            } catch (Exception e) {
                logger.error("Failed to navigate to LoginView", e);
            }
        } catch (Exception e) {
            logger.error("Exception during logout", e);
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
     * Gets the welcome message.
     *
     * @return the welcome message
     */
    public String getWelcomeMessage() {
        return welcomeMessage.get();
    }

    /**
     * Gets the username property.
     *
     * @return the username property
     */
    public StringProperty usernameProperty() {
        return username;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username.get();
    }
    
    /**
     * Gets the admin role availability property.
     *
     * @return the admin role availability property
     */
    public BooleanProperty adminRoleAvailableProperty() {
        return adminRoleAvailable;
    }
    
    /**
     * Gets whether the admin role is available.
     *
     * @return true if the admin role is available, false otherwise
     */
    public boolean isAdminRoleAvailable() {
        return adminRoleAvailable.get();
    }
    
    /**
     * Gets the QA role availability property.
     *
     * @return the QA role availability property
     */
    public BooleanProperty qaRoleAvailableProperty() {
        return qaRoleAvailable;
    }
    
    /**
     * Gets whether the QA role is available.
     *
     * @return true if the QA role is available, false otherwise
     */
    public boolean isQARoleAvailable() {
        return qaRoleAvailable.get();
    }
    
    /**
     * Gets the production role availability property.
     *
     * @return the production role availability property
     */
    public BooleanProperty productionRoleAvailableProperty() {
        return productionRoleAvailable;
    }
    
    /**
     * Gets whether the production role is available.
     *
     * @return true if the production role is available, false otherwise
     */
    public boolean isProductionRoleAvailable() {
        return productionRoleAvailable.get();
    }
}