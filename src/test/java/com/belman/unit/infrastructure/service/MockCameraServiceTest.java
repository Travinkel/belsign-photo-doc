package com.belman.unit.infrastructure.service;

import com.belman.business.richbe.services.CameraService;
import com.belman.data.service.MockCameraService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MockCameraService class.
 * These tests verify that the mock implementation works correctly
 * for desktop platforms.
 */
class MockCameraServiceTest {

    @Mock
    private Stage mockStage;
    
    @Mock
    private FileChooser mockFileChooser;
    
    private CameraService cameraService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a MockCameraService with the mock stage
        cameraService = new MockCameraService(mockStage);
        
        // Use reflection to replace the FileChooser with our mock
        try {
            java.lang.reflect.Field field = MockCameraService.class.getDeclaredField("fileChooser");
            field.setAccessible(true);
            field.set(cameraService, mockFileChooser);
        } catch (Exception e) {
            // If this fails, the test will fail anyway
            e.printStackTrace();
        }
    }
    
    /**
     * Test that isCameraAvailable always returns true for the mock implementation.
     */
    @Test
    void isCameraAvailable_alwaysReturnsTrue() {
        assertTrue(cameraService.isCameraAvailable());
    }
    
    /**
     * Test that isGalleryAvailable always returns true for the mock implementation.
     */
    @Test
    void isGalleryAvailable_alwaysReturnsTrue() {
        assertTrue(cameraService.isGalleryAvailable());
    }
    
    /**
     * Test that takePhoto returns an Optional with the selected file when a file is selected.
     */
    @Test
    void takePhoto_whenFileSelected_returnsOptionalWithFile() {
        // Create a mock file
        File mockFile = new File("mockFile.jpg");
        
        // Mock the FileChooser to return the mock file
        when(mockFileChooser.showOpenDialog(mockStage)).thenReturn(mockFile);
        
        // Test that takePhoto returns an Optional with the mock file
        Optional<File> result = cameraService.takePhoto();
        assertTrue(result.isPresent());
        assertEquals(mockFile, result.get());
    }
    
    /**
     * Test that takePhoto returns an empty Optional when no file is selected.
     */
    @Test
    void takePhoto_whenNoFileSelected_returnsEmptyOptional() {
        // Mock the FileChooser to return null (no file selected)
        when(mockFileChooser.showOpenDialog(mockStage)).thenReturn(null);
        
        // Test that takePhoto returns an empty Optional
        Optional<File> result = cameraService.takePhoto();
        assertTrue(result.isEmpty());
    }
    
    /**
     * Test that selectPhoto returns an Optional with the selected file when a file is selected.
     */
    @Test
    void selectPhoto_whenFileSelected_returnsOptionalWithFile() {
        // Create a mock file
        File mockFile = new File("mockFile.jpg");
        
        // Mock the FileChooser to return the mock file
        when(mockFileChooser.showOpenDialog(mockStage)).thenReturn(mockFile);
        
        // Test that selectPhoto returns an Optional with the mock file
        Optional<File> result = cameraService.selectPhoto();
        assertTrue(result.isPresent());
        assertEquals(mockFile, result.get());
    }
    
    /**
     * Test that selectPhoto returns an empty Optional when no file is selected.
     */
    @Test
    void selectPhoto_whenNoFileSelected_returnsEmptyOptional() {
        // Mock the FileChooser to return null (no file selected)
        when(mockFileChooser.showOpenDialog(mockStage)).thenReturn(null);
        
        // Test that selectPhoto returns an empty Optional
        Optional<File> result = cameraService.selectPhoto();
        assertTrue(result.isEmpty());
    }
}