package com.belman.belsign.application.nodes;

import com.belman.belsign.application.scenes.AppScene;

import java.util.function.Supplier;

public record UploadNode() implements AppScene {

    @Override
    public Supplier<Object> controllerFactory() {
        return () -> injector.get(UploadController.class);
    }
}
