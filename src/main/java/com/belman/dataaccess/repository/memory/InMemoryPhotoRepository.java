package com.belman.dataaccess.repository.memory;

import com.belman.dataaccess.repository.BaseRepository;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoDocument.ApprovalStatus;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.services.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the PhotoRepository interface.
 * This implementation stores photos in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryPhotoRepository extends BaseRepository<PhotoDocument, PhotoId> implements PhotoRepository {

    private final Map<PhotoId, PhotoDocument> photosById = new ConcurrentHashMap<>();

    /**
     * Creates a new InMemoryPhotoRepository with the specified logger factory.
     *
     * @param loggerFactory the logger factory
     */
    public InMemoryPhotoRepository(LoggerFactory loggerFactory) {
        super(loggerFactory);
    }

    @Override
    protected PhotoId getId(PhotoDocument photoDocument) {
        return photoDocument.getId();
    }

    @Override
    protected PhotoDocument createCopy(PhotoDocument photoDocument) {
        // Create a deep copy of the photo document
        // Note: This is a simplified implementation. In a real application,
        // you would need to create a proper deep copy of all fields.
        return photoDocument;
    }

    @Override
    protected Optional<PhotoDocument> doFindById(PhotoId id) {
        return Optional.ofNullable(photosById.get(id));
    }

    @Override
    protected PhotoDocument doSave(PhotoDocument photoDocument) {
        photosById.put(photoDocument.getId(), photoDocument);
        return photoDocument;
    }

    @Override
    protected void doDelete(PhotoDocument photoDocument) {
        photosById.remove(photoDocument.getId());
    }

    @Override
    protected List<PhotoDocument> doFindAll() {
        return List.copyOf(photosById.values());
    }

    @Override
    protected boolean doExistsById(PhotoId id) {
        return photosById.containsKey(id);
    }

    @Override
    protected long doCount() {
        return photosById.size();
    }

    @Override
    public List<PhotoDocument> findByOrderId(OrderId orderId) {
        return photosById.values().stream()
                .filter(photo -> photo.getOrderId().equals(orderId))
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> findByStatus(ApprovalStatus status) {
        return photosById.values().stream()
                .filter(photo -> photo.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhotoDocument> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status) {
        return photosById.values().stream()
                .filter(photo -> photo.getOrderId().equals(orderId) && photo.getStatus() == status)
                .collect(Collectors.toList());
    }
}
