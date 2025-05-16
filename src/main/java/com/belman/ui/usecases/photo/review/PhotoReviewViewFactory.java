package com.belman.ui.usecases.photo.review;

import com.belman.ui.core.AbstractViewFactory;
import com.belman.ui.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating PhotoReviewView instances.
 * This class is part of the Factory Method pattern for view creation.
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