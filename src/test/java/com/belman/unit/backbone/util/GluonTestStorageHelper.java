package com.belman.unit.backbone.util;

import com.belman.infrastructure.GluonStorageManager;
import com.belman.infrastructure.service.StorageServiceFactory;
import com.gluonhq.attach.storage.StorageService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A test helper class that provides platform-agnostic file access for tests.
 * This class is designed to work with both desktop and mobile platforms.
 * It uses the GluonStorageManager for mobile platforms and falls back to
 * standard Java file operations for desktop platforms.
 */
public class GluonTestStorageHelper {

    private static final boolean IS_MOBILE = detectMobilePlatform();
    private static final String TEST_RESOURCES_DIR = "test-resources";

    /**
     * Detects if the current platform is mobile (Android or iOS).
     * 
     * @return true if running on a mobile platform, false otherwise
     */
    private static boolean detectMobilePlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("android") || osName.contains("ios");
    }

    /**
     * Creates a temporary file for testing purposes.
     * On mobile platforms, this uses GluonStorageManager.
     * On desktop platforms, this uses standard Java file operations.
     * 
     * @param fileName the name of the file to create
     * @param content the content to write to the file
     * @return a File object representing the created file
     * @throws IOException if an I/O error occurs
     */
    public static File createTempTestFile(String fileName, byte[] content) throws IOException {
        if (IS_MOBILE) {
            // Use GluonStorageManager for mobile platforms
            String fullFileName = TEST_RESOURCES_DIR + "/" + fileName;
            String contentStr = new String(content, StandardCharsets.UTF_8);
            GluonStorageManager.saveToFile(fullFileName, contentStr);

            // Get the file path from StorageService
            Optional<StorageService> storageService = StorageServiceFactory.getStorageService();
            if (storageService.isPresent()) {
                Optional<File> privateStorage = storageService.get().getPrivateStorage();
                if (privateStorage.isPresent()) {
                    return new File(privateStorage.get(), fullFileName);
                }
            }
            throw new IOException("Failed to get file reference from StorageService");
        } else {
            // Use standard Java file operations for desktop platforms
            Path tempDir = Files.createTempDirectory("gluon-test");
            Path filePath = tempDir.resolve(fileName);
            Files.write(filePath, content);
            return filePath.toFile();
        }
    }

    /**
     * Creates a list of temporary files for testing purposes.
     * 
     * @param fileNames the names of the files to create
     * @param contents the contents to write to each file
     * @return a list of File objects representing the created files
     * @throws IOException if an I/O error occurs
     */
    public static List<File> createTempTestFiles(List<String> fileNames, List<byte[]> contents) throws IOException {
        if (fileNames.size() != contents.size()) {
            throw new IllegalArgumentException("File names and contents must have the same size");
        }

        List<File> files = new ArrayList<>();
        for (int i = 0; i < fileNames.size(); i++) {
            files.add(createTempTestFile(fileNames.get(i), contents.get(i)));
        }
        return files;
    }

    /**
     * Cleans up temporary files created for testing purposes.
     * 
     * @param files the files to clean up
     */
    public static void cleanupTempTestFiles(List<File> files) {
        if (!IS_MOBILE) {
            // Use standard Java file operations for desktop platforms
            for (File file : files) {
                file.delete();
            }

            // Try to delete the parent directory if it's empty
            if (!files.isEmpty()) {
                File parent = files.get(0).getParentFile();
                if (parent != null && parent.exists() && parent.list().length == 0) {
                    parent.delete();
                }
            }
        }
        // Note: For mobile platforms, we don't need to explicitly delete files
        // as they are managed by the StorageService and will be cleaned up
        // when the app is uninstalled or when the storage is cleared
    }
}
