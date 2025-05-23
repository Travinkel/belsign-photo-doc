package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceRegistry;
import com.belman.common.logging.EmojiLogger;
import com.belman.presentation.usecases.worker.photocube.managers.OrderManager;
import com.belman.presentation.usecases.worker.photocube.managers.PhotoCaptureManager;
import com.belman.presentation.usecases.worker.photocube.managers.TemplateManager;

/**
 * Initializes the manager classes for the PhotoCube feature.
 * This class is responsible for creating and registering the manager classes
 * that are used by the PhotoCubeViewModel.
 */
public class PhotoCubeManagersInitializer {

    private static final EmojiLogger logger = EmojiLogger.getLogger(PhotoCubeManagersInitializer.class);

    /**
     * Initializes the manager classes for the PhotoCube feature.
     * This method creates instances of the manager classes and registers them with the ServiceRegistry.
     */
    public static void initialize() {
        logger.debug("Initializing PhotoCube managers");

        try {
            // Create and register OrderManager
            logger.debug("Creating OrderManager");
            OrderManager orderManager = new OrderManager();
            ServiceRegistry.registerService(orderManager);
            logger.success("OrderManager registered successfully");

            // Create and register PhotoCaptureManager
            logger.debug("Creating PhotoCaptureManager");
            PhotoCaptureManager photoCaptureManager = new PhotoCaptureManager();
            ServiceRegistry.registerService(photoCaptureManager);
            logger.success("PhotoCaptureManager registered successfully");

            // Create and register TemplateManager
            logger.debug("Creating TemplateManager");
            TemplateManager templateManager = new TemplateManager();
            ServiceRegistry.registerService(templateManager);
            logger.success("TemplateManager registered successfully");

            logger.success("PhotoCube managers initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize PhotoCube managers", e);
            throw new RuntimeException("Failed to initialize PhotoCube managers", e);
        }
    }
}