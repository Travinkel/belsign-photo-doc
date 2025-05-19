package com.belman.presentation.usecases.admin.dashboard;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating AdminDashboardView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the Admin dashboard screen.
 */
public class AdminDashboardViewFactory extends AbstractViewFactory {

    /**
     * Creates a new AdminDashboardViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public AdminDashboardViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new AdminDashboardView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new AdminDashboardView();
    }
}