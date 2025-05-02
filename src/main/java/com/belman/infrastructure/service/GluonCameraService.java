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

        // This is a temporary implementation to make the code compile
        // In a real implementation, we would use the PicturesService API correctly
        return Optional.empty();
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with Gluon PicturesService");

        // This is a temporary implementation to make the code compile
        // In a real implementation, we would use the PicturesService API correctly
        return Optional.empty();
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
            String fileName = prefix + UUID.randomUUID().toString() + ".png";
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
        // Maximum dimensions for uploaded images
        final int MAX_WIDTH = 1920;
        final int MAX_HEIGHT = 1080;

        double width = image.getWidth();
        double height = image.getHeight();

        // Check if the image needs to be resized
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return image;
        }

        // Calculate the scale factor
        double scaleFactor = Math.min(MAX_WIDTH / width, MAX_HEIGHT / height);

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
            "png",
            file
        );
    }
}
