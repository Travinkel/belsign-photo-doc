package com.belman.belsign.infrastructure.navigation;

import com.belman.belsign.infrastructure.util.ViewLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class Router {
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
