package com.belman.domain.user;

import java.time.Instant;

public interface ApprovalState {
    static ApprovalState createPendingState() {
        return new PendingApprovalState();
    }

    static ApprovalState createApproved() {
        return new ApprovedState();
    }

    static ApprovalState createRejected(String reason) {
        return new RejectedState(reason);
    }

    static ApprovalState createApprovedState(UserBusiness approver, Instant approvedAt) {
        return new ApprovedState();
    }

    static ApprovalState createRejectedState(String reason) {
        return new RejectedState(reason);
    }

    ApprovalStatus getStatusEnum();

    ApprovalState approve(UserBusiness user, UserBusiness reviewer, Instant reviewedAt);

    ApprovalState reject(UserBusiness user, UserBusiness reviewer, Instant reviewedAt, String reason);

    boolean isPending();

    boolean isApproved();

    boolean isRejected();
}
