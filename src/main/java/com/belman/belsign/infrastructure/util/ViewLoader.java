package com.belman.belsign.infrastructure.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ViewLoader {

    public static class LoadedView<T> {
        private final Parent root;
        private final T controller;

        public LoadedView(Parent root, T controller) {
            this.root = root;
            this.controller = controller;
        }

        public Parent getRoot() {
            return root;
        }

        public T getController() {
            return controller;
        }
    }

    public static <T> LoadedView<T> loadWithController(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(ViewLoader.class.getClassLoader().getResource(fxmlPath));
        Parent root = loader.load();
        T controller = loader.getController();
        return new LoadedView<>(root, controller);
    }
}
