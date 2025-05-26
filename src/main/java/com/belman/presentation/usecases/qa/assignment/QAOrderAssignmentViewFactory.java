package com.belman.presentation.usecases.qa.assignment;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating QAOrderAssignmentView instances.
 * This factory is responsible for creating and initializing the view with its ViewModel.
 */
public class QAOrderAssignmentViewFactory extends AbstractViewFactory {

    /**
     * Creates a new QAOrderAssignmentViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the ViewDependencies instance
     */
    public QAOrderAssignmentViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new QAOrderAssignmentView instance.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new QAOrderAssignmentView();
    }
}
