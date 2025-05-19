package com.belman.application.usecase.report;

import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.report.ReportBusiness;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportId;
import com.belman.domain.report.ReportRepository;
import com.belman.domain.report.ReportType;
import com.belman.domain.photo.PhotoDocument.ApprovalStatus;
import com.belman.domain.user.UserBusiness;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the ReportService interface.
 * This service provides report generation and management functionality.
 */
public class DefaultReportService implements ReportService {

    private final ReportRepository reportRepository;
    private final OrderRepository orderRepository;
    private final PhotoRepository photoRepository;
    private final PDFExportService pdfExportService;

    /**
     * Creates a new DefaultReportService with the specified repositories.
     *
     * @param reportRepository the report repository
     * @param orderRepository  the order repository
     * @param photoRepository  the photo repository
     * @param pdfExportService the PDF export service
     */
    public DefaultReportService(ReportRepository reportRepository, OrderRepository orderRepository,
                               PhotoRepository photoRepository, PDFExportService pdfExportService) {
        this.reportRepository = reportRepository;
        this.orderRepository = orderRepository;
        this.photoRepository = photoRepository;
        this.pdfExportService = pdfExportService;
    }

    @Override
    public Optional<ReportBusiness> getReportById(ReportId reportId) {
        return reportRepository.findById(reportId);
    }

    @Override
    public List<ReportBusiness> getReportsByOrderId(OrderId orderId) {
        return reportRepository.findByOrderId(orderId);
    }

    @Override
    public List<ReportBusiness> getReportsByType(ReportType type) {
        // In a real implementation, we would filter reports by type
        // For now, just return all reports
        return reportRepository.findAll();
    }

    @Override
    public ReportBusiness generateReport(OrderId orderId, ReportType type, ReportFormat format, UserBusiness generatedBy) {
        // Get the order
        Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        OrderBusiness order = orderOpt.get();

        // Get approved photos for the order
        List<PhotoDocument> approvedPhotos = photoRepository.findByStatus(ApprovalStatus.APPROVED)
                .stream()
                .filter(photo -> photo.getOrderId().equals(orderId))
                .collect(Collectors.toList());

        // Create a new report
        ReportBusiness report = ReportBusiness.builder()
                .id(ReportId.newId())
                .orderId(orderId)
                .approvedPhotos(approvedPhotos)
                .generatedBy(generatedBy)
                .generatedAt(Timestamp.now())
                .format(format)
                .build();

        // Save the report
        return reportRepository.save(report);
    }

    @Override
    public byte[] previewReport(OrderId orderId, ReportType type, ReportFormat format) {
        try {
            // Get the order
            Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
            if (orderOpt.isEmpty()) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }

            OrderBusiness order = orderOpt.get();

            // Get approved photos for the order
            List<PhotoDocument> approvedPhotos = photoRepository.findByStatus(ApprovalStatus.APPROVED)
                    .stream()
                    .filter(photo -> photo.getOrderId().equals(orderId))
                    .collect(Collectors.toList());

            // Create a temporary report
            ReportBusiness tempReport = ReportBusiness.builder()
                    .id(ReportId.newId())
                    .orderId(orderId)
                    .approvedPhotos(approvedPhotos)
                    .generatedBy(null) // Not needed for preview
                    .generatedAt(Timestamp.now())
                    .format(format)
                    .build();

            // Create a temporary file for the PDF
            File tempFile = File.createTempFile("report_preview_", ".pdf");

            // Generate the PDF
            pdfExportService.exportQCReport(tempReport, tempFile);

            // Read the PDF into a byte array
            byte[] pdfBytes = Files.readAllBytes(tempFile.toPath());

            // Delete the temporary file
            tempFile.delete();

            return pdfBytes;
        } catch (IOException e) {
            throw new RuntimeException("Error generating report preview", e);
        }
    }

    @Override
    public boolean sendReport(ReportId reportId, String recipientEmail, String subject, String message, UserBusiness sentBy) {
        // Get the report
        Optional<ReportBusiness> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            return false;
        }

        ReportBusiness report = reportOpt.get();

        // In a real implementation, we would send the report via email
        // For now, just mark the report as delivered
        report.markAsSent(null);

        // Save the updated report
        reportRepository.save(report);

        return true;
    }

    @Override
    public boolean deleteReport(ReportId reportId, UserBusiness deletedBy) {
        // Get the report
        Optional<ReportBusiness> reportOpt = reportRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            return false;
        }

        // Delete the report
        return reportRepository.deleteById(reportId);
    }
}
