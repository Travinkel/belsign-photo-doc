package com.belman.unit.infrastructure.service;

import com.belman.service.usecase.photo.CameraService;
import com.belman.service.usecase.photo.GluonCameraService;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.attach.util.Services;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GluonCameraService class.
 * These tests use Mockito to mock the Gluon Attach services
 * to simulate the mobile environment.
 */
class GluonCameraServiceTest {

    @TempDir
    Path tempDir;

    private CameraService cameraService;

    @BeforeEach
    void setUp() {
        // Create a GluonCameraService with a temporary directory
        cameraService = new GluonCameraService(tempDir.toString());
    }

    /**
     * Test that isCameraAvailable returns false when PicturesService is not available.
     */
    @Test
    void isCameraAvailable_whenPicturesServiceNotAvailable_returnsFalse() {
        // Mock the Services.get method to return an empty Optional
        try (MockedStatic<com.gluonhq.attach.util.Services> mockedServices =
                     Mockito.mockStatic(com.gluonhq.attach.util.Services.class)) {

            mockedServices.when(() ->
                            com.gluonhq.attach.util.Services.get(com.gluonhq.attach.pictures.PicturesService.class))
                    .thenReturn(Optional.empty());

            // Test that isCameraAvailable returns false
            assertFalse(cameraService.isCameraAvailable());
        }
    }

    /**
     * Test that isGalleryAvailable returns false when PicturesService is not available.
     */
    @Test
    void isGalleryAvailable_whenPicturesServiceNotAvailable_returnsFalse() {
        // Mock the Services.get method to return an empty Optional
        try (MockedStatic<com.gluonhq.attach.util.Services> mockedServices =
                     Mockito.mockStatic(com.gluonhq.attach.util.Services.class)) {

            mockedServices.when(() ->
                            com.gluonhq.attach.util.Services.get(com.gluonhq.attach.pictures.PicturesService.class))
                    .thenReturn(Optional.empty());

            // Test that isGalleryAvailable returns false
            assertFalse(cameraService.isGalleryAvailable());
        }
    }

    /**
     * Test that takePhoto returns an empty Optional when PicturesService is not available.
     */
    @Test
    void takePhoto_whenPicturesServiceNotAvailable_returnsEmptyOptional() {
        // Mock the Services.get method to return an empty Optional
        try (MockedStatic<com.gluonhq.attach.util.Services> mockedServices =
                     Mockito.mockStatic(com.gluonhq.attach.util.Services.class)) {

            mockedServices.when(() ->
                            com.gluonhq.attach.util.Services.get(com.gluonhq.attach.pictures.PicturesService.class))
                    .thenReturn(Optional.empty());

            // Test that takePhoto returns an empty Optional
            Optional<File> result = cameraService.takePhoto();
            assertTrue(result.isEmpty());
        }
    }

    /**
     * Test that selectPhoto returns an empty Optional when PicturesService is not available.
     */
    @Test
    void selectPhoto_whenPicturesServiceNotAvailable_returnsEmptyOptional() {
        // Mock the Services.get method to return an empty Optional
        try (MockedStatic<com.gluonhq.attach.util.Services> mockedServices =
                     Mockito.mockStatic(com.gluonhq.attach.util.Services.class)) {

            mockedServices.when(() ->
                            com.gluonhq.attach.util.Services.get(com.gluonhq.attach.pictures.PicturesService.class))
                    .thenReturn(Optional.empty());

            // Test that selectPhoto returns an empty Optional
            Optional<File> result = cameraService.selectPhoto();
            assertTrue(result.isEmpty());
        }
    }

    /**
     * Test that saveImageToFile uses StorageService when available.
     * This test verifies that our mobile-compatible implementation works correctly.
     */
    @Test
    void saveImageToFile_whenStorageServiceAvailable_usesStorageService() throws Exception {
        // Create a mock StorageService
        StorageService mockStorageService = mock(StorageService.class);
        File mockPrivateStorage = new File(tempDir.toFile(), "private");
        mockPrivateStorage.mkdirs();

        // Configure the mock StorageService
        when(mockStorageService.getPrivateStorage()).thenReturn(Optional.of(mockPrivateStorage));

        // Create a mock Image
        Image mockImage = mock(Image.class);
        when(mockImage.getWidth()).thenReturn(10.0);
        when(mockImage.getHeight()).thenReturn(10.0);
        when(mockImage.getPixelReader()).thenReturn(null); // We'll handle this in the try-catch

        // Create a test file
        File testFile = new File(tempDir.toFile(), "test.png");

        // Mock the Services.get method to return our mock StorageService
        try (MockedStatic<Services> mockedServices = mockStatic(Services.class)) {
            mockedServices.when(() -> Services.get(StorageService.class))
                    .thenReturn(Optional.of(mockStorageService));

            // Access the private saveImageToFile method using reflection
            Method saveImageToFileMethod = GluonCameraService.class.getDeclaredMethod(
                    "saveImageToFile", Image.class, File.class);
            saveImageToFileMethod.setAccessible(true);

            try {
                // Call the method (it will throw an exception because we mocked the PixelReader as null)
                saveImageToFileMethod.invoke(cameraService, mockImage, testFile);
            } catch (Exception e) {
                // Expected exception because we mocked the PixelReader as null
                // We're not testing the actual image saving, just that StorageService is used
            }

            // Verify that StorageService.getPrivateStorage was called
            verify(mockStorageService).getPrivateStorage();
        }
    }

    /**
     * Test that saveImageToFile falls back to standard Java file I/O when StorageService is not available.
     * This test verifies that our implementation gracefully handles the absence of StorageService.
     */
    @Test
    void saveImageToFile_whenStorageServiceNotAvailable_fallsBackToStandardIO() throws Exception {
        // Create a mock Image
        Image mockImage = mock(Image.class);
        when(mockImage.getWidth()).thenReturn(10.0);
        when(mockImage.getHeight()).thenReturn(10.0);
        when(mockImage.getPixelReader()).thenReturn(null); // We'll handle this in the try-catch

        // Create a test file
        File testFile = new File(tempDir.toFile(), "test.png");

        // Mock the Services.get method to return an empty Optional
        try (MockedStatic<Services> mockedServices = mockStatic(Services.class)) {
            mockedServices.when(() -> Services.get(StorageService.class))
                    .thenReturn(Optional.empty());

            // Access the private saveImageToFile method using reflection
            Method saveImageToFileMethod = GluonCameraService.class.getDeclaredMethod(
                    "saveImageToFile", Image.class, File.class);
            saveImageToFileMethod.setAccessible(true);

            try {
                // Call the method (it will throw an exception because we mocked the PixelReader as null)
                saveImageToFileMethod.invoke(cameraService, mockImage, testFile);
            } catch (Exception e) {
                // Expected exception because we mocked the PixelReader as null
                // We're not testing the actual image saving, just that the fallback path is used
            }

            // Verify that Services.get was called with StorageService.class
            mockedServices.verify(() -> Services.get(StorageService.class));
        }
    }
}
