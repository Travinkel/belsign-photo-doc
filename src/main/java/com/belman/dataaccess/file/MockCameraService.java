package com.belman.dataaccess.file;

import com.belman.application.usecase.photo.CameraService;
import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.services.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Mock implementation of the CameraService interface for testing and demo purposes.
 * This implementation reads image files from a predefined directory structure
 * to simulate camera functionality without requiring actual hardware.
 */
public class MockCameraService implements CameraService {

    private static final String MOCK_CAMERA_PATH = "src/main/resources/photos";
    private final LoggerFactory loggerFactory;
    private final Random random = new Random();

    /**
     * Creates a new MockCameraService.
     *
     * @param loggerFactory the logger factory
     */
    public MockCameraService(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Optional<File> takePhoto() {
        logInfo("Taking photo with mock camera service");
        return getRandomImageFile();
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with mock camera service");
        return getRandomImageFile();
    }

    @Override
    public boolean isCameraAvailable() {
        // Always return true for the mock implementation
        return true;
    }

    @Override
    public boolean isGalleryAvailable() {
        // Always return true for the mock implementation
        return true;
    }

    /**
     * Gets a random image file from the mock camera directory.
     *
     * @return an Optional containing the image file if found, or empty if not found
     */
    private Optional<File> getRandomImageFile() {
        try {
            // First try to get images from order directories
            List<Path> orderDirs = Files.list(Paths.get(MOCK_CAMERA_PATH))
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());

            if (!orderDirs.isEmpty()) {
                // Select a random order directory
                Path orderDir = orderDirs.get(random.nextInt(orderDirs.size()));

                // Get all image files in the order directory
                List<Path> imageFiles = Files.list(orderDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                               fileName.endsWith(".png") || fileName.endsWith(".gif");
                    })
                    .collect(Collectors.toList());

                if (!imageFiles.isEmpty()) {
                    // Select a random image file
                    Path imageFile = imageFiles.get(random.nextInt(imageFiles.size()));
                    return Optional.of(imageFile.toFile());
                } else {
                    logInfo("No image files found in order directory: " + orderDir + ", falling back to root directory");
                }
            } else {
                logInfo("No order directories found in mock camera path, checking for images directly in the directory");
            }

            // If no images found in order directories, check for images directly in the mock camera directory
            List<Path> rootImageFiles = Files.list(Paths.get(MOCK_CAMERA_PATH))
                .filter(path -> {
                    String fileName = path.getFileName().toString().toLowerCase();
                    return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
                           fileName.endsWith(".png") || fileName.endsWith(".gif");
                })
                .collect(Collectors.toList());

            if (rootImageFiles.isEmpty()) {
                logError("No image files found in mock camera directory", null);
                return Optional.empty();
            }

            // Select a random image file
            Path imageFile = rootImageFiles.get(random.nextInt(rootImageFiles.size()));
            logInfo("Using image file from photos directory: " + imageFile.getFileName());
            System.out.println("[DEBUG_LOG] Found image in photos directory: " + imageFile.getFileName() + " (path: " + MOCK_CAMERA_PATH + ")");
            return Optional.of(imageFile.toFile());
        } catch (IOException e) {
            logError("Error accessing mock camera directory", e);
            return Optional.empty();
        }
    }

    /**
     * Gets the metadata file for an image file.
     * The metadata file has the same name as the image file but with a .txt extension.
     *
     * @param imageFile the image file
     * @return an Optional containing the metadata file if found, or empty if not found
     */
    public Optional<File> getMetadataFile(File imageFile) {
        if (imageFile == null) {
            return Optional.empty();
        }

        String imagePath = imageFile.getAbsolutePath();
        String metadataPath = imagePath.substring(0, imagePath.lastIndexOf('.')) + ".txt";
        File metadataFile = new File(metadataPath);

        return metadataFile.exists() ? Optional.of(metadataFile) : Optional.empty();
    }

    /**
     * Logs an info message.
     *
     * @param message the info message
     */
    private void logInfo(String message) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).info(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param e       the exception, or null if there is no exception
     */
    private void logError(String message, Exception e) {
        if (loggerFactory != null) {
            if (e != null) {
                loggerFactory.getLogger(this.getClass()).error(message, e);
            } else {
                loggerFactory.getLogger(this.getClass()).error(message);
            }
        } else {
            System.err.println(message);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
}
