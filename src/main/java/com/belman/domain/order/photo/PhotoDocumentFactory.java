package com.belman.domain.order.photo;

import com.belman.domain.common.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserBusiness;

import java.util.UUID;

/**
 * Factory for creating PhotoDocument instances.
 * <p>
 * This factory encapsulates the creation logic for photo documents,
 * following the Factory pattern from Domain-Driven Design.
 * It provides methods to create various types of photo documents
 * with appropriate validation and defaults.
 */
public class PhotoDocumentFactory {

    /**
     * Creates a new PhotoDocument with the current timestamp.
     *
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @return a new PhotoDocument instance with the current timestamp
     */
    public static PhotoDocument createWithCurrentTimestamp(PhotoTemplate angle, Photo imagePath,
                                                           UserBusiness uploadedBy) {
        return create(angle, imagePath, uploadedBy, Timestamp.now());
    }

    /**
     * Creates a new PhotoDocument with the specified parameters.
     *
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @param uploadedAt the timestamp when this photo was uploaded
     * @return a new PhotoDocument instance
     */
    public static PhotoDocument create(PhotoTemplate angle, Photo imagePath,
                                       UserBusiness uploadedBy, Timestamp uploadedAt) {
        return PhotoDocument.builder()
                .photoId(new PhotoId(UUID.randomUUID().toString()))
                .template(angle)
                .imagePath(imagePath)
                .uploadedBy(uploadedBy)
                .uploadedAt(uploadedAt)
                .build();
    }

    /**
     * Creates a new PhotoDocument with the current timestamp and assigns it to an order.
     *
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @param orderId    the ID of the order to assign this photo to
     * @return a new PhotoDocument instance with the current timestamp, assigned to the specified order
     */
    public static PhotoDocument createForOrderWithCurrentTimestamp(PhotoTemplate angle, Photo imagePath,
                                                                   UserBusiness uploadedBy,
                                                                   OrderId orderId) {
        return createForOrder(angle, imagePath, uploadedBy, Timestamp.now(), orderId);
    }

    /**
     * Creates a new PhotoDocument with the specified parameters and assigns it to an order.
     *
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @param uploadedAt the timestamp when this photo was uploaded
     * @param orderId    the ID of the order to assign this photo to
     * @return a new PhotoDocument instance assigned to the specified order
     */
    public static PhotoDocument createForOrder(PhotoTemplate angle, Photo imagePath,
                                               UserBusiness uploadedBy, Timestamp uploadedAt,
                                               OrderId orderId) {
        return PhotoDocument.builder()
                .photoId(new PhotoId(UUID.randomUUID().toString()))
                .template(angle)
                .imagePath(imagePath)
                .uploadedBy(uploadedBy)
                .uploadedAt(uploadedAt)
                .orderId(orderId)
                .build();
    }
}