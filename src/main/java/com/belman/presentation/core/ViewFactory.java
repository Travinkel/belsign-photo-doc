package com.belman.presentation.core;

import com.gluonhq.charm.glisten.mvc.View;
import com.belman.infrastructure.logging.EmojiLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Factory for creating and managing Gluon views.
 */
public class ViewFactory {
    private static final EmojiLogger logger = EmojiLogger.getLogger(ViewFactory.class);
    private static final Map<String, Supplier<View>> viewSuppliers = new HashMap<>();

    /**
     * Registers a view with a supplier.
     *
     * @param viewId the unique ID of the view
     * @param supplier the supplier that creates the view
     */
    public static void registerView(String viewId, Supplier<View> supplier) {
        if (viewId == null || viewId.isBlank()) {
            throw new IllegalArgumentException("View ID cannot be null or blank");
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        if (viewSuppliers.containsKey(viewId)) {
            logger.warn("View already registered: {}", viewId);
            return;
        }
        viewSuppliers.put(viewId, supplier);
        logger.info("Registered view: {}", viewId);
    }

    /**
     * Creates a view by its ID.
     *
     * @param viewId the unique ID of the view
     * @return the created view
     */
    public static View createView(String viewId) {
        Supplier<View> supplier = viewSuppliers.get(viewId);
        if (supplier == null) {
            logger.error("No supplier registered for view: {}", viewId);
            throw new IllegalArgumentException("No supplier registered for view: " + viewId);
        }
        return supplier.get();
    }
}
