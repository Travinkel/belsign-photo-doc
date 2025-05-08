package com.belman.business.domain.user;

import java.time.Instant;

public class PendingApprovalState implements ApprovalState {

    @Override
    public ApprovalStatus getStatusEnum() {
        return ApprovalStatus.PENDING;
    }

    @Override
    public ApprovalState approve(UserAggregate user, UserAggregate reviewer, Instant reviewedAt) {
        user.setApprovalState(ApprovalState.createApproved());
        return ApprovalState.createApproved();
    }

    @Override
    public ApprovalState reject(UserAggregate user, UserAggregate reviewer, Instant reviewedAt, String reason) {
        user.setApprovalState(ApprovalState.createRejected(reason));
        return ApprovalState.createRejected(reason);
    }

    @Override
    public boolean isPending() {
        return true;
    }

    @Override
    public boolean isApproved() {
        return false;
    }

    @Override
    public boolean isRejected() {
        return false;
    }
}
