package com.belman.ui.usecases.authentication.login.flow;

public class StartLoginState implements LoginState {
    public void handle(LoginContext context) {
        context.setLoginInProgress(true);
        context.logDebug("Login started");
        context.setState(new AttemptLoginState());
    }
}