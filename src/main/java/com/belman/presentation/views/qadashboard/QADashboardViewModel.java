package com.belman.presentation.views.qadashboard;


import com.belman.backbone.core.base.BaseViewModel;
import com.belman.backbone.core.navigation.Router;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class QADashboardViewModel extends BaseViewModel<QADashboardViewModel> {
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
     * Navigates to the login view.
     * If running on mobile, uses Router to navigate to LoginView.
     * If running on desktop, uses Router to navigate to LoginView.
     */
    public void onLoadingComplete() {
        // Navigate to the login view
        Router.navigateTo(com.belman.presentation.views.login.LoginView.class);
    }

    @Override
    public void onShow() {
        // Initialize any resources or data needed for the splash screen
    }
}
