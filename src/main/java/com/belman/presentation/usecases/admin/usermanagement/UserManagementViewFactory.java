package com.belman.presentation.usecases.admin.usermanagement;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating UserManagementView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the User Management screen.
 */
public class UserManagementViewFactory extends AbstractViewFactory {

    /**
     * Creates a new UserManagementViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public UserManagementViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new UserManagementView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new UserManagementView();
    }
}