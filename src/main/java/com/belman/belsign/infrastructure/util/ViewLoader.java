package com.belman.belsign.infrastructure.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ViewLoader {
    public static Parent load(String fxmlPath) throws IOException {
        return new FXMLLoader(ViewLoader.class.getClassLoader().getResource(fxmlPath)).load();
    }
}
