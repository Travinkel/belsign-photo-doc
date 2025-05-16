package com.belman.service.camera.impl;

import com.belman.service.platform.CameraService;
import com.belman.repository.camera.GluonCameraService;

import java.io.File;
import java.util.Optional;

/**
 * Adapter for GluonCameraService that implements the CameraService interface.
 * This class delegates all method calls to an instance of GluonCameraService.
 */
public class GluonCameraServiceAdapter implements CameraService {
    private final GluonCameraService delegate;

    /**
     * Creates a new GluonCameraServiceAdapter with the specified temporary directory.
     *
     * @param tempDirectory the directory to store temporary files
     */
    public GluonCameraServiceAdapter(String tempDirectory) {
        this.delegate = new GluonCameraService(tempDirectory);
    }

    @Override
    public Optional<File> takePhoto() {
        return delegate.takePhoto();
    }

    @Override
    public Optional<File> selectPhoto() {
        return delegate.selectPhoto();
    }

    @Override
    public boolean isCameraAvailable() {
        return delegate.isCameraAvailable();
    }

    @Override
    public boolean isGalleryAvailable() {
        return delegate.isGalleryAvailable();
    }
}