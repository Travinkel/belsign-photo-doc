package com.belman.service.usecase.security;

import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.service.base.BaseService;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the ExtendedAuthenticationService interface.
 * This class delegates to DefaultAuthenticationService for standard authentication methods
 * and adds support for NFC authentication.
 */
public class DefaultExtendedAuthenticationService extends BaseService implements ExtendedAuthenticationService {
    // Constants for brute force protection
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    // Log message constants
    private static final String LOG_NFC_AUTHENTICATION_FAILED = "NFC authentication failed for ID: {}";
    private static final String LOG_NFC_AUTHENTICATED = "User authenticated successfully with NFC";
    private static final String LOG_AUTHENTICATION_ERROR = "Error during authentication";
    private static final String LOG_ACCOUNT_LOCKED_OUT =
            "Authentication failed: Account {} is locked out due to too many failed attempts";
    private static final String LOG_USER_NOT_ACTIVE = "Authentication failed: User {} is not active";
    private static final String LOG_USER_LOCKED = "Authentication failed: User {} is locked";

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    // Track failed login attempts by username
    private final Map<String, FailedLoginTracker> failedLoginAttempts = new ConcurrentHashMap<>();
    private UserBusiness currentUser;
    private Instant lastActivityTime;

    /**
     * Creates a new DefaultExtendedAuthenticationService with the specified UserRepository.
     *
     * @param userRepository the user repository
     */
    public DefaultExtendedAuthenticationService(UserRepository userRepository) {
        super(EmojiLoggerFactory.getInstance());
        this.userRepository = userRepository;
        this.authenticationService = new DefaultAuthenticationService(userRepository);
    }

    @Override
    public Optional<UserBusiness> authenticate(String username, String password) {
        return authenticationService.authenticate(username, password);
    }

    @Override
    public Optional<UserBusiness> getCurrentUser() {
        return authenticationService.getCurrentUser();
    }

    @Override
    public void logout() {
        authenticationService.logout();
    }

    @Override
    public boolean isLoggedIn() {
        return authenticationService.isLoggedIn();
    }

    /**
     * Sets the current user.
     *
     * @param user the user to set as current
     */
    protected void setCurrentUser(UserBusiness user) {
        this.currentUser = user;
    }

    /**
     * Checks if an account is locked out due to too many failed login attempts.
     *
     * @param username the username to check
     * @return true if the account is locked out, false otherwise
     */
    protected boolean isAccountLockedOut(String username) {
        FailedLoginTracker tracker = failedLoginAttempts.get(username);
        return tracker != null && tracker.isLockedOut();
    }

    /**
     * Records a failed login attempt for the specified username.
     *
     * @param username the username to record the failed attempt for
     */
    protected void recordFailedLoginAttempt(String username) {
        failedLoginAttempts.compute(username, (key, tracker) -> {
            if (tracker == null) {
                return new FailedLoginTracker();
            } else {
                tracker.incrementAttempts();
                return tracker;
            }
        });
    }

    /**
     * Resets the failed login attempts for the specified username.
     *
     * @param username the username to reset the failed attempts for
     */
    protected void resetFailedLoginAttempts(String username) {
        failedLoginAttempts.computeIfPresent(username, (key, tracker) -> {
            tracker.resetAttempts();
            return tracker;
        });
    }

    /**
     * Updates the last activity time to the current time.
     */
    protected void updateLastActivityTime() {
        this.lastActivityTime = Instant.now();
    }

    @Override
    public Optional<UserBusiness> authenticateWithNfc(String nfcId) {
        if (nfcId == null || nfcId.isBlank()) {
            return Optional.empty();
        }

        try {
            // Find the user by NFC ID
            Optional<UserBusiness> userOpt = userRepository.findByNfcId(nfcId);

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();
                String username = user.getUsername().value();

                // Check if the account is locked out due to too many failed attempts
                if (isAccountLockedOut(username)) {
                    logWarn(LOG_ACCOUNT_LOCKED_OUT, username);
                    return Optional.empty();
                }

                // Check if the user is active (approved)
                if (!user.getApprovalState().isApproved()) {
                    logWarn(LOG_USER_NOT_ACTIVE, username);
                    recordFailedLoginAttempt(username);
                    return Optional.empty();
                }

                // Check if the user is locked (rejected)
                if (user.getApprovalState().isRejected()) {
                    logWarn(LOG_USER_LOCKED, username);
                    recordFailedLoginAttempt(username);
                    return Optional.empty();
                }

                // Reset failed login attempts
                resetFailedLoginAttempts(username);

                // Set the current user and update last activity time
                setCurrentUser(user);
                updateLastActivityTime();

                logInfo(LOG_NFC_AUTHENTICATED);
                return Optional.of(user);
            }

            logWarn(LOG_NFC_AUTHENTICATION_FAILED, nfcId);
            return Optional.empty();
        } catch (Exception e) {
            logError(LOG_AUTHENTICATION_ERROR, e);
            return Optional.empty();
        }
    }

    /**
     * Class to track failed login attempts
     */
    private static class FailedLoginTracker {
        private int attempts;
        private Instant lockoutTime;

        public FailedLoginTracker() {
            this.attempts = 1;
            this.lockoutTime = null;
        }

        public void incrementAttempts() {
            this.attempts++;
            if (this.attempts >= MAX_FAILED_ATTEMPTS) {
                this.lockoutTime = Instant.now().plus(LOCKOUT_DURATION);
            }
        }

        public boolean isLockedOut() {
            return lockoutTime != null && Instant.now().isBefore(lockoutTime);
        }

        public void resetAttempts() {
            this.attempts = 0;
            this.lockoutTime = null;
        }
    }
}