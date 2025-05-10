package com.belman.domain.user;

import java.time.Instant;

public class ApprovedState implements ApprovalState {

    @Override
    public ApprovalStatus getStatusEnum() {
        return ApprovalStatus.APPROVED;
    }

    @Override
    public ApprovalState approve(UserBusiness user, UserBusiness reviewer, Instant reviewedAt) {
        throw new UnsupportedOperationException("Cannot approve a photo that is already in the approved state.");
    }

    @Override
    public ApprovalState reject(UserBusiness user, UserBusiness reviewer, Instant reviewedAt, String reason) {
        throw new UnsupportedOperationException("Cannot reject a photo that is already in the approved state.");
    }

    @Override
    public boolean isPending() {
        return false;
    }

    @Override
    public boolean isApproved() {
        return true; // Corrected to return true for the approved state
    }

    @Override
    public boolean isRejected() {
        return false;
    }
}
