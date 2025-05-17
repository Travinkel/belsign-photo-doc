package com.belman.presentation.usecases.archive.authentication.login.flow;

import javax.security.auth.login.LoginException;

public interface LoginState {

    void handle(LoginContext context) throws LoginException;
}