package com.belman.belsign.infrastructure.navigation;

import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static final Router instance = new Router();
    private StackPane rootContainer;
    private final Map<String, String> routes = new HashMap<>();

    private Router() {
        // Initialize routes
        routes.put("splash", "/fxml/splash.fxml");
        routes.put("login", "/fxml/login.fxml");
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
            Parent view = ViewLoader.load(fxmlPath);
            rootContainer.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle error (e.g., show an error message)
        }
    }

}
