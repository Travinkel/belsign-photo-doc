package com.belman.domain.user;

import java.sql.Timestamp;

public interface ApprovalState {
    String getStatus();

    void approve(UserAggregate user, UserAggregate reviewer, Timestamp reviewedAt);

    void reject(UserAggregate user, UserAggregate reviewer, Timestamp reviewedAt, String reason);
}
