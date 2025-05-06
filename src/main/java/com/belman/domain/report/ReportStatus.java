package com.belman.domain.report;

/**
 * Enum representing the status of a report throughout its lifecycle.
 */
public enum ReportStatus {
    /**
     * Report has been created but not yet processed.
     */
    PENDING,

    /**
     * Report is being processed and generated.
     */
    PROCESSING,

    /**
     * Report has been generated and is ready for review.
     */
    GENERATED,

    /**
     * Report has been reviewed and approved.
     */
    APPROVED,

    /**
     * Report has been delivered to the customer.
     */
    DELIVERED,

    /**
     * Report has been rejected and needs to be regenerated.
     */
    REJECTED
}