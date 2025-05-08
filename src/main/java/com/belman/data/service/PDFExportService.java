package com.belman.data.service;
/*
import com.belman.belsign.application.qcreport.QCReport;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class PDFExportService {
    public void exportQCReport(QCReport report, File destinationFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Quality Control ReportAggregate");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText("OrderAggregate: " + report.getOrder().getOrderNumber());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Created by: " + report.getCreatedBy().getUsername());
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Created at: " + report.getCreatedAt().toString());
            contentStream.endText();

            List<PhotoDocument> photoDocuments = report.getPhotos();
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
                contentStream.showText("Photo ID: " + photoDocument.getId() + " - Uploader: " + photoDocument.getUploader() + " - Status: " +
                                       photoDocument.getApprovalStatus());
                contentStream.endText();
                yPosition -= 20;
            }

            contentStream.close();
            document.save(destinationFile);
        }
    }
}*/