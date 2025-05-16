package com.belman.ui.usecases.admin.usermanagement;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating UserManagementView instances.
 * This class is part of the Factory Method pattern for view creation.
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