package com.belman.presentation.core;

import com.belman.application.core.GluonLifecycleManager;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.ProgressIndicator;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 * Base class for all views in the application.
 * This class extends Gluon's View class and provides additional functionality
 * for both desktop and mobile applications.
 * 
 * @param <T> The type of ViewModel associated with this view
 */
public abstract class BaseView<T extends BaseViewModel<?>> extends View {

    private final BaseController<?> controller;
    protected final T viewModel;

    // AppBar properties
    private final StringProperty title = new SimpleStringProperty("");
    private final BooleanProperty showBackButton = new SimpleBooleanProperty(false);
    private final BooleanProperty showMenuButton = new SimpleBooleanProperty(false);

    // Loading indicator
    private ProgressIndicator loadingIndicator;
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
                throw new RuntimeException("ViewLoader failed to load components for: " + this.getClass().getSimpleName());
            }

            // Set the loaded parent as the center of the view
            Node parentNode = (Node) components.parent();
            this.setCenter(parentNode);

            this.controller = components.controller();
            this.viewModel = (T) components.viewModel();

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
        // Register this view with the GluonLifecycleManager for lifecycle management
        try {
            GluonLifecycleManager.registerView(this);
        } catch (Exception e) {
            // Gracefully handle any exceptions during registration
            // This ensures that unit tests and other scenarios without the full framework still work
            System.err.println("Warning: Could not register view with GluonLifecycleManager: " + e.getMessage());
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
     * Called when the view is shown.
     * Delegates to the ViewModel's onShow method.
     */
    protected void onViewShown() {
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
     */
    protected void onViewHidden() {
        if (viewModel != null) {
            viewModel.onHide();
        }

        // Call the lifecycle method
        onHide();
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
     * Gets the ViewModel associated with this view.
     * 
     * @return The ViewModel
     */
    public T getViewModel() {
        return viewModel;
    }

    /**
     * Sets up the AppBar for this view.
     */
    protected void setUpAppBar() {
        // To be overridden by subclasses
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
     * Updates the AppBar with the current properties.
     * This method gracefully handles cases where MobileApplication.getInstance() returns null,
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
     * Sets the title of the view.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title.set(title);
        updateAppBar();
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
     * Gets the title property.
     *
     * @return the title property
     */
    public StringProperty titleProperty() {
        return title;
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
     * Gets whether the back button is shown.
     *
     * @return true if the back button is shown, false otherwise
     */
    public boolean isShowBackButton() {
        return showBackButton.get();
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
     * Sets whether to show the menu button.
     *
     * @param show true to show the menu button, false otherwise
     */
    public void setShowMenuButton(boolean show) {
        showMenuButton.set(show);
        updateAppBar();
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
}
