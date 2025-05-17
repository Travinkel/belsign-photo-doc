package com.belman.presentation.core;

import com.belman.presentation.di.ViewDependencies;

public abstract class AbstractViewFactory implements ViewFactory {
    protected final ViewDependencies viewDependencies;

    protected AbstractViewFactory(ViewDependencies viewDependencies) {
        this.viewDependencies = viewDependencies;
    }
}
