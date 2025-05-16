package com.belman.ui.core;

import com.belman.ui.di.ViewDependencies;

public abstract class AbstractViewFactory implements ViewFactory {
    protected final ViewDependencies viewDependencies;

    protected AbstractViewFactory(ViewDependencies viewDependencies) {
        this.viewDependencies = viewDependencies;
    }
}
