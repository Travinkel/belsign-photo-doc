package com.belman.presentation.core;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.naming.NamingConventions;
import com.belman.presentation.base.BaseController;
import com.belman.presentation.base.BaseViewModel;
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
     * Loads a view and its associated controller and view model.
     * This method works with both standard JavaFX and Gluon Mobile applications.
     *
     * @param viewClass the view class to load
     * @param <T>       the view model type
     * @param <P>       the parent type (e.g., Parent in JavaFX)
     * @return a record containing the loaded components
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseViewModel<?>, P> LoadedComponents<T, P> load(Class<?> viewClass) {
        try {
            System.out.println("Loading view: " + viewClass.getSimpleName());

            // Load the FXML file
            String path = "/" + viewClass.getPackageName().replace('.', '/') + "/" + viewClass.getSimpleName() +
                          ".fxml";
            URL fxmlUrl = viewClass.getResource(viewClass.getSimpleName() + ".fxml");
            System.out.println("Looking for FXML at: " + path);

            // If the FXML file is not found in the same package as the view class, try to load it from the constructed path
            if (fxmlUrl == null) {
                System.out.println("FXML not found in view class package, trying constructed path: " + path);
                fxmlUrl = ViewLoader.class.getResource(path);

                // If still null, try another approach
                if (fxmlUrl == null) {
                    path = "/com/belman/presentation/views/" + viewClass.getSimpleName().toLowerCase() + "/" +
                           viewClass.getSimpleName() + ".fxml";
                    System.out.println("Still not found, trying convention-based path: " + path);
                    fxmlUrl = ViewLoader.class.getResource(path);
                }
            }

            if (fxmlUrl == null) {
                throw new FileNotFoundException("FXML file not found: " + path);
            }

            System.out.println("Found FXML at: " + fxmlUrl);

            // Create the view model first
            T viewModel = createViewModel(viewClass);
            if (viewModel == null) {
                System.err.println("Error: Failed to create view model for: " + viewClass.getSimpleName());
            } else {
                System.out.println("ViewModel created: " + viewModel.getClass().getSimpleName());
            }

            // Inject services into the view model
            if (viewModel != null) {
                ServiceLocator.injectServices(viewModel);
            }

            // Create a controller factory that sets the view model before JavaFX calls initialize
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(controllerClass -> {
                try {
                    BaseController<T> controller =
                            (BaseController<T>) controllerClass.getDeclaredConstructor().newInstance();
                    if (viewModel != null) {
                        System.out.println("Setting view model: " + viewModel.getClass().getSimpleName()
                                           + " to controller: " + controller.getClass().getSimpleName());
                        controller.setViewModel(viewModel);
                        ServiceLocator.injectServices(controller);
                    }
                    return controller;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create controller: " + controllerClass.getName(), e);
                }
            });

            P root = loader.load();
            System.out.println("FXML loaded successfully");

            // Get the controller
            BaseController<T> controller = loader.getController();
            if (controller == null) {
                System.err.println("Warning: No controller found in FXML");
            } else {
                System.out.println("Controller loaded: " + controller.getClass().getSimpleName());
            }

            return new LoadedComponents<>(root, controller, viewModel);
        } catch (Exception e) {
            System.err.println("Failed to load view: " + viewClass.getSimpleName() + " - Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to load view: " + viewClass.getSimpleName(), e);
        }
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
     * This method is no longer used as the controller factory now handles this.
     *
     * @param controller the controller
     * @param viewModel  the view model
     * @param <T>        the view model type
     * @deprecated Use the controller factory approach instead
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    protected static <T extends BaseViewModel<?>> void setupViewModel(BaseController<T> controller, T viewModel) {
        // This method is kept for backward compatibility but is no longer used
        // The controller factory now handles setting the view model before JavaFX calls initialize

        // Inject services into ViewModel
        ServiceLocator.injectServices(viewModel);

        // Bind ViewModel to Controller
        if (controller != null) {
            if (viewModel != null) {
                System.out.println("Setting view model: " + viewModel.getClass().getSimpleName()
                                   + " to controller: " + controller.getClass().getSimpleName());
                controller.setViewModel(viewModel);
                // Don't call initialize here as it's already been called by JavaFX
            } else {
                System.err.println("Error: ViewModel is null for controller: " + controller.getClass().getSimpleName());
            }
        } else {
            System.err.println("Error: Controller is null for view model: " +
                               (viewModel != null ? viewModel.getClass().getSimpleName() : "null"));
        }
    }

    /**
     * Record containing the loaded components: parent, controller and view model.
     *
     * @param <T> the view model type
     * @param <P> the parent type (e.g., Parent in JavaFX)
     */
    public record LoadedComponents<T extends BaseViewModel<?>, P>(P parent, BaseController<T> controller, T viewModel) {
    }
}
