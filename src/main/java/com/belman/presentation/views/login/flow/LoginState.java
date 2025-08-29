package com.belman.presentation.views.login.flow;

import com.belman.presentation.views.login.flow.LoginContext;
import javax.security.auth.login.LoginException;

public interface LoginState {

    void handle(LoginContext context) throws LoginException;
}
