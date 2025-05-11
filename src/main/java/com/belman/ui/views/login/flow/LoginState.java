package com.belman.ui.views.login.flow;

import com.belman.ui.views.login.flow.LoginContext;
import javax.security.auth.login.LoginException;

public interface LoginState {

    void handle(LoginContext context) throws LoginException;
}
