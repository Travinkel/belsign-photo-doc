package com.belman.belsign.application.nodes;

import com.belman.belsign.application.scenes.AppScene;

import java.util.function.Supplier;

public record ReportNode() implements AppScene {


    @Override
    public Supplier<Object> controllerFactory() {
        return () -> injector.get(ReportController.class);
    }
}
