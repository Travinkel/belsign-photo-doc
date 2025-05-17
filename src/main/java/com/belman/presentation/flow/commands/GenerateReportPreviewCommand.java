package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderId;
import com.belman.domain.report.ReportFormat;
import com.belman.domain.report.ReportType;
import com.belman.application.usecase.report.ReportService;

import java.util.concurrent.CompletableFuture;

/**
 * Command for generating a report preview for an order.
 * <p>
 * This command uses the ReportService to generate a preview of a report
 * for an order without saving it.
 */
public class GenerateReportPreviewCommand implements Command<byte[]> {
    
    @Inject
    private ReportService reportService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final OrderId orderId;
    private final ReportType reportType;
    private final ReportFormat reportFormat;
    
    /**
     * Creates a new GenerateReportPreviewCommand for the specified order, report type, and format.
     *
     * @param orderId      the ID of the order to generate a report preview for
     * @param reportType   the type of report to generate
     * @param reportFormat the format of the report
     */
    public GenerateReportPreviewCommand(OrderId orderId, ReportType reportType, ReportFormat reportFormat) {
        this.orderId = orderId;
        this.reportType = reportType;
        this.reportFormat = reportFormat;
    }
    
    @Override
    public CompletableFuture<byte[]> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (orderId == null) {
                throw new IllegalArgumentException("Order ID cannot be null");
            }
            if (reportType == null) {
                throw new IllegalArgumentException("Report type cannot be null");
            }
            if (reportFormat == null) {
                throw new IllegalArgumentException("Report format cannot be null");
            }
            
            // Generate the report preview
            return reportService.previewReport(orderId, reportType, reportFormat);
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        // This command doesn't modify any state, so there's nothing to undo
        return CompletableFuture.completedFuture(null);
    }
    
    @Override
    public boolean canUndo() {
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Generate " + reportType + " report preview in " + reportFormat + " format for order: " + orderId.id();
    }
}