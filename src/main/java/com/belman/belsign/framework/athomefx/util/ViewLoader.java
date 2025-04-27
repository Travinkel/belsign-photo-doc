package com.belman.belsign.framework.athomefx.util;

import com.belman.belsign.framework.athomefx.core.BaseController;
import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ViewLoader {

    public record LoadedComponents(Parent parent, BaseController<?> controller, BaseViewModel viewModel) {}

    public static LoadedComponents load(Class<?> viewClass) {
        try {
            String viewName = viewClass.getSimpleName();
            String fxmlFile = "/fxml/" + viewName + ".fxml";
            FXMLLoader loader = new FXMLLoader(viewClass.getResource(fxmlFile));
            Parent parent = loader.load();
            BaseController<?> controller = loader.getController();

            // Try to load ViewModel
            String packageName = viewClass.getPackageName();
            String viewModelClassName = packageName + ".viewmodels." + viewName.replace("View", "ViewModel");
            Class<?> viewModelClass = Class.forName(viewModelClassName);
            BaseViewModel viewModel = (BaseViewModel) viewModelClass.getDeclaredConstructor().newInstance();

            return new LoadedComponents(parent, controller, viewModel);

        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException("Failed to load view: " + viewClass.getSimpleName(), e);
        }
    }
}
