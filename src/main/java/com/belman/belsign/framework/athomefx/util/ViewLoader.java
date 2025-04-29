package com.belman.belsign.framework.athomefx.util;

import com.belman.belsign.framework.athomefx.core.BaseController;
import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import com.belman.belsign.framework.athomefx.di.ServiceLocator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Utility class for loading FXML views and creating the corresponding controllers and view models.
 */
public class ViewLoader {

    /**
     * Record containing the loaded components: parent node, controller, and view model.
     */
    public record LoadedComponents(Parent parent, BaseController<?> controller, BaseViewModel<?> viewModel) {}

    /**
     * Loads a view and creates the corresponding controller and view model.
     *
     * @param viewClass the view class
     * @param <T> the view model type
     * @return the loaded components
     * @throws RuntimeException if the view cannot be loaded
     */
    public static <T extends BaseViewModel<?>> LoadedComponents load(Class<?> viewClass) {
        try {
            // Load the FXML file
            String fxmlFileName = NamingConventions.getFxmlFileName(viewClass.getSimpleName());
            FXMLLoader loader = new FXMLLoader(viewClass.getResource(fxmlFileName));
            Parent parent = loader.load();
            BaseController<T> controller = loader.getController();

            // Create the view model
            T viewModel = createViewModel(viewClass);

            // Inject services into ViewModel
            ServiceLocator.injectServices(viewModel);

            // Bind ViewModel to Controller
            if (controller != null) {
                controller.setViewModel(viewModel);
                controller.initializeBinding();
            }

            return new LoadedComponents(parent, controller, viewModel);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML for view: " + viewClass.getSimpleName(), e);
        }
    }

    /**
     * Creates a view model instance for the given view class.
     * Uses NamingConventions to find the view model class based on standard naming patterns.
     *
     * @param viewClass the view class
     * @param <T> the view model type
     * @return the view model instance
     * @throws RuntimeException if the view model class cannot be found or instantiated
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseViewModel<?>> T createViewModel(Class<?> viewClass) {
        String viewClassName = viewClass.getSimpleName();

        // Get possible view model class names using NamingConventions
        List<String> possibleViewModelClassNames = NamingConventions.getPossibleViewModelClassNames(viewClass);

        Exception lastException = null;

        for (String viewModelClassName : possibleViewModelClassNames) {
            try {
                Class<?> viewModelClass = Class.forName(viewModelClassName);
                return (T) viewModelClass.getDeclaredConstructor().newInstance();
            } catch (ClassNotFoundException e) {
                // Try the next naming convention
                lastException = e;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to instantiate view model: " + viewModelClassName, e);
            }
        }

        throw new RuntimeException("Could not find view model class for view: " + viewClassName + 
            ". Tried: " + String.join(", ", possibleViewModelClassNames), lastException);
    }
}
