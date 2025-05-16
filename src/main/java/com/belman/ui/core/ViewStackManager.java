package com.belman.ui.core;

import com.belman.common.logging.EmojiLogger;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.admin.AdminViewFactory;
import com.belman.ui.usecases.admin.usermanagement.UserManagementViewFactory;
import com.belman.ui.usecases.authentication.login.LoginViewFactory;
import com.belman.ui.usecases.authentication.logout.LogoutViewFactory;
import com.belman.ui.usecases.common.main.MainViewFactory;
import com.belman.ui.usecases.common.splash.SplashViewFactory;
import com.belman.ui.usecases.order.gallery.OrderGalleryViewFactory;
import com.belman.ui.usecases.photo.review.PhotoReviewViewFactory;
import com.belman.ui.usecases.photo.upload.PhotoUploadViewFactory;
import com.belman.ui.usecases.qa.dashboard.QADashboardViewFactory;
import com.belman.ui.usecases.report.preview.ReportPreviewViewFactory;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.mvc.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Manager for view stacks in a Gluon Mobile application.
 * This class integrates with Gluon's MVC pattern and provides support for the ViewStack pattern.
 */
public class ViewStackManager {
    private static final EmojiLogger logger = EmojiLogger.getLogger(ViewStackManager.class);
    private static ViewStackManager instance;
    private final MobileApplication application;
    private final Map<String, ViewFactory> viewFactories = new HashMap<>();
    private final ViewDependencies viewDependencies;
    private final Stack<String> viewStack = new Stack<>();

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param application the MobileApplication instance
     * @param viewDependencies the ViewDependencies instance
     */
    private ViewStackManager(MobileApplication application, ViewDependencies viewDependencies) {
        this.application = application;
        this.viewDependencies = viewDependencies;
    }

    /**
     * Gets the singleton instance of the ViewStackManager.
     *
     * @param application the MobileApplication instance
     * @param viewDependencies the ViewDependencies instance
     * @return the ViewStackManager instance
     */
    public static ViewStackManager getInstance(MobileApplication application, ViewDependencies viewDependencies) {
        if (instance == null) {
            instance = new ViewStackManager(application, viewDependencies);
        }
        return instance;
    }

    /**
     * Initializes the ViewStackManager with the MobileApplication and ViewDependencies.
     * This method should be called once during application startup.
     *
     * @param application the MobileApplication instance
     * @param navigationService the RoleBasedNavigationService instance
     * @param viewRegistry the ViewRegistry instance
     * @return the ViewStackManager instance
     */
    public static ViewStackManager initialize(MobileApplication application, 
                                             com.belman.ui.navigation.RoleBasedNavigationService navigationService,
                                             ViewRegistry viewRegistry) {
        logger.startup("Initializing ViewStackManager");

        // Create ViewDependencies
        ViewDependencies viewDependencies = new ViewDependencies(navigationService, viewRegistry);

        // Get the ViewStackManager instance
        ViewStackManager manager = getInstance(application, viewDependencies);

        // Register all views
        manager.registerAllViews();

        // Set up the Router with the MobileApplication
        Router.setMobileApplication(application);

        logger.success("ViewStackManager initialized successfully");

        return manager;
    }

    /**
     * Gets the singleton instance of the ViewStackManager.
     *
     * @return the ViewStackManager instance
     * @throws IllegalStateException if the ViewStackManager has not been initialized
     */
    public static ViewStackManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ViewStackManager has not been initialized");
        }
        return instance;
    }

    /**
     * Registers a view factory with the ViewStackManager.
     *
     * @param viewId the view ID
     * @param factory the view factory
     */
    public void registerView(String viewId, ViewFactory factory) {
        logger.debug("Registering view: {}", viewId);
        viewFactories.put(viewId, factory);

        // Register the view with the MobileApplication
        application.addViewFactory(viewId, () -> {
            View view = factory.createView();
            logger.debug("Created view: {}", viewId);
            return view;
        });

        logger.success("Registered view: " + viewId);
    }

    /**
     * Creates a view using the registered factory.
     *
     * @param viewId the view ID
     * @return the created view
     * @throws IllegalArgumentException if no factory is registered for the view ID
     */
    public View createView(String viewId) {
        ViewFactory factory = viewFactories.get(viewId);
        if (factory == null) {
            throw new IllegalArgumentException("No factory registered for view ID: " + viewId);
        }
        return factory.createView();
    }

    /**
     * Navigates to the specified view.
     *
     * @param viewId the view ID
     */
    public void navigateTo(String viewId) {
        logger.debug("Navigating to view: {}", viewId);

        // Push the view ID onto the stack
        viewStack.push(viewId);

        // Switch to the view
        application.switchView(viewId);

        logger.success("Navigated to view: " + viewId);
    }

    /**
     * Navigates back to the previous view.
     *
     * @return true if navigation was successful, false if there is no previous view
     */
    public boolean navigateBack() {
        logger.debug("Attempting to navigate back. Stack size: {}", viewStack.size());

        if (viewStack.size() <= 1) {
            logger.warn("Cannot navigate back: no previous view in stack");
            return false;
        }

        // Remove the current view from the stack
        viewStack.pop();

        // Get the previous view
        String previousViewId = viewStack.peek();
        logger.debug("Navigating back to previous view: {}", previousViewId);

        // Switch to the previous view
        application.switchView(previousViewId);

        logger.success("Successfully navigated back to: " + previousViewId);

        return true;
    }

    /**
     * Gets the current view ID.
     *
     * @return the current view ID, or null if the stack is empty
     */
    public String getCurrentViewId() {
        return viewStack.isEmpty() ? null : viewStack.peek();
    }

    /**
     * Gets the ViewDependencies instance.
     *
     * @return the ViewDependencies instance
     */
    public ViewDependencies getViewDependencies() {
        return viewDependencies;
    }

    /**
     * Registers all views in the new structure.
     * This method should be called once during application startup.
     */
    public void registerAllViews() {
        logger.debug("Registering all views in the new structure");

        // Register the splash view
        registerView("SplashView", new SplashViewFactory(viewDependencies));

        // Register the login view
        registerView("LoginView", new LoginViewFactory(viewDependencies));

        // Register the admin view
        registerView("AdminView", new AdminViewFactory(viewDependencies));

        // Register the main view
        registerView("MainView", new MainViewFactory(viewDependencies));

        // Register the QA dashboard view
        registerView("QADashboardView", new QADashboardViewFactory(viewDependencies));

        // Register the Photo Review view
        registerView("PhotoReviewView", new PhotoReviewViewFactory(viewDependencies));

        // Register the Photo Upload view
        registerView("PhotoUploadView", new PhotoUploadViewFactory(viewDependencies));

        // Register the Order Gallery view
        registerView("OrderGalleryView", new OrderGalleryViewFactory(viewDependencies));

        // Register the Report Preview view
        registerView("ReportPreviewView", new ReportPreviewViewFactory(viewDependencies));

        // Register the User Management view
        registerView("UserManagementView", new UserManagementViewFactory(viewDependencies));

        // Register the Logout view
        registerView("LogoutView", new LogoutViewFactory(viewDependencies));

        // All views have been registered

        logger.success("All views registered successfully");
    }
}
