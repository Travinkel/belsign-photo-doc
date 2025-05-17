package com.belman.presentation.usecases.archive.admin;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating AdminView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class AdminViewFactory extends AbstractViewFactory {
    /**
     * Creates a new AdminViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public AdminViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new AdminView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new AdminView();
    }
}