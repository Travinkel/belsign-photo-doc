package com.belman.data.persistence.memory;

import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.order.photo.PhotoRepository;
import com.belman.domain.user.ApprovalStatus;
import com.belman.data.logging.EmojiLoggerFactory;
import com.belman.business.base.BaseService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the PhotoRepository interface.
 * This implementation stores photo documents in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryPhotoRepository extends BaseService implements PhotoRepository {
    private final Map<PhotoId, PhotoDocument> photosById = new HashMap<>();
    private final Map<OrderId, List<PhotoId>> photoIdsByOrderId = new HashMap<>();

    /**
     * Creates a new InMemoryPhotoRepository.
     */
    public InMemoryPhotoRepository() {
        super(EmojiLoggerFactory.getInstance());
    }

    @Override
    public Optional<PhotoDocument> findById(PhotoId id) {
        return Optional.ofNullable(photosById.get(id));
    }

    @Override
    public List<PhotoDocument> findByOrderId(OrderId orderId) {
        List<PhotoId> photoIds = photoIdsByOrderId.getOrDefault(orderId, new ArrayList<>());
        return photoIds.stream()
                .map(photosById::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> findByStatus(ApprovalStatus status) {
        return photosById.values().stream()
                .filter(photo -> convertStatus(photo.getStatus()) == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status) {
        return findByOrderId(orderId).stream()
                .filter(photo -> convertStatus(photo.getStatus()) == status)
                .collect(Collectors.toList());
    }

    /**
     * Converts from PhotoDocument.ApprovalStatus to com.belman.module.user.ApprovalStatus.
     *
     * @param status the PhotoDocument.ApprovalStatus to convert
     * @return the equivalent com.belman.module.user.ApprovalStatus
     */
    private ApprovalStatus convertStatus(PhotoDocument.ApprovalStatus status) {
        return switch (status) {
            case PENDING -> ApprovalStatus.PENDING;
            case APPROVED -> ApprovalStatus.APPROVED;
            case REJECTED -> ApprovalStatus.REJECTED;
        };
    }

    @Override
    public PhotoDocument save(PhotoDocument photoDocument) {
        if (photoDocument == null) {
            throw new IllegalArgumentException("Photo document cannot be null");
        }

        PhotoId photoId = photoDocument.getId();
        OrderId orderId = photoDocument.getOrderId();

        // Store the photo document by ID
        photosById.put(photoId, photoDocument);

        // Update the mapping from order ID to photo IDs
        if (orderId != null) {
            List<PhotoId> photoIds = photoIdsByOrderId.computeIfAbsent(orderId, k -> new ArrayList<>());
            if (!photoIds.contains(photoId)) {
                photoIds.add(photoId);
            }
        }

        return photoDocument;
    }

    @Override
    public void delete(PhotoDocument photoDocument) {
        if (photoDocument == null) {
            throw new IllegalArgumentException("Photo document cannot be null");
        }

        deleteById(photoDocument.getId());
    }

    @Override
    public boolean deleteById(PhotoId id) {
        PhotoDocument photoDocument = photosById.get(id);
        if (photoDocument != null) {
            // Remove the photo document from the ID map
            photosById.remove(id);

            // Remove the photo ID from the order ID mapping
            OrderId orderId = photoDocument.getOrderId();
            if (orderId != null) {
                List<PhotoId> photoIds = photoIdsByOrderId.get(orderId);
                if (photoIds != null) {
                    photoIds.remove(id);
                    if (photoIds.isEmpty()) {
                        photoIdsByOrderId.remove(orderId);
                    }
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public List<PhotoDocument> findAll() {
        return new ArrayList<>(photosById.values());
    }

    @Override
    public boolean existsById(PhotoId id) {
        return photosById.containsKey(id);
    }

    @Override
    public long count() {
        return photosById.size();
    }
}
