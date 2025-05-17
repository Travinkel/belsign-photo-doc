package com.belman.presentation.usecases.archive.order.gallery;

import com.belman.presentation.core.AbstractViewFactory;
import com.belman.presentation.di.ViewDependencies;
import com.gluonhq.charm.glisten.mvc.View;

/**
 * Factory for creating OrderGalleryView instances.
 * This class is part of the Factory Method pattern for view creation.
 */
public class OrderGalleryViewFactory extends AbstractViewFactory {
    /**
     * Creates a new OrderGalleryViewFactory with the specified ViewDependencies.
     *
     * @param viewDependencies the view dependencies
     */
    public OrderGalleryViewFactory(ViewDependencies viewDependencies) {
        super(viewDependencies);
    }

    /**
     * Creates a new OrderGalleryView.
     * The view model will be created automatically by the BaseView constructor
     * using the ViewLoader.
     *
     * @return the created view
     */
    @Override
    public View createView() {
        return new OrderGalleryView();
    }
}