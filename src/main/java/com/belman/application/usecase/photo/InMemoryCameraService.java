package com.belman.application.usecase.photo;

import com.belman.bootstrap.di.ServiceLocator;
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
import java.security.SecureRandom;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the CameraService interface for use in memory mode.
 * This implementation uses images from src/main/resources/photos when taking pictures for testing.
 * <p>
 * It returns a random image from the photos directory when takePhoto() is called.
 */
public class InMemoryCameraService extends BaseService implements CameraService {

    private static final String PHOTOS_PATH = "src/main/resources/photos";
    private List<File> availableImages = new ArrayList<>();
    private final SecureRandom random = new SecureRandom();

    /**
     * Creates a new InMemoryCameraService.
     */
    public InMemoryCameraService() {
        super(ServiceLocator.getService(LoggerFactory.class));
        loadImages();
        logInfo("InMemoryCameraService initialized with " + availableImages.size() + " images");
    }

    @Override
    public Optional<File> takePhoto() {
        logInfo("Taking photo with in-memory camera service");
        if (availableImages.isEmpty()) {
            logWarn("No images available in " + PHOTOS_PATH);
            return Optional.empty();
        }

        // Return a random image from the available images
        File selectedImage = availableImages.get(random.nextInt(availableImages.size()));
        logInfo("Selected image: " + selectedImage.getName());
        return Optional.of(selectedImage);
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with in-memory camera service");
        if (availableImages.isEmpty()) {
            logWarn("No images available in " + PHOTOS_PATH);
            return Optional.empty();
        }

        // Return a random image from the available images
        File selectedImage = availableImages.get(random.nextInt(availableImages.size()));
        logInfo("Selected image: " + selectedImage.getName());
        return Optional.of(selectedImage);
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

    /**
     * Loads images from the photos directory.
     */
    private void loadImages() {
        availableImages.clear();

        try {
            Path photosPath = Paths.get(PHOTOS_PATH);
            if (Files.exists(photosPath)) {
                loadImagesFromDirectory(photosPath);
                logInfo("Loaded " + availableImages.size() + " images from " + PHOTOS_PATH);
                System.out.println("[DEBUG_LOG] Loaded " + availableImages.size() + " images from " + PHOTOS_PATH);

                // Log the names of the loaded images for debugging
                if (!availableImages.isEmpty()) {
                    System.out.println("[DEBUG_LOG] Loaded images:");
                    for (File image : availableImages) {
                        System.out.println("[DEBUG_LOG] - " + image.getName());
                    }
                }
            } else {
                logWarn("Photos directory not found: " + PHOTOS_PATH);
                System.out.println("[DEBUG_LOG] Photos directory not found: " + PHOTOS_PATH);
            }
        } catch (IOException e) {
            logError("Error loading images", e);
            System.out.println("[DEBUG_LOG] Error loading images: " + e.getMessage());
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

        availableImages.addAll(imageFiles);
    }
}
