package com.belman.domain.photo;

import com.belman.domain.common.Timestamp;
import com.belman.domain.order.OrderId;

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
     * Creates a new PhotoDocument with the specified parameters.
     *
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @param uploadedAt the timestamp when this photo was uploaded
     * @return a new PhotoDocument instance
     */
    public static PhotoDocumentd create(PhotoAngle angle, ImagePath imagePath,
                                        UserReference uploadedBy, Timestamp uploadedAt) {
        PhotoId photoId = new PhotoId(UUID.randomUUID().toString());
        return new PhotoDocumentd(photoId, angle, imagePath, uploadedBy, uploadedAt);
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
    public static PhotoDocumentd createForOrder(PhotoAngle angle, ImagePath imagePath,
                                                UserReference uploadedBy, Timestamp uploadedAt,
                                                OrderId orderId) {
        PhotoDocumentd photo = create(angle, imagePath, uploadedBy, uploadedAt);
        photo.assignToOrder(orderId);
        return photo;
    }

    /**
     * Creates a new PhotoDocument with the current timestamp.
     *
     * @param angle      the angle at which the photo was taken
     * @param imagePath  the path to the image file
     * @param uploadedBy reference to the user who uploaded this photo
     * @return a new PhotoDocument instance with the current timestamp
     */
    public static PhotoDocumentd createWithCurrentTimestamp(PhotoAngle angle, ImagePath imagePath,
                                                            UserReference uploadedBy) {
        return create(angle, imagePath, uploadedBy, Timestamp.now());
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
    public static PhotoDocumentd createForOrderWithCurrentTimestamp(PhotoAngle angle, ImagePath imagePath,
                                                                    UserReference uploadedBy,
                                                                    OrderId orderId) {
        return createForOrder(angle, imagePath, uploadedBy, Timestamp.now(), orderId);
    }
}