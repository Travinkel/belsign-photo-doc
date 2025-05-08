package com.belman.business.richbe.report;

/**
 * Enum representing the type of report that can be generated.
 */
public enum ReportType {
    /**
     * Photo documentation report containing approved photos.
     */
    PHOTO_DOCUMENTATION,

    /**
     * Quality assurance report with detailed inspection results.
     */
    QUALITY_ASSURANCE,

    /**
     * Production report summarizing manufacturing details.
     */
    PRODUCTION,

    /**
     * Customer delivery report with packaging and shipping details.
     */
    DELIVERY,

    /**
     * Custom report with special formatting or content.
     */
    CUSTOM
}