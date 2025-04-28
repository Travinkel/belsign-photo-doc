package com.belman.belsign.domain.repository;

import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository {
    void save(PhotoDocument photo);
    List<PhotoDocument> findByOrderId(UUID orderId);
    void delete(UUID photoId);
}
