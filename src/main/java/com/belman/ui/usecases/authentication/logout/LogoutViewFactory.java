package com.belman.ui.usecases.authentication.logout;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating LogoutView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class LogoutViewFactory extends AbstractViewFactory {
    /**
     * Creates a new LogoutViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public LogoutViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new LogoutView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new LogoutView();
    }
}