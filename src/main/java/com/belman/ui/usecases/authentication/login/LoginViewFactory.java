package com.belman.ui.usecases.authentication.login;

import com.belman.ui.core.ViewFactory;
import javafx.scene.Parent;

/**
 * Factory for creating LoginView instances.
 * This is part of the Factory Method pattern for view creation.
 */
public class LoginViewFactory implements ViewFactory {
    /**
     * Creates a LoginView.
     *
     * @return the created LoginView
     */
    @Override
    public Parent createView() {
        return new LoginView();
    }
}