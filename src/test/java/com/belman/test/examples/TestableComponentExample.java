package com.belman.test.examples;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class demonstrates how to test JavaFX components using test-specific implementations.
 * It follows the recommended approach of:
 * 1. Creating testable implementations that don't rely on JavaFX UI components
 * 2. Testing the core logic without JavaFX dependencies
 * 3. Separating UI logic from business logic
 */
public class TestableComponentExample {

    /**
     * A simple interface for a photo item.
     */
    interface PhotoItem {
        String getUrl();
        String getCaption();
        boolean isSelected();
        void setSelected(boolean selected);
    }

    /**
     * A real implementation of PhotoItem that would be used in the actual application.
     * This class has JavaFX dependencies and would be difficult to test directly.
     */
    static class RealPhotoItem implements PhotoItem {
        private final StringProperty url = new SimpleStringProperty();
        private final StringProperty caption = new SimpleStringProperty();
        private final BooleanProperty selected = new SimpleBooleanProperty();

        public RealPhotoItem(String url, String caption) {
            this.url.set(url);
            this.caption.set(caption);
            this.selected.set(false);
        }

        @Override
        public String getUrl() {
            return url.get();
        }

        @Override
        public String getCaption() {
            return caption.get();
        }

        @Override
        public boolean isSelected() {
            return selected.get();
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        // JavaFX property getters for binding
        public StringProperty urlProperty() {
            return url;
        }

        public StringProperty captionProperty() {
            return caption;
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }
    }

    /**
     * A test-specific implementation of PhotoItem that doesn't have JavaFX dependencies.
     * This class can be easily tested without a JavaFX environment.
     */
    static class TestablePhotoItem implements PhotoItem {
        private String url;
        private String caption;
        private boolean selected;

        public TestablePhotoItem(String url, String caption) {
            this.url = url;
            this.caption = caption;
            this.selected = false;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getCaption() {
            return caption;
        }

        @Override
        public boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    /**
     * A simple photo gallery component that would be used in the actual application.
     * This class has JavaFX dependencies and would be difficult to test directly.
     */
    static class PhotoGalleryComponent {
        private final ListView<PhotoItem> listView = new ListView<>();
        private final ObservableList<PhotoItem> items = FXCollections.observableArrayList();
        private PhotoItem selectedItem;

        public PhotoGalleryComponent() {
            listView.setItems(items);
            listView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (oldVal != null) {
                    oldVal.setSelected(false);
                }
                if (newVal != null) {
                    newVal.setSelected(true);
                    selectedItem = newVal;
                }
            });
        }

        public void addPhoto(String url, String caption) {
            RealPhotoItem item = new RealPhotoItem(url, caption);
            items.add(item);
        }

        public void selectPhoto(PhotoItem item) {
            if (items.contains(item)) {
                listView.getSelectionModel().select(item);
            }
        }

        public PhotoItem getSelectedPhoto() {
            return selectedItem;
        }

        public ObservableList<PhotoItem> getItems() {
            return items;
        }

        public ListView<PhotoItem> getListView() {
            return listView;
        }
    }

    /**
     * A testable photo gallery that implements the core logic without JavaFX dependencies.
     * This class can be easily tested without a JavaFX environment.
     */
    static class TestablePhotoGallery {
        private final List<PhotoItem> items = FXCollections.observableArrayList();
        private PhotoItem selectedItem;

        public void addPhoto(String url, String caption) {
            TestablePhotoItem item = new TestablePhotoItem(url, caption);
            items.add(item);
        }

        public void selectPhoto(PhotoItem item) {
            if (items.contains(item)) {
                if (selectedItem != null) {
                    selectedItem.setSelected(false);
                }
                item.setSelected(true);
                selectedItem = item;
            }
        }

        public PhotoItem getSelectedPhoto() {
            return selectedItem;
        }

        public List<PhotoItem> getItems() {
            return items;
        }
    }

    private TestablePhotoGallery gallery;

    @BeforeEach
    public void setUp() {
        gallery = new TestablePhotoGallery();
    }

    /**
     * Test that photos can be added to the gallery.
     */
    @Test
    public void testAddPhotos() {
        // Arrange
        String url1 = "http://example.com/photo1.jpg";
        String caption1 = "Photo 1";
        String url2 = "http://example.com/photo2.jpg";
        String caption2 = "Photo 2";

        // Act
        gallery.addPhoto(url1, caption1);
        gallery.addPhoto(url2, caption2);

        // Assert
        assertEquals(2, gallery.getItems().size(), "Gallery should have 2 items");
        assertEquals(url1, gallery.getItems().get(0).getUrl(), "First item should have correct URL");
        assertEquals(caption1, gallery.getItems().get(0).getCaption(), "First item should have correct caption");
        assertEquals(url2, gallery.getItems().get(1).getUrl(), "Second item should have correct URL");
        assertEquals(caption2, gallery.getItems().get(1).getCaption(), "Second item should have correct caption");
    }

    /**
     * Test that photos can be selected.
     */
    @Test
    public void testSelectPhoto() {
        // Arrange
        gallery.addPhoto("http://example.com/photo1.jpg", "Photo 1");
        gallery.addPhoto("http://example.com/photo2.jpg", "Photo 2");
        PhotoItem item1 = gallery.getItems().get(0);
        PhotoItem item2 = gallery.getItems().get(1);

        // Act
        gallery.selectPhoto(item1);

        // Assert
        assertEquals(item1, gallery.getSelectedPhoto(), "First item should be selected");
        assertTrue(item1.isSelected(), "First item's selected property should be true");
        assertFalse(item2.isSelected(), "Second item's selected property should be false");

        // Act again - select the second item
        gallery.selectPhoto(item2);

        // Assert again
        assertEquals(item2, gallery.getSelectedPhoto(), "Second item should be selected");
        assertFalse(item1.isSelected(), "First item's selected property should be false");
        assertTrue(item2.isSelected(), "Second item's selected property should be true");
    }

    /**
     * Test that selecting a non-existent photo has no effect.
     */
    @Test
    public void testSelectNonExistentPhoto() {
        // Arrange
        gallery.addPhoto("http://example.com/photo1.jpg", "Photo 1");
        PhotoItem item1 = gallery.getItems().get(0);
        PhotoItem nonExistentItem = new TestablePhotoItem("http://example.com/nonexistent.jpg", "Non-existent");

        // Act
        gallery.selectPhoto(item1);
        gallery.selectPhoto(nonExistentItem);

        // Assert
        assertEquals(item1, gallery.getSelectedPhoto(), "First item should still be selected");
        assertTrue(item1.isSelected(), "First item's selected property should still be true");
        assertFalse(nonExistentItem.isSelected(), "Non-existent item's selected property should be false");
    }
}