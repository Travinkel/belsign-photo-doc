package com.belman.application.usecase.security;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.logging.AuthLoggingService;
import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.domain.security.ExtendedAuthenticationService;
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
 * Default implementation of the ExtendedAuthenticationService interface.
 */
public class DefaultAuthenticationService extends BaseService implements ExtendedAuthenticationService {
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
    private static final String LOG_NFC_AUTHENTICATION_FAILED = "NFC authentication failed for ID: {}";
    private static final String LOG_NFC_AUTHENTICATED = "User authenticated successfully with NFC";
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
        AuthLoggingService.logAuth("DefaultAuthenticationService", "Attempting to authenticate user: " + username);

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: Username or password is empty");
            return Optional.empty();
        }

        try {
            // Check if the account is locked out due to too many failed attempts
            if (isAccountLockedOut(username)) {
                logWarn(LOG_ACCOUNT_LOCKED_OUT, username);
                AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: Account is locked out: " + username);
                return Optional.empty();
            }

            // Find the user by username
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Looking up user in repository: " + username);
            Optional<UserBusiness> userOpt = userRepository.findByUsername(new Username(username));

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();
                AuthLoggingService.logAuth("DefaultAuthenticationService", "User found: " + username + ", ID: " + user.getId().id() + ", Roles: " + user.getRoles());

                // Check if the user is active (approved)
                if (!user.getApprovalState().isApproved()) {
                    logWarn(LOG_USER_NOT_ACTIVE, username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: User is not active: " + username);
                    recordFailedLoginAttempt(username);
                    return Optional.empty();
                }

                // Check if the user is locked (rejected)
                if (user.getApprovalState().isRejected()) {
                    logWarn(LOG_USER_LOCKED, username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: User is locked: " + username);
                    recordFailedLoginAttempt(username);
                    return Optional.empty();
                }

                // Check if the password matches the user's password
                HashedPassword hashedPassword = user.getPassword();
                AuthLoggingService.logAuth("DefaultAuthenticationService", "Checking password for user: " + username);
                boolean isValid = org.mindrot.jbcrypt.BCrypt.checkpw(password, user.getPassword().value());

                if (isValid) {
                    // Reset failed login attempts
                    resetFailedLoginAttempts(username);

                    // Set the current user and update last activity time
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Setting current user: " + username + ", ID: " + user.getId().id());
                    currentUser = user;
                    updateLastActivityTime();

                    // Publish a UserLoggedInEvent
                    // TODO: Fix event publishing mechanism for BusinessEvent objects
                    // publishEvent(new UserLoggedInEvent(user));

                    logInfo(LOG_USER_AUTHENTICATED, username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication successful for user: " + username + ", ID: " + user.getId().id() + ", Roles: " + user.getRoles());
                    return Optional.of(user);
                } else {
                    // Record failed login attempt
                    recordFailedLoginAttempt(username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: Invalid password for user: " + username);

                    // If max failed attempts reached, lock the user account in the database
                    FailedLoginTracker tracker = failedLoginAttempts.get(username);
                    if (tracker != null && tracker.getAttempts() >= MAX_FAILED_ATTEMPTS) {
                        // Reject the user (equivalent to locking)
                        ApprovalState rejectedState = ApprovalState.createRejected(
                                "Locked due to too many failed login attempts");
                        user.setApprovalState(rejectedState);
                        userRepository.save(user);
                        logWarn(LOG_USER_LOCKED_FAILED_ATTEMPTS, username);
                        AuthLoggingService.logAuth("DefaultAuthenticationService", "User locked due to too many failed attempts: " + username);
                    }
                }
            } else {
                AuthLoggingService.logAuth("DefaultAuthenticationService", "User not found: " + username);
            }

            logWarn(LOG_AUTHENTICATION_FAILED, username);
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed for user: " + username);
            return Optional.empty();
        } catch (Exception e) {
            logError(LOG_AUTHENTICATION_ERROR, e);
            AuthLoggingService.logError("DefaultAuthenticationService", "Error during authentication: " + e.getMessage());
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
        AuthLoggingService.logSession("DefaultAuthenticationService", "Getting current user");

        // Check for session timeout
        if (currentUser != null && isSessionTimedOut()) {
            logInfo(LOG_SESSION_TIMEOUT, currentUser.getUsername().value());
            AuthLoggingService.logSession("DefaultAuthenticationService", "Session timed out for user: " + currentUser.getUsername().value());
            logout();
            return Optional.empty();
        }

        // Update last activity time if user is logged in
        if (currentUser != null) {
            updateLastActivityTime();
            AuthLoggingService.logSession("DefaultAuthenticationService", "Current user: " + currentUser.getUsername().value() + ", ID: " + currentUser.getId().id() + ", Roles: " + currentUser.getRoles());
        } else {
            AuthLoggingService.logSession("DefaultAuthenticationService", "No current user");
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
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Logging out user: " + currentUser.getUsername().value() + ", ID: " + currentUser.getId().id());

            // Publish a UserLoggedOutEvent
            // TODO: Fix event publishing mechanism for BusinessEvent objects
            // publishEvent(new UserLoggedOutEvent(currentUser));

            logInfo(LOG_USER_LOGGED_OUT, currentUser.getUsername().value());
            currentUser = null;
            lastActivityTime = null;

            AuthLoggingService.logAuth("DefaultAuthenticationService", "User logged out successfully");
        } else {
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Logout called but no user was logged in");
        }
    }

    @Override
    public boolean isLoggedIn() {
        AuthLoggingService.logSession("DefaultAuthenticationService", "Checking if user is logged in");

        // Check for session timeout
        if (currentUser != null && isSessionTimedOut()) {
            logInfo(LOG_SESSION_TIMEOUT, currentUser.getUsername().value());
            AuthLoggingService.logSession("DefaultAuthenticationService", "Session timed out for user: " + currentUser.getUsername().value());
            logout();
            return false;
        }

        // Update last activity time if user is logged in
        if (currentUser != null) {
            updateLastActivityTime();
            AuthLoggingService.logSession("DefaultAuthenticationService", "User is logged in: " + currentUser.getUsername().value() + ", ID: " + currentUser.getId().id());
        } else {
            AuthLoggingService.logSession("DefaultAuthenticationService", "No user is logged in");
        }

        return currentUser != null;
    }

    @Override
    public Optional<UserBusiness> authenticateWithNfc(String nfcId) {
        AuthLoggingService.logAuth("DefaultAuthenticationService", "Attempting to authenticate with NFC ID: " + nfcId);

        if (nfcId == null || nfcId.isBlank()) {
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: NFC ID is empty");
            return Optional.empty();
        }

        try {
            // Find the user by NFC ID
            AuthLoggingService.logAuth("DefaultAuthenticationService", "Looking up user by NFC ID: " + nfcId);
            Optional<UserBusiness> userOpt = userRepository.findByNfcId(nfcId);

            if (userOpt.isPresent()) {
                UserBusiness user = userOpt.get();
                String username = user.getUsername().value();
                AuthLoggingService.logAuth("DefaultAuthenticationService", "User found by NFC ID: " + username + ", ID: " + user.getId().id() + ", Roles: " + user.getRoles());

                // Check if the account is locked out due to too many failed attempts
                if (isAccountLockedOut(username)) {
                    logWarn(LOG_ACCOUNT_LOCKED_OUT, username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: Account is locked out: " + username);
                    return Optional.empty();
                }

                // Check if the user is active (approved)
                if (!user.getApprovalState().isApproved()) {
                    logWarn(LOG_USER_NOT_ACTIVE, username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: User is not active: " + username);
                    recordFailedLoginAttempt(username);
                    return Optional.empty();
                }

                // Check if the user is locked (rejected)
                if (user.getApprovalState().isRejected()) {
                    logWarn(LOG_USER_LOCKED, username);
                    AuthLoggingService.logAuth("DefaultAuthenticationService", "Authentication failed: User is locked: " + username);
                    recordFailedLoginAttempt(username);
                    return Optional.empty();
                }

                // Reset failed login attempts
                resetFailedLoginAttempts(username);

                // Set the current user and update last activity time
                AuthLoggingService.logAuth("DefaultAuthenticationService", "Setting current user from NFC: " + username + ", ID: " + user.getId().id());
                currentUser = user;
                updateLastActivityTime();

                logInfo(LOG_NFC_AUTHENTICATED);
                AuthLoggingService.logAuth("DefaultAuthenticationService", "NFC authentication successful for user: " + username + ", ID: " + user.getId().id() + ", Roles: " + user.getRoles());
                return Optional.of(user);
            }

            logWarn(LOG_NFC_AUTHENTICATION_FAILED, nfcId);
            AuthLoggingService.logAuth("DefaultAuthenticationService", "NFC authentication failed: User not found for NFC ID: " + nfcId);
            return Optional.empty();
        } catch (Exception e) {
            logError(LOG_AUTHENTICATION_ERROR, e);
            AuthLoggingService.logError("DefaultAuthenticationService", "Error during NFC authentication: " + e.getMessage());
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
