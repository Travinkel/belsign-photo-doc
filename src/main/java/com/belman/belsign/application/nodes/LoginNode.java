package com.belman.belsign.application.nodes;

import javafx.scene.Node;

import java.util.function.Supplier;

public class LoginNode() extends AbstractAppNode {
    private final LoginViewModel viewModel = new LoginViewModel();

    @Override
    protected String getFXMLPath() {
        return "/fxml/LoginView.fxml";
    }

    @Override
    protected Supplier<Object> controllerFactory() {
        return () -> new LoginController(viewModel);
    }

    @Override
    public Object getViewModel() {
        return viewModel;
    }
}
