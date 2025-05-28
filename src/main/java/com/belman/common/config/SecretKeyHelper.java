package com.belman.common.config;

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Helper class for managing secret keys.
 * This class is package-private to ensure it's only used within the security package.
 */
class SecretKeyHelper {
    private static final int AES_KEY_SIZE = 256;

    /**
     * Loads a secret key from a file.
     *
     * @param file the file to load the key from
     * @return the loaded key
     * @throws IOException if an I/O error occurs
     */
    static SecretKey loadKey(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new SecretKeySpec(encoded, "AES");
    }

    /**
     * Generates a new AES secret key.
     *
     * @return the generated key
     * @throws NoSuchAlgorithmException if the AES algorithm is not available
     */
    static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    /**
     * Saves a secret key to a file.
     *
     * @param key  the key to save
     * @param file the file to save the key to
     * @throws IOException if an I/O error occurs
     */
    static void saveKey(SecretKey key, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(key.getEncoded());
        }

        // Set file permissions to be readable only by the owner
        Path path = file.toPath();
        try {
            Files.setPosixFilePermissions(path, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException e) {
            // Windows doesn't support POSIX file permissions
            // Use alternative method to restrict access on Windows
            file.setReadable(true, true);
            file.setWritable(true, true);
            file.setExecutable(false);
        }
    }
}