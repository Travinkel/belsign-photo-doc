package com.belman.application.usecase.photo;

import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoTemplate;

import java.util.List;

/**
 * Service for photo template operations.
 * Provides methods for managing photo templates and checking template requirements.
 */
public interface PhotoTemplateService {
    /**
     * Gets all available photo templates for an order.
     *
     * @param orderId the ID of the order
     * @return a list of available photo templates
     */
    List<PhotoTemplate> getAvailableTemplates(OrderId orderId);

    /**
     * Checks if an order has all required photos.
     *
     * @param orderId the ID of the order
     * @return true if the order has all required photos, false otherwise
     */
    boolean hasAllRequiredPhotos(OrderId orderId);

    /**
     * Gets the missing required templates for an order.
     *
     * @param orderId the ID of the order
     * @return a list of required templates that are missing photos
     */
    List<PhotoTemplate> getMissingRequiredTemplates(OrderId orderId);
}