package com.belman.application.usecase.security;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.Username;
import com.belman.application.base.BaseService;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the AuthenticationService interface.
 */
public class DefaultAuthenticationService extends BaseService implements AuthenticationService {
    // Constants for brute force protection
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);
    private static final int INITIAL_ATTEMPTS = 1;
    // Constants for session timeout
    private static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);
    // Log message constants
    private static final String LOG_ACCOUNT_LOCKED_OUT =
            "Authentication failed: Account {} is locked out due to too many failed attempts";
    private static final String LOG_USER_NOT_ACTIVE = "Authentication failed: User {} is not active";
    private static final String LOG_USER_LOCKED = "Authentication failed: User {} is locked";
    private static final String LOG_USER_AUTHENTICATED = "User {} authenticated successfully";
    private static final String LOG_USER_LOCKED_FAILED_ATTEMPTS =
            "User {} locked due to too many failed login attempts";
    private static final String LOG_AUTHENTICATION_FAILED = "Authentication failed for user: {}";
    private static final String LOG_SESSION_TIMEOUT = "Session timed out for user {}";
    private static final String LOG_USER_LOGGED_OUT = "User {} logged out";
    private static final String LOG_AUTHENTICATION_ERROR = "Error during authentication";
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    // Track failed login attempts by username
    private final Map<String, FailedLoginTracker> failedLoginAttempts = new ConcurrentHashMap<>();
    private UserBusiness currentUser;
    private Instant lastActivityTime;

    /**
     * Creates a new DefaultAuthenticationService with the specified UserRepository.
     *
     * @param userRepository the user repository
     */
    public DefaultAuthenticationService(UserRepository userRepository) {
        super(EmojiLoggerFactory.getInstance());
        this.userRepository = userRepository;
        this.passwordHasher = new BCryptPasswordHasher();
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return ServiceLocator.getService(LoggerFactory.class);
    }

    @Override
    public Optional<UserBusiness> authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        try {
            // Check if the account is locked out due to too many failed attempts
            if (isAccountLockedOut(username)) {
                logWarn(LOG_ACCOUNT_LOCKED_OUT, username);
                return Optional.empty();
            }

            // Find the user by username
            Optional<UserBusiness> userOpt = userRepository.findByUsername(new Username(username));

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();

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

                // Check if the password matches the user's password
                HashedPassword hashedPassword = user.getPassword();
                boolean isValid = hashedPassword.matches(password, passwordHasher);

                if (isValid) {
                    // Reset failed login attempts
                    resetFailedLoginAttempts(username);

                    // Set the current user and update last activity time
                    currentUser = user;
                    updateLastActivityTime();

                    // Publish a UserLoggedInEvent
                    // TODO: Fix event publishing mechanism for BusinessEvent objects
                    // publishEvent(new UserLoggedInEvent(user));

                    logInfo(LOG_USER_AUTHENTICATED, username);
                    return Optional.of(user);
                } else {
                    // Record failed login attempt
                    recordFailedLoginAttempt(username);

                    // If max failed attempts reached, lock the user account in the database
                    FailedLoginTracker tracker = failedLoginAttempts.get(username);
                    if (tracker != null && tracker.getAttempts() >= MAX_FAILED_ATTEMPTS) {
                        // Reject the user (equivalent to locking)
                        ApprovalState rejectedState = ApprovalState.createRejected(
                                "Locked due to too many failed login attempts");
                        user.setApprovalState(rejectedState);
                        userRepository.save(user);
                        logWarn(LOG_USER_LOCKED_FAILED_ATTEMPTS, username);
                    }
                }
            }

            logWarn(LOG_AUTHENTICATION_FAILED, username);
            return Optional.empty();
        } catch (Exception e) {
            logError(LOG_AUTHENTICATION_ERROR, e);
            return Optional.empty();
        }
    }

    /**
     * Checks if an account is locked out due to too many failed login attempts.
     *
     * @param username the username to check
     * @return true if the account is locked out, false otherwise
     */
    private boolean isAccountLockedOut(String username) {
        FailedLoginTracker tracker = failedLoginAttempts.get(username);
        return tracker != null && tracker.isLockedOut();
    }

    /**
     * Records a failed login attempt for the specified username.
     *
     * @param username the username to record the failed attempt for
     */
    private void recordFailedLoginAttempt(String username) {
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
    private void resetFailedLoginAttempts(String username) {
        failedLoginAttempts.computeIfPresent(username, (key, tracker) -> {
            tracker.resetAttempts();
            return tracker;
        });
    }

    /**
     * Updates the last activity time to the current time.
     */
    private void updateLastActivityTime() {
        lastActivityTime = Instant.now();
    }

    @Override
    public Optional<UserBusiness> getCurrentUser() {
        // Check for session timeout
        if (currentUser != null && isSessionTimedOut()) {
            logInfo(LOG_SESSION_TIMEOUT, currentUser.getUsername().value());
            logout();
            return Optional.empty();
        }

        // Update last activity time if user is logged in
        if (currentUser != null) {
            updateLastActivityTime();
        }

        return Optional.ofNullable(currentUser);
    }

    /**
     * Checks if the current session has timed out.
     *
     * @return true if the session has timed out, false otherwise
     */
    private boolean isSessionTimedOut() {
        return lastActivityTime != null &&
               Duration.between(lastActivityTime, Instant.now()).compareTo(SESSION_TIMEOUT) > 0;
    }

    @Override
    public void logout() {
        if (currentUser != null) {
            // Publish a UserLoggedOutEvent
            // TODO: Fix event publishing mechanism for BusinessEvent objects
            // publishEvent(new UserLoggedOutEvent(currentUser));

            logInfo(LOG_USER_LOGGED_OUT, currentUser.getUsername().value());
            currentUser = null;
            lastActivityTime = null;
        }
    }

    @Override
    public boolean isLoggedIn() {
        // Check for session timeout
        if (currentUser != null && isSessionTimedOut()) {
            logInfo(LOG_SESSION_TIMEOUT, currentUser.getUsername().value());
            logout();
            return false;
        }

        // Update last activity time if user is logged in
        if (currentUser != null) {
            updateLastActivityTime();
        }

        return currentUser != null;
    }

    /**
     * Class to track failed login attempts
     */
    private static class FailedLoginTracker {
        private int attempts;
        private Instant lockoutTime;

        public FailedLoginTracker() {
            this.attempts = INITIAL_ATTEMPTS;
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

        public int getAttempts() {
            return attempts;
        }

        public Instant getLockoutTime() {
            return lockoutTime;
        }
    }
}
