package com.belman.domain.user;

import com.belman.domain.user.ApprovalStatus;
import com.belman.domain.user.UserAggregate;
import java.time.Instant;

public interface ApprovalState {
    ApprovalStatus getStatusEnum();

    ApprovalState approve(UserAggregate user, UserAggregate reviewer, Instant reviewedAt);

    ApprovalState reject(UserAggregate user, UserAggregate reviewer, Instant reviewedAt, String reason);

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