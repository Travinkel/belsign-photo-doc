package com.belman.presentation.usecases.worker;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Static context for sharing data between views in the production worker flow.
 * This class provides a simple way to store and retrieve data that needs to be
 * shared between different views in the flow.
 */
public class WorkerFlowContext {

    private static OrderBusiness currentOrder;
    private static PhotoTemplate selectedTemplate;
    private static final List<PhotoDocument> takenPhotos = new ArrayList<>();
    private static final Map<String, Object> attributes = new HashMap<>();

    /**
     * Sets the current order.
     *
     * @param order the order to set
     */
    public static void setCurrentOrder(OrderBusiness order) {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Setting current order: " + 
                          (order != null ? 
                           "ID=" + order.getId().id() + ", Number=" + 
                           (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") : 
                           "null"));
        currentOrder = order;
    }

    /**
     * Gets the current order.
     *
     * @return the current order
     */
    public static OrderBusiness getCurrentOrder() {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Getting current order: " + 
                          (currentOrder != null ? 
                           "ID=" + currentOrder.getId().id() + ", Number=" + 
                           (currentOrder.getOrderNumber() != null ? currentOrder.getOrderNumber().value() : "null") : 
                           "null"));
        return currentOrder;
    }

    /**
     * Sets the selected template.
     *
     * @param template the template to set
     */
    public static void setSelectedTemplate(PhotoTemplate template) {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Setting selected template: " + 
                          (template != null ? template.name() : "null"));
        selectedTemplate = template;
    }

    /**
     * Gets the selected template.
     *
     * @return the selected template
     */
    public static PhotoTemplate getSelectedTemplate() {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Getting selected template: " + 
                          (selectedTemplate != null ? selectedTemplate.name() : "null"));
        return selectedTemplate;
    }

    /**
     * Adds a photo to the list of taken photos.
     *
     * @param photo the photo to add
     */
    public static void addTakenPhoto(PhotoDocument photo) {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Adding taken photo: " + 
                          (photo != null ? 
                           "ID=" + photo.getPhotoId().id() + ", Template=" + photo.getTemplate().name() : 
                           "null"));
        takenPhotos.add(photo);
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Total taken photos now: " + takenPhotos.size());
    }

    /**
     * Gets the list of taken photos.
     *
     * @return the list of taken photos
     */
    public static List<PhotoDocument> getTakenPhotos() {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Getting taken photos list, count: " + takenPhotos.size());
        return new ArrayList<>(takenPhotos);
    }

    /**
     * Sets an attribute.
     *
     * @param key the key
     * @param value the value
     */
    public static void setAttribute(String key, Object value) {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Setting attribute: key=" + key + 
                          ", value=" + (value != null ? value.toString() : "null"));
        attributes.put(key, value);
    }

    /**
     * Gets an attribute.
     *
     * @param key the key
     * @return the value, or null if not found
     */
    public static Object getAttribute(String key) {
        Object value = attributes.get(key);
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Getting attribute: key=" + key + 
                          ", value=" + (value != null ? value.toString() : "null"));
        return value;
    }

    /**
     * Clears all data in the context.
     */
    public static void clear() {
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Clearing all context data");
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Clearing current order: " + 
                          (currentOrder != null ? 
                           "ID=" + currentOrder.getId().id() + ", Number=" + 
                           (currentOrder.getOrderNumber() != null ? currentOrder.getOrderNumber().value() : "null") : 
                           "null"));
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Clearing selected template: " + 
                          (selectedTemplate != null ? selectedTemplate.name() : "null"));
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Clearing " + takenPhotos.size() + " taken photos");
        System.out.println("[DEBUG_LOG] WorkerFlowContext: Clearing " + attributes.size() + " attributes");

        currentOrder = null;
        selectedTemplate = null;
        takenPhotos.clear();
        attributes.clear();
    }
}
