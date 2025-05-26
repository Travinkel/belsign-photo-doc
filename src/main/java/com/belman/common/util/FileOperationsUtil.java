package com.belman.common.util;

import com.belman.common.platform.PlatformUtils;
import com.belman.presentation.error.ErrorHandler;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Utility class for file operations.
 * Provides methods for copying and deleting files, with support for both desktop and mobile platforms.
 */
public class FileOperationsUtil {

    // File operation constants
    private static final StandardCopyOption FILE_COPY_OPTION = StandardCopyOption.REPLACE_EXISTING;
    private static final int BUFFER_SIZE = 8192;

    // Error message constants
    private static final String COPY_ERROR_MESSAGE = "Failed to copy file with Gluon Storage: ";
    private static final String STANDARD_COPY_ERROR_MESSAGE = "Failed to copy file with standard I/O: ";
    private static final String DELETE_ERROR_MESSAGE = "Failed to delete file with Gluon Storage";
    private static final String STANDARD_DELETE_ERROR_MESSAGE = "Failed to delete file with standard I/O";
    private static final String STORAGE_ERROR_MESSAGE = "Private storage not available";
    private static final String GLUON_STORAGE_ERROR_MESSAGE = "Error in deleteFileWithGluonStorage";

    // File path constants
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private static final ErrorHandler errorHandler = ErrorHandler.getInstance();

    /**
     * Private constructor to prevent instantiation.
     */
    private FileOperationsUtil() {
        // Utility class, do not instantiate
    }

    /**
     * Copies a file to a target location.
     * Uses Gluon's StorageService on mobile platforms and standard Java file I/O on desktop.
     *
     * @param sourceFile the source file
     * @param targetFileName the target file name
     * @param targetDirectory the target directory
     * @throws IOException if an I/O error occurs
     */
    public static void copyFile(File sourceFile, String targetFileName, String targetDirectory) throws IOException {
        // Check if we're running on a mobile device
        if (PlatformUtils.isRunningOnMobile()) {
            // Use Gluon's StorageService for mobile devices
            copyFileWithGluonStorage(sourceFile, targetFileName);
        } else {
            // Use standard Java file I/O for desktop
            Path sourcePath = sourceFile.toPath();
            Path targetPath = Paths.get(targetDirectory, targetFileName);
            Files.copy(sourcePath, targetPath, FILE_COPY_OPTION);
        }
    }

    /**
     * Deletes a file.
     * Uses Gluon's StorageService on mobile platforms and standard Java file I/O on desktop.
     *
     * @param fileName the name of the file to delete
     * @param directory the directory containing the file
     * @return true if the file was deleted successfully, false otherwise
     */
    public static boolean deleteFile(String fileName, String directory) {
        // Check if we're running on a mobile device
        if (PlatformUtils.isRunningOnMobile()) {
            // Use Gluon's StorageService for mobile devices
            return deleteFileWithGluonStorage(fileName);
        } else {
            // Use standard Java file I/O for desktop
            File file = new File(directory, fileName);
            return file.delete();
        }
    }

    /**
     * Generates a unique file name based on a prefix and the original file name.
     *
     * @param originalFileName the original file name
     * @param prefix the prefix to add to the file name
     * @return a unique file name
     */
    public static String generateUniqueFileName(String originalFileName, String prefix) {
        // Extract the file extension
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        // Generate a unique file name based on the prefix and a timestamp
        return prefix + "_" + System.currentTimeMillis() + extension;
    }

    /**
     * Copies a file using Gluon's StorageService.
     *
     * @param sourceFile the source file
     * @param targetFileName the target file name
     * @throws IOException if an I/O error occurs
     */
    private static void copyFileWithGluonStorage(File sourceFile, String targetFileName) throws IOException {
        Services.get(StorageService.class).ifPresentOrElse(storageService -> {
            try {
                // Get the private storage directory
                Optional<File> privateStorageDirOpt = storageService.getPrivateStorage();
                if (privateStorageDirOpt.isEmpty()) {
                    throw new IOException(STORAGE_ERROR_MESSAGE);
                }

                File privateStorageDir = privateStorageDirOpt.get();

                // Create the target file
                File targetFile = new File(privateStorageDir, targetFileName);

                // Ensure parent directories exist
                File parentDir = targetFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    parentDir.mkdirs();
                }

                // Copy the file using streams
                try (InputStream in = new FileInputStream(sourceFile);
                     OutputStream out = new FileOutputStream(targetFile)) {

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            } catch (IOException e) {
                String errorMessage = COPY_ERROR_MESSAGE + e.getMessage();
                errorHandler.handleException(e, errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        }, () -> {
            // StorageService not available, fall back to standard Java file I/O
            try {
                Path sourcePath = sourceFile.toPath();
                Path targetPath = Paths.get(System.getProperty("java.io.tmpdir"), targetFileName);
                Files.copy(sourcePath, targetPath, FILE_COPY_OPTION);
            } catch (IOException e) {
                String errorMessage = STANDARD_COPY_ERROR_MESSAGE + e.getMessage();
                errorHandler.handleException(e, errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        });
    }

    /**
     * Deletes a file using Gluon's StorageService.
     *
     * @param fileName the name of the file to delete
     * @return true if the file was deleted successfully, false otherwise
     */
    private static boolean deleteFileWithGluonStorage(String fileName) {
        try {
            return Services.get(StorageService.class).map(storageService -> {
                try {
                    // Get the private storage directory
                    Optional<File> privateStorageDirOpt = storageService.getPrivateStorage();
                    if (privateStorageDirOpt.isEmpty()) {
                        errorHandler.handleErrorQuietly("Private storage not available");
                        return false;
                    }

                    File privateStorageDir = privateStorageDirOpt.get();

                    // Create the file reference
                    File file = new File(privateStorageDir, fileName);

                    // Delete the file
                    return file.delete();
                } catch (Exception e) {
                    errorHandler.handleExceptionQuietly(e, DELETE_ERROR_MESSAGE);
                    return false;
                }
            }).orElseGet(() -> {
                // StorageService not available, fall back to standard Java file I/O
                try {
                    File file = new File(System.getProperty("java.io.tmpdir"), fileName);
                    return file.delete();
                } catch (Exception e) {
                    errorHandler.handleExceptionQuietly(e, STANDARD_DELETE_ERROR_MESSAGE);
                    return false;
                }
            });
        } catch (Exception e) {
            errorHandler.handleExceptionQuietly(e, GLUON_STORAGE_ERROR_MESSAGE);
            return false;
        }
    }
}