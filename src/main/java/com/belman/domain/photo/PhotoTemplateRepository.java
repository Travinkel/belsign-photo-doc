package com.belman.domain.photo;

import com.belman.domain.common.base.Repository;
import com.belman.domain.order.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing photo templates.
 * This interface follows the Repository pattern from Domain-Driven Design and provides
 * methods for accessing photo templates stored in the database.
 */
public interface PhotoTemplateRepository extends Repository<PhotoTemplate, String> {

    /**
     * Finds a photo template by ID.
     *
     * @param id the template ID to search for
     * @return an Optional containing the photo template if found, or empty if not found
     */
    Optional<PhotoTemplate> findById(String id);

    /**
     * Finds all photo templates for a specific order.
     *
     * @param orderId the order ID to search for
     * @return a list of photo templates for the specified order
     */
    List<PhotoTemplate> findByOrderId(OrderId orderId);

    /**
     * Finds all photo templates.
     *
     * @return a list of all photo templates
     */
    List<PhotoTemplate> findAll();

    /**
     * Associates a photo template with an order.
     *
     * @param orderId    the order ID
     * @param templateId the template ID
     * @param required   whether the template is required for the order
     * @return true if the association was created successfully, false otherwise
     */
    boolean associateWithOrder(OrderId orderId, String templateId, boolean required);

    /**
     * Removes the association between a photo template and an order.
     *
     * @param orderId    the order ID
     * @param templateId the template ID
     * @return true if the association was removed successfully, false otherwise
     */
    boolean removeFromOrder(OrderId orderId, String templateId);

    /**
     * Checks if a photo template is required for an order.
     *
     * @param orderId    the order ID
     * @param templateId the template ID
     * @return true if the template is required for the order, false otherwise
     */
    boolean isRequiredForOrder(OrderId orderId, String templateId);
}