package com.belman.belsign.application.scenes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public final class LoginScene implements AppScene {
    private final LoginViewModel viewModel;

    public LoginScene(LoginViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public String getFXMLPath() {
        return "/fxml/LoginView.fxml";
    }

    @Override
    public Object getViewModel() {
        return viewModel;
    }

    public Scene createScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getFXMLPath()));
            loader.setControllerFactory(param -> viewModel);
            Parent root = loader.load();
            return new Scene(root);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML", e);
        }
    }
}
