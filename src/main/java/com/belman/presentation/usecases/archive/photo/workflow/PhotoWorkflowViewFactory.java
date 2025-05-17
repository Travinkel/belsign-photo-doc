package com.belman.presentation.usecases.archive.photo.workflow;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating PhotoWorkflowView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class PhotoWorkflowViewFactory extends AbstractViewFactory {
    /**
     * Creates a new PhotoWorkflowViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public PhotoWorkflowViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new PhotoWorkflowView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new PhotoWorkflowView();
    }
}