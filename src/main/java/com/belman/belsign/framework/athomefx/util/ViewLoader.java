package com.belman.belsign.framework.athomefx.util;

import com.belman.belsign.framework.athomefx.core.BaseController;
import com.belman.belsign.framework.athomefx.core.BaseViewModel;
import com.belman.belsign.framework.athomefx.di.ServiceLocator;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class ViewLoader {

    public record LoadedComponents(Parent parent, BaseController<?> controller, BaseViewModel<?> viewModel) {}

    public static <T extends BaseViewModel<?>> LoadedComponents load(Class<?> viewClass) {
        try {
            FXMLLoader loader = new FXMLLoader(viewClass.getResource(viewClass.getSimpleName() + ".fxml"));
            Parent parent = loader.load();
            BaseController<T> controller = loader.getController();

            // Automatically resolve ViewModel class
            String viewModelClassName = viewClass.getPackageName() + "." + viewClass.getSimpleName() + "Model";
            T viewModel = (T) Class.forName(viewModelClassName).getDeclaredConstructor().newInstance();


            // Inject services into ViewModel
            ServiceLocator.injectServices(viewModel);

            // Bind ViewModel to Controller
            if (controller != null) {
                controller.setViewModel(viewModel);
                controller.initializeBinding();
            }

            return new LoadedComponents(parent, controller, viewModel);

        } catch (IOException | ReflectiveOperationException e) {
            throw new RuntimeException("Failed to load view: " + viewClass.getSimpleName(), e);
        }
    }
}
