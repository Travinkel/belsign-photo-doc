package com.belman.domain.photo;

/**
 * Represents the approval status of a photo document in the quality control process.
 * <p>
 * The approval status tracks the photo document's position in the QA workflow:
 * - PENDING: Initial state when a photo is uploaded but not yet reviewed
 * - APPROVED: The photo has been reviewed and approved by QA personnel
 * - REJECTED: The photo has been reviewed and rejected by QA personnel
 * <p>
 * Only approved photos are included in quality control reports sent to customers.
 * Rejected photos typically include comments explaining why they were rejected.
 */
public enum ApprovalStatus {
    /**
     * Initial state when a photo is uploaded but not yet reviewed by QA.
     * Photos in this state are awaiting quality assessment.
     */
    PENDING,

    /**
     * The photo has been reviewed and approved by QA personnel.
     * Photos in this state meet quality standards and can be included in reports.
     */
    APPROVED,

    /**
     * The photo has been reviewed and rejected by QA personnel.
     * Photos in this state do not meet quality standards and need to be retaken.
     */
    REJECTED
}