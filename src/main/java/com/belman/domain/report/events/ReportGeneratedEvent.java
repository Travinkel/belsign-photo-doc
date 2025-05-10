package com.belman.domain.report.events;

import com.belman.domain.events.BaseAuditEvent;
import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportType;
import com.belman.domain.user.UserReference;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Audit event that is published when a new report is generated.
 * This event signifies that a report has been created and is ready for further processing.
 */
public class ReportGeneratedEvent extends BaseAuditEvent {
    private final ReportId reportId;
    private final OrderId orderId;
    private final ReportType reportType;
    private final UserReference generatedBy;

    /**
     * Creates a new ReportGeneratedEvent with the specified parameters.
     *
     * @param reportId    the ID of the report that was generated
     * @param orderId     the ID of the order this report is for
     * @param reportType  the type of report
     * @param generatedBy the user who generated this report
     */
    public ReportGeneratedEvent(ReportId reportId, OrderId orderId, ReportType reportType, UserReference generatedBy) {
        super();
        this.reportId = Objects.requireNonNull(reportId, "reportId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.reportType = Objects.requireNonNull(reportType, "reportType must not be null");
        this.generatedBy = Objects.requireNonNull(generatedBy, "generatedBy must not be null");
    }

    /**
     * Constructor for event deserialization/reconstitution.
     *
     * @param eventId     the ID of this event
     * @param occurredOn  the timestamp when this event occurred
     * @param reportId    the ID of the report that was generated
     * @param orderId     the ID of the order this report is for
     * @param reportType  the type of report
     * @param generatedBy the user who generated this report
     */
    public ReportGeneratedEvent(UUID eventId, Instant occurredOn, ReportId reportId, OrderId orderId,
                                ReportType reportType, UserReference generatedBy) {
        super(eventId, occurredOn);
        this.reportId = Objects.requireNonNull(reportId, "reportId must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.reportType = Objects.requireNonNull(reportType, "reportType must not be null");
        this.generatedBy = Objects.requireNonNull(generatedBy, "generatedBy must not be null");
    }

    /**
     * Returns the ID of the report that was generated.
     *
     * @return the report ID
     */
    public ReportId getReportId() {
        return reportId;
    }

    /**
     * Returns the ID of the order this report is for.
     *
     * @return the order ID
     */
    public OrderId getOrderId() {
        return orderId;
    }

    /**
     * Returns the type of report that was generated.
     *
     * @return the report type
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Returns the user who generated this report.
     *
     * @return the generating user
     */
    public UserReference getGeneratedBy() {
        return generatedBy;
    }
}
