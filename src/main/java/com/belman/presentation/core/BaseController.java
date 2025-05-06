package com.belman.presentation.core;

import com.belman.application.core.ControllerLifecycle;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Base controller class for all UI controllers in the MVVM+C pattern.
 * <p>
 * In the MVVM+C pattern, controllers are responsible for:
 * <ul>
 *     <li>Handling UI events</li>
 *     <li>Coordinating between views and ViewModels</li>
 *     <li>Managing navigation and routing</li>
 *     <li>Initializing and binding ViewModels to Views</li>
 * </ul>
 * <p>
 * Controllers should contain minimal logic, delegating to ViewModels for presentation logic
 * and to application services for business logic. They should not contain business or presentation logic.
 *
 * @param <T> the type of ViewModel this controller works with
 */
public abstract class BaseController<T extends BaseViewModel<?>> implements Initializable, ControllerLifecycle {

    /**
     * The ViewModel associated with this controller.
     */
    protected T viewModel;

    /**
     * Sets the ViewModel for this controller.
     * This method should be called before the controller is initialized.
     *
     * @param viewModel the ViewModel to set
     */
    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Gets the ViewModel for this controller.
     *
     * @return the ViewModel
     */
    public T getViewModel() {
        return viewModel;
    }

    /**
     * Gets the class name of the controller.
     * 
     * @return the simple class name of the controller
     */
    @Override
    public String getControllerName() {
        return this.getClass().getSimpleName();
    }

    /**
     * Initializes the controller.
     * This method is called automatically by JavaFX when the FXML is loaded.
     * <p>
     * Subclasses should override this method to:
     * <ul>
     *     <li>Set up bindings between UI components and ViewModel properties</li>
     *     <li>Register event handlers</li>
     *     <li>Initialize the UI state</li>
     * </ul>
     *
     * @param location   the location used to resolve relative paths for the root object
     * @param resources  the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (viewModel == null) {
            throw new IllegalStateException("ViewModel must be set before initializing controller");
        }

        // Set up common bindings and initialization
        setupBindings();

        // Initialize the ViewModel
        viewModel.initialize();
    }

    /**
     * Sets up bindings between UI components and ViewModel properties.
     * This method should be overridden by subclasses to establish
     * specific bindings for their UI components.
     */
    protected abstract void setupBindings();

    /**
     * Shows an error message to the user.
     * Default implementation does nothing and should be overridden by subclasses
     * to display errors in an appropriate way.
     *
     * @param message the error message to show
     */
    protected void showError(String message) {
        // Default empty implementation, to be overridden by subclasses
    }

    /**
     * Called when the view is about to be closed or navigated away from.
     * This method should clean up any resources used by the controller
     * and call the ViewModel's dispose method.
     */
    public void onClose() {
        if (viewModel != null) {
            viewModel.dispose();
        }
    }

    /**
     * Utility method to run a task on the JavaFX application thread.
     *
     * @param task the task to run
     */
    protected void runOnUiThread(Runnable task) {
        Platform.runLater(task);
    }

    /**
     * Initializes the controller.
     * This method is called once when the controller is created.
     */
    @Override
    public void initialize() {
        // Default implementation initializes bindings
        Platform.runLater(this::initializeBinding);
    }

    /**
     * Initializes the binding between the view model and the view.
     * Override this method in subclasses to set up bindings.
     */
    public void initializeBinding() {
        // Optional: Override in subclasses to bind ViewModel to the view
    }

    /**
     * Called when the associated view is shown.
     * Override this method to perform initialization when the view becomes visible.
     */
    @Override
    public void onShow() {
        // Default implementation does nothing
    }

    /**
     * Called when the associated view is hidden.
     * Override this method to perform cleanup when the view is no longer visible.
     */
    @Override
    public void onHide() {
        // Default implementation does nothing
    }
}
