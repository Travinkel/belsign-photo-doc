package com.belman.business.domain.report.events;

import com.belman.business.domain.report.ReportId;
import com.belman.business.domain.order.OrderId;
import com.belman.business.domain.report.ReportFormat;

import java.net.URL;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event that is published when a report is completed.
 * This is part of the report bounded context.
 */
public final class ReportCompletedEvent {
    private final String eventId;
    private final Instant timestamp;
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
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.reportId = Objects.requireNonNull(reportId, "reportId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.format = Objects.requireNonNull(format, "format must not be null");
        this.fileUrl = Objects.requireNonNull(fileUrl, "fileUrl must not be null");
    }

    /**
     * Gets the unique identifier of this event.
     *
     * @return the event ID
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Gets the timestamp when this event occurred.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
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