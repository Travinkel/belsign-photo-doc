package com.belman.bootstrap.camera;

import com.belman.domain.services.CameraService;
import com.belman.data.service.MockCameraService;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * Adapter for MockCameraService that implements the CameraService interface.
 * This class delegates all method calls to an instance of MockCameraService.
 */
public class MockCameraServiceAdapter implements CameraService {
    private final MockCameraService delegate;

    /**
     * Creates a new MockCameraServiceAdapter with a null stage.
     * This constructor is useful for testing or when a stage is not available.
     */
    public MockCameraServiceAdapter() {
        this.delegate = new MockCameraService();
    }

    /**
     * Creates a new MockCameraServiceAdapter with the specified stage.
     *
     * @param stage the JavaFX stage to use for file chooser dialogs
     */
    public MockCameraServiceAdapter(Stage stage) {
        this.delegate = new MockCameraService(stage);
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