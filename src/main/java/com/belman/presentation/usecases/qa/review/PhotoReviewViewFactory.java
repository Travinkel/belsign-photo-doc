package com.belman.presentation.usecases.qa.review;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating PhotoReviewView instances.
 * This factory is responsible for creating and initializing the view,
 * controller, and view model for the photo review screen.
 */
public class PhotoReviewViewFactory extends AbstractViewFactory {

    /**
     * Creates a new PhotoReviewViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public PhotoReviewViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new PhotoReviewView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new PhotoReviewView();
    }
}