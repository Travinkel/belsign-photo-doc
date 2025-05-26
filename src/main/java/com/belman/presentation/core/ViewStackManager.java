package com.belman.presentation.core;

import com.belman.common.logging.EmojiLogger;
import com.belman.presentation.di.ViewDependencies;
import com.belman.presentation.usecases.login.LoginViewFactory;
import com.belman.presentation.usecases.splash.SplashViewFactory;
import com.belman.presentation.usecases.worker.assignedorder.AssignedOrderViewFactory;
import com.belman.presentation.usecases.worker.photocube.PhotoCubeViewFactory;
import com.belman.presentation.usecases.worker.capture.CaptureViewFactory;
import com.belman.presentation.usecases.worker.summary.SummaryViewFactory;
import com.belman.presentation.usecases.worker.completed.CompletedViewFactory;
import com.gluonhq.charm.glisten.mvc.View;
import javafx.scene.layout.StackPane;

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
    private static StackPane rootPane;
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
     * Initializes the ViewStackManager with a root StackPane.
     * This method should be called once during application startup.
     *
     * @param pane the root StackPane
     */
    public static void initWithRootPane(StackPane pane) {
        logger.debug("Initializing ViewStackManager with root pane");
        rootPane = pane;
        logger.success("ViewStackManager initialized with root pane");
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
     * Navigates to the specified view with a smooth transition.
     *
     * @param viewId the view ID
     */
    public void navigateTo(String viewId) {
        try {
            logger.debug("Navigating to view: {}", viewId);

            // Check if root pane is initialized
            if (rootPane == null) {
                logger.error("Root pane is null. Call initWithRootPane() before navigating.");
                throw new IllegalStateException("Root pane is null. Call initWithRootPane() before navigating.");
            }

            // Push the view ID onto the stack
            viewStack.push(viewId);

            // Create the view if it doesn't exist in the cache
            View view;
            try {
                if (!viewCache.containsKey(viewId)) {
                    view = createView(viewId);
                    viewCache.put(viewId, view);
                } else {
                    view = viewCache.get(viewId);
                }
            } catch (Exception e) {
                logger.error("Failed to create or retrieve view: " + viewId + " - " + e.getMessage());
                // Create a fallback error view
                view = createErrorView("Failed to load view: " + viewId, e.getMessage());
                // Don't cache the error view
            }

            // Get the current view for transition
            View currentView = null;
            if (!rootPane.getChildren().isEmpty() && rootPane.getChildren().get(0) instanceof View) {
                currentView = (View) rootPane.getChildren().get(0);
            }

            // Set initial opacity for smooth transition
            view.setOpacity(0.0);

            // Notify the view that it's being shown
            try {
                if (view instanceof com.belman.presentation.lifecycle.ViewLifecycle) {
                    ((com.belman.presentation.lifecycle.ViewLifecycle<?, ?>) view).onViewShown();
                }
            } catch (Exception e) {
                logger.warn("Error in onViewShown for view: " + viewId + " - " + e.getMessage());
                // Continue with navigation despite the error
            }

            // Add the new view to the scene
            final View finalView = view;

            // Create fade-out transition for current view if it exists
            if (currentView != null) {
                javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(150), currentView);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    // Replace with new view after fade out
                    rootPane.getChildren().setAll(finalView);

                    // Create fade-in transition for new view
                    javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), finalView);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                // No current view, just add the new view and fade it in
                rootPane.getChildren().setAll(finalView);
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), finalView);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            logger.success("✅ Navigated to view: " + viewId);
        } catch (Exception e) {
            logger.error("Navigation failed: " + e.getMessage());
            e.printStackTrace();
            // Show error view if navigation fails completely
            showErrorView("Navigation Failed", "Failed to navigate to view: " + viewId + "\n" + e.getMessage());
        }
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
     * Navigates back to the previous view with a smooth transition.
     *
     * @return true if navigation was successful, false if there is no previous view
     */
    public boolean navigateBack() {
        try {
            logger.debug("Attempting to navigate back. Stack size: {}", viewStack.size());

            // Check if we can navigate back
            if (viewStack.size() <= 1) {
                logger.warn("Cannot navigate back: no previous view in stack");
                return false;
            }

            // Check if root pane is initialized
            if (rootPane == null) {
                logger.error("Root pane is null. Call initWithRootPane() before navigating.");
                throw new IllegalStateException("Root pane is null. Call initWithRootPane() before navigating.");
            }

            // Get the current view and notify it that it's being hidden
            String currentViewId = viewStack.peek();
            View currentView = viewCache.get(currentViewId);

            try {
                if (currentView instanceof com.belman.presentation.lifecycle.ViewLifecycle) {
                    ((com.belman.presentation.lifecycle.ViewLifecycle<?, ?>) currentView).onViewHidden();
                }
            } catch (Exception e) {
                logger.warn("Error in onViewHidden for view: " + currentViewId + " - " + e.getMessage());
                // Continue with navigation despite the error
            }

            // Remove the current view from the stack
            viewStack.pop();

            // Get the previous view
            String previousViewId = viewStack.peek();
            logger.debug("Navigating back to previous view: {}", previousViewId);

            // Get the view from the cache
            View previousView;
            try {
                previousView = viewCache.get(previousViewId);
                if (previousView == null) {
                    throw new IllegalStateException("Previous view not found in cache: " + previousViewId);
                }
            } catch (Exception e) {
                logger.error("Failed to retrieve previous view: " + previousViewId + " - " + e.getMessage());
                // Create a fallback error view
                previousView = createErrorView("Failed to load previous view", e.getMessage());
                // Don't cache the error view
            }

            // Set initial opacity for smooth transition
            previousView.setOpacity(0.0);

            // Notify the view that it's being shown
            try {
                if (previousView instanceof com.belman.presentation.lifecycle.ViewLifecycle) {
                    ((com.belman.presentation.lifecycle.ViewLifecycle<?, ?>) previousView).onViewShown();
                }
            } catch (Exception e) {
                logger.warn("Error in onViewShown for view: " + previousViewId + " - " + e.getMessage());
                // Continue with navigation despite the error
            }

            // Create fade-out transition for current view
            final View finalPreviousView = previousView;

            logger.debug("RootPane before: {}", rootPane.getChildren());
            logger.debug("Setting new root for view: {}", previousViewId);

            if (currentView != null) {
                javafx.animation.FadeTransition fadeOut = new javafx.animation.FadeTransition(javafx.util.Duration.millis(150), currentView);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    // Replace with previous view after fade out
                    rootPane.getChildren().setAll(finalPreviousView);

                    // Create fade-in transition for previous view
                    javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), finalPreviousView);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                });
                fadeOut.play();
            } else {
                // No current view, just add the previous view and fade it in
                rootPane.getChildren().setAll(finalPreviousView);
                javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(200), finalPreviousView);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            }

            logger.success("✅ Successfully navigated back to: " + previousViewId);
            return true;
        } catch (Exception e) {
            logger.error("Navigation back failed: " + e.getMessage());
            e.printStackTrace();
            // Show error view if navigation fails completely
            showErrorView("Navigation Failed", "Failed to navigate back\n" + e.getMessage());
            return false;
        }
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

        // Register the admin view (archived version)
        // registerView("AdminView", new AdminViewFactory(viewDependencies));

        // Register the new Admin Dashboard view
        registerView("AdminDashboardView", new com.belman.presentation.usecases.admin.dashboard.AdminDashboardViewFactory(viewDependencies));

        // Register the new User Management view
        registerView("UserManagementView", new com.belman.presentation.usecases.admin.usermanagement.UserManagementViewFactory(viewDependencies));

        // Register the System Settings view
        registerView("SystemSettingsView", new com.belman.presentation.usecases.admin.settings.SystemSettingsViewFactory(viewDependencies));

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

        // Register the QA Done view
        registerView("QADoneView", new com.belman.presentation.usecases.qa.done.QADoneViewFactory(viewDependencies));

        // Register the QA Order Assignment view
        registerView("QAOrderAssignmentView", new com.belman.presentation.usecases.qa.assignment.QAOrderAssignmentViewFactory(viewDependencies));

        // Legacy views - commented out as they are no longer used
        // registerView("PhotoUploadView", new PhotoUploadViewFactory(viewDependencies));
        // registerView("PhotoWorkflowView", new PhotoWorkflowViewFactory(viewDependencies));
        // registerView("OrderGalleryView", new OrderGalleryViewFactory(viewDependencies));
        // registerView("ReportPreviewView", new ReportPreviewViewFactory(viewDependencies));
        // registerView("LogoutView", new LogoutViewFactory(viewDependencies));

        // Register the Production Worker Flow views
        registerView("AssignedOrderView", new AssignedOrderViewFactory(viewDependencies));
        registerView("PhotoCubeView", new PhotoCubeViewFactory(viewDependencies));
        registerView("CaptureView", new CaptureViewFactory(viewDependencies));
        registerView("SummaryView", new SummaryViewFactory(viewDependencies));
        registerView("CompletedView", new CompletedViewFactory(viewDependencies));

        // All views have been registered

        logger.success("All views registered successfully");
    }

    /**
     * Creates an error view to display when view loading fails.
     *
     * @param title the error title
     * @param message the error message
     * @return the error view
     */
    private View createErrorView(String title, String message) {
        try {
            // Create a simple View with error message
            View errorView = new View();

            // Create a VBox to hold the error content
            javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(15);
            content.setAlignment(javafx.geometry.Pos.CENTER);
            content.setPadding(new javafx.geometry.Insets(30));
            content.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb; -fx-border-width: 1px;");

            // Create title label
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #721c24;");

            // Create message label
            javafx.scene.control.Label messageLabel = new javafx.scene.control.Label(message);
            messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #721c24; -fx-wrap-text: true;");

            // Create retry button
            javafx.scene.control.Button retryButton = new javafx.scene.control.Button("Go Back");
            retryButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 10px 20px;");
            retryButton.setOnAction(e -> {
                // Try to navigate back
                navigateBack();
            });

            // Add components to VBox
            content.getChildren().addAll(titleLabel, messageLabel, retryButton);

            // Set the content of the view
            errorView.setCenter(content);

            return errorView;
        } catch (Exception e) {
            logger.error("Failed to create error view: " + e.getMessage());
            // Create an even simpler fallback view
            View fallbackView = new View();
            javafx.scene.control.Label label = new javafx.scene.control.Label("Error: " + title);
            fallbackView.setCenter(label);
            return fallbackView;
        }
    }

    /**
     * Shows an error view in the root pane.
     *
     * @param title the error title
     * @param message the error message
     */
    private void showErrorView(String title, String message) {
        try {
            if (rootPane == null) {
                logger.error("Cannot show error view: root pane is null");
                return;
            }

            View errorView = createErrorView(title, message);

            // Add the error view to the scene with a fade-in animation
            errorView.setOpacity(0.0);
            rootPane.getChildren().setAll(errorView);

            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(javafx.util.Duration.millis(300), errorView);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            logger.warn("Showing error view: " + title);
        } catch (Exception e) {
            logger.error("Failed to show error view: " + e.getMessage());
            // Last resort: show a simple alert
            try {
                javafx.application.Platform.runLater(() -> {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Navigation Error");
                    alert.setHeaderText(title);
                    alert.setContentText(message);
                    alert.showAndWait();
                });
            } catch (Exception ex) {
                logger.error("Failed to show error alert: " + ex.getMessage());
            }
        }
    }
}
