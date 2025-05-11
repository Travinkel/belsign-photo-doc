package com.belman.ui.views.login.flow;

import javax.security.auth.login.LoginException;

/**
 * State for handling camera scan login.
 */
public class CameraScanLoginState implements LoginState {
    @Override
    public void handle(LoginContext context) throws LoginException {
        context.logDebug("Camera scan login started");
        
        // In a real implementation, this would use the CameraService to take a photo
        // and then process it to extract the barcode/QR code
        
        // Set login in progress
        context.setLoginInProgress(true);
        
        // Simulate camera scan
        // In a real implementation, this would call a service to scan the barcode/QR code
        
        // For now, we'll just transition to the next state
        context.setState(new AttemptLoginState());
    }
}