package com.belman.domain.report;

/**
 * Enum representing the status of a report throughout its lifecycle.
 */
public enum ReportStatus {
    /**
     * ReportAggregate has been created but not yet processed.
     */
    PENDING,

    /**
     * ReportAggregate is being processed and generated.
     */
    PROCESSING,

    /**
     * ReportAggregate has been generated and is ready for review.
     */
    GENERATED,

    /**
     * ReportAggregate has been reviewed and approved.
     */
    APPROVED,

    /**
     * ReportAggregate has been delivered to the customer.
     */
    DELIVERED,

    /**
     * ReportAggregate has been rejected and needs to be regenerated.
     */
    REJECTED
}