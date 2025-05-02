package com.belman.infrastructure.service;

import com.belman.backbone.core.base.BaseService;
import com.belman.backbone.core.exceptions.ErrorHandler;
import com.belman.domain.services.CameraService;
import com.gluonhq.attach.pictures.PicturesService;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the CameraService interface using Gluon Attach's PicturesService.
 * This implementation is designed for mobile platforms and uses the device's camera
 * and photo gallery.
 */
public class GluonCameraService extends BaseService implements CameraService {

    // File format constants
    private static final String IMAGE_FILE_EXTENSION = ".png";
    private static final String IMAGE_FORMAT = "png";

    // Image size constants
    private static final int MAX_IMAGE_WIDTH = 1920;
    private static final int MAX_IMAGE_HEIGHT = 1080;

    private final ErrorHandler errorHandler = ErrorHandler.getInstance();
    private final String tempDirectory;

    /**
     * Creates a new GluonCameraService with the specified temporary directory.
     * 
     * @param tempDirectory the directory to store temporary files
     */
    public GluonCameraService(String tempDirectory) {
        this.tempDirectory = tempDirectory;

        // Create the temporary directory if it doesn't exist
        File directory = new File(tempDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public Optional<File> takePhoto() {
        logInfo("Taking photo with Gluon PicturesService");

        try {
            // Get the PicturesService
            Optional<PicturesService> picturesServiceOpt = Services.get(PicturesService.class);
            if (!picturesServiceOpt.isPresent()) {
                logError("PicturesService not available");
                return Optional.empty();
            }

            PicturesService picturesService = picturesServiceOpt.get();

            // Create a temporary file for the image
            String fileName = "camera_" + UUID.randomUUID().toString() + IMAGE_FILE_EXTENSION;
            File outputFile = new File(tempDirectory, fileName);

            // Ensure parent directories exist
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Use reflection to find and invoke the takePhoto method
            try {
                // Try to find a method named "takePhoto"
                java.lang.reflect.Method takePhotoMethod = null;

                for (java.lang.reflect.Method method : picturesService.getClass().getMethods()) {
                    if (method.getName().equals("takePhoto")) {
                        takePhotoMethod = method;
                        break;
                    }
                }

                if (takePhotoMethod == null) {
                    logError("takePhoto method not found in PicturesService");
                    return Optional.empty();
                }

                // Determine the number of parameters
                int paramCount = takePhotoMethod.getParameterCount();

                // Invoke the method with the appropriate number of parameters
                Object result;
                if (paramCount == 0) {
                    result = takePhotoMethod.invoke(picturesService);
                } else {
                    // If parameters are required, pass null for each parameter
                    Object[] params = new Object[paramCount];
                    result = takePhotoMethod.invoke(picturesService, params);
                }

                // Check if the result is an Optional<Image>
                if (result instanceof Optional) {
                    Optional<?> optResult = (Optional<?>) result;
                    if (optResult.isPresent() && optResult.get() instanceof Image) {
                        Image image = (Image) optResult.get();

                        // Resize the image if needed
                        Image resizedImage = resizeImageIfNeeded(image);

                        // Save to file
                        saveImageToFile(resizedImage, outputFile);

                        return Optional.of(outputFile);
                    }
                }

                logError("takePhoto method did not return an Optional<Image>");
                return Optional.empty();
            } catch (Exception e) {
                logError("Error invoking takePhoto method", e);
                return Optional.empty();
            }
        } catch (Exception e) {
            logError("Error taking photo", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with Gluon PicturesService");

        try {
            // Get the PicturesService
            Optional<PicturesService> picturesServiceOpt = Services.get(PicturesService.class);
            if (!picturesServiceOpt.isPresent()) {
                logError("PicturesService not available");
                return Optional.empty();
            }

            PicturesService picturesService = picturesServiceOpt.get();

            // Create a temporary file for the image
            String fileName = "gallery_" + UUID.randomUUID().toString() + IMAGE_FILE_EXTENSION;
            File outputFile = new File(tempDirectory, fileName);

            // Ensure parent directories exist
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Try different method names that might be used for selecting photos
            String[] methodNames = {"selectPhoto", "pickImage", "getImage", "retrieveImage", "pickMedia", "browsePhotos"};

            for (String methodName : methodNames) {
                try {
                    // Try to find a method with the current name
                    java.lang.reflect.Method method = null;

                    for (java.lang.reflect.Method m : picturesService.getClass().getMethods()) {
                        if (m.getName().equals(methodName)) {
                            method = m;
                            break;
                        }
                    }

                    if (method == null) {
                        continue; // Method not found, try the next name
                    }

                    // Determine the number of parameters
                    int paramCount = method.getParameterCount();

                    // Invoke the method with the appropriate number of parameters
                    Object result;
                    if (paramCount == 0) {
                        result = method.invoke(picturesService);
                    } else {
                        // If parameters are required, pass null for each parameter
                        Object[] params = new Object[paramCount];
                        result = method.invoke(picturesService, params);
                    }

                    // Check if the result is an Optional<Image>
                    if (result instanceof Optional) {
                        Optional<?> optResult = (Optional<?>) result;
                        if (optResult.isPresent() && optResult.get() instanceof Image) {
                            Image image = (Image) optResult.get();

                            // Resize the image if needed
                            Image resizedImage = resizeImageIfNeeded(image);

                            // Save to file
                            saveImageToFile(resizedImage, outputFile);

                            return Optional.of(outputFile);
                        }
                    }
                } catch (Exception e) {
                    // Ignore exceptions and try the next method name
                }
            }

            // If we get here, none of the methods worked
            logError("No suitable method found for selecting photos");
            return Optional.empty();
        } catch (Exception e) {
            logError("Error selecting photo", e);
            return Optional.empty();
        }
    }

    @Override
    public boolean isCameraAvailable() {
        return Services.get(PicturesService.class)
            .map(picturesService -> true)
            .orElse(false);
    }

    @Override
    public boolean isGalleryAvailable() {
        return Services.get(PicturesService.class)
            .map(picturesService -> true)
            .orElse(false);
    }

    /**
     * Saves an image to a temporary file.
     * 
     * @param image the image to save
     * @param prefix the prefix for the file name
     * @return the saved file
     */
    private File saveImageToFile(Image image, String prefix) {
        try {
            // Create a temporary file
            String fileName = prefix + UUID.randomUUID().toString() + IMAGE_FILE_EXTENSION;
            File file = new File(tempDirectory, fileName);

            // Resize the image if it's too large
            Image resizedImage = resizeImageIfNeeded(image);

            // Save the image to the file
            saveImageToFile(resizedImage, file);

            return file;
        } catch (Exception e) {
            errorHandler.handleException(e, "Failed to save image to file");
            throw new RuntimeException("Failed to save image to file", e);
        }
    }

    /**
     * Resizes an image if it's larger than the maximum dimensions.
     * 
     * @param image the image to resize
     * @return the resized image, or the original image if it's not too large
     */
    private Image resizeImageIfNeeded(Image image) {
        double width = image.getWidth();
        double height = image.getHeight();

        // Check if the image needs to be resized
        if (width <= MAX_IMAGE_WIDTH && height <= MAX_IMAGE_HEIGHT) {
            return image;
        }

        // Calculate the scale factor
        double scaleFactor = Math.min(MAX_IMAGE_WIDTH / width, MAX_IMAGE_HEIGHT / height);

        // Calculate the new dimensions
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);

        // Create a new image with the new dimensions
        javafx.scene.image.WritableImage resizedImage = new javafx.scene.image.WritableImage(newWidth, newHeight);
        javafx.scene.image.PixelWriter pixelWriter = resizedImage.getPixelWriter();
        javafx.scene.image.PixelReader pixelReader = image.getPixelReader();

        // Copy the pixels from the original image to the resized image
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int srcX = (int) (x / scaleFactor);
                int srcY = (int) (y / scaleFactor);
                pixelWriter.setArgb(x, y, pixelReader.getArgb(srcX, srcY));
            }
        }

        return resizedImage;
    }

    /**
     * Saves an image to a file.
     * 
     * @param image the image to save
     * @param file the file to save to
     * @throws IOException if an I/O error occurs
     */
    private void saveImageToFile(Image image, File file) throws IOException {
        // Ensure parent directories exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Get the pixel reader
        javafx.scene.image.PixelReader pixelReader = image.getPixelReader();

        // Create a JavaFX WritableImage
        javafx.scene.image.WritableImage writableImage = new javafx.scene.image.WritableImage(
            (int) image.getWidth(),
            (int) image.getHeight()
        );

        // Copy the pixels
        javafx.scene.image.PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                pixelWriter.setArgb(x, y, pixelReader.getArgb(x, y));
            }
        }

        // Save the image to the file
        javax.imageio.ImageIO.write(
            javafx.embed.swing.SwingFXUtils.fromFXImage(writableImage, null),
            IMAGE_FORMAT,
            file
        );
    }
}
