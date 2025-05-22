package com.belman.presentation.base;

import com.belman.presentation.core.FlowViewController;
import com.belman.presentation.lifecycle.ControllerLifecycle;
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
public abstract class BaseController<T extends BaseViewModel<?>> implements Initializable, ControllerLifecycle, FlowViewController {

    /**
     * The ViewModel associated with this controller.
     */
    protected T viewModel;

    /**
     * Gets the ViewModel for this controller.
     *
     * @return the ViewModel
     */
    public T getViewModel() {
        return viewModel;
    }

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
     * @param location  the location used to resolve relative paths for the root object
     * @param resources the resources used to localize the root object
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (viewModel == null) {
            System.err.println("Error: ViewModel is null in " + getClass().getSimpleName());
            throw new IllegalStateException("ViewModel must be set before initializing controller");
        }

        try {
            // Set up common bindings and initialization
            setupBindings();
        } catch (Exception e) {
            System.err.println("Error in setupBindings() for " + getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            // Continue execution to avoid crashing the application
        }

        try {
            // Initialize the ViewModel
            viewModel.initialize();
        } catch (Exception e) {
            System.err.println("Error initializing ViewModel in " + getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            // Continue execution to avoid crashing the application
        }
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
     * Called before the view is shown.
     * This method delegates to the onShow() method for backward compatibility.
     */
    @Override
    public void beforeShow() {
        onShow();

        // Also notify the ViewModel
        if (viewModel != null) {
            viewModel.onShow();
        }
    }

    /**
     * Called after the view is hidden.
     * This method delegates to the onHide() method for backward compatibility.
     */
    @Override
    public void afterHide() {
        onHide();

        // Also notify the ViewModel
        if (viewModel != null) {
            viewModel.onHide();
        }
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
     * Safely binds a property to a UI element, checking for null first.
     * This helps prevent NullPointerExceptions when UI elements are not properly initialized.
     *
     * @param uiElement The UI element to bind to
     * @param property The property to bind
     * @param <T> The type of the property
     * @return true if binding was successful, false otherwise
     */
    protected <T> boolean safelyBind(javafx.scene.control.Labeled uiElement, javafx.beans.property.Property<T> property) {
        if (uiElement == null) {
            System.err.println("Warning: UI element is null in " + getClass().getSimpleName() + " when trying to bind property");
            return false;
        }

        if (property == null) {
            System.err.println("Warning: Property is null in " + getClass().getSimpleName() + " when trying to bind to " + uiElement);
            return false;
        }

        try {
            if (property instanceof javafx.beans.property.StringProperty) {
                uiElement.textProperty().bind((javafx.beans.property.StringProperty) property);
            } else {
                // For non-string properties, use a binding that converts to string
                uiElement.textProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
                    () -> property.getValue() != null ? property.getValue().toString() : "",
                    property
                ));
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error binding property to UI element in " + getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely binds a property to a UI element's visibility, checking for null first.
     *
     * @param uiElement The UI element to bind to
     * @param property The property to bind
     * @return true if binding was successful, false otherwise
     */
    protected boolean safelyBindVisibility(javafx.scene.Node uiElement, javafx.beans.property.BooleanProperty property) {
        if (uiElement == null) {
            System.err.println("Warning: UI element is null in " + getClass().getSimpleName() + " when trying to bind visibility");
            return false;
        }

        if (property == null) {
            System.err.println("Warning: Property is null in " + getClass().getSimpleName() + " when trying to bind visibility to " + uiElement);
            return false;
        }

        try {
            uiElement.visibleProperty().bind(property);
            return true;
        } catch (Exception e) {
            System.err.println("Error binding visibility property to UI element in " + getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Safely sets an event handler on a UI element, checking for null first.
     *
     * @param uiElement The UI element to set the event handler on
     * @param eventHandler The event handler to set
     * @param <T> The type of the event
     * @return true if setting the event handler was successful, false otherwise
     */
    protected <T extends javafx.event.Event> boolean safelySetEventHandler(javafx.scene.Node uiElement, javafx.event.EventType<T> eventType, javafx.event.EventHandler<T> eventHandler) {
        if (uiElement == null) {
            System.err.println("Warning: UI element is null in " + getClass().getSimpleName() + " when trying to set event handler");
            return false;
        }

        if (eventHandler == null) {
            System.err.println("Warning: Event handler is null in " + getClass().getSimpleName() + " when trying to set on " + uiElement);
            return false;
        }

        try {
            uiElement.addEventHandler(eventType, eventHandler);
            return true;
        } catch (Exception e) {
            System.err.println("Error setting event handler on UI element in " + getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }
}
