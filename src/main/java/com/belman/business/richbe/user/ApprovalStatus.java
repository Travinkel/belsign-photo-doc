package com.belman.business.richbe.user;

/**
 * Represents the approval status of a user in the registration process.
 * <p>
 * The approval status tracks the user's position in the registration workflow:
 * - PENDING: Initial state when a user registers but not yet reviewed
 * - APPROVED: The user has been reviewed and approved by administrators
 * - REJECTED: The user has been reviewed and rejected by administrators
 * <p>
 * Only approved users are granted full access to the system.
 * Rejected users typically include comments explaining why they were rejected.
 */
public enum ApprovalStatus {
    /**
     * Initial state when a user registers but not yet reviewed by administrators.
     * Users in this state are awaiting approval.
     */
    PENDING,

    /**
     * The user has been reviewed and approved by administrators.
     * Users in this state meet registration requirements and can access the system.
     */
    APPROVED,

    /**
     * The user has been reviewed and rejected by administrators.
     * Users in this state do not meet registration requirements and are required to reapply.
     */
    REJECTED
}