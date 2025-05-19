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
                    path = "/com/belman/presentation/views/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/" +
                           viewClass.getSimpleName() + ".fxml";
                    System.out.println("Still not found, trying convention-based path: " + path);
                    fxmlUrl = ViewLoader.class.getResource(path);
                }

                // Try with worker subdirectory
                if (fxmlUrl == null) {
                    path = "/com/belman/presentation/views/worker/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/" +
                           viewClass.getSimpleName() + ".fxml";
                    System.out.println("Still not found, trying worker subdirectory path: " + path);
                    fxmlUrl = ViewLoader.class.getResource(path);
                }

                // Try one more approach - look in the usecases directory
                if (fxmlUrl == null) {
                    path = "/com/belman/presentation/usecases/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/" +
                           viewClass.getSimpleName() + ".fxml";
                    System.out.println("Still not found, trying usecases path: " + path);
                    fxmlUrl = ViewLoader.class.getResource(path);
                }

                // Try with direct resources path
                if (fxmlUrl == null) {
                    path = "/views/worker/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/" +
                           viewClass.getSimpleName() + ".fxml";
                    System.out.println("Still not found, trying direct resources path: " + path);
                    fxmlUrl = ViewLoader.class.getResource(path);
                }
            }

            if (fxmlUrl == null) {
                String errorMessage = "Failed to load view: " + viewClass.getSimpleName() + " - Error: FXML file not found: " + path;
                System.err.println(errorMessage);
                System.err.println("Please ensure the FXML file exists at one of these locations:");
                System.err.println("1. In the same package as the view class: " + viewClass.getPackageName());
                System.err.println("2. In the conventional path: /com/belman/presentation/views/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/");
                System.err.println("3. In the worker path: /com/belman/presentation/views/worker/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/");
                System.err.println("4. In the usecases path: /com/belman/presentation/usecases/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/");
                System.err.println("5. In the direct resources path: /views/worker/" + viewClass.getSimpleName().toLowerCase().replace("view", "") + "/");

                // Log the error for debugging
                java.util.logging.Logger.getLogger(ViewLoader.class.getName()).severe(errorMessage);

                // Create a fallback view with an error message
                return createFallbackView(viewClass);
            }

            System.out.println("Found FXML at: " + fxmlUrl);

            // Create the view model first
            T viewModel = createViewModel(viewClass);
            if (viewModel == null) {
                System.err.println("Error: Failed to create view model for: " + viewClass.getSimpleName());
                // Create a fallback view model
                viewModel = createFallbackViewModel(viewClass);
            } else {
                System.out.println("ViewModel created: " + viewModel.getClass().getSimpleName());
            }

            // Inject services into the view model
            if (viewModel != null) {
                try {
                    ServiceLocator.injectServices(viewModel);
                } catch (Exception e) {
                    System.err.println("Warning: Failed to inject services into view model: " + e.getMessage());
                }
            }

            // Create a controller factory that sets the view model before JavaFX calls initialize
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            final T finalViewModel = viewModel; // Make effectively final for lambda
            loader.setControllerFactory(controllerClass -> {
                try {
                    BaseController<T> controller =
                            (BaseController<T>) controllerClass.getDeclaredConstructor().newInstance();
                    if (finalViewModel != null) {
                        System.out.println("Setting view model: " + finalViewModel.getClass().getSimpleName()
                                           + " to controller: " + controller.getClass().getSimpleName());
                        controller.setViewModel(finalViewModel);
                        try {
                            ServiceLocator.injectServices(controller);
                        } catch (Exception e) {
                            System.err.println("Warning: Failed to inject services into controller: " + e.getMessage());
                        }
                    }
                    return controller;
                } catch (Exception e) {
                    System.err.println("Error creating controller: " + controllerClass.getName() + " - " + e.getMessage());
                    // Return a fallback controller
                    return createFallbackController(finalViewModel);
                }
            });

            P root;
            try {
                root = loader.load();
                System.out.println("FXML loaded successfully");
            } catch (Exception e) {
                System.err.println("Error loading FXML: " + e.getMessage());
                e.printStackTrace();
                // Create a fallback view with an error message
                return createFallbackView(viewClass);
            }

            // Get the controller
            BaseController<T> controller = loader.getController();
            if (controller == null) {
                System.err.println("Warning: No controller found in FXML");
                // Create a fallback controller
                controller = createFallbackController(viewModel);
            } else {
                System.out.println("Controller loaded: " + controller.getClass().getSimpleName());
            }

            return new LoadedComponents<>(root, controller, viewModel);
        } catch (Exception e) {
            System.err.println("Failed to load view: " + viewClass.getSimpleName() + " - Error: " + e.getMessage());
            e.printStackTrace();
            // Create a fallback view with an error message
            return createFallbackView(viewClass);
        }
    }

    /**
     * Creates a fallback view with an error message when the normal view loading fails.
     * This ensures the application doesn't crash when a view can't be loaded.
     *
     * @param viewClass the view class that failed to load
     * @param <T>       the view model type
     * @param <P>       the parent type
     * @return a LoadedComponents object with a fallback view
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseViewModel<?>, P> LoadedComponents<T, P> createFallbackView(Class<?> viewClass) {
        try {
            // Create a simple VBox with an error message
            javafx.scene.layout.VBox vbox = new javafx.scene.layout.VBox(15);
            vbox.setAlignment(javafx.geometry.Pos.CENTER);
            vbox.setPadding(new javafx.geometry.Insets(30));
            vbox.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #f5c6cb; -fx-border-width: 1px; -fx-border-radius: 5px;");

            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Error Loading View");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #721c24;");

            // Create a more detailed error message
            String detailedError = "Failed to load view: " + viewClass.getSimpleName() + "\n\n" +
                                  "Possible causes:\n" +
                                  "• FXML file not found\n" +
                                  "• FXML file contains errors\n" +
                                  "• Missing fx:id references in FXML\n" +
                                  "• Controller class not properly defined\n\n" +
                                  "Searched in:\n" +
                                  "• " + viewClass.getPackageName() + "\n" +
                                  "• /com/belman/presentation/views/...\n" +
                                  "• /com/belman/presentation/views/worker/...\n" +
                                  "• /com/belman/presentation/usecases/...\n" +
                                  "• /views/worker/...\n\n" +
                                  "Please check the console for more details.";

            javafx.scene.control.TextArea messageArea = new javafx.scene.control.TextArea(detailedError);
            messageArea.setEditable(false);
            messageArea.setWrapText(true);
            messageArea.setPrefRowCount(10);
            messageArea.setPrefWidth(400);
            messageArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #721c24; -fx-control-inner-background: #f8d7da;");

            javafx.scene.layout.HBox buttonBox = new javafx.scene.layout.HBox(10);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

            javafx.scene.control.Button retryButton = new javafx.scene.control.Button("Retry Loading View");
            retryButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
            retryButton.setOnAction(e -> {
                // Try to reload the view
                System.out.println("Retrying to load view: " + viewClass.getSimpleName());
                try {
                    load(viewClass);
                } catch (Exception ex) {
                    System.err.println("Retry failed: " + ex.getMessage());
                }
            });

            buttonBox.getChildren().add(retryButton);

            // Add a spacer
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            spacer.setPrefHeight(10);

            vbox.getChildren().addAll(titleLabel, spacer, messageArea, buttonBox);

            // Create a fallback view model
            T viewModel = createFallbackViewModel(viewClass);

            // Create a fallback controller
            BaseController<T> controller = createFallbackController(viewModel);

            return new LoadedComponents<>((P) vbox, controller, viewModel);
        } catch (Exception e) {
            System.err.println("Failed to create fallback view: " + e.getMessage());
            e.printStackTrace();
            // If even the fallback view fails, return null components
            return new LoadedComponents<>(null, null, null);
        }
    }

    /**
     * Creates a fallback view model when the normal view model creation fails.
     *
     * @param viewClass the view class
     * @param <T>       the view model type
     * @return a fallback view model
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseViewModel<?>> T createFallbackViewModel(Class<?> viewClass) {
        try {
            // Create a simple BaseViewModel that can be used as a fallback
            BaseViewModel<?> fallbackViewModel = new BaseViewModel<Object>() {
                @Override
                public void onShow() {
                    // Do nothing
                }
            };
            return (T) fallbackViewModel;
        } catch (Exception e) {
            System.err.println("Failed to create fallback view model: " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates a fallback controller when the normal controller creation fails.
     *
     * @param viewModel the view model
     * @param <T>       the view model type
     * @return a fallback controller
     */
    @SuppressWarnings("unchecked")
    private static <T extends BaseViewModel<?>> BaseController<T> createFallbackController(T viewModel) {
        try {
            // Create a simple BaseController that can be used as a fallback
            BaseController<T> fallbackController = new BaseController<T>() {
                @Override
                protected void setupBindings() {
                    // Do nothing
                }
            };
            fallbackController.setViewModel(viewModel);
            return fallbackController;
        } catch (Exception e) {
            System.err.println("Failed to create fallback controller: " + e.getMessage());
            return null;
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
