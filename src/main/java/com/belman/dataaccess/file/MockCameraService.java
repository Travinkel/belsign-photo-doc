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
import java.security.SecureRandom;
import java.util.stream.Collectors;

/**
 * Mock implementation of the CameraService interface for testing and demo purposes.
 * This implementation reads image files from a predefined directory structure
 * to simulate camera functionality without requiring actual hardware.
 */
public class MockCameraService implements CameraService {

    private static final String MOCK_CAMERA_PATH = "src/main/resources/photos";
    private final LoggerFactory loggerFactory;
    private final SecureRandom random = new SecureRandom();

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
        return Optional.of("Taking photo with mock camera service")
                .map(message -> {
                    logInfo(message);
                    return getRandomImageFile();
                })
                .orElseGet(Optional::empty);
    }

    @Override
    public Optional<File> selectPhoto() {
        return Optional.of("Selecting photo with mock camera service")
                .map(message -> {
                    logInfo(message);
                    return getRandomImageFile();
                })
                .orElseGet(Optional::empty);
    }

    @Override
    public boolean isCameraAvailable() {
        // Always return true for the mock implementation
        return java.util.stream.Stream.of(true)
                .findFirst()
                .orElse(false);
    }

    @Override
    public boolean isGalleryAvailable() {
        // Always return true for the mock implementation
        return java.util.stream.Stream.of(true)
                .findFirst()
                .orElse(false);
    }

    /**
     * Gets a random image file from the mock camera directory.
     *
     * @return an Optional containing the image file if found, or empty if not found
     */
    private Optional<File> getRandomImageFile() {
        try {
            // First try to get images from order directories
            return Files.list(Paths.get(MOCK_CAMERA_PATH))
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList())
                    .stream()
                    .findAny()
                    .map(orderDir -> {
                        try {
                            // Get all image files in the order directory
                            List<Path> imageFiles = Files.list(orderDir)
                                .filter(path -> {
                                    String fileName = path.getFileName().toString().toLowerCase();
                                    return java.util.stream.Stream.of(".jpg", ".jpeg", ".png", ".gif")
                                            .anyMatch(fileName::endsWith);
                                })
                                .collect(Collectors.toList());

                            return Optional.of(imageFiles)
                                    .filter(files -> !files.isEmpty())
                                    .map(files -> files.get(random.nextInt(files.size())))
                                    .map(Path::toFile)
                                    .orElseGet(() -> {
                                        logInfo("No image files found in order directory: " + orderDir + ", falling back to root directory");
                                        return null;
                                    });
                        } catch (IOException e) {
                            logError("Error accessing order directory: " + orderDir, e);
                            return null;
                        }
                    })
                    .or(() -> {
                        logInfo("No order directories found or no images in order directories, checking root directory");
                        try {
                            // If no images found in order directories, check for images directly in the mock camera directory
                            return Files.list(Paths.get(MOCK_CAMERA_PATH))
                                .filter(path -> {
                                    String fileName = path.getFileName().toString().toLowerCase();
                                    return java.util.stream.Stream.of(".jpg", ".jpeg", ".png", ".gif")
                                            .anyMatch(fileName::endsWith);
                                })
                                .collect(Collectors.toList())
                                .stream()
                                .findAny()
                                .map(path -> {
                                    logInfo("Using image file from photos directory: " + path.getFileName());
                                    System.out.println("[DEBUG_LOG] Found image in photos directory: " + path.getFileName() + " (path: " + MOCK_CAMERA_PATH + ")");
                                    return path.toFile();
                                });
                        } catch (IOException e) {
                            logError("Error accessing mock camera directory", e);
                            return Optional.empty();
                        }
                    });
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
        return Optional.ofNullable(imageFile)
                .map(File::getAbsolutePath)
                .map(path -> {
                    int lastDotIndex = path.lastIndexOf('.');
                    return lastDotIndex > 0 ? path.substring(0, lastDotIndex) + ".txt" : path + ".txt";
                })
                .map(File::new)
                .filter(File::exists);
    }

    /**
     * Logs an info message.
     *
     * @param message the info message
     */
    private void logInfo(String message) {
        Optional.ofNullable(loggerFactory)
                .ifPresentOrElse(
                        factory -> factory.getLogger(this.getClass()).info(message),
                        () -> System.out.println(message)
                );
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param e       the exception, or null if there is no exception
     */
    private void logError(String message, Exception e) {
        Optional.ofNullable(loggerFactory)
                .ifPresentOrElse(
                        factory -> Optional.ofNullable(e)
                                .ifPresentOrElse(
                                        ex -> factory.getLogger(this.getClass()).error(message, ex),
                                        () -> factory.getLogger(this.getClass()).error(message)
                                ),
                        () -> {
                            System.err.println(message);
                            Optional.ofNullable(e).ifPresent(Exception::printStackTrace);
                        }
                );
    }
}
