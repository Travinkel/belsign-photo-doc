package com.belman.business.richbe.user;

import java.time.Instant;

public interface ApprovalState {
    ApprovalStatus getStatusEnum();

    ApprovalState approve(UserBusiness user, UserBusiness reviewer, Instant reviewedAt);

    ApprovalState reject(UserBusiness user, UserBusiness reviewer, Instant reviewedAt, String reason);

    boolean isPending();
    boolean isApproved();
    boolean isRejected();

    static ApprovalState createPendingState() {
        return new PendingApprovalState();
    }

    static ApprovalState createApproved() {
        return new ApprovedState();
    }

    static ApprovalState createRejected(String reason) {
        return new RejectedState(reason);
    }
}
