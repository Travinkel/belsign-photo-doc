package com.belman.domain.services;

import com.belman.domain.aggregates.User;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.valueobjects.ImagePath;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.valueobjects.PhotoId;

import java.io.File;
import java.util.List;

/**
 * Service interface for managing photo documents.
 */
public interface PhotoService {
    
    /**
     * Uploads a photo and associates it with an order.
     * 
     * @param file the photo file to upload
     * @param orderId the ID of the order to associate the photo with
     * @param angle the angle at which the photo was taken
     * @param uploadedBy the user who uploaded the photo
     * @return the created photo document
     */
    PhotoDocument uploadPhoto(File file, OrderId orderId, PhotoAngle angle, User uploadedBy);
    
    /**
     * Deletes a photo document.
     * 
     * @param photoId the ID of the photo document to delete
     * @return true if the photo was deleted successfully, false otherwise
     */
    boolean deletePhoto(PhotoId photoId);
    
    /**
     * Gets all photos for an order.
     * 
     * @param orderId the ID of the order
     * @return a list of photo documents for the order
     */
    List<PhotoDocument> getPhotosForOrder(OrderId orderId);
    
    /**
     * Gets a photo document by its ID.
     * 
     * @param photoId the ID of the photo document
     * @return the photo document, or null if not found
     */
    PhotoDocument getPhotoById(PhotoId photoId);
    
    /**
     * Generates a unique file path for a photo.
     * 
     * @param originalFileName the original file name
     * @param orderId the ID of the order
     * @return a unique file path
     */
    ImagePath generateUniqueFilePath(String originalFileName, OrderId orderId);
}