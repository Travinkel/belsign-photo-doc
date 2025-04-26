package com.belman.belsign.application.scenes;


import javafx.scene.Parent;
import javafx.stage.Stage;

public class SceneManager {
    private final Stage primaryStage;

    public SceneManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void switchTo(Class<? extends AppScene> scene) {
        scene.getClass().onExit();
        try {
            Parent root;


            sceneClass.onEnter();
            if (state != null) {
                state.onEnter();
            }

            System.out.println("Switching to scene: " + sceneClass.getSimpleName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
