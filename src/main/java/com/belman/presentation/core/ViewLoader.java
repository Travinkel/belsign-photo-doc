package com.belman.presentation.core;

import com.belman.application.core.ServiceLocator;
import com.belman.infrastructure.NamingConventions;
import javafx.fxml.FXMLLoader;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;

/**
 * Utility class for loading views and creating the corresponding controllers and view models.
 * This class is designed to work with Gluon Mobile and standard JavaFX applications.
 */
public class ViewLoader {

    /**
     * Record containing the loaded components: parent, controller and view model.
     *
     * @param <T> the view model type
     * @param <P> the parent type (e.g., Parent in JavaFX)
     */
    public record LoadedComponents<T extends BaseViewModel<?>, P>(P parent, BaseController<T> controller, T viewModel) {
    }

    /**
     * Creates a view model instance for the given view class.
     * Uses NamingConventions to find the view model class based on standard naming patterns.
     *
     * @param viewClass the view class
     * @param <T>       the view model type
     * @return the view model instance
     * @throws RuntimeException if the view model class cannot be found or instantiated
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseViewModel<?>> T createViewModel(Class<?> viewClass) {
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
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException("Failed to instantiate view model: " + viewModelClassName, e);
            }
        }

        throw new RuntimeException("Could not find view model class for view: " + viewClassName +
                                   ". Tried: " + String.join(", ", possibleViewModelClassNames), lastException);
    }

    /**
     * Injects services into the view model and binds it to the controller.
     *
     * @param controller the controller
     * @param viewModel  the view model
     * @param <T>        the view model type
     */
    @SuppressWarnings("unchecked")
    protected static <T extends BaseViewModel<?>> void setupViewModel(BaseController<T> controller, T viewModel) {
        // Inject services into ViewModel
        ServiceLocator.injectServices(viewModel);

        // Bind ViewModel to Controller
        if (controller != null) {
            controller.setViewModel(viewModel);
            controller.initializeBinding();
        }
    }

    /**
     * Loads a view and its associated controller and view model.
     * This method works with both standard JavaFX and Gluon Mobile applications.
     * 
     * @param viewClass the view class to load
     * @param <T> the view model type
     * @param <P> the parent type (e.g., Parent in JavaFX)
     * @return a record containing the loaded components
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseViewModel<?>, P> LoadedComponents<T, P> load(Class<?> viewClass) {
        try {
            // Load the FXML file
            String path = "/" + viewClass.getPackageName().replace('.', '/') + "/" + viewClass.getSimpleName() + ".fxml";
            URL fxmlUrl = viewClass.getResource(viewClass.getSimpleName() + ".fxml");

            // If the FXML file is not found in the same package as the view class, try to load it from the constructed path
            if (fxmlUrl == null) {
                fxmlUrl = ViewLoader.class.getResource(path);
            }

            if (fxmlUrl == null) {
                throw new FileNotFoundException("FXML file not found: " + path);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            P root = (P) loader.load();

            // Get the controller
            BaseController<T> controller = loader.getController();

            // Create the view model
            T viewModel = createViewModel(viewClass);

            // Inject services into the controller and view model
            ServiceLocator.injectServices(controller);
            ServiceLocator.injectServices(viewModel);

            // Set up the view model
            setupViewModel(controller, viewModel);

            return new LoadedComponents<>(root, controller, viewModel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load view: " + viewClass.getSimpleName(), e);
        }
    }
}
