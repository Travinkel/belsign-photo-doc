package com.belman.belsign.framework.athomefx.navigation;

import com.belman.belsign.framework.athomefx.core.BaseController;
import com.belman.belsign.framework.athomefx.core.BaseView;
import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Router {
    private static Stage primaryStage;
    private static final Router instance = new Router();
    private static BaseView<?> currentView;

    private Router() {
        // Singleton constructor
    }

    public static Router getInstance() {
        return instance;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(Class<? extends BaseView<?>> viewClass) {
        try {

            BaseView<?> view = viewClass.getDeclaredConstructor().newInstance();
            if (currentView != null) {
                currentView.onHide();
            }

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(view.getRoot());
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(view.getRoot());
            }
            primaryStage.show();

            view.onShow();
            currentView = view;
        } catch (Exception e) {
            throw new RuntimeException("Failed to navigate to: " + viewClass.getSimpleName(), e);
        }
    }
}
