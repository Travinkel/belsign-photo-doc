package com.belman.presentation.core;

import com.belman.common.logging.EmojiLogger;
import com.belman.presentation.di.ViewDependencies;
import com.belman.presentation.usecases.archive.admin.AdminViewFactory;
import com.belman.presentation.usecases.archive.admin.usermanagement.UserManagementViewFactory;
import com.belman.presentation.usecases.archive.authentication.login.LoginViewFactory;
import com.belman.presentation.usecases.archive.authentication.logout.LogoutViewFactory;
import com.belman.presentation.usecases.splash.SplashViewFactory;
import com.belman.presentation.usecases.archive.order.gallery.OrderGalleryViewFactory;
import com.belman.presentation.usecases.archive.photo.review.PhotoReviewViewFactory;
import com.belman.presentation.usecases.archive.photo.upload.PhotoUploadViewFactory;
import com.belman.presentation.usecases.archive.photo.workflow.PhotoWorkflowViewFactory;
import com.belman.presentation.usecases.archive.qa.dashboard.QADashboardViewFactory;
import com.belman.presentation.usecases.archive.report.preview.ReportPreviewViewFactory;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderViewFactory;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewFactory;
import com.belman.presentation.usecases.worker.capture.CaptureViewFactory;
import com.belman.presentation.usecases.worker.summary.SummaryViewFactory;
import com.belman.presentation.usecases.worker.completed.CompletedViewFactory;
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
    private final Map<String, ViewFactory> viewFactories = new HashMap<>();
    private final ViewDependencies viewDependencies;
    private final Stack<String> viewStack = new Stack<>();
    private final Map<String, View> viewCache = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern.
     *
     * @param viewDependencies the ViewDependencies instance
     */
    private ViewStackManager(ViewDependencies viewDependencies) {
        this.viewDependencies = viewDependencies;
    }

    /**
     * Gets the singleton instance of the ViewStackManager.
     *
     * @param viewDependencies the ViewDependencies instance
     * @return the ViewStackManager instance
     */
    public static ViewStackManager getInstance(ViewDependencies viewDependencies) {
        if (instance == null) {
            instance = new ViewStackManager(viewDependencies);
        }
        return instance;
    }

    /**
     * Initializes the ViewStackManager with the ViewDependencies.
     * This method should be called once during application startup.
     *
     * @param navigationService the RoleBasedNavigationService instance
     * @param viewRegistry the ViewRegistry instance
     * @return the ViewStackManager instance
     */
    public static ViewStackManager initialize(
                                             com.belman.presentation.navigation.RoleBasedNavigationService navigationService,
                                             ViewRegistry viewRegistry) {
        logger.startup("Initializing ViewStackManager");

        // Create ViewDependencies
        ViewDependencies viewDependencies = new ViewDependencies(navigationService, viewRegistry);

        // Get the ViewStackManager instance
        ViewStackManager manager = getInstance(viewDependencies);

        // Register all views
        manager.registerAllViews();

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

        // Create the view if it doesn't exist in the cache
        if (!viewCache.containsKey(viewId)) {
            View view = createView(viewId);
            viewCache.put(viewId, view);
        }

        // Get the view from the cache
        View view = viewCache.get(viewId);

        // Notify the view that it's being shown
        if (view instanceof com.belman.presentation.lifecycle.ViewLifecycle) {
            ((com.belman.presentation.lifecycle.ViewLifecycle<?, ?>) view).onViewShown();
        }

        logger.success("Navigated to view: " + viewId);
    }

    /**
     * Alias for navigateTo for compatibility with the issue description.
     *
     * @param viewId the view ID
     */
    public void push(String viewId) {
        navigateTo(viewId);
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

        // Get the current view and notify it that it's being hidden
        String currentViewId = viewStack.peek();
        View currentView = viewCache.get(currentViewId);
        if (currentView instanceof com.belman.presentation.lifecycle.ViewLifecycle) {
            ((com.belman.presentation.lifecycle.ViewLifecycle<?, ?>) currentView).onViewHidden();
        }

        // Remove the current view from the stack
        viewStack.pop();

        // Get the previous view
        String previousViewId = viewStack.peek();
        logger.debug("Navigating back to previous view: {}", previousViewId);

        // Get the view from the cache
        View previousView = viewCache.get(previousViewId);

        // Notify the view that it's being shown
        if (previousView instanceof com.belman.presentation.lifecycle.ViewLifecycle) {
            ((com.belman.presentation.lifecycle.ViewLifecycle<?, ?>) previousView).onViewShown();
        }

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


        // Register the QA dashboard view (archived version)
        // registerView("QADashboardView", new QADashboardViewFactory(viewDependencies));

        // Register the new QA dashboard view
        registerView("QADashboardView", new com.belman.presentation.usecases.qa.dashboard.QADashboardViewFactory(viewDependencies));

        // Register the Photo Review view (archived version)
        // registerView("PhotoReviewView", new PhotoReviewViewFactory(viewDependencies));

        // Register the new Photo Review view
        registerView("PhotoReviewView", new com.belman.presentation.usecases.qa.review.PhotoReviewViewFactory(viewDependencies));

        // Register the Approval Summary view
        registerView("ApprovalSummaryView", new com.belman.presentation.usecases.qa.summary.ApprovalSummaryViewFactory(viewDependencies));

        // Register the Photo Upload view
        registerView("PhotoUploadView", new PhotoUploadViewFactory(viewDependencies));

        // Register the Photo Workflow view
        registerView("PhotoWorkflowView", new PhotoWorkflowViewFactory(viewDependencies));

        // Register the Order Gallery view
        registerView("OrderGalleryView", new OrderGalleryViewFactory(viewDependencies));

        // Register the Report Preview view
        registerView("ReportPreviewView", new ReportPreviewViewFactory(viewDependencies));

        // Register the User Management view
        registerView("UserManagementView", new UserManagementViewFactory(viewDependencies));

        // Register the Logout view
        registerView("LogoutView", new LogoutViewFactory(viewDependencies));

        // Register the Production Worker Flow views
        registerView("AssignedOrderView", new AssignedOrderViewFactory(viewDependencies));
        registerView("PhotoCubeView", new PhotoCubeViewFactory(viewDependencies));
        registerView("CaptureView", new CaptureViewFactory(viewDependencies));
        registerView("SummaryView", new SummaryViewFactory(viewDependencies));
        registerView("CompletedView", new CompletedViewFactory(viewDependencies));

        // All views have been registered

        logger.success("All views registered successfully");
    }
}
