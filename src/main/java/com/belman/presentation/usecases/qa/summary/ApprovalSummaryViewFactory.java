package com.belman.presentation.usecases.qa.summary;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating ApprovalSummaryView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the approval summary screen.
 */
public class ApprovalSummaryViewFactory extends AbstractViewFactory {

    /**
     * Creates a new ApprovalSummaryViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public ApprovalSummaryViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new ApprovalSummaryView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new ApprovalSummaryView();
    }
}