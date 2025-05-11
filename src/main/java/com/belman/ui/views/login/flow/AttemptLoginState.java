package com.belman.ui.views.login.flow;

import com.belman.domain.user.UserBusiness;

import com.belman.ui.views.login.flow.LoginContext;
import javax.security.auth.login.LoginException;
import java.util.Optional;


public class AttemptLoginState implements LoginState {
    public void handle(LoginContext context) throws LoginException {
        Optional<UserBusiness> userOpt = context.login();

        if (userOpt.isPresent()) {
            context.setUser(userOpt.get());
            context.logSuccess("Login successful");
            context.setState(new HandlePreferencesState());
        } else {
            context.logFailure("Login failed");
            context.setState(new HandleLoginFailureState());
        }
    }
}
