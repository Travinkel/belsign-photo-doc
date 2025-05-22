package com.belman.application.usecase.photo;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.services.LoggerFactory;
import com.belman.application.base.BaseService;

import java.io.File;
import java.util.Optional;

/**
 * In-memory implementation of the CameraService interface for use in memory mode.
 * This implementation doesn't search for mock/camera files and is used when the application
 * is running in memory mode (BELSIGN_STORAGE_TYPE=memory).
 * <p>
 * It always returns empty results for photo operations, as photos are created and managed
 * directly through the PhotoRepository in memory mode.
 */
public class InMemoryCameraService extends BaseService implements CameraService {

    /**
     * Creates a new InMemoryCameraService.
     */
    public InMemoryCameraService() {
        super(ServiceLocator.getService(LoggerFactory.class));
        logInfo("InMemoryCameraService initialized");
    }

    @Override
    public Optional<File> takePhoto() {
        logInfo("Taking photo with in-memory camera service (always returns empty)");
        return Optional.empty();
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with in-memory camera service (always returns empty)");
        return Optional.empty();
    }

    @Override
    public boolean isCameraAvailable() {
        // Always return true to indicate that the service is available
        return true;
    }

    @Override
    public boolean isGalleryAvailable() {
        // Always return true to indicate that the service is available
        return true;
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return ServiceLocator.getService(LoggerFactory.class);
    }
}