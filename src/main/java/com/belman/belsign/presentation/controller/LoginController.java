package com.belman.belsign.presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class LoginController {
    @FXML
    private StackPane contentArea;

    public LoginController() {
        // Constructor logic if needed
    }

    public void setContent(Parent view) {
        contentArea.getChildren().setAll(view);
    }
}
