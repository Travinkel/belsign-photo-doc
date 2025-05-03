package com.belman.infrastructure.service;

import com.gluonhq.attach.storage.StorageService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A fallback implementation of StorageService for desktop platforms.
 * This class provides a persistent directory in the user's home directory
 * that can be used as a fallback for the private storage directory when
 * running on desktop platforms.
 */
public class DesktopStorageServiceFallback implements StorageService {
    private static final Logger LOGGER = Logger.getLogger(DesktopStorageServiceFallback.class.getName());
    private static final String APP_DIR_NAME = "BelSign";
    private final File privateStorageDir;

    /**
     * Creates a new DesktopStorageServiceFallback.
     * This constructor creates a persistent directory in the user's home directory
     * that will be used as the private storage directory.
     */
    public DesktopStorageServiceFallback() {
        File storageDir = null;
        try {
            // Create a persistent directory in the user's home directory
            String userHome = System.getProperty("user.home");
            Path storagePath = Paths.get(userHome, APP_DIR_NAME);

            // Create the directory if it doesn't exist
            Files.createDirectories(storagePath);
            storageDir = storagePath.toFile();

            LOGGER.info("Using persistent directory for desktop storage: " + storageDir.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to create persistent directory for desktop storage", e);

            // Fallback to temporary directory if we can't create a persistent one
            try {
                Path tempPath = Files.createTempDirectory("belsign-desktop-storage-");
                storageDir = tempPath.toFile();
                LOGGER.warning("Falling back to temporary directory: " + storageDir.getAbsolutePath());
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to create temporary directory as fallback", ex);
            }
        }

        this.privateStorageDir = storageDir;
    }

    @Override
    public Optional<File> getPrivateStorage() {
        return Optional.ofNullable(privateStorageDir);
    }

    @Override
    public Optional<File> getPublicStorage(String subdirectory) {
        // Not implemented for desktop fallback
        return Optional.empty();
    }

    @Override
    public boolean isExternalStorageWritable() {
        // Not implemented for desktop fallback
        return false;
    }

    @Override
    public boolean isExternalStorageReadable() {
        // Not implemented for desktop fallback
        return false;
    }
}
