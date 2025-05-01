package com.belman.presentation.views.splash;


import com.belman.presentation.views.main.MainView;
import dev.stefan.core.BaseViewModel;
import dev.stefan.navigation.GluonRouter;
import dev.stefan.navigation.Router;
import dev.stefan.util.PlatformUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SplashViewModel extends BaseViewModel<SplashViewModel> {
    private final StringProperty message = new SimpleStringProperty("Loading application...");

    /**
     * Get the message property.
     * @return The message property.
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Get the message value.
     * @return The message value.
     */
    public String getMessage() {
        return message.get();
    }

    /**
     * Set the message value.
     * @param message The new message value.
     */
    public void setMessage(String message) {
        this.message.set(message);
    }

    /**
     * Called when loading is complete.
     * Navigates to the main view.
     * If running on mobile, uses Router to navigate to GluonMainView.
     * If running on desktop, uses Router to navigate to MainView.
     */
    public void onLoadingComplete() {
        if (PlatformUtils.isRunningOnMobile()) {
            // Navigate to the Gluon main view when running on mobile
            GluonRouter.navigateTo(GluonMainView.class);
        } else {
            // Navigate to the JavaFX main view when running on desktop
            Router.navigateTo(MainView.class);
        }
    }

    @Override
    public void onShow() {
        // Initialize any resources or data needed for the splash screen
    }
}
