package application.viewmodel;

import com.belman.belsign.framework.athomefx.lifecycle.ViewLifecycle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SplashViewModel implements ViewLifecycle {
    private final StringProperty message = new SimpleStringProperty("Welcome to BelSign!");

    public StringProperty messageProperty() {
        return message;
    }
    public String getMessage() {
        return message.get();
    }
    public void setMessage(String message) {
        this.message.set(message);
    }
    @Override
    public void onShow() {
        setMessage("Loading...");
        System.out.println("Splash screen is now visible.");
    }

}
