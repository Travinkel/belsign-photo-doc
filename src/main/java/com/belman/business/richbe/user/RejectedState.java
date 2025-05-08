package com.belman.business.richbe.user;

import java.time.Instant;
import java.util.Objects;

public class RejectedState implements ApprovalState {
    private final String reason;

    public RejectedState(String reason) {
        this.reason = Objects.requireNonNull(reason, "Reason must not be null");
    }

    @Override
    public ApprovalStatus getStatusEnum() {
        return ApprovalStatus.REJECTED;
    }

    @Override
    public ApprovalState approve(UserBusiness user, UserBusiness reviewer, Instant reviewedAt) {
        throw new UnsupportedOperationException("Cannot approve a rejected state");
    }

    @Override
    public ApprovalState reject(UserBusiness user, UserBusiness reviewer, Instant reviewedAt, String reason) {
        throw new UnsupportedOperationException("Already rejected.");
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isApproved() {
        return false;
    }

    @Override
    public boolean isRejected() {
        return true;
    }

    public String getReason() {
        return reason;
    }
}
