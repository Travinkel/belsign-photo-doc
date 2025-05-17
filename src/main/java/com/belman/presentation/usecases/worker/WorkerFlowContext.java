package com.belman.presentation.usecases.worker;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.photo.PhotoDocument;
import com.belman.domain.order.photo.PhotoTemplate;

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
        currentOrder = order;
    }
    
    /**
     * Gets the current order.
     *
     * @return the current order
     */
    public static OrderBusiness getCurrentOrder() {
        return currentOrder;
    }
    
    /**
     * Sets the selected template.
     *
     * @param template the template to set
     */
    public static void setSelectedTemplate(PhotoTemplate template) {
        selectedTemplate = template;
    }
    
    /**
     * Gets the selected template.
     *
     * @return the selected template
     */
    public static PhotoTemplate getSelectedTemplate() {
        return selectedTemplate;
    }
    
    /**
     * Adds a photo to the list of taken photos.
     *
     * @param photo the photo to add
     */
    public static void addTakenPhoto(PhotoDocument photo) {
        takenPhotos.add(photo);
    }
    
    /**
     * Gets the list of taken photos.
     *
     * @return the list of taken photos
     */
    public static List<PhotoDocument> getTakenPhotos() {
        return new ArrayList<>(takenPhotos);
    }
    
    /**
     * Sets an attribute.
     *
     * @param key the key
     * @param value the value
     */
    public static void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * Gets an attribute.
     *
     * @param key the key
     * @return the value, or null if not found
     */
    public static Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Clears all data in the context.
     */
    public static void clear() {
        currentOrder = null;
        selectedTemplate = null;
        takenPhotos.clear();
        attributes.clear();
    }
}