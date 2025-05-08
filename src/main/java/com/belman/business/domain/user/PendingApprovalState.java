package com.belman.business.domain.user;

import java.time.Instant;

public class PendingApprovalState implements ApprovalState {

    @Override
    public ApprovalStatus getStatusEnum() {
        return ApprovalStatus.PENDING;
    }

    @Override
    public void approve(UserAggregate user, UserAggregate reviewer, Instant reviewedAt) {
        user.setApprovalState(ApprovalState.createApproved());
    }

    @Override
    public void reject(UserAggregate user, UserAggregate reviewer, Instant reviewedAt, String reason) {
        user.setApprovalState(ApprovalState.createRejected(reason));
    }
}