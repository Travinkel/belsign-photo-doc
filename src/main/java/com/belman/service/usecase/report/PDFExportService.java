package com.belman.service.usecase.report;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.report.ReportBusiness;
import com.belman.service.base.BaseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Service for exporting reports to PDF format.
 * This service uses Apache PDFBox to generate PDF files from report data.
 */
public class PDFExportService extends BaseService {
    private final OrderRepository orderRepository;

    /**
     * Creates a new PDFExportService with the specified order repository.
     *
     * @param orderRepository the repository for retrieving order information
     */
    public PDFExportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Exports a quality control report to a PDF file.
     *
     * @param report          the report to export
     * @param destinationFile the file to save the PDF to
     * @throws IOException if an I/O error occurs
     */
    public void exportQCReport(ReportBusiness report, File destinationFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Quality Control Report");
            contentStream.endText();

            // Get order information
            Optional<OrderBusiness> orderOpt = orderRepository.findById(report.getOrderId());
            String orderNumber = orderOpt.map(OrderBusiness::getOrderNumber).map(Object::toString).orElse("Unknown");

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("Order: " + orderNumber);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Created by: " + report.getGeneratedBy().getUsername());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Created at: " + report.getGeneratedAt().toString());
            contentStream.endText();

            List<PhotoDocument> photoDocuments = report.getApprovedPhotos();
            int yPosition = 650;

            for (PhotoDocument photoDocument : photoDocuments) {
                if (yPosition < 100) {
                    contentStream.close();
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    yPosition = 750;
                }
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(
                        "Photo ID: " + photoDocument.getId() + " - Uploader: " +
                        photoDocument.getUploadedBy().getUsername() +
                        " - Status: " +
                        photoDocument.getStatus());
                contentStream.endText();
                yPosition -= 20;
            }

            contentStream.close();
            document.save(destinationFile);
        }
    }
}
