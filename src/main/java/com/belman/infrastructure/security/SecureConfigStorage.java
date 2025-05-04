package com.belman.infrastructure.security;

import com.belman.infrastructure.core.InfrastructureService;
import com.belman.infrastructure.ErrorHandler;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Provides secure storage for sensitive configuration data such as database credentials.
 * Uses AES-GCM encryption to protect data at rest.
 */
public class SecureConfigStorage extends InfrastructureService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int GCM_IV_LENGTH = 12;
    private static final int AES_KEY_SIZE = 256;

    private static final String KEY_FILENAME = "config.key";
    private static final String ENCRYPTED_CONFIG_FILENAME = "config.enc";

    private static String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".belsign";

    /**
     * Sets a custom configuration directory path for testing purposes.
     * This method should only be used in test environments.
     * 
     * @param configDir the custom configuration directory path
     */
    public static void setConfigDirForTesting(String configDir) {
        CONFIG_DIR = configDir;
        // Reset the instance to force reinitialization with the new config dir
        instance = null;
    }

    private static SecureConfigStorage instance;
    private final ErrorHandler errorHandler = ErrorHandler.getInstance();

    private SecretKey secretKey;
    private final Map<String, String> configCache = new HashMap<>();

    /**
     * Private constructor to enforce singleton pattern.
     */
    private SecureConfigStorage() {
        try {
            initialize();
        } catch (Exception e) {
            errorHandler.handleException(e, "Failed to initialize secure config storage");
        }
    }

    /**
     * Gets the singleton instance of SecureConfigStorage.
     * 
     * @return the SecureConfigStorage instance
     */
    public static synchronized SecureConfigStorage getInstance() {
        if (instance == null) {
            instance = new SecureConfigStorage();
        }
        return instance;
    }

    /**
     * Initializes the secure storage by creating the config directory and loading or generating the encryption key.
     * 
     * @throws IOException if an I/O error occurs
     * @throws NoSuchAlgorithmException if the encryption algorithm is not available
     */
    private void initialize() throws IOException, NoSuchAlgorithmException {
        // Create config directory if it doesn't exist
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            if (!configDir.mkdirs()) {
                throw new IOException("Failed to create config directory: " + CONFIG_DIR);
            }
        }

        // Load or generate encryption key
        File keyFile = new File(configDir, KEY_FILENAME);
        if (keyFile.exists()) {
            secretKey = loadKey(keyFile);
        } else {
            secretKey = generateKey();
            saveKey(secretKey, keyFile);
        }

        // Load encrypted config if it exists
        File encryptedConfigFile = new File(configDir, ENCRYPTED_CONFIG_FILENAME);
        if (encryptedConfigFile.exists()) {
            loadEncryptedConfig(encryptedConfigFile);
        }
    }

    /**
     * Generates a new AES encryption key.
     * 
     * @return the generated key
     * @throws NoSuchAlgorithmException if the AES algorithm is not available
     */
    private SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(AES_KEY_SIZE, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    /**
     * Saves the encryption key to a file.
     * 
     * @param key the key to save
     * @param file the file to save the key to
     * @throws IOException if an I/O error occurs
     */
    private void saveKey(SecretKey key, File file) throws IOException {
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

    /**
     * Loads the encryption key from a file.
     * 
     * @param file the file to load the key from
     * @return the loaded key
     * @throws IOException if an I/O error occurs
     */
    private SecretKey loadKey(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new SecretKeySpec(encoded, "AES");
    }

    /**
     * Encrypts a string value.
     * 
     * @param value the value to encrypt
     * @return the encrypted value as a Base64-encoded string
     * @throws Exception if encryption fails
     */
    private String encrypt(String value) throws Exception {
        // Generate a random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Initialize cipher with key, IV, and GCM parameters
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        // Encrypt the value
        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));

        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        // Return as Base64-encoded string
        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * Decrypts a Base64-encoded encrypted string.
     * 
     * @param encrypted the encrypted value as a Base64-encoded string
     * @return the decrypted value
     * @throws Exception if decryption fails
     */
    private String decrypt(String encrypted) throws Exception {
        // Decode from Base64
        byte[] combined = Base64.getDecoder().decode(encrypted);

        // Extract IV and encrypted data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedData = new byte[combined.length - GCM_IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        System.arraycopy(combined, iv.length, encryptedData, 0, encryptedData.length);

        // Initialize cipher with key, IV, and GCM parameters
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        // Decrypt the data
        byte[] decrypted = cipher.doFinal(encryptedData);

        // Return as string
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Stores a configuration value securely.
     * 
     * @param key the configuration key
     * @param value the configuration value
     * @return true if the value was stored successfully, false otherwise
     */
    public boolean storeValue(String key, String value) {
        try {
            // Add to cache
            configCache.put(key, value);

            // Encrypt and save all values
            saveEncryptedConfig();

            return true;
        } catch (Exception e) {
            errorHandler.handleException(e, "Failed to store configuration value: " + key);
            return false;
        }
    }

    /**
     * Retrieves a configuration value.
     * 
     * @param key the configuration key
     * @return the configuration value, or null if not found
     */
    public String getValue(String key) {
        return configCache.get(key);
    }

    /**
     * Retrieves a configuration value with a default value if not found.
     * 
     * @param key the configuration key
     * @param defaultValue the default value to return if the key is not found
     * @return the configuration value, or the default value if not found
     */
    public String getValue(String key, String defaultValue) {
        return configCache.getOrDefault(key, defaultValue);
    }

    /**
     * Saves all configuration values to an encrypted file.
     * 
     * @throws Exception if saving fails
     */
    private void saveEncryptedConfig() throws Exception {
        // Convert cache to Properties
        Properties props = new Properties();
        for (Map.Entry<String, String> entry : configCache.entrySet()) {
            props.setProperty(entry.getKey(), encrypt(entry.getValue()));
        }

        // Save to file
        File configFile = new File(CONFIG_DIR, ENCRYPTED_CONFIG_FILENAME);
        try (FileOutputStream fos = new FileOutputStream(configFile)) {
            props.store(fos, "Encrypted Configuration");
        }

        // Set file permissions
        Path path = configFile.toPath();
        try {
            Files.setPosixFilePermissions(path, java.nio.file.attribute.PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException e) {
            // Windows doesn't support POSIX file permissions
            configFile.setReadable(true, true);
            configFile.setWritable(true, true);
            configFile.setExecutable(false);
        }
    }

    /**
     * Loads configuration values from an encrypted file.
     * 
     * @param file the encrypted configuration file
     * @throws IOException if loading fails
     */
    private void loadEncryptedConfig(File file) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);

            // Decrypt and add to cache
            for (String key : props.stringPropertyNames()) {
                try {
                    String encryptedValue = props.getProperty(key);
                    String decryptedValue = decrypt(encryptedValue);
                    configCache.put(key, decryptedValue);
                } catch (Exception e) {
                    errorHandler.handleException(e, "Failed to decrypt configuration value: " + key);
                }
            }
        }
    }

    /**
     * Imports configuration from a properties file.
     * 
     * @param propertiesFile the properties file to import
     * @return true if the import was successful, false otherwise
     */
    public boolean importFromProperties(String propertiesFile) {
        try {
            Properties props = new Properties();
            try (InputStream input = getClass().getClassLoader().getResourceAsStream(propertiesFile)) {
                if (input == null) {
                    throw new IOException("Unable to find " + propertiesFile);
                }
                props.load(input);
            }

            // Add all properties to cache
            for (String key : props.stringPropertyNames()) {
                configCache.put(key, props.getProperty(key));
            }

            // Save to encrypted file
            saveEncryptedConfig();

            return true;
        } catch (Exception e) {
            // Use quiet error handling to avoid JavaFX dependency in tests
            errorHandler.handleErrorQuietly("Failed to import configuration from " + propertiesFile + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Imports configuration from properties for testing purposes.
     * This method doesn't use the ErrorHandler to avoid JavaFX dependencies in tests.
     * 
     * @param properties the properties to import
     * @return true if the import was successful, false otherwise
     */
    public boolean importFromPropertiesForTesting(Properties properties) {
        try {
            // Add all properties to cache
            for (String key : properties.stringPropertyNames()) {
                configCache.put(key, properties.getProperty(key));
            }

            // Save to encrypted file
            saveEncryptedConfig();

            return true;
        } catch (Exception e) {
            // Just log the error without using ErrorHandler
            System.err.println("Failed to import configuration for testing: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a configuration value.
     * 
     * @param key the configuration key to remove
     * @return true if the value was removed successfully, false otherwise
     */
    public boolean removeValue(String key) {
        try {
            configCache.remove(key);
            saveEncryptedConfig();
            return true;
        } catch (Exception e) {
            errorHandler.handleException(e, "Failed to remove configuration value: " + key);
            return false;
        }
    }

    /**
     * Clears all configuration values.
     * 
     * @return true if the values were cleared successfully, false otherwise
     */
    public boolean clearAll() {
        try {
            configCache.clear();
            saveEncryptedConfig();
            return true;
        } catch (Exception e) {
            errorHandler.handleException(e, "Failed to clear configuration values");
            return false;
        }
    }
}
