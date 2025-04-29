package presentation.views.splash;


import core.BaseViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import navigation.Router;

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
     */
    public void onLoadingComplete() {
        // Navigate to the main view
        Router.navigateTo(presentation.views.main.MainView.class);
    }

    @Override
    public void onShow() {
        // Initialize any resources or data needed for the splash screen
    }
}
