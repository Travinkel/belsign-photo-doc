package com.belman.data.service;

import com.belman.business.core.BaseService;
import com.belman.business.domain.services.CameraService;
import com.belman.business.domain.services.LoggerFactory;
import com.belman.data.logging.EmojiLoggerFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

/**
 * Mock implementation of the CameraService interface for testing purposes.
 * This implementation uses a file chooser to select photos from the file system,
 * simulating the behavior of a camera or photo gallery.
 */
public class MockCameraService extends BaseService implements CameraService {

    private final Stage stage;
    // Make fileChooser a field so it can be mocked in tests
    private FileChooser fileChooser;

    /**
     * Creates a new MockCameraService with the specified stage.
     * 
     * @param stage the JavaFX stage to use for file chooser dialogs
     */
    public MockCameraService(Stage stage) {
        super(EmojiLoggerFactory.getInstance());
        this.stage = stage;
        this.fileChooser = new FileChooser();
        this.fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
    }

    /**
     * Creates a new MockCameraService with a null stage.
     * This constructor is useful for testing or when a stage is not available.
     */
    public MockCameraService() {
        this(null);
    }

    @Override
    public Optional<File> takePhoto() {
        logInfo("Taking photo with mock camera service");
        return selectPhotoWithFileChooser("Take Photo");
    }

    @Override
    public Optional<File> selectPhoto() {
        logInfo("Selecting photo with mock camera service");
        return selectPhotoWithFileChooser("Select Photo");
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
     * Uses a file chooser to select a photo from the file system.
     * 
     * @param title the title for the file chooser dialog
     * @return an Optional containing the selected file, or empty if no file was selected
     */
    private Optional<File> selectPhotoWithFileChooser(String title) {
        fileChooser.setTitle(title);

        File file = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(file);
    }
}
