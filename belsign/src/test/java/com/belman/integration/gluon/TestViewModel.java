package com.belman.integration.gluon;

import com.belman.backbone.core.base.BaseViewModel;
import dev.stefan.athomefx.core.di.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Test view model for integration testing.
 */
public class TestViewModel extends BaseViewModel<TestViewModel> {
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty loginInProgress = new SimpleBooleanProperty(false);
    
    private TestService service;
    private boolean onShowCalled = false;
    private boolean onHideCalled = false;
    
    @Inject
    public void setService(TestService service) {
        this.service = service;
    }
    
    public TestService getService() {
        return service;
    }
    
    @Override
    public void onShow() {
        super.onShow();
        onShowCalled = true;
    }
    
    @Override
    public void onHide() {
        super.onHide();
        onHideCalled = true;
    }
    
    public void login() {
        // Simulate login logic
        loginInProgress.set(true);
        
        if (username.get().isEmpty() || password.get().isEmpty()) {
            errorMessage.set("Username and password are required");
            loginInProgress.set(false);
            return;
        }
        
        // Use the injected service
        if (service != null) {
            String data = service.getData();
            // Do something with the data
        }
        
        // Simulate successful login
        errorMessage.set("");
        loginInProgress.set(false);
    }
    
    public void cancel() {
        // Clear fields
        username.set("");
        password.set("");
        errorMessage.set("");
    }
    
    public StringProperty usernameProperty() {
        return username;
    }
    
    public StringProperty passwordProperty() {
        return password;
    }
    
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
    
    public BooleanProperty loginInProgressProperty() {
        return loginInProgress;
    }
    
    public boolean isOnShowCalled() {
        return onShowCalled;
    }
    
    public boolean isOnHideCalled() {
        return onHideCalled;
    }
}