package com.belman.belsign.framework.athomefx.core;

import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import com.belman.belsign.framework.athomefx.util.ViewLoader;
import javafx.scene.Parent;

import java.io.IOException;

public abstract class BaseView<T extends BaseViewModel<?>> implements ViewLifecycle {

    private final Parent root;
    private final BaseController<?> controller;
    protected final T viewModel;


    @SuppressWarnings("unchecked")
    public BaseView() {
        try {
            var components = ViewLoader.load(this.getClass());
            this.root = components.parent();
            this.controller = components.controller();
            this.viewModel = (T) components.viewModel();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load view: " + this.getClass().getSimpleName(), e);
        }
    }

    public Parent getRoot() {
        return root;
    }

    public T getViewModel() {
        return viewModel;
    }

    public BaseController<?> getController() {
        return controller;
    }

    @Override
    public void onShow() {
        if (viewModel != null) {
            viewModel.onShow();
        }
    }

    @Override
    public void onHide() {
        if (viewModel != null) {
            viewModel.onHide();
        }
    }
}
