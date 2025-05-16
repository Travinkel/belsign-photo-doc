package com.belman.ui.base;

import com.belman.bootstrap.lifecycle.LifecycleManager;
import com.belman.ui.core.ViewLoader;
import com.belman.ui.lifecycle.ViewLifecycle;
import com.belman.ui.utils.DialogUtils;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

/**
 * Base class for all views in the application.
 * This class extends Gluon's View class and provides additional functionality
 * for both desktop and mobile applications.
 *
 * @param <T> The type of ViewModel associated with this view
 */
public abstract class BaseView<T extends BaseViewModel<?>> extends View implements ViewLifecycle<T, BaseController<?>> {

    protected final T viewModel;
    private final BaseController<?> controller;
    // AppBar properties
    private final StringProperty title = new SimpleStringProperty("");
    private final BooleanProperty showBackButton = new SimpleBooleanProperty(false);
    private final BooleanProperty showMenuButton = new SimpleBooleanProperty(false);

    // Loading indicator
    private final ProgressIndicator loadingIndicator;
    private boolean loadingIndicatorAdded = false;

    /**
     * Creates a new BaseView.
     * Loads the FXML and initializes the controller and viewModel.
     */
    @SuppressWarnings("unchecked")
    public BaseView() {
        try {
            var components = ViewLoader.load(this.getClass());
            if (components == null) {
                throw new RuntimeException("ViewLoader failed to load components for: " + getClass().getSimpleName());
            }

            this.viewModel = (T) components.viewModel();
            this.controller = components.controller();
            this.setCenter((Node) components.parent());

            // Initialize the loading indicator
            loadingIndicator = new ProgressIndicator();
            loadingIndicator.setVisible(false);
            loadingIndicator.setMaxSize(50, 50);

            // Set up lifecycle listeners
            initializeLifecycleListeners();

        } catch (Exception e) {
            throw new RuntimeException("Failed to load view: " + this.getClass().getSimpleName(), e);
        }
    }

    private void initializeLifecycleListeners() {
        // Register this view with the LifecycleManager for lifecycle management
        try {
            LifecycleManager.registerView(this);
        } catch (Exception e) {
            // Gracefully handle any exceptions during registration
            // This ensures that unit tests and other scenarios without the full framework still work
            System.err.println("Warning: Could not register view with LifecycleManager: " + e.getMessage());
        }

        // Keep the original listener for backward compatibility
        this.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                onViewShown();
            } else {
                onViewHidden();
            }
        });
    }

    /**
     * Updates the AppBar with the current properties.
     * This method gracefully handles cases where MobileApplicationManager.getInstance() returns null,
     * which can happen in unit tests or when running outside of a Gluon Mobile environment.
     */
    protected void updateAppBar() {
        try {
            // If the app bar should not be shown, do nothing
            if (!shouldShowAppBar()) {
                return;
            }

            MobileApplication app = MobileApplication.getInstance();
            if (app == null) {
                // In unit tests or non-Gluon environments, MobileApplication.getInstance() may return null
                return;
            }

            AppBar appBar = app.getAppBar();
            if (appBar != null) {
                appBar.setTitleText(title.get());
                appBar.setNavIcon(showBackButton.get() ?
                        MaterialDesignIcon.ARROW_BACK.button(e -> navigateBack()) : null);

                // Clear existing action items and add menu button if enabled
                if (appBar.getActionItems() != null) {
                    appBar.getActionItems().clear();

                    // Add menu button if enabled
                    if (showMenuButton.get()) {
                        appBar.getActionItems().add(MaterialDesignIcon.MENU.button(e -> showMenu()));
                    }
                }
            }
        } catch (Exception e) {
            // Gracefully handle any exceptions that might occur when accessing the AppBar
            // This ensures that unit tests and other scenarios without the full framework still work
            System.err.println("Warning: Could not update AppBar: " + e.getMessage());
        }
    }

    /**
     * Called when the view is shown.
     */
    public void onShow() {
        // Default implementation does nothing
    }

    /**
     * Called when the view is hidden.
     */
    public void onHide() {
        // Default implementation does nothing
    }

    /**
     * Checks if the app bar should be shown for this view.
     * By default, returns true. Override in subclasses to hide the app bar.
     *
     * @return true if the app bar should be shown, false otherwise
     */
    public boolean shouldShowAppBar() {
        return true;
    }

    /**
     * Navigates back to the previous view.
     */
    protected void navigateBack() {
        // To be overridden by subclasses or handled by a router
    }

    /**
     * Shows the menu.
     * This method should be overridden by subclasses that want to show a menu.
     */
    protected void showMenu() {
        // To be overridden by subclasses
    }

    /**
     * Creates the ViewModel for this View.
     * Default implementation uses NamingConventions to find the ViewModel class.
     *
     * @return the ViewModel instance
     */
    @SuppressWarnings("unchecked")
    protected T createViewModel() {
        try {
            String viewModelClassName = getClass().getName().replace("View", "ViewModel");
            Class<?> viewModelClass = Class.forName(viewModelClassName);
            return (T) viewModelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create ViewModel for " + getClass().getSimpleName(), e);
        }
    }

    /**
     * Loads the FXML file for this View.
     * Default implementation uses the View class name to find the FXML file.
     *
     * @return the FXMLLoader instance
     */
    protected FXMLLoader loadFXML() throws IOException {
        String viewName = getClass().getSimpleName();
        String fxmlPath = "/" + getClass().getPackageName().replace('.', '/') + "/" + viewName + ".fxml";

        URL fxmlUrl = getClass().getResource(viewName + ".fxml");
        if (fxmlUrl == null) {
            fxmlUrl = getClass().getResource(fxmlPath);
        }

        if (fxmlUrl == null) {
            throw new IOException("FXML file not found for " + viewName);
        }

        return new FXMLLoader(fxmlUrl);
    }

    /**
     * Sets up the AppBar for this view.
     */
    protected void setUpAppBar() {
        // To be overridden by subclasses
    }

    /**
     * Gets the underlying View object.
     * Implementation of the ViewLifecycle interface.
     *
     * @return this view
     */
    @Override
    public View getView() {
        return this;
    }

    /**
     * Gets the ViewModel associated with this view.
     *
     * @return The ViewModel
     */
    public T getViewModel() {
        return viewModel;
    }

    /**
     * Gets the controller associated with this view.
     *
     * @return The controller
     */
    public BaseController<?> getController() {
        return controller;
    }

    /**
     * Called when the view is shown.
     * Delegates to the ViewModel's onShow method.
     * Implementation of the ViewLifecycle interface.
     */
    @Override
    public void onViewShown() {
        if (viewModel != null) {
            viewModel.onShow();
        }

        // Update the AppBar
        updateAppBar();

        // Call the lifecycle method
        onShow();
    }

    /**
     * Called when the view is hidden.
     * Delegates to the ViewModel's onHide method.
     * Implementation of the ViewLifecycle interface.
     */
    @Override
    public void onViewHidden() {
        if (viewModel != null) {
            viewModel.onHide();
        }

        // Call the lifecycle method
        onHide();
    }

    /**
     * Gets the class name of the view.
     * Implementation of the ViewLifecycle interface.
     *
     * @return the simple class name of the view
     */
    @Override
    public String getViewName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Gets the title of the view.
     *
     * @return the title
     */
    public String getTitle() {
        return title.get();
    }

    /**
     * Sets the title of the view.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title.set(title);
        updateAppBar();
    }

    /**
     * Gets the title property.
     *
     * @return the title property
     */
    public StringProperty titleProperty() {
        return title;
    }

    /**
     * Gets whether the back button is shown.
     *
     * @return true if the back button is shown, false otherwise
     */
    public boolean isShowBackButton() {
        return showBackButton.get();
    }

    /**
     * Sets whether to show the back button.
     *
     * @param show true to show the back button, false otherwise
     */
    public void setShowBackButton(boolean show) {
        showBackButton.set(show);
        updateAppBar();
    }

    /**
     * Gets the showBackButton property.
     *
     * @return the showBackButton property
     */
    public BooleanProperty showBackButtonProperty() {
        return showBackButton;
    }

    /**
     * Gets whether the menu button is shown.
     *
     * @return true if the menu button is shown, false otherwise
     */
    public boolean isShowMenuButton() {
        return showMenuButton.get();
    }

    /**
     * Sets whether to show the menu button.
     *
     * @param show true to show the menu button, false otherwise
     */
    public void setShowMenuButton(boolean show) {
        showMenuButton.set(show);
        updateAppBar();
    }

    /**
     * Gets the showMenuButton property.
     *
     * @return the showMenuButton property
     */
    public BooleanProperty showMenuButtonProperty() {
        return showMenuButton;
    }

    /**
     * Shows a loading indicator.
     */
    public void showLoading() {
        if (!loadingIndicatorAdded) {
            getChildren().add(loadingIndicator);
            loadingIndicatorAdded = true;
        }
        loadingIndicator.setVisible(true);
    }

    /**
     * Hides the loading indicator.
     */
    public void hideLoading() {
        loadingIndicator.setVisible(false);
    }


    /**
     * Executes a function and handles any errors that occur.
     *
     * @param function the function to execute
     */
    protected void tryExecute(Runnable function) {
        try {
            function.run();
        } catch (Throwable error) {
            handleError(error);
        }
    }

    /**
     * Handles an error.
     *
     * @param error the error to handle
     */
    protected void handleError(Throwable error) {
        logger.error("Error in view: {}", error.getMessage(), error);

        // Show an error dialog
        DialogUtils.showError(
                "Error",
                "An error occurred",
                error.getMessage()
        );

        // Render a fallback UI
        Node fallback = renderFallback(error);
        if (fallback != null) {
            // Replace the current content with the fallback
            getChildren().clear();
            getChildren().add(fallback);
        }
    }

    /**
     * Renders a fallback UI when an error occurs.
     * Subclasses can override this method to provide a custom fallback UI.
     *
     * @param error the error that occurred
     * @return the fallback UI
     */
    protected Node renderFallback(Throwable error) {
        VBox fallback = new VBox(10);
        fallback.getStyleClass().add("error-fallback");

        Label errorLabel = new Label("An error occurred");
        errorLabel.getStyleClass().add("error-title");

        Label messageLabel = new Label(error.getMessage());
        messageLabel.getStyleClass().add("error-message");
        messageLabel.setWrapText(true);

        fallback.getChildren().addAll(errorLabel, messageLabel);

        return fallback;
    }

    /**
     * Executes a function that returns a value and handles any errors that occur.
     *
     * @param <R>      the type of the return value
     * @param function the function to execute
     * @param fallback the fallback value to return if an error occurs
     * @return the result of the function, or the fallback value if an error occurs
     */
    protected <R> R tryExecute(ThrowingSupplier<R> function, R fallback) {
        try {
            return function.get();
        } catch (Throwable error) {
            handleError(error);
            return fallback;
        }
    }

    /**
     * Functional interface for a supplier that can throw an exception.
     *
     * @param <R> the type of the return value
     */
    @FunctionalInterface
    public interface ThrowingSupplier<R> {
        /**
         * Gets a result.
         *
         * @return a result
         * @throws Throwable if an error occurs
         */
        R get() throws Throwable;
    }
}
