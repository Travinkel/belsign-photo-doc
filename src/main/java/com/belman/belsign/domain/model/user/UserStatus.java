package com.belman.belsign.domain.model.user;

/**
 * Enum representing the status of a user.
 */
public enum UserStatus {
    /**
     * User is active and can log in.
     */
    ACTIVE,
    
    /**
     * User is inactive and cannot log in.
     */
    INACTIVE,
    
    /**
     * User is locked due to too many failed login attempts.
     */
    LOCKED,
    
    /**
     * User is pending activation.
     */
    PENDING
}