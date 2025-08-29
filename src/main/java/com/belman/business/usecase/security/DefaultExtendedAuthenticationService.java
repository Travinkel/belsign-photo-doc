package com.belman.business.usecase.security;

import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.ExtendedAuthenticationService;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import com.belman.common.logging.EmojiLogger;
import com.belman.common.logging.EmojiLoggerFactory;
import com.belman.business.base.BaseService;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of the ExtendedAuthenticationService interface.
 * This class delegates to DefaultAuthenticationService for standard authentication methods
 * and adds support for PIN code and QR code authentication.
 */
public class DefaultExtendedAuthenticationService extends BaseService implements ExtendedAuthenticationService {
    // Constants for brute force protection
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    // Log message constants
    private static final String LOG_PIN_AUTHENTICATION_FAILED = "PIN authentication failed for PIN: {}";
    private static final String LOG_PIN_AUTHENTICATED = "User authenticated successfully with PIN";
    private static final String LOG_QR_AUTHENTICATION_FAILED = "QR code authentication failed for hash: {}";
    private static final String LOG_QR_AUTHENTICATED = "User authenticated successfully with QR code";
    private static final String LOG_AUTHENTICATION_ERROR = "Error during authentication";
    private static final String LOG_ACCOUNT_LOCKED_OUT = "Authentication failed: Account {} is locked out due to too many failed attempts";
    private static final String LOG_USER_NOT_ACTIVE = "Authentication failed: User {} is not active";
    private static final String LOG_USER_LOCKED = "Authentication failed: User {} is locked";

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private UserBusiness currentUser;
    private Instant lastActivityTime;

    // Track failed login attempts by username
    private final Map<String, FailedLoginTracker> failedLoginAttempts = new ConcurrentHashMap<>();

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
     * Sets the current user.
     *
     * @param user the user to set as current
     */
    protected void setCurrentUser(UserBusiness user) {
        this.currentUser = user;
    }

    /**
     * Updates the last activity time to the current time.
     */
    protected void updateLastActivityTime() {
        this.lastActivityTime = Instant.now();
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

    @Override
    public Optional<UserBusiness> authenticateWithPin(String pinCode) {
        if (pinCode == null || pinCode.isBlank()) {
            return Optional.empty();
        }

        try {
            // Find the user by PIN code
            Optional<UserBusiness> userOpt = userRepository.findByPinCode(pinCode);

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

                logInfo(LOG_PIN_AUTHENTICATED);
                return Optional.of(user);
            }

            logWarn(LOG_PIN_AUTHENTICATION_FAILED, pinCode);
            return Optional.empty();
        } catch (Exception e) {
            logError(LOG_AUTHENTICATION_ERROR, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserBusiness> authenticateWithQrCode(String qrCodeHash) {
        if (qrCodeHash == null || qrCodeHash.isBlank()) {
            return Optional.empty();
        }

        try {
            // Find the user by QR code hash
            Optional<UserBusiness> userOpt = userRepository.findByQrCodeHash(qrCodeHash);

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

                logInfo(LOG_QR_AUTHENTICATED);
                return Optional.of(user);
            }

            logWarn(LOG_QR_AUTHENTICATION_FAILED, qrCodeHash);
            return Optional.empty();
        } catch (Exception e) {
            logError(LOG_AUTHENTICATION_ERROR, e);
            return Optional.empty();
        }
    }
}
