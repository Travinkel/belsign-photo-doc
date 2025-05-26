package com.belman.common.session;

import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * A session-scoped store for photos.
 * This class is used to store photos in memory for the current session,
 * allowing them to be shared between different users (e.g., production user and qa_user)
 * during the same application session.
 */
public class SessionPhotoStore {
    private static final Logger logger = Logger.getLogger(SessionPhotoStore.class.getName());
    private static SessionPhotoStore instance;
    
    // Use ConcurrentHashMap for thread safety
    private final Map<PhotoId, PhotoDocument> photoStore = new ConcurrentHashMap<>();
    private final Map<OrderId, List<PhotoId>> orderPhotoMap = new ConcurrentHashMap<>();
    
    /**
     * Private constructor to enforce singleton pattern.
     */
    private SessionPhotoStore() {
        logger.info("Initializing SessionPhotoStore");
    }
    
    /**
     * Gets the singleton instance of SessionPhotoStore.
     * 
     * @return the singleton instance
     */
    public static synchronized SessionPhotoStore getInstance() {
        if (instance == null) {
            instance = new SessionPhotoStore();
        }
        return instance;
    }
    
    /**
     * Adds a photo to the session store.
     * 
     * @param photo the photo to add
     */
    public void addPhoto(PhotoDocument photo) {
        if (photo == null || photo.getId() == null) {
            logger.warning("Attempted to add null photo or photo with null ID to session store");
            return;
        }
        
        logger.info("Adding photo " + photo.getId() + " to session store");
        photoStore.put(photo.getId(), photo);
        
        // Add to order-photo mapping
        OrderId orderId = photo.getOrderId();
        if (orderId != null) {
            orderPhotoMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(photo.getId());
        }
    }
    
    /**
     * Gets a photo by ID from the session store.
     * 
     * @param photoId the ID of the photo to get
     * @return an Optional containing the photo if found, or empty if not found
     */
    public Optional<PhotoDocument> getPhoto(PhotoId photoId) {
        if (photoId == null) {
            return Optional.empty();
        }
        
        PhotoDocument photo = photoStore.get(photoId);
        return Optional.ofNullable(photo);
    }
    
    /**
     * Gets all photos for an order from the session store.
     * 
     * @param orderId the ID of the order
     * @return a list of photos for the order
     */
    public List<PhotoDocument> getPhotosByOrderId(OrderId orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        
        List<PhotoId> photoIds = orderPhotoMap.getOrDefault(orderId, new ArrayList<>());
        return photoIds.stream()
                .map(photoStore::get)
                .filter(photo -> photo != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Updates a photo in the session store.
     * 
     * @param photo the photo to update
     */
    public void updatePhoto(PhotoDocument photo) {
        if (photo == null || photo.getId() == null) {
            logger.warning("Attempted to update null photo or photo with null ID in session store");
            return;
        }
        
        logger.info("Updating photo " + photo.getId() + " in session store");
        photoStore.put(photo.getId(), photo);
    }
    
    /**
     * Removes a photo from the session store.
     * 
     * @param photoId the ID of the photo to remove
     * @return true if the photo was removed, false otherwise
     */
    public boolean removePhoto(PhotoId photoId) {
        if (photoId == null) {
            return false;
        }
        
        logger.info("Removing photo " + photoId + " from session store");
        PhotoDocument removedPhoto = photoStore.remove(photoId);
        
        if (removedPhoto != null && removedPhoto.getOrderId() != null) {
            List<PhotoId> photoIds = orderPhotoMap.get(removedPhoto.getOrderId());
            if (photoIds != null) {
                photoIds.remove(photoId);
            }
        }
        
        return removedPhoto != null;
    }
    
    /**
     * Clears all photos from the session store.
     */
    public void clear() {
        logger.info("Clearing session photo store");
        photoStore.clear();
        orderPhotoMap.clear();
    }
    
    /**
     * Gets the number of photos in the session store.
     * 
     * @return the number of photos
     */
    public int size() {
        return photoStore.size();
    }
}