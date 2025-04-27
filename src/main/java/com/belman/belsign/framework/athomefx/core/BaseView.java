package com.belman.belsign.framework.athomefx.core;

import com.belman.belsign.framework.athomefx.util.ViewLoader;
import javafx.scene.Parent;

/**
 * Base View that loads FXML, Controller, and ViewModel automatically.
 */
public abstract class BaseView<T extends BaseViewModel> {

    private final Parent root;
    protected final T viewModel;

    public BaseView(T viewModel) {
        var triple = ViewLoader.load(this.getClass());
        this.root = triple.parent();
        var controller = triple.controller();
        this.viewModel = triple.viewModel();

        if (controller != null) {
            controller.setViewModel(viewModel);
            controller.initializeBinding();
        }
    }

    public Parent getRoot() {
        return root;
    }

    public T getViewModel() {
        return viewModel;
    }
}
