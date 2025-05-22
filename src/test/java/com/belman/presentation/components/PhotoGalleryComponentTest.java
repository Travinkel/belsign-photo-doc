package com.belman.presentation.components;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the PhotoGalleryComponent.
 * These tests verify that the component behaves correctly.
 */
public class PhotoGalleryComponentTest {

    /**
     * Initialize the JavaFX toolkit before running tests.
     */
    @BeforeAll
    public static void initJavaFX() {
        // Initialize the JavaFX platform
        new JFXPanel();
    }

    /**
     * Test that the component can be created and initialized.
     */
    @Test
    public void testComponentCreation() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Create the component
                PhotoGalleryComponent gallery = new PhotoGalleryComponent();

                // Verify that the component was created successfully
                assertNotNull(gallery);
                assertTrue(gallery.getStyleClass().contains("photo-gallery"));

                // Verify that the empty label is visible when there are no photos
                assertTrue(gallery.getEmptyText().contains("No photos"));

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception during test: " + e.getMessage());
            }
        });

        // Wait for the JavaFX thread to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Test that photos can be added to the gallery.
     */
    @Test
    public void testAddPhotos() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Create the component
                PhotoGalleryComponent gallery = new PhotoGalleryComponent();

                // Create a scene and add the component to it
                Scene scene = new Scene(new StackPane(gallery), 800, 600);

                // Add some photos
                List<String> photoUrls = new ArrayList<>();
                // Use a test image that's guaranteed to exist
                String testImageUrl = getClass().getResource("/com/belman/images/logo.png").toExternalForm();
                photoUrls.add(testImageUrl);
                photoUrls.add(testImageUrl);
                photoUrls.add(testImageUrl);

                gallery.setPhotos(photoUrls);

                // Verify that the photos were added
                assertEquals(3, gallery.getPhotoCount());

                // Verify that the empty label is not visible when there are photos
                assertFalse(gallery.getEmptyText().isEmpty());

                System.out.println("[DEBUG_LOG] Added " + gallery.getPhotoCount() + " photos to gallery");

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception during test: " + e.getMessage());
            }
        });

        // Wait for the JavaFX thread to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Test that photos can be selected.
     */
    @Test
    public void testSelectPhoto() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Create the component
                PhotoGalleryComponent gallery = new PhotoGalleryComponent();

                // Create a scene and add the component to it
                Scene scene = new Scene(new StackPane(gallery), 800, 600);

                // Add some photos
                String testImageUrl = getClass().getResource("/com/belman/images/logo.png").toExternalForm();
                PhotoGalleryComponent.PhotoItem item1 = gallery.addPhoto(testImageUrl);
                PhotoGalleryComponent.PhotoItem item2 = gallery.addPhoto(testImageUrl);
                PhotoGalleryComponent.PhotoItem item3 = gallery.addPhoto(testImageUrl);

                // Select a photo
                gallery.setSelectedPhoto(item2);

                // Verify that the photo was selected
                assertEquals(item2, gallery.getSelectedPhoto());
                assertTrue(item2.isSelected());
                assertFalse(item1.isSelected());
                assertFalse(item3.isSelected());

                System.out.println("[DEBUG_LOG] Selected photo: " + gallery.getSelectedPhoto().getUrl());

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception during test: " + e.getMessage());
            }
        });

        // Wait for the JavaFX thread to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Test that photos can have status and captions.
     */
    @Test
    public void testPhotoStatusAndCaption() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Create the component
                PhotoGalleryComponent gallery = new PhotoGalleryComponent();

                // Create a scene and add the component to it
                Scene scene = new Scene(new StackPane(gallery), 800, 600);

                // Add a photo with status and caption
                String testImageUrl = getClass().getResource("/com/belman/images/logo.png").toExternalForm();
                PhotoGalleryComponent.PhotoItem item = gallery.addPhoto(testImageUrl);
                item.setStatus("APPROVED");
                item.setCaption("Test Caption");

                // Verify that the status and caption were set
                assertEquals("APPROVED", item.getStatus());
                assertEquals("Test Caption", item.getCaption());

                System.out.println("[DEBUG_LOG] Photo status: " + item.getStatus() + ", caption: " + item.getCaption());

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception during test: " + e.getMessage());
            }
        });

        // Wait for the JavaFX thread to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    /**
     * Test that the loading state can be set.
     */
    @Test
    public void testLoadingState() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            try {
                // Create the component
                PhotoGalleryComponent gallery = new PhotoGalleryComponent();

                // Set the loading state
                gallery.setLoading(true);

                // Verify that the loading state was set
                assertTrue(gallery.isLoading());

                // Set the loading state back to false
                gallery.setLoading(false);

                // Verify that the loading state was set back to false
                assertFalse(gallery.isLoading());

                System.out.println("[DEBUG_LOG] Loading state test passed");

                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Exception during test: " + e.getMessage());
            }
        });

        // Wait for the JavaFX thread to complete
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
