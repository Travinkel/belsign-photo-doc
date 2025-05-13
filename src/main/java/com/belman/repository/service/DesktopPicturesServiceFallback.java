package com.belman.repository.service;

import com.gluonhq.attach.pictures.PicturesService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A fallback implementation of PicturesService for desktop platforms.
 * This class provides a file chooser dialog for selecting images from the file system.
 */
public class DesktopPicturesServiceFallback implements PicturesService {
    private static final Logger LOGGER = Logger.getLogger(DesktopPicturesServiceFallback.class.getName());
    private final FileChooser fileChooser;
    private Stage stage;
    private final ObjectProperty<Image> imageProperty = new SimpleObjectProperty<>();

    /**
     * Creates a new DesktopPicturesServiceFallback.
     */
    public DesktopPicturesServiceFallback() {
        this.fileChooser = new FileChooser();
        this.fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
    }

    /**
     * Sets the stage to use for file chooser dialogs.
     *
     * @param stage the JavaFX stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Takes a photo using the device camera.
     * On desktop, this opens a file chooser dialog to select an image file.
     *
     * @return an Optional containing the selected image, or empty if no image was selected
     */
    public Optional<Image> takePhoto() {
        return selectImage("Take Photo");
    }

    /**
     * Takes a photo using the device camera.
     * On desktop, this opens a file chooser dialog to select an image file.
     *
     * @param savePhoto whether to save the photo to the device gallery
     * @return an Optional containing the selected image, or empty if no image was selected
     */
    public Optional<Image> takePhoto(boolean savePhoto) {
        return selectImage("Take Photo");
    }

    /**
     * Takes a photo asynchronously using the device camera.
     * On desktop, this opens a file chooser dialog to select an image file.
     *
     * @param savePhoto whether to save the photo to the device gallery
     */
    public void asyncTakePhoto(boolean savePhoto) {
        // This is a synchronous implementation for desktop
        takePhoto(savePhoto);
    }

    /**
     * Selects an image from the device gallery.
     * On desktop, this opens a file chooser dialog to select an image file.
     *
     * @return an Optional containing the selected image, or empty if no image was selected
     */
    public Optional<Image> retrieveImage() {
        return selectImage("Select Image");
    }

    /**
     * Loads an image from the device gallery.
     * On desktop, this opens a file chooser dialog to select an image file.
     *
     * @return an Optional containing the selected image, or empty if no image was selected
     */
    public Optional<Image> loadImageFromGallery() {
        return selectImage("Load Image from Gallery");
    }

    /**
     * Loads an image asynchronously from the device gallery.
     * On desktop, this opens a file chooser dialog to select an image file.
     */
    public void asyncLoadImageFromGallery() {
        // This is a synchronous implementation for desktop
        loadImageFromGallery();
    }

    /**
     * Gets the file for the selected image.
     * On desktop, this opens a file chooser dialog to select an image file.
     *
     * @return an Optional containing the selected file, or empty if no file was selected
     */
    public Optional<File> getImageFile() {
        fileChooser.setTitle("Select Image File");
        File file = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(file);
    }

    /**
     * Gets the image property.
     *
     * @return the image property
     */
    public ObjectProperty<Image> imageProperty() {
        return imageProperty;
    }

    /**
     * Selects an image using a file chooser dialog.
     *
     * @param title the title for the file chooser dialog
     * @return an Optional containing the selected image, or empty if no image was selected
     */
    private Optional<Image> selectImage(String title) {
        fileChooser.setTitle(title);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Image image = new Image(new FileInputStream(file));
                return Optional.of(image);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to load image from file: " + file.getAbsolutePath(), e);
            }
        }

        return Optional.empty();
    }
}
