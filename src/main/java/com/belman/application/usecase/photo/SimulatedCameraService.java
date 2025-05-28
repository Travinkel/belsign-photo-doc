package com.belman.application.usecase.photo;

import com.belman.common.di.ServiceProviderFactory;
import com.belman.domain.services.LoggerFactory;
import com.belman.application.base.BaseService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Simulated implementation of the CameraService interface for development and testing.
 * This implementation loads mock images from a predefined directory and allows the user
 * to select one to simulate taking a photo.
 */
public class SimulatedCameraService extends BaseService implements CameraService {

    private static final String MOCK_CAMERA_PATH = "src/main/resources/photos";
    private static final String DEV_SIMULATED_PATH = "src/main/resources/photos/dev-simulated";

    private List<File> availableMockImages = new ArrayList<>();
    private File selectedImage = null;

    /**
     * Creates a new SimulatedCameraService.
     */
    public SimulatedCameraService() {
        super(ServiceLocator.getService(LoggerFactory.class));
        loadMockImages();
    }

    @Override
    public Optional<File> takePhoto() {
        logInfo("Taking photo with simulated camera service");
        return Optional.ofNullable(selectedImage);
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with simulated camera service");
        return Optional.ofNullable(selectedImage);
    }

    @Override
    public boolean isCameraAvailable() {
        // Always return true for the simulated implementation
        return true;
    }

    @Override
    public boolean isGalleryAvailable() {
        // Always return true for the simulated implementation
        return true;
    }

    /**
     * Gets the list of available mock images.
     *
     * @return the list of available mock images
     */
    public List<File> getAvailableMockImages() {
        return availableMockImages;
    }

    /**
     * Sets the selected image.
     *
     * @param image the selected image
     */
    public void setSelectedImage(File image) {
        this.selectedImage = image;
    }

    /**
     * Loads the mock images from the mock camera directory.
     */
    private void loadMockImages() {
        availableMockImages.clear();

        try {
            // First try to load from dev-simulated directory
            Path devSimulatedPath = Paths.get(DEV_SIMULATED_PATH);
            if (Files.exists(devSimulatedPath)) {
                loadImagesFromDirectory(devSimulatedPath);
            }

            // If no images were found in dev-simulated, load from the main mock camera directory
            if (availableMockImages.isEmpty()) {
                Path mockCameraPath = Paths.get(MOCK_CAMERA_PATH);
                if (Files.exists(mockCameraPath)) {
                    loadImagesFromDirectory(mockCameraPath);
                }
            }

            logInfo("Loaded " + availableMockImages.size() + " mock images from " + MOCK_CAMERA_PATH);
            System.out.println("[DEBUG_LOG] Loaded " + availableMockImages.size() + " mock images from " + MOCK_CAMERA_PATH);
        } catch (IOException e) {
            logError("Error loading mock images", e);
        }
    }

    /**
     * Loads images from the specified directory.
     *
     * @param directory the directory to load images from
     * @throws IOException if an I/O error occurs
     */
    private void loadImagesFromDirectory(Path directory) throws IOException {
        List<File> imageFiles = Files.list(directory)
            .filter(path -> {
                String fileName = path.getFileName().toString().toLowerCase();
                return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                       fileName.endsWith(".png") || fileName.endsWith(".gif");
            })
            .map(Path::toFile)
            .collect(Collectors.toList());

        availableMockImages.addAll(imageFiles);
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return ServiceLocator.getService(LoggerFactory.class);
    }
}
