package com.belman.ui.usecases.authentication.login;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating LoginView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class LoginViewFactory extends AbstractViewFactory {
    /**
     * Creates a new LoginViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public LoginViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new LoginView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new LoginView();
    }
}
