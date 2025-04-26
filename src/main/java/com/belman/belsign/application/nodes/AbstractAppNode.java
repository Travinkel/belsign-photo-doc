package com.belman.belsign.application.nodes;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.util.function.Supplier;

public abstract class AbstractAppNode implements AppNode {
    private final Node node;
    private final Object controller;

    protected AbstractAppNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(getFXMLPath()));
            loader.setControllerFactory(param -> controllerFactory().get());
            this.node = loader.load();
            this.controller = loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + getFXMLPath(), e);
        }
    }

    protected abstract String getFXMLPath();

    protected abstract Supplier<Object> controllerFactory();

    @Override
    public Node getNode() {
        return node;
    }

    public Object getController() {
        return controller;
    }
}
