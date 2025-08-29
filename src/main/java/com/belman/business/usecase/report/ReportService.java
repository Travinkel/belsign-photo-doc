package com.belman.business.usecase.report;

import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportAggregate;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportType;
import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;

/**
 * Service for report generation and management.
 * Provides methods for generating, retrieving, and managing reports.
 */
public interface ReportService {
    /**
     * Gets a report by ID.
     *
     * @param reportId the ID of the report to get
     * @return an Optional containing the report if found, or empty if not found
     */
    Optional<ReportAggregate> getReportById(ReportId reportId);

    /**
     * Gets all reports for an order.
     *
     * @param orderId the ID of the order
     * @return a list of reports for the order
     */
    List<ReportAggregate> getReportsByOrderId(OrderId orderId);

    /**
     * Gets all reports of a specific type.
     *
     * @param type the type of reports to get
     * @return a list of reports of the specified type
     */
    List<ReportAggregate> getReportsByType(ReportType type);

    /**
     * Generates a report for an order.
     *
     * @param orderId     the ID of the order
     * @param type        the type of report to generate
     * @param format      the format of the report
     * @param generatedBy the user who generated the report
     * @return the generated report
     */
    ReportAggregate generateReport(OrderId orderId, ReportType type, ReportFormat format, UserBusiness generatedBy);

    /**
     * Previews a report for an order without saving it.
     *
     * @param orderId the ID of the order
     * @param type    the type of report to preview
     * @param format  the format of the report
     * @return the preview of the report
     */
    byte[] previewReport(OrderId orderId, ReportType type, ReportFormat format);

    /**
     * Sends a report to a recipient.
     *
     * @param reportId       the ID of the report to send
     * @param recipientEmail the email address of the recipient
     * @param subject        the subject of the email
     * @param message        the message body of the email
     * @param sentBy         the user who sent the report
     * @return true if the report was sent, false if the report was not found
     */
    boolean sendReport(ReportId reportId, String recipientEmail, String subject, String message, UserBusiness sentBy);

    /**
     * Deletes a report.
     *
     * @param reportId  the ID of the report to delete
     * @param deletedBy the user who deleted the report
     * @return true if the report was deleted, false if the report was not found
     */
    boolean deleteReport(ReportId reportId, UserBusiness deletedBy);
}