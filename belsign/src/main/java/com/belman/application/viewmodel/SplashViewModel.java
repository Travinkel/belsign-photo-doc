package com.belman.application.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import dev.stefan.lifecycle.ViewLifecycle;

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
