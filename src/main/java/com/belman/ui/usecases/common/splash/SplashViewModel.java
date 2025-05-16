package com.belman.ui.usecases.common.splash;


import com.belman.ui.base.BaseViewModel;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.authentication.login.LoginView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SplashViewModel extends BaseViewModel<SplashViewModel> {
    private final StringProperty message = new SimpleStringProperty("Loading application...");

    /**
     * Get the message property.
     *
     * @return The message property.
     */
    public StringProperty messageProperty() {
        return message;
    }

    /**
     * Get the message value.
     *
     * @return The message value.
     */
    public String getMessage() {
        return message.get();
    }

    /**
     * Set the message value.
     *
     * @param message The new message value.
     */
    public void setMessage(String message) {
        this.message.set(message);
    }

    /**
     * Called when loading is complete.
     * Navigates to the login view.
     * If running on mobile, uses Router to navigate to LoginView.
     * If running on desktop, uses Router to navigate to LoginView.
     */
    public void onLoadingComplete() {
        try {
            System.out.println("[DEBUG_LOG] SplashViewModel.onLoadingComplete() called");
            // Navigate to the login view
            System.out.println("[DEBUG_LOG] About to navigate to LoginView");
            Router.navigateTo(LoginView.class);
            System.out.println("[DEBUG_LOG] Navigation to LoginView completed");
        } catch (Exception e) {
            // Log and handle navigation errors
            System.err.println("[DEBUG_LOG] Error navigating from SplashView: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onShow() {
        // No need to update the app bar title as we want to hide the app bar
    }
}