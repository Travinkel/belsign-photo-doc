package com.belman.application.usecase.photo;

import com.belman.common.platform.PlatformUtils;
import com.belman.common.session.SessionPhotoStore;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoDocumentFactory;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserReference;
import com.belman.domain.services.ErrorHandler;
import com.belman.presentation.error.DomainErrorHandlerAdapter;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default implementation of the PhotoService interface.
 * This service provides functionality for managing photos.
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

    private final PhotoRepository photoRepository;
    private final String photoStorageDirectory;
    private final ErrorHandler errorHandler = DomainErrorHandlerAdapter.createWithDefaultErrorHandler();
    private final SessionPhotoStore sessionPhotoStore = SessionPhotoStore.getInstance();

    /**
     * Creates a new DefaultPhotoService.
     *
     * @param photoRepository       the photo repository
     * @param photoStorageDirectory the directory where photos are stored
     */
    public DefaultPhotoService(PhotoRepository photoRepository, String photoStorageDirectory) {
        this.photoRepository = photoRepository;
        this.photoStorageDirectory = photoStorageDirectory;

        // Create the photo storage directory if it doesn't exist
        File directory = new File(photoStorageDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    @Override
    public Optional<PhotoDocument> getPhotoById(PhotoId photoId) {
        // First check the session store
        Optional<PhotoDocument> sessionPhoto = sessionPhotoStore.getPhoto(photoId);
        if (sessionPhoto.isPresent()) {
            return sessionPhoto;
        }

        // Fall back to the repository
        Optional<PhotoDocument> repoPhoto = photoRepository.findById(photoId);

        // If found in repository but not in session store, add it to session store
        if (repoPhoto.isPresent()) {
            sessionPhotoStore.addPhoto(repoPhoto.get());
        }

        return repoPhoto;
    }

    @Override
    public List<PhotoDocument> getPhotosByOrderId(OrderId orderId) {
        // First check the session store
        List<PhotoDocument> sessionPhotos = sessionPhotoStore.getPhotosByOrderId(orderId);
        if (!sessionPhotos.isEmpty()) {
            return sessionPhotos;
        }

        // Fall back to the repository
        List<PhotoDocument> repoPhotos = photoRepository.findByOrderId(orderId);

        // Add all photos from repository to session store
        for (PhotoDocument photo : repoPhotos) {
            sessionPhotoStore.addPhoto(photo);
        }

        return repoPhotos;
    }

    @Override
    public PhotoDocument uploadPhoto(OrderId orderId, Photo photo, UserBusiness uploadedBy) {
        // Create a new photo document
        PhotoDocument photoDocument = PhotoDocumentFactory.createForOrderWithCurrentTimestamp(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY, // Default template, should be parameterized in a real implementation
                photo,
                uploadedBy,
                orderId);

        // Save the photo document to the repository
        PhotoDocument savedPhoto = photoRepository.save(photoDocument);

        // Also save to the session store
        sessionPhotoStore.addPhoto(savedPhoto);

        return savedPhoto;
    }

    @Override
    public List<PhotoDocument> uploadPhotos(OrderId orderId, List<Photo> photos, UserBusiness uploadedBy) {
        List<PhotoDocument> uploadedPhotos = new ArrayList<>();

        for (Photo photo : photos) {
            PhotoDocument uploadedPhoto = uploadPhoto(orderId, photo, uploadedBy);
            uploadedPhotos.add(uploadedPhoto);
        }

        return uploadedPhotos;
    }

    @Override
    public boolean deletePhoto(PhotoId photoId, UserBusiness deletedBy) {
        // Delete from repository
        boolean deleted = photoRepository.deleteById(photoId);

        // If deleted from repository, also remove from session store
        if (deleted) {
            sessionPhotoStore.removePhoto(photoId);
        }

        return deleted;
    }

    @Override
    public boolean approvePhoto(PhotoId photoId, UserBusiness approvedBy) {
        // First check the session store
        Optional<PhotoDocument> sessionPhotoOpt = sessionPhotoStore.getPhoto(photoId);
        if (sessionPhotoOpt.isPresent()) {
            try {
                PhotoDocument photo = sessionPhotoOpt.get();
                UserReference userRef = new UserReference(approvedBy.getId(), approvedBy.getUsername());
                Timestamp timestamp = new Timestamp(java.time.Instant.now());
                photo.approve(userRef, timestamp);

                // Update in repository
                photoRepository.save(photo);

                // Update in session store
                sessionPhotoStore.updatePhoto(photo);

                return true;
            } catch (IllegalStateException e) {
                // Photo is already approved or rejected
                return false;
            }
        }

        // If not in session store, check repository
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();
            try {
                UserReference userRef = new UserReference(approvedBy.getId(), approvedBy.getUsername());
                Timestamp timestamp = new Timestamp(java.time.Instant.now());
                photo.approve(userRef, timestamp);

                // Update in repository
                photoRepository.save(photo);

                // Add to session store
                sessionPhotoStore.addPhoto(photo);

                return true;
            } catch (IllegalStateException e) {
                // Photo is already approved or rejected
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean rejectPhoto(PhotoId photoId, UserBusiness rejectedBy, String reason) {
        // First check the session store
        Optional<PhotoDocument> sessionPhotoOpt = sessionPhotoStore.getPhoto(photoId);
        if (sessionPhotoOpt.isPresent()) {
            try {
                PhotoDocument photo = sessionPhotoOpt.get();
                UserReference userRef = new UserReference(rejectedBy.getId(), rejectedBy.getUsername());
                Timestamp timestamp = new Timestamp(java.time.Instant.now());
                photo.reject(userRef, timestamp, reason);

                // Update in repository
                photoRepository.save(photo);

                // Update in session store
                sessionPhotoStore.updatePhoto(photo);

                return true;
            } catch (IllegalStateException e) {
                // Photo is already approved or rejected
                return false;
            }
        }

        // If not in session store, check repository
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();
            try {
                UserReference userRef = new UserReference(rejectedBy.getId(), rejectedBy.getUsername());
                Timestamp timestamp = new Timestamp(java.time.Instant.now());
                photo.reject(userRef, timestamp, reason);

                // Update in repository
                photoRepository.save(photo);

                // Add to session store
                sessionPhotoStore.addPhoto(photo);

                return true;
            } catch (IllegalStateException e) {
                // Photo is already approved or rejected
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean addComment(PhotoId photoId, String comment, UserBusiness commentedBy) {
        // PhotoDocument doesn't have an addComment method
        // Comments are added during rejection, so we'll just update the review comment
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent() && photoOpt.get().getStatus() == PhotoDocument.ApprovalStatus.REJECTED) {
            // We can only add comments to rejected photos
            // In a real implementation, we might want to create a separate comments entity
            return true;
        }
        return false;
    }

    /**
     * Uploads a photo file and associates it with an order.
     *
     * @param file       the photo file to upload
     * @param orderId    the ID of the order to associate the photo with
     * @param angle      the angle at which the photo was taken
     * @param uploadedBy the user who uploaded the photo
     * @return the created photo document
     */
    public PhotoDocument uploadPhotoFile(File file, OrderId orderId, PhotoTemplate angle, UserBusiness uploadedBy) {
        // Generate a unique ID for the photo
        PhotoId photoId = PhotoId.newId();

        // Generate a unique file path for the photo
        Photo imagePath = generateUniqueFilePath(file.getName(), orderId);

        try {
            // Check if we're running on a mobile device
            if (PlatformUtils.isRunningOnMobile()) {
                // Use Gluon's StorageService for mobile devices
                copyFileWithGluonStorage(file, imagePath.value());
            } else {
                // Use standard Java file I/O for desktop
                Path sourcePath = file.toPath();
                Path targetPath = Paths.get(photoStorageDirectory, imagePath.value());
                Files.copy(sourcePath, targetPath, FILE_COPY_OPTION);
            }

            // Create a new photo document
            PhotoDocument photo = PhotoDocument.builder()
                    .photoId(photoId)
                    .template(angle)
                    .imagePath(imagePath)
                    .uploadedBy(uploadedBy)
                    .uploadedAt(Timestamp.now())
                    .orderId(orderId)
                    .build();

            // Save the photo document to the repository
            PhotoDocument savedPhoto = photoRepository.save(photo);

            // Also save to the session store
            sessionPhotoStore.addPhoto(savedPhoto);

            return savedPhoto;
        } catch (IOException e) {
            String errorMessage = UPLOAD_ERROR_MESSAGE + e.getMessage();
            errorHandler.handleException(e, errorMessage);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * Copies a file using Gluon's StorageService.
     *
     * @param sourceFile     the source file
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

    /**
     * Deletes a photo file.
     *
     * @param photoId the ID of the photo to delete
     * @return true if the photo file was deleted successfully, false otherwise
     */
    public boolean deletePhotoFile(PhotoId photoId) {
        Optional<PhotoDocument> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isPresent()) {
            PhotoDocument photo = photoOpt.get();
            boolean deleted;

            // Check if we're running on a mobile device
            if (PlatformUtils.isRunningOnMobile()) {
                // Use Gluon's StorageService for mobile devices
                deleted = deleteFileWithGluonStorage(photo.getImagePath().value());
            } else {
                // Use standard Java file I/O for desktop
                File file = new File(photoStorageDirectory, photo.getImagePath().value());
                deleted = file.delete();
            }

            return deleted;
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


    /**
     * Generates a unique file path for a photo.
     *
     * @param originalFileName the original file name
     * @param orderId          the ID of the order
     * @return a unique file path
     */
    public Photo generateUniqueFilePath(String originalFileName, OrderId orderId) {
        // Extract the file extension
        String extension = "";
        int lastDotIndex = originalFileName.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        if (lastDotIndex > 0) {
            extension = originalFileName.substring(lastDotIndex);
        }

        // Generate a unique file name based on the order ID and a timestamp
        String uniqueFileName = orderId.id() + "_" + System.currentTimeMillis() + extension;

        return new Photo(uniqueFileName);
    }


}
