package com.belman.repository.storage;

import com.gluonhq.attach.storage.StorageService;
import com.belman.repository.service.StorageServiceFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Utility class for managing persistent storage in Gluon applications.
 */
public class GluonStorageManager {

    /**
     * Saves data to a file in the storage service.
     *
     * @param fileName the name of the file
     * @param data the data to save
     */
    public static void saveToFile(String fileName, String data) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or blank");
        }
        Optional<StorageService> storageService = StorageServiceFactory.getStorageService();
        storageService.ifPresent(service -> {
            Optional<File> privateStorage = service.getPrivateStorage(); // Get the private storage directory
            privateStorage.ifPresent(storageDir -> {
                try {
                    Path path = storageDir.toPath().resolve(fileName); // Convert File to Path and resolve the file path
                    Files.writeString(path, data); // Write data to the file
                } catch (IOException e) {
                    throw new RuntimeException("Failed to save data to file: " + fileName, e);
                }
            });
        });
    }

    /**
     * Reads data from a file in the storage service.
     *
     * @param fileName the name of the file
     * @return the data read from the file
     */
    public static Optional<String> readFromFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or blank");
        }
        Optional<StorageService> storageService = StorageServiceFactory.getStorageService();
        return storageService.flatMap(service -> {
            Optional<File> privateStorage = service.getPrivateStorage(); // Get the private storage directory
            return privateStorage.flatMap(storageDir -> {
                try {
                    Path path = storageDir.toPath().resolve(fileName); // Convert File to Path and resolve the file path
                    return Files.exists(path) ? Optional.of(Files.readString(path)) : Optional.empty();
                } catch (IOException e) {
                    throw new RuntimeException("Failed to read data from file: " + fileName, e);
                }
            });
        });
    }
}
