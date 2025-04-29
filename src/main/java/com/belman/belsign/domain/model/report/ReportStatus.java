package com.belman.belsign.domain.model.report;

/**
 * Enum representing the status of a report.
 */
public enum ReportStatus {
    /**
     * Report is in draft state and can be modified.
     */
    DRAFT,
    
    /**
     * Report has been finalized and cannot be modified.
     */
    FINAL,
    
    /**
     * Report has been sent to the customer.
     */
    SENT,
    
    /**
     * Report has been archived.
     */
    ARCHIVED
}