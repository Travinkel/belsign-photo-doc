package com.belman.application.qcreport;



import com.belman.domain.aggregates.Order;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.aggregates.User;

import java.time.LocalDateTime;
import java.util.List;

public class QCReport {
    private final Order order;
    private final List<PhotoDocument> photos;
    private final User createdBy;
    private final LocalDateTime createdAt;

    public QCReport(QCReportBuilder builder) {
        this.order = builder.order;
        this.photos = builder.photos;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdOn;
    }

    public Order getOrder() {
        return order;
    }
    public List<PhotoDocument> getPhotos() {
        return photos;
    }
    public User getCreatedBy() {
        return createdBy;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static QCReportBuilder builder() {
        return new QCReportBuilder();
    }
}
