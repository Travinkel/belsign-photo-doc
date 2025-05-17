package com.belman.presentation.usecases.archive.report.preview;


import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.archive.authentication.login.LoginView;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportPreviewViewModel extends BaseViewModel<ReportPreviewViewModel> {
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
        // Navigate to the login view
        Router.navigateTo(LoginView.class);
    }

    @Override
    public void onShow() {
        // Initialize any resources or data needed for the splash screen
    }
}