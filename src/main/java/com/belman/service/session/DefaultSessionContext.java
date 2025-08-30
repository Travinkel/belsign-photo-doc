package com.belman.service.session;

import com.belman.domain.user.UserBusiness;
import com.belman.repository.logging.EmojiLoggerFactory;
import com.belman.service.base.BaseService;

import java.util.Optional;

/**
 * Default implementation of the SessionContext interface.
 * This class provides the default implementation of the session context
 * used in the application.
 */
public class DefaultSessionContext extends BaseService implements SessionContext {
    private final SessionService sessionService;
    private SessionState currentState;
    private UserBusiness currentUser;

    /**
     * Creates a new DefaultSessionContext with the specified session service.
     *
     * @param sessionService the session service
     */
    public DefaultSessionContext(SessionService sessionService) {
        super(EmojiLoggerFactory.getInstance());
        this.sessionService = sessionService;
        this.currentState = new LoggedOutState();
    }

    @Override
    public void setUser(UserBusiness user) {
        this.currentUser = user;
    }

    @Override
    public Optional<UserBusiness> getUser() {
        if (currentUser != null) {
            return Optional.of(currentUser);
        }
        return sessionService.getCurrentUser();
    }

    @Override
    public void setState(SessionState state) {
        this.currentState = state;
        logInfo("Session state changed to: {}", state.getName());
    }

    @Override
    public SessionState getState() {
        return currentState;
    }

    @Override
    public void logEvent(String message) {
        logInfo(message);
    }

    @Override
    public void refreshSession() {
        sessionService.refreshSession();
        logInfo("Session refreshed");
    }

    @Override
    public boolean isSessionValid() {
        return sessionService.isLoggedIn();
    }

    /**
     * Gets the session service.
     *
     * @return the session service
     */
    public SessionService getSessionService() {
        return sessionService;
    }
}