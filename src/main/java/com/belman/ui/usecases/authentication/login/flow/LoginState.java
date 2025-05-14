package com.belman.ui.usecases.authentication.login.flow;

import javax.security.auth.login.LoginException;

public interface LoginState {

    void handle(LoginContext context) throws LoginException;
}