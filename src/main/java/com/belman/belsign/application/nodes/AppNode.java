package com.belman.belsign.application.nodes;

import com.belman.belsign.application.scenes.ScreenPreset;
import javafx.scene.Node;

import java.util.function.Supplier;

public sealed interface AppNode permits LoginNode, UploadNode, ReportNode {
    Node getNode();

    default Object getViewModel() {
        return null;
    }

    default String fxmlPath() {
        return "/views/" + this.getClass().getSimpleName() + ".fxml";
    }

    default String stylesheet() {
        return "/styles/ipad.css";
    }

    default ScreenPreset screenPreset() {
        return ScreenPreset.IPAD_DEFAULT;
    }

    default String title() {
        return this.getClass().getSimpleName();
    }

    default void onEnter() {
    }

    default void onExit() {
    }

    // Default values for window properties, can be overridden in
    // specific scenes, if a specific scene should not
    // adhere to iPad-specific constraints
    default int windowWidth() {
        return 800;
    }

    default int windowHeight() {
        return 600;
    }

    default boolean isResizable() {
        return false;
    }

    Supplier<Object> controllerFactory();
}
