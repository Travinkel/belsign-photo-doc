package com.belman.belsign.domain.model.report;



import com.belman.belsign.domain.model.order.OrderId;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.user.User;
import com.belman.belsign.domain.model.shared.Timestamp;

import java.util.List;

/**
 * Entity representing a QC report for an order.
 * Contains approved photos and metadata about its creation.
 */
public class Report {
    private final OrderId orderId;
    private final List<PhotoDocument> approvedPhotos;
    private final User generatedBy;
    private final Timestamp generatedAt;

    public Report(OrderId orderId, List<PhotoDocument> approvedPhotos, User generatedBy, Timestamp generatedAt) {
        this.orderId = orderId;
        this.approvedPhotos = approvedPhotos;
        this.generatedBy = generatedBy;
        this.generatedAt = generatedAt;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public List<PhotoDocument> getApprovedPhotos() {
        return approvedPhotos;
    }

    public User getGeneratedBy() {
        return generatedBy;
    }

    public Timestamp getGeneratedAt() {
        return generatedAt;
    }
}
