package com.belman.domain.user;

/**
 * Enum representing the status of a user in the system.
 * <p>
 * The user status determines whether a user can log in, what actions they can perform,
 * and how the system should treat their account. It is a key part of the user lifecycle
 * and security model.
 */
public enum UserStatus {
    /**
     * User is active and can log in.
     * This is the normal status for users who can fully use the system.
     */
    ACTIVE,

    /**
     * User is inactive and cannot log in.
     * This status is typically used for users who are no longer with the organization
     * but whose accounts are kept for record-keeping purposes.
     */
    INACTIVE,

    /**
     * User is temporarily locked and cannot log in.
     * This status is typically triggered automatically due to security events
     * such as too many failed login attempts or suspicious activity.
     */
    LOCKED,

    /**
     * User is pending activation.
     * This status is used for newly created accounts that require confirmation
     * or other activation steps before they can be used.
     */
    PENDING,

    /**
     * User is suspended by an administrator.
     * This status is used when an administrator has manually suspended the account
     * due to policy violations or other administrative reasons.
     */
    SUSPENDED,

    /**
     * User account is expired.
     * This status is used for accounts that have time limits and have exceeded
     * their allowed time period.
     */
    EXPIRED
}