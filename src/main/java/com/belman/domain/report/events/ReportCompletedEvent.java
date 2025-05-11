package com.belman.domain.report.events;

import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportId;

import java.net.URL;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a report is completed.
 * This event signifies that a report has been finalized and is ready for delivery.
 */
public final class ReportCompletedEvent extends BaseAuditEvent {
    private final ReportId reportId;
    private final OrderId orderId;
    private final ReportFormat format;
    private final URL fileUrl;

    /**
     * Creates a new ReportCompletedEvent.
     *
     * @param reportId the ID of the report that was completed
     * @param orderId  the ID of the order the report is for
     * @param format   the format of the report
     * @param fileUrl  the URL where the report file can be accessed
     */
    public ReportCompletedEvent(ReportId reportId, OrderId orderId, ReportFormat format, URL fileUrl) {
        super();
        this.reportId = Objects.requireNonNull(reportId, "reportId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.format = Objects.requireNonNull(format, "format must not be null");
        this.fileUrl = Objects.requireNonNull(fileUrl, "fileUrl must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId    the ID of the event
     * @param occurredOn the timestamp when the event occurred
     * @param reportId   the ID of the report that was completed
     * @param orderId    the ID of the order the report is for
     * @param format     the format of the report
     * @param fileUrl    the URL where the report file can be accessed
     */
    public ReportCompletedEvent(UUID eventId, Instant occurredOn, ReportId reportId, OrderId orderId,
                                ReportFormat format, URL fileUrl) {
        super(eventId, occurredOn);
        this.reportId = Objects.requireNonNull(reportId, "reportId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.format = Objects.requireNonNull(format, "format must not be null");
        this.fileUrl = Objects.requireNonNull(fileUrl, "fileUrl must not be null");
    }

    /**
     * Gets the ID of the report that was completed.
     *
     * @return the report ID
     */
    public ReportId getReportId() {
        return reportId;
    }

    /**
     * Gets the ID of the order the report is for.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Gets the format of the report.
     *
     * @return the report format
     */
    public ReportFormat getFormat() {
        return format;
    }

    /**
     * Gets the URL where the report file can be accessed.
     *
     * @return the file URL
     */
    public URL getFileUrl() {
        return fileUrl;
    }
}
