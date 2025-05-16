package com.belman.repository.service;

import com.belman.service.platform.CameraService;
import com.belman.repository.logging.EmojiLoggerFactory;
import com.belman.service.base.BaseService;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Mock implementation of the CameraService interface for testing purposes.
 * This implementation uses a file chooser to select photos from the file system,
 * simulating the behavior of a camera or photo gallery.
 */
public class MockCameraService extends BaseService implements CameraService {

    private final Stage stage;
    // Make fileChooser a field so it can be mocked in tests
    private final FileChooser fileChooser;

    /**
     * Creates a new MockCameraService with a null stage.
     * This constructor is useful for testing or when a stage is not available.
     */
    public MockCameraService() {
        this(null);
    }

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
     * This method handles JavaFX threading properly by using Platform.runLater
     * and CompletableFuture to ensure it works correctly in all scenarios.
     *
     * @param title the title for the file chooser dialog
     * @return an Optional containing the selected file, or empty if no file was selected
     */
    private Optional<File> selectPhotoWithFileChooser(String title) {
        // If we're not on the JavaFX application thread, use CompletableFuture to wait for the result
        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<Optional<File>> future = new CompletableFuture<>();

            Platform.runLater(() -> {
                try {
                    future.complete(showFileChooser(title));
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            });

            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                logError("Error showing file chooser", e);
                return Optional.empty();
            }
        } else {
            // If we're already on the JavaFX application thread, just show the file chooser
            return showFileChooser(title);
        }
    }

    /**
     * Shows the file chooser dialog.
     * This method must be called on the JavaFX application thread.
     *
     * @param title the title for the file chooser dialog
     * @return an Optional containing the selected file, or empty if no file was selected
     */
    private Optional<File> showFileChooser(String title) {
        fileChooser.setTitle(title);
        File file = fileChooser.showOpenDialog(stage);
        return Optional.ofNullable(file);
    }
}
