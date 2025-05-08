package com.belman.business.domain.report;

/**
 * Enum representing the format of a report document.
 * <p>
 * Different report formats are supported to meet various customer requirements
 * and integration needs. Each format has different characteristics regarding
 * fidelity, accessibility, and compatibility.
 */
public enum ReportFormat {
    /**
     * Portable Document Format (PDF).
     * Provides a fixed layout that looks the same across all devices and platforms.
     * Ideal for formal reports and archival purposes.
     */
    PDF,

    /**
     * HyperText Markup Language (HTML).
     * Web-friendly format that can be viewed in browsers.
     * Supports interactive elements and responsive layouts.
     */
    HTML,

    /**
     * Plain text format.
     * Simple format with no formatting, suitable for basic reporting
     * or integration with legacy systems.
     */
    TEXT,

    /**
     * Microsoft Word Document format (DOCX).
     * Rich text format that supports complex layouts and is editable.
     * Useful when customers need to modify or incorporate the report.
     */
    DOCX,

    /**
     * Microsoft Excel format (XLSX).
     * Spreadsheet format suitable for data-heavy reports
     * where calculations or data manipulation might be needed.
     */
    XLSX,

    /**
     * JSON format.
     * Machine-readable format ideal for API integrations and
     * programmatic consumption of report data.
     */
    JSON
}