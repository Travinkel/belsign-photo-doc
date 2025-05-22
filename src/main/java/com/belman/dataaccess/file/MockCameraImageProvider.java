package com.belman.dataaccess.file;

import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mock implementation of the CameraImageProvider interface for testing and demo purposes.
 * This implementation reads image files from a predefined directory structure
 * to simulate camera functionality without requiring actual hardware.
 */
public class MockCameraImageProvider implements CameraImageProvider {

    private static final String MOCK_CAMERA_PATH = "src/main/resources/mock/camera";
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new MockCameraImageProvider.
     *
     * @param loggerFactory the logger factory
     */
    public MockCameraImageProvider(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Optional<File> takePhoto(PhotoTemplate template, String orderId) {
        logInfo("Taking mock photo for template: " + template.name() + ", order: " + orderId);

        // First try to find a matching image for this template and order
        Optional<File> matchingImage = findMatchingImage(template, orderId);

        if (matchingImage.isPresent()) {
            return matchingImage;
        }

        // If no matching image found, try to find any image for this template
        Optional<File> anyTemplateImage = findAnyImageForTemplate(template);

        if (anyTemplateImage.isPresent()) {
            return anyTemplateImage;
        }

        // If still no image found, fall back to any image in the mock directory
        return findAnyImage();
    }

    @Override
    public Optional<File> selectPhoto(PhotoTemplate template, String orderId) {
        logInfo("Selecting mock photo for template: " + template.name() + ", order: " + orderId);
        // Use the same logic as takePhoto for consistency
        return takePhoto(template, orderId);
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

    @Override
    public boolean isMockProvider() {
        // This is a mock implementation
        return true;
    }

    /**
     * Finds an image that matches the specified template and order ID.
     *
     * @param template the photo template
     * @param orderId the order ID
     * @return an Optional containing the image file if found, or empty if not found
     */
    private Optional<File> findMatchingImage(PhotoTemplate template, String orderId) {
        try {
            // Check if there's a directory for this order
            Path orderDir = findOrderDirectory(orderId);

            if (orderDir != null) {
                // Look for images that match the template name
                List<Path> matchingImages = Files.list(orderDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return isImageFile(fileName) && 
                               (fileName.contains(template.name().toLowerCase()) || 
                                fileName.contains(sanitizeTemplateName(template.name()).toLowerCase()));
                    })
                    .collect(Collectors.toList());

                if (!matchingImages.isEmpty()) {
                    // Return the first matching image
                    return Optional.of(matchingImages.get(0).toFile());
                }
            }

            return Optional.empty();
        } catch (IOException e) {
            logError("Error finding matching image", e);
            return Optional.empty();
        }
    }

    /**
     * Finds any image for the specified template, regardless of order.
     *
     * @param template the photo template
     * @return an Optional containing the image file if found, or empty if not found
     */
    private Optional<File> findAnyImageForTemplate(PhotoTemplate template) {
        try {
            // Special handling for OF_WELD template
            if (template != null && template.name() != null && 
                (template.name().contains("WELD") || template.name().contains("Weld") || template.name().contains("weld"))) {
                logInfo("Special handling for WELD template: " + template.name());

                // First check if there are any images with "weld" in the filename directly in the mock camera directory
                List<Path> weldImages = Files.list(Paths.get(MOCK_CAMERA_PATH))
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return isImageFile(fileName) && fileName.contains("weld");
                    })
                    .collect(Collectors.toList());

                if (!weldImages.isEmpty()) {
                    logInfo("Found weld image in root directory: " + weldImages.get(0));
                    return Optional.of(weldImages.get(0).toFile());
                }

                // If no weld-specific images found, use any image for weld templates
                List<Path> anyImages = Files.list(Paths.get(MOCK_CAMERA_PATH))
                    .filter(path -> isImageFile(path.getFileName().toString().toLowerCase()))
                    .collect(Collectors.toList());

                if (!anyImages.isEmpty()) {
                    logInfo("Using generic image for weld template: " + anyImages.get(0));
                    return Optional.of(anyImages.get(0).toFile());
                }

                // Check mock camer directory
                Path devSimulatedDir = Paths.get(MOCK_CAMERA_PATH);
                if (Files.exists(devSimulatedDir)) {
                    try {
                        List<Path> devImages = Files.list(devSimulatedDir)
                            .filter(path -> isImageFile(path.getFileName().toString().toLowerCase()))
                            .collect(Collectors.toList());

                        if (!devImages.isEmpty()) {
                            logInfo("Found image in mock camera directory: " + devImages.get(0));
                            return Optional.of(devImages.get(0).toFile());
                        }
                    } catch (IOException e) {
                        logError("Error listing files in mock camera directory", e);
                    }
                }
            }

            // Standard handling for other templates
            // Look in all order directories
            List<Path> orderDirs = Files.list(Paths.get(MOCK_CAMERA_PATH))
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

            for (Path orderDir : orderDirs) {
                try {
                    // Look for images that match the template name
                    List<Path> matchingImages = Files.list(orderDir)
                        .filter(path -> {
                            String fileName = path.getFileName().toString().toLowerCase();
                            return isImageFile(fileName) && 
                                   (fileName.contains(template.name().toLowerCase()) || 
                                    fileName.contains(sanitizeTemplateName(template.name()).toLowerCase()));
                        })
                        .collect(Collectors.toList());

                    if (!matchingImages.isEmpty()) {
                        // Return the first matching image
                        return Optional.of(matchingImages.get(0).toFile());
                    }
                } catch (IOException e) {
                    // Log but continue to next directory
                    logError("Error listing files in directory: " + orderDir, e);
                }
            }

            // If no matching images found in directories, check root directory
            List<Path> rootImages = Files.list(Paths.get(MOCK_CAMERA_PATH))
                .filter(path -> isImageFile(path.getFileName().toString().toLowerCase()))
                .collect(Collectors.toList());

            if (!rootImages.isEmpty()) {
                // Just use the first image as a fallback
                logInfo("No specific image found for template " + template.name() + ", using first available image");
                return Optional.of(rootImages.get(0).toFile());
            }

            return Optional.empty();
        } catch (IOException e) {
            logError("Error finding any image for template", e);
            return Optional.empty();
        }
    }

    /**
     * Finds any image in the mock camera directory.
     *
     * @return an Optional containing the image file if found, or empty if not found
     */
    private Optional<File> findAnyImage() {
        try {
            // First try to find images directly in the mock camera directory
            // This is the most likely location in the current project structure
            List<Path> rootImages = Files.list(Paths.get(MOCK_CAMERA_PATH))
                .filter(path -> isImageFile(path.getFileName().toString().toLowerCase()))
                .collect(Collectors.toList());

            if (!rootImages.isEmpty()) {
                // Return the first image found
                return Optional.of(rootImages.get(0).toFile());
            }

            // If no images found directly, try to find images in subdirectories
            List<Path> orderDirs = Files.list(Paths.get(MOCK_CAMERA_PATH))
                .filter(Files::isDirectory)
                .collect(Collectors.toList());

            for (Path orderDir : orderDirs) {
                try {
                    List<Path> images = Files.list(orderDir)
                        .filter(path -> isImageFile(path.getFileName().toString().toLowerCase()))
                        .collect(Collectors.toList());

                    if (!images.isEmpty()) {
                        // Return the first image found
                        return Optional.of(images.get(0).toFile());
                    }
                } catch (IOException e) {
                    // Log but continue to next directory
                    logError("Error listing files in directory: " + orderDir, e);
                }
            }

            // If still no images found, try to create the mock camera directory and copy images there
            Path devSimulatedDir = Paths.get(MOCK_CAMERA_PATH);
            if (!Files.exists(devSimulatedDir)) {
                try {
                    Files.createDirectories(devSimulatedDir);
                    logInfo("Created mock camer directory: " + devSimulatedDir);

                    // Copy some images from the root to the mock camer directory
                    if (!rootImages.isEmpty()) {
                        for (int i = 0; i < Math.min(3, rootImages.size()); i++) {
                            Path source = rootImages.get(i);
                            Path target = devSimulatedDir.resolve("weld_" + (i+1) + ".jpg");
                            Files.copy(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            logInfo("Copied image to mock camer: " + target);
                        }

                        // Return the first copied image
                        return Optional.of(devSimulatedDir.resolve("weld_1.jpg").toFile());
                    }
                } catch (IOException e) {
                    logError("Error creating mock camer directory or copying files", e);
                }
            }

            logError("No image files found in mock camera directory", null);
            return Optional.empty();
        } catch (IOException e) {
            logError("Error finding any image", e);
            return Optional.empty();
        }
    }

    /**
     * Finds the directory for the specified order ID.
     *
     * @param orderId the order ID
     * @return the Path to the order directory, or null if not found
     */
    private Path findOrderDirectory(String orderId) {
        try {
            if (orderId == null || orderId.isBlank()) {
                return null;
            }

            // Look for a directory that contains the order ID
            List<Path> matchingDirs = Files.list(Paths.get(MOCK_CAMERA_PATH))
                .filter(Files::isDirectory)
                .filter(path -> path.getFileName().toString().contains(orderId))
                .collect(Collectors.toList());

            if (!matchingDirs.isEmpty()) {
                return matchingDirs.get(0);
            }

            return null;
        } catch (IOException e) {
            logError("Error finding order directory", e);
            return null;
        }
    }

    /**
     * Checks if the specified file name is an image file.
     *
     * @param fileName the file name
     * @return true if the file is an image, false otherwise
     */
    private boolean isImageFile(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || 
               fileName.endsWith(".png") || fileName.endsWith(".gif");
    }

    /**
     * Sanitizes a template name for file matching.
     * Removes underscores and converts to lowercase.
     *
     * @param templateName the template name
     * @return the sanitized template name
     */
    private String sanitizeTemplateName(String templateName) {
        return templateName.replace("_", "").toLowerCase();
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
