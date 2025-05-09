package com.belman.infrastructure.service;

import com.belman.infrastructure.platform.ErrorHandler;
import com.belman.infrastructure.platform.PlatformUtils;
import com.belman.domain.aggregates.Order;
import com.belman.domain.aggregates.User;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.repositories.OrderRepository;
import com.belman.domain.services.PhotoService;
import com.belman.domain.valueobjects.ImagePath;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.valueobjects.PhotoId;
import com.belman.domain.valueobjects.Timestamp;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the PhotoService interface.
 */
public class DefaultPhotoService implements PhotoService {

    // File operation constants
    private static final StandardCopyOption FILE_COPY_OPTION = StandardCopyOption.REPLACE_EXISTING;
    private static final int BUFFER_SIZE = 8192;

    // Error message constants
    private static final String UPLOAD_ERROR_MESSAGE = "Failed to upload photo: ";
    private static final String COPY_ERROR_MESSAGE = "Failed to copy file with Gluon Storage: ";
    private static final String STANDARD_COPY_ERROR_MESSAGE = "Failed to copy file with standard I/O: ";
    private static final String DELETE_ERROR_MESSAGE = "Failed to delete file with Gluon Storage";
    private static final String STANDARD_DELETE_ERROR_MESSAGE = "Failed to delete file with standard I/O";
    private static final String STORAGE_ERROR_MESSAGE = "Private storage not available";
    private static final String GLUON_STORAGE_ERROR_MESSAGE = "Error in deleteFileWithGluonStorage";

    // File path constants
    private static final String FILE_EXTENSION_SEPARATOR = ".";

    private final OrderRepository orderRepository;
    private final String photoStorageDirectory;
    private final ErrorHandler errorHandler = ErrorHandler.getInstance();

    /**
     * Creates a new DefaultPhotoService.
     * 
     * @param orderRepository the order repository
     * @param photoStorageDirectory the directory where photos are stored
     */
    public DefaultPhotoService(OrderRepository orderRepository, String photoStorageDirectory) {
        this.orderRepository = orderRepository;
        this.photoStorageDirectory = photoStorageDirectory;

        // Create the photo storage directory if it doesn't exist
        File directory = new File(photoStorageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public PhotoDocument uploadPhoto(File file, OrderId orderId, PhotoAngle angle, User uploadedBy) {
        // Generate a unique ID for the photo
        PhotoId photoId = PhotoId.newId();

        // Generate a unique file path for the photo
        ImagePath imagePath = generateUniqueFilePath(file.getName(), orderId);

        try {
            // Check if we're running on a mobile device
            if (PlatformUtils.isRunningOnMobile()) {
                // Use Gluon's StorageService for mobile devices
                copyFileWithGluonStorage(file, imagePath.path());
            } else {
                // Use standard Java file I/O for desktop
                Path sourcePath = file.toPath();
                Path targetPath = Paths.get(photoStorageDirectory, imagePath.path());
                Files.copy(sourcePath, targetPath, FILE_COPY_OPTION);
            }

            // Create a new photo document
            PhotoDocument photo = new PhotoDocument(
                photoId,
                angle,
                imagePath,
                uploadedBy,
                Timestamp.now()
            );

            // Find the order and add the photo to it
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                order.addPhoto(photo);
                orderRepository.save(order);
            }

            return photo;
        } catch (IOException e) {
            String errorMessage = UPLOAD_ERROR_MESSAGE + e.getMessage();
            errorHandler.handleException(e, errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Copies a file using Gluon's StorageService.
     * 
     * @param sourceFile the source file
     * @param targetFileName the target file name
     * @throws IOException if an I/O error occurs
     */
    private void copyFileWithGluonStorage(File sourceFile, String targetFileName) throws IOException {
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
                Path targetPath = Paths.get(photoStorageDirectory, targetFileName);
                Files.copy(sourcePath, targetPath, FILE_COPY_OPTION);
            } catch (IOException e) {
                String errorMessage = STANDARD_COPY_ERROR_MESSAGE + e.getMessage();
                errorHandler.handleException(e, errorMessage);
                throw new RuntimeException(errorMessage, e);
            }
        });
    }

    @Override
    public boolean deletePhoto(PhotoId photoId) {
        // Find the order that contains the photo
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            List<PhotoDocument> photos = order.getPhotos();
            for (PhotoDocument photo : photos) {
                if (photo.getPhotoId().equals(photoId)) {
                    boolean deleted;

                    // Check if we're running on a mobile device
                    if (PlatformUtils.isRunningOnMobile()) {
                        // Use Gluon's StorageService for mobile devices
                        deleted = deleteFileWithGluonStorage(photo.getImagePath().path());
                    } else {
                        // Use standard Java file I/O for desktop
                        File file = new File(photoStorageDirectory, photo.getImagePath().path());
                        deleted = file.delete();
                    }

                    // Remove the photo from the order
                    order.getPhotos().remove(photo);
                    orderRepository.save(order);

                    return deleted;
                }
            }
        }

        return false;
    }

    /**
     * Deletes a file using Gluon's StorageService.
     * 
     * @param fileName the name of the file to delete
     * @return true if the file was deleted successfully, false otherwise
     */
    private boolean deleteFileWithGluonStorage(String fileName) {
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
                    File file = new File(photoStorageDirectory, fileName);
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

    @Override
    public List<PhotoDocument> getPhotosForOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId);
        return order != null ? order.getPhotos() : List.of();
    }

    @Override
    public PhotoDocument getPhotoById(PhotoId photoId) {
        List<Order> orders = orderRepository.findAll();
        for (Order order : orders) {
            for (PhotoDocument photo : order.getPhotos()) {
                if (photo.getPhotoId().equals(photoId)) {
                    return photo;
                }
            }
        }

        return null;
    }

    @Override
    public ImagePath generateUniqueFilePath(String originalFileName, OrderId orderId) {
        // Extract the file extension
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        // Generate a unique file name based on the order ID and a timestamp
        String uniqueFileName = orderId.id().toString() + "_" + System.currentTimeMillis() + extension;

        return new ImagePath(uniqueFileName);
    }
}
