package com.belman.bootstrap.config;

import com.belman.bootstrap.di.ServiceLocator;
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

            // Explicitly inject services into OrderManager
            logger.debug("Injecting services into OrderManager");
            ServiceLocator.injectServices(orderManager);
            logger.success("OrderManager registered and injected successfully");

            // Create and register PhotoCaptureManager
            logger.debug("Creating PhotoCaptureManager");
            PhotoCaptureManager photoCaptureManager = new PhotoCaptureManager();
            ServiceRegistry.registerService(photoCaptureManager);

            // Explicitly inject services into PhotoCaptureManager
            logger.debug("Injecting services into PhotoCaptureManager");
            ServiceLocator.injectServices(photoCaptureManager);
            logger.success("PhotoCaptureManager registered and injected successfully");

            // Create and register TemplateManager
            logger.debug("Creating TemplateManager");
            TemplateManager templateManager = new TemplateManager();
            ServiceRegistry.registerService(templateManager);

            // Explicitly inject services into TemplateManager
            logger.debug("Injecting services into TemplateManager");
            ServiceLocator.injectServices(templateManager);
            logger.success("TemplateManager registered and injected successfully");

            logger.success("PhotoCube managers initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize PhotoCube managers", e);
            throw new RuntimeException("Failed to initialize PhotoCube managers", e);
        }
    }
}
