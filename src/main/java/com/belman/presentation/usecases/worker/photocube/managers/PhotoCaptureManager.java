package com.belman.presentation.usecases.worker.photocube.managers;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.user.UserBusiness;
import com.belman.application.usecase.photo.CameraImageProvider;
import com.belman.application.usecase.photo.CameraImageProviderFactory;
import com.belman.application.usecase.photo.PhotoCaptureService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

import java.io.File;
import java.util.Optional;

/**
 * Manages photo capture and camera interactions for the PhotoCubeViewModel.
 * This class is responsible for camera operations and photo capture.
 */
public class PhotoCaptureManager {

    @Inject
    private PhotoCaptureService photoCaptureService;

    private final CameraImageProvider cameraImageProvider = CameraImageProviderFactory.getInstance();

    // Properties for UI binding
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final BooleanProperty cameraActive = new SimpleBooleanProperty(false);
    private final BooleanProperty captureInProgress = new SimpleBooleanProperty(false);
    private final ObjectProperty<Image> currentPhotoPreview = new SimpleObjectProperty<>();

    /**
     * Starts the camera preview for the selected template.
     * 
     * @param template the selected template
     * @return true if the camera preview was started successfully, false otherwise
     */
    public boolean startCameraPreview(PhotoTemplate template) {
        statusMessage.set("Starting camera preview...");

        try {
            if (!cameraImageProvider.isCameraAvailable()) {
                errorMessage.set("Camera is not available on this device. Please check camera permissions or try another device.");
                return false;
            }

            // Check if a template is selected
            if (template == null) {
                errorMessage.set("Please select a template before starting the camera.");
                return false;
            }

            // Start the camera preview
            cameraActive.set(true);
            statusMessage.set("Camera preview started. Position the camera and tap 'Capture' when ready.");
            return true;
        } catch (Exception e) {
            errorMessage.set("Error starting camera preview: " + e.getMessage() + ". Please try again or use another device.");
            cameraActive.set(false);
            return false;
        }
    }

    /**
     * Captures a photo for the selected template.
     * 
     * @param order the current order
     * @param template the selected template
     * @return the captured photo document, or null if the capture failed
     */
    public PhotoDocument capturePhoto(OrderBusiness order, PhotoTemplate template) {
        if (!cameraActive.get()) {
            errorMessage.set("Camera is not active. Please start the camera preview first.");
            return null;
        }

        if (template == null) {
            errorMessage.set("No template selected. Please select a template before capturing a photo.");
            return null;
        }

        if (captureInProgress.get()) {
            errorMessage.set("Capture already in progress. Please wait.");
            return null;
        }

        captureInProgress.set(true);
        statusMessage.set("Capturing photo...");

        try {
            // Capture the photo using the camera image provider
            String orderId = order != null ? order.getId().toString() : "";
            Optional<File> capturedPhotoFile = cameraImageProvider.takePhoto(template, orderId);

            if (capturedPhotoFile.isEmpty()) {
                errorMessage.set("Unable to save photo. Please check camera permissions and try again.");
                captureInProgress.set(false);
                return null;
            }

            // Convert the file to a Photo object
            File photoFile = capturedPhotoFile.get();
            Photo capturedPhoto = new Photo(photoFile.getName());

            // Save the photo
            return savePhoto(order.getId(), template, capturedPhoto);
        } catch (Exception e) {
            errorMessage.set("Error capturing photo: " + e.getMessage() + ". Please try again.");
            captureInProgress.set(false);
            return null;
        }
    }

    /**
     * Saves a captured photo for the selected template.
     * 
     * @param orderId the order ID
     * @param template the template
     * @param photo the photo to save
     * @return the saved photo document, or null if the save failed
     */
    private PhotoDocument savePhoto(OrderId orderId, PhotoTemplate template, Photo photo) {
        statusMessage.set("Saving photo...");

        try {
            if (orderId == null || template == null) {
                errorMessage.set("Missing order or template information. Please try again.");
                captureInProgress.set(false);
                return null;
            }

            // Get the current user
            return SessionContext.getCurrentUser().map(user -> {
                try {
                    // Save the photo using the photo capture service
                    PhotoDocument savedPhoto = photoCaptureService.capturePhoto(
                        orderId, template, photo, user);

                    if (savedPhoto == null) {
                        errorMessage.set("Failed to save photo. Please try again.");
                        return null;
                    }

                    // Update the UI
                    updateAfterPhotoCapture(savedPhoto);
                    return savedPhoto;
                } catch (Exception e) {
                    errorMessage.set("Error saving photo: " + e.getMessage() + ". Please try again.");
                    return null;
                } finally {
                    captureInProgress.set(false);
                }
            }).orElseGet(() -> {
                errorMessage.set("No user is logged in. Please log in to continue.");
                captureInProgress.set(false);
                return null;
            });
        } catch (Exception e) {
            errorMessage.set("Error saving photo: " + e.getMessage() + ". Please try again.");
            captureInProgress.set(false);
            return null;
        }
    }

    /**
     * Updates the UI after a photo is captured and saved.
     * 
     * @param savedPhoto the saved photo document
     */
    private void updateAfterPhotoCapture(PhotoDocument savedPhoto) {
        try {
            // Load the photo as an image for preview
            Image image = loadPhotoAsImage(savedPhoto);
            if (image != null) {
                currentPhotoPreview.set(image);
            }

            // Stop the camera preview
            cameraActive.set(false);

            // Reset the capture in progress flag
            captureInProgress.set(false);

            // Update the status message
            statusMessage.set("Photo captured successfully.");
        } catch (Exception e) {
            errorMessage.set("Error updating UI after photo capture: " + e.getMessage() + ". Please try again.");
            captureInProgress.set(false);
        }
    }

    /**
     * Loads a photo as a JavaFX Image.
     * 
     * @param photoDocument the photo document to load
     * @return the photo as a JavaFX Image, or null if the photo could not be loaded
     */
    private Image loadPhotoAsImage(PhotoDocument photoDocument) {
        try {
            if (photoDocument == null) {
                System.err.println("Photo document is null");
                return loadFallbackImage();
            }

            // Get the photo path from the photo document
            String photoPath = photoDocument.getImagePath().value();
            if (photoPath == null || photoPath.isBlank()) {
                System.err.println("Photo path is null or empty");
                return loadFallbackImage();
            }

            // Check if the photo path is a URL
            if (photoPath.startsWith("http://") || photoPath.startsWith("https://")) {
                // Load the image from the URL
                return new Image(photoPath, true); // Use background loading
            } else {
                // Load the image from the file system
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    // Load the image from the file
                    return new Image(photoFile.toURI().toString(), true); // Use background loading
                } 

                // Try to load from photos directory
                File mockCameraFile = new File("src/main/resources/photos/" + photoFile.getName());
                if (mockCameraFile.exists()) {
                    System.out.println("[DEBUG_LOG] Found image in photos directory: " + mockCameraFile.getName());
                    return new Image(mockCameraFile.toURI().toString(), true); // Use background loading
                }

                // Try to load from photos dev-simulated directory
                File mockDevFile = new File("src/main/resources/photos/dev-simulated/" + photoFile.getName());
                if (mockDevFile.exists()) {
                    return new Image(mockDevFile.toURI().toString(), true); // Use background loading
                }

                // Try to load from the class path
                String classPathResource = "/com/belman/images/" + photoPath;
                var inputStream = getClass().getResourceAsStream(classPathResource);
                if (inputStream != null) {
                    return new Image(inputStream);
                }

                // If all else fails, load a fallback image
                System.err.println("Could not find image at path: " + photoPath);
                return loadFallbackImage();
            }
        } catch (Exception e) {
            // Log the error and return fallback image
            System.err.println("Error loading photo as image: " + e.getMessage());
            return loadFallbackImage();
        }
    }

    /**
     * Loads a fallback image when the requested image cannot be found.
     * 
     * @return a fallback image
     */
    private Image loadFallbackImage() {
        try {
            // First try to load a placeholder image from the classpath
            var inputStream = getClass().getResourceAsStream("/com/belman/images/placeholder.png");
            if (inputStream != null) {
                return new Image(inputStream);
            }

            // Try to load any image from photos directory
            File mockCameraDir = new File("src/main/resources/photos");
            if (mockCameraDir.exists() && mockCameraDir.isDirectory()) {
                File[] imageFiles = mockCameraDir.listFiles((dir, name) -> 
                    name.toLowerCase().endsWith(".jpg") || 
                    name.toLowerCase().endsWith(".png") || 
                    name.toLowerCase().endsWith(".gif"));

                if (imageFiles != null && imageFiles.length > 0) {
                    return new Image(imageFiles[0].toURI().toString(), true);
                }
            }

            // If all else fails, create a simple placeholder image
            System.err.println("Creating empty placeholder image");
            return new Image("https://via.placeholder.com/150x150.png?text=No+Image");
        } catch (Exception e) {
            System.err.println("Error loading fallback image: " + e.getMessage());
            // Return null as last resort
            return null;
        }
    }

    // Getters for properties

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public BooleanProperty cameraActiveProperty() {
        return cameraActive;
    }

    public BooleanProperty captureInProgressProperty() {
        return captureInProgress;
    }

    public ObjectProperty<Image> currentPhotoPreviewProperty() {
        return currentPhotoPreview;
    }
}
