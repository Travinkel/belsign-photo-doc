package com.belman.belsign.presentation.controller;


import com.belman.belsign.infrastructure.navigation.Router;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SplashController {
    @FXML
    private StackPane splashRoot;

    @FXML
    public void initialize() {
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> Router.getInstance().navigate("login"));
        delay.play();
    }
}
