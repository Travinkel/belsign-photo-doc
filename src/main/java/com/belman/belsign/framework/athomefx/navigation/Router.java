package com.belman.belsign.framework.athomefx.navigation;

import com.belman.belsign.framework.athomefx.core.BaseView;
import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import com.belman.belsign.framework.athomefx.util.ViewLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static Stage primaryStage;
    private static final Router instance = new Router();
    private final Map<String, String> routes = new HashMap<>();
    private StackPane rootContainer;
    private ViewLifecycle currentLifecycle;

    private Router() {
        // Initialize routes
        routes.put("splash", "/fxml/SplashView.fxml");
        routes.put("login", "/fxml/LoginView.fxml");
        routes.put("home", "/fxml/home.fxml");
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(Class<? extends BaseView<?>> viewClass) {
        try {
            BaseView<?> view = viewClass.getDeclaredConstructor().newInstance();
            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(new StackPane());
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(view.getRoot());
            }
            primaryStage.show();

            view.getViewModel().onShow();
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to: " + viewClass.getSimpleName(), e);
        }
    }

    public static Router getInstance() {
        return instance;
    }

    public void initialize(StackPane rootContainer) {
        this.rootContainer = rootContainer;
    }

    public void navigate(String routeName) {
        try {
            String fxmlPath = routes.get(routeName);
            if (fxmlPath == null) {
                throw new IllegalArgumentException("Route not found: " + routeName);
            }

            ViewLoader.LoadedView<?> loadedView = ViewLoader.loadWithController(fxmlPath);

            if (currentLifecycle != null) {
                currentLifecycle.onHide();
            }

            if (loadedView.getController() instanceof ViewLifecycle lifecycle) {
                currentLifecycle = lifecycle;
                lifecycle.onShow();
            }

            rootContainer.getChildren().setAll(loadedView.getRoot());

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error (e.g., show an error message)
        }
    }

}
