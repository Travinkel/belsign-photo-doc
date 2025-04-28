package com.belman.belsign.domain.model.order;

import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;

import java.util.Objects;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class Order {
    private final UUID id;
    private final OrderNumber orderNumber;
    private final List<PhotoDocument> photoDocuments = new ArrayList<>();

    public Order(UUID id, OrderNumber orderNumber) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderNumber = Objects.requireNonNull(orderNumber, "orderNumber must not be null");
    }

    public void addPhotoDocument(PhotoDocument photoDocument) {
        Objects.requireNonNull(photoDocument, "photoDocument must not be null");
        photoDocuments.add(photoDocument);
    }

    public void removePhotoDocument(PhotoId photoId) {
        Objects.requireNonNull(photoId, "photoId must not be null");
        photoDocuments.removeIf(photoDocument -> photoDocument.getId().equals(photoId));
    }

    public List<PhotoDocument> getPhotoDocuments() {
        return List.copyOf(photoDocuments);
    }

    public UUID getId() {
        return id;
    }

    public OrderNumber getOrderNumber() {
        return orderNumber;
    }
}
