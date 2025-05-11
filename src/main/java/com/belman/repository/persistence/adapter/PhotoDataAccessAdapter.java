package com.belman.repository.persistence.adapter;

import com.belman.domain.order.OrderId;
import com.belman.domain.order.photo.PhotoDataAccess;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoId;
import com.belman.domain.order.photo.PhotoRepository;
import com.belman.domain.user.ApprovalStatus;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementation of the PhotoDataAccess interface.
 * This class adapts the InMemoryPhotoRepository to the PhotoDataAccess interface,
 * allowing the business layer to interact with the data layer through the PhotoDataAccess interface.
 */
public class PhotoDataAccessAdapter implements PhotoDataAccess {
    private final PhotoRepository repository;

    /**
     * Creates a new PhotoDataAccessAdapter with the specified repository.
     *
     * @param repository the repository to adapt
     */
    public PhotoDataAccessAdapter(PhotoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PhotoDocument> findById(PhotoId id) {
        return repository.findById(id);
    }

    @Override
    public PhotoDocument save(PhotoDocument component) {
        return repository.save(component);
    }

    @Override
    public void delete(PhotoDocument component) {
        repository.delete(component);
    }

    @Override
    public boolean deleteById(PhotoId id) {
        return repository.deleteById(id);
    }

    @Override
    public List<PhotoDocument> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean existsById(PhotoId id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<PhotoDocument> findByOrderId(OrderId orderId) {
        return repository.findByOrderId(orderId);
    }

    @Override
    public List<PhotoDocument> findByStatus(ApprovalStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<PhotoDocument> findByOrderIdAndStatus(OrderId orderId, ApprovalStatus status) {
        return repository.findByOrderIdAndStatus(orderId, status);
    }
}