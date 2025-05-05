package com.belman.unit.backbone.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for testing file operations in a way that's compatible with both
 * standard JVM and GraalVM/Gluon Mobile. This class provides platform-agnostic
 * methods for creating and managing temporary files during tests.
 */
public class GluonTestStorageHelper {

    private static final Logger LOGGER = Logger.getLogger(GluonTestStorageHelper.class.getName());
    private static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";
    private static final String TEST_PREFIX = "test-";

    /**
     * Creates a temporary file with the given name and content.
     * This method works on both standard JVM and GraalVM/Gluon Substrate.
     *
     * @param fileName the name of the temporary file
     * @param content  the content to write to the file
     * @return the created temporary file
     * @throws IOException if an I/O error occurs
     */
    public static File createTempTestFile(String fileName, byte[] content) throws IOException {
        // Get the system temp directory in a platform-agnostic way
        String tempDirPath = System.getProperty(TEMP_DIR_PROPERTY);
        File tempDir = new File(tempDirPath);

        // Ensure the temp directory exists
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException("Failed to create temp directory: " + tempDir);
        }

        // Create a unique file name if one wasn't provided
        String uniqueFileName = fileName != null ? fileName : TEST_PREFIX + System.currentTimeMillis();
        File tempFile = new File(tempDir, uniqueFileName);

        // Write the content to the file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content);
            fos.flush();
        }

        // Register a shutdown hook to clean up the file when the JVM exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (tempFile.exists() && !tempFile.delete()) {
                LOGGER.warning("Failed to delete temporary file: " + tempFile);
            }
        }));

        return tempFile;
    }

    /**
     * Cleans up the given list of temporary test files.
     *
     * @param files the list of files to clean up
     */
    public static void cleanupTempTestFiles(List<File> files) {
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file != null && file.exists() && !file.delete()) {
                LOGGER.log(Level.WARNING, "Failed to delete temporary file: {0}", file.getAbsolutePath());
            }
        }
    }

    /**
     * Creates an empty directory in the temp folder.
     *
     * @param dirName the name of the directory
     * @return the created directory
     * @throws IOException if an I/O error occurs
     */
    public static File createTempTestDirectory(String dirName) throws IOException {
        String tempDirPath = System.getProperty(TEMP_DIR_PROPERTY);
        File baseDir = new File(tempDirPath);

        String uniqueDirName = dirName != null ? dirName : TEST_PREFIX + "dir-" + System.currentTimeMillis();
        File tempDir = new File(baseDir, uniqueDirName);

        if (!tempDir.mkdirs()) {
            throw new IOException("Failed to create temp directory: " + tempDir);
        }

        return tempDir;
    }
}