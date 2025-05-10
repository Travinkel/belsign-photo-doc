package com.belman.domain.user.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.user.ApprovalStatus;
import com.belman.domain.user.UserId;

import java.time.Instant;
import java.util.UUID;

/**
 * Audit event that is published when a user is approved.
 */
public class UserApprovedAuditEvent extends BaseAuditEvent {

    private final UserId userId;
    private final UserId approverId;
    private final ApprovalStatus approvalStatus;

    /**
     * Creates a new UserApprovedAuditEvent with the specified user ID, approver ID, and approval status.
     *
     * @param userId         the ID of the user who was approved
     * @param approverId     the ID of the user who approved the user
     * @param approvalStatus the approval status
     */
    public UserApprovedAuditEvent(UserId userId, UserId approverId, ApprovalStatus approvalStatus) {
        super(UUID.randomUUID(), Instant.now());
        this.userId = userId;
        this.approverId = approverId;
        this.approvalStatus = approvalStatus;
    }

    /**
     * Gets the ID of the user who was approved.
     *
     * @return the user ID
     */
    public UserId getUserId() {
        return userId;
    }

    /**
     * Gets the ID of the user who approved the user.
     *
     * @return the approver ID
     */
    public UserId getApproverId() {
        return approverId;
    }

    /**
     * Gets the approval status.
     *
     * @return the approval status
     */
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    @Override
    public String getEventType() {
        return "UserApproved";
    }
}