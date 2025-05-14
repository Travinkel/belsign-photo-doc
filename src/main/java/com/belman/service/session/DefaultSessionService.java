package com.belman.service.session;

import com.belman.domain.user.UserBusiness;
import com.belman.repository.logging.EmojiLoggerFactory;
import com.belman.service.base.BaseService;

import java.util.Optional;

/**
 * Default implementation of the SessionService interface.
 * This class delegates to the SessionManager for most of its functionality.
 */
public class DefaultSessionService extends BaseService implements SessionService {
    private final SessionManager sessionManager;

    /**
     * Creates a new DefaultSessionService with the specified SessionManager.
     *
     * @param sessionManager the session manager
     */
    public DefaultSessionService(SessionManager sessionManager) {
        super(EmojiLoggerFactory.getInstance());
        this.sessionManager = sessionManager;
    }

    @Override
    public Optional<UserBusiness> login(String username, String password) {
        return sessionManager.login(username, password);
    }

    @Override
    public void logout() {
        sessionManager.logout();
    }

    @Override
    public Optional<UserBusiness> getCurrentUser() {
        return sessionManager.getCurrentUser();
    }

    @Override
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    @Override
    public void refreshSession() {
        // Implementation depends on session management requirements
        // For now, we'll just log a message
        logInfo("Session refreshed");

        // In a real implementation, this might:
        // 1. Check if the session is still valid
        // 2. Extend the session timeout
        // 3. Refresh any cached user data
        // 4. Verify the user's authentication token
    }
}