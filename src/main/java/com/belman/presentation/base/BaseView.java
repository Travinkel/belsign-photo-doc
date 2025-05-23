package com.belman.presentation.base;

import com.belman.bootstrap.lifecycle.LifecycleManager;
import com.belman.presentation.core.ViewLoader;
import com.belman.presentation.lifecycle.ViewLifecycle;
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
     * This method uses GlistenUtils to get the AppBar for this view.
     */
    protected void updateAppBar() {
        try {
            // If the app bar should not be shown, do nothing
            if (!shouldShowAppBar()) {
                return;
            }

            // Get the AppBar for this view using GlistenUtils
            AppBar appBar = com.belman.presentation.util.GlistenUtils.getAppBar(this);
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

                // Call the view-specific updateAppBar method
                updateAppBar(appBar);
            }
        } catch (Exception e) {
            // Gracefully handle any exceptions that might occur when accessing the AppBar
            // This ensures that unit tests and other scenarios without the full framework still work
            System.err.println("Warning: Could not update AppBar: " + e.getMessage());
        }
    }

    /**
     * Updates the AppBar with view-specific properties.
     * This method is called by updateAppBar() and should be overridden by subclasses
     * that need to customize the AppBar.
     *
     * @param appBar the AppBar to update
     */
    public void updateAppBar(AppBar appBar) {
        // Default implementation does nothing
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

        // Special case for authentication.login package in usecases
        if (fxmlUrl == null && fxmlPath.contains("/presentation/usecases/authentication/login/")) {
            String loginPath = fxmlPath.replace("/presentation/usecases/authentication/login/", "/presentation/usecases/login/");
            System.out.println("FXML not found in authentication.login path, trying login path: " + loginPath);
            fxmlUrl = getClass().getResource(loginPath);
        }


        if (fxmlUrl == null) {
            String errorMessage = "FXML file not found for " + viewName + ". Tried paths: " + fxmlPath;

            if (fxmlPath.contains("/presentation/usecases/authentication/login/")) {
                errorMessage += ", " + fxmlPath.replace("/presentation/usecases/authentication/login/", "/presentation/usecases/login/");
            }

            throw new IOException(errorMessage);
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

        // Call beforeShow on the controller if it implements FlowViewController
        if (controller instanceof com.belman.presentation.core.FlowViewController) {
            ((com.belman.presentation.core.FlowViewController) controller).beforeShow();
        }
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

        // Call afterHide on the controller if it implements FlowViewController
        if (controller instanceof com.belman.presentation.core.FlowViewController) {
            ((com.belman.presentation.core.FlowViewController) controller).afterHide();
        }
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

}
