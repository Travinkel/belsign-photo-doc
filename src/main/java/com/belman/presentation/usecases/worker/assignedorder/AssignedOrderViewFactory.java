package com.belman.presentation.usecases.worker.assignedorder;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating AssignedOrderView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the assigned order screen.
 */
public class AssignedOrderViewFactory extends AbstractViewFactory {

    /**
     * Creates a new AssignedOrderViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public AssignedOrderViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new AssignedOrderView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new AssignedOrderView();
    }
}
