package com.belman.ui.views.login.flow;

import com.belman.ui.views.login.flow.LoginContext;

public class StartLoginState implements LoginState {
    public void handle(LoginContext context) {
        context.setLoginInProgress(true);
        context.logDebug("Login started");
        context.setState(new AttemptLoginState());
    }
}
