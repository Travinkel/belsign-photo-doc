package com.belman.presentation.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A reusable photo gallery component that can display photos in a grid layout.
 * This component supports selection, zooming, and basic editing of photos.
 */
public class PhotoGalleryComponent extends StackPane {

    private static final double DEFAULT_THUMBNAIL_SIZE = 120.0;
    private static final double DEFAULT_SPACING = 10.0;
    private static final int DEFAULT_COLUMNS = 3;

    private final FlowPane galleryPane;
    private final ScrollPane scrollPane;
    private final Label emptyLabel;
    private final StackPane loadingOverlay;
    private final VBox zoomOverlay;
    private final ImageView zoomedImageView;

    private final ObservableList<PhotoItem> photos = FXCollections.observableArrayList();
    private final BooleanProperty selectionMode = new SimpleBooleanProperty(false);
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final StringProperty emptyText = new SimpleStringProperty("No photos available");
    private final ObjectProperty<PhotoItem> selectedPhoto = new SimpleObjectProperty<>();

    private double thumbnailSize = DEFAULT_THUMBNAIL_SIZE;
    private Consumer<PhotoItem> onPhotoSelected;
    private Consumer<PhotoItem> onPhotoDoubleClicked;

    /**
     * Creates a new PhotoGalleryComponent with default settings.
     */
    public PhotoGalleryComponent() {
        // Create the gallery pane
        galleryPane = new FlowPane();
        galleryPane.setHgap(DEFAULT_SPACING);
        galleryPane.setVgap(DEFAULT_SPACING);
        galleryPane.setPadding(new Insets(DEFAULT_SPACING));
        galleryPane.setAlignment(Pos.TOP_LEFT);

        // Create the scroll pane
        scrollPane = new ScrollPane(galleryPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("photo-gallery-scroll");

        // Create the empty label
        emptyLabel = new Label(emptyText.get());
        emptyLabel.getStyleClass().add("photo-gallery-empty-label");
        emptyLabel.setAlignment(Pos.CENTER);
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setMaxHeight(Double.MAX_VALUE);
        // Set initial visibility based on whether the photos list is empty
        emptyLabel.setVisible(photos.isEmpty());

        // Update visibility when the photos list changes
        photos.addListener((javafx.collections.ListChangeListener<PhotoItem>) c -> {
            emptyLabel.setVisible(photos.isEmpty());
        });
        emptyLabel.textProperty().bind(emptyText);

        // Create the loading overlay
        loadingOverlay = new StackPane();
        loadingOverlay.getStyleClass().add("photo-gallery-loading-overlay");
        loadingOverlay.setVisible(false);
        Label loadingLabel = new Label("Loading...");
        loadingLabel.getStyleClass().add("photo-gallery-loading-label");
        loadingOverlay.getChildren().add(loadingLabel);
        loadingOverlay.visibleProperty().bind(loading);

        // Create the zoom overlay
        zoomedImageView = new ImageView();
        zoomedImageView.setPreserveRatio(true);
        zoomedImageView.setSmooth(true);
        zoomedImageView.setCache(true);
        zoomedImageView.getStyleClass().add("photo-gallery-zoomed-image");

        zoomOverlay = new VBox(zoomedImageView);
        zoomOverlay.setAlignment(Pos.CENTER);
        zoomOverlay.getStyleClass().add("photo-gallery-zoom-overlay");
        zoomOverlay.setVisible(false);
        zoomOverlay.setOnMouseClicked(e -> hideZoomOverlay());

        // Add all components to the stack pane
        getChildren().addAll(scrollPane, emptyLabel, loadingOverlay, zoomOverlay);
        getStyleClass().add("photo-gallery");

        // Listen for changes to the photos list to refresh the gallery
        photos.addListener((javafx.collections.ListChangeListener.Change<? extends PhotoItem> c) -> refreshGallery());
    }

    /**
     * Sets the photos to display in the gallery.
     *
     * @param photoUrls the URLs of the photos to display
     */
    public void setPhotos(List<String> photoUrls) {
        List<PhotoItem> items = new ArrayList<>();
        for (String url : photoUrls) {
            items.add(new PhotoItem(url));
        }
        setPhotoItems(items);
    }

    /**
     * Sets the photo items to display in the gallery.
     *
     * @param items the photo items to display
     */
    public void setPhotoItems(List<PhotoItem> items) {
        photos.clear();
        photos.addAll(items);
    }

    /**
     * Adds a photo to the gallery.
     *
     * @param photoUrl the URL of the photo to add
     * @return the added photo item
     */
    public PhotoItem addPhoto(String photoUrl) {
        PhotoItem item = new PhotoItem(photoUrl);
        photos.add(item);
        return item;
    }

    /**
     * Adds a photo item to the gallery.
     *
     * @param item the photo item to add
     */
    public void addPhotoItem(PhotoItem item) {
        photos.add(item);
    }

    /**
     * Removes a photo from the gallery.
     *
     * @param item the photo item to remove
     * @return true if the photo was removed, false otherwise
     */
    public boolean removePhoto(PhotoItem item) {
        return photos.remove(item);
    }

    /**
     * Clears all photos from the gallery.
     */
    public void clearPhotos() {
        photos.clear();
    }

    /**
     * Gets the number of photos in the gallery.
     *
     * @return the number of photos
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Sets the size of the thumbnails in the gallery.
     *
     * @param size the size of the thumbnails in pixels
     */
    public void setThumbnailSize(double size) {
        this.thumbnailSize = size;
        refreshGallery();
    }

    /**
     * Sets whether selection mode is enabled.
     * In selection mode, clicking a photo selects it without zooming.
     *
     * @param selectionMode true to enable selection mode, false otherwise
     */
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode.set(selectionMode);
    }

    /**
     * Gets whether selection mode is enabled.
     *
     * @return true if selection mode is enabled, false otherwise
     */
    public boolean isSelectionMode() {
        return selectionMode.get();
    }

    /**
     * Gets the selection mode property.
     *
     * @return the selection mode property
     */
    public BooleanProperty selectionModeProperty() {
        return selectionMode;
    }

    /**
     * Sets the text to display when there are no photos.
     *
     * @param text the text to display
     */
    public void setEmptyText(String text) {
        emptyText.set(text);
    }

    /**
     * Gets the text displayed when there are no photos.
     *
     * @return the empty text
     */
    public String getEmptyText() {
        return emptyText.get();
    }

    /**
     * Gets the empty text property.
     *
     * @return the empty text property
     */
    public StringProperty emptyTextProperty() {
        return emptyText;
    }

    /**
     * Sets the loading state of the gallery.
     *
     * @param loading true if the gallery is loading, false otherwise
     */
    public void setLoading(boolean loading) {
        this.loading.set(loading);
    }

    /**
     * Gets whether the gallery is loading.
     *
     * @return true if the gallery is loading, false otherwise
     */
    public boolean isLoading() {
        return loading.get();
    }

    /**
     * Gets the loading property.
     *
     * @return the loading property
     */
    public BooleanProperty loadingProperty() {
        return loading;
    }

    /**
     * Sets the callback to be called when a photo is selected.
     *
     * @param callback the callback to call
     */
    public void setOnPhotoSelected(Consumer<PhotoItem> callback) {
        this.onPhotoSelected = callback;
    }

    /**
     * Sets the callback to be called when a photo is double-clicked.
     *
     * @param callback the callback to call
     */
    public void setOnPhotoDoubleClicked(Consumer<PhotoItem> callback) {
        this.onPhotoDoubleClicked = callback;
    }

    /**
     * Gets the currently selected photo.
     *
     * @return the selected photo, or null if no photo is selected
     */
    public PhotoItem getSelectedPhoto() {
        return selectedPhoto.get();
    }

    /**
     * Sets the selected photo.
     *
     * @param item the photo to select, or null to deselect
     */
    public void setSelectedPhoto(PhotoItem item) {
        // Deselect the previously selected photo
        if (selectedPhoto.get() != null) {
            selectedPhoto.get().setSelected(false);
        }

        // Select the new photo
        selectedPhoto.set(item);
        if (item != null) {
            item.setSelected(true);
        }

        // Call the callback
        if (onPhotoSelected != null && item != null) {
            onPhotoSelected.accept(item);
        }
    }

    /**
     * Gets the selected photo property.
     *
     * @return the selected photo property
     */
    public ObjectProperty<PhotoItem> selectedPhotoProperty() {
        return selectedPhoto;
    }

    /**
     * Shows the zoom overlay for the specified photo.
     *
     * @param item the photo to zoom
     */
    public void showZoomOverlay(PhotoItem item) {
        if (item != null && item.getImage() != null) {
            zoomedImageView.setImage(item.getImage());
            zoomOverlay.setVisible(true);
        }
    }

    /**
     * Hides the zoom overlay.
     */
    public void hideZoomOverlay() {
        zoomOverlay.setVisible(false);
    }

    /**
     * Refreshes the gallery display.
     */
    private void refreshGallery() {
        galleryPane.getChildren().clear();

        for (PhotoItem item : photos) {
            // Create a thumbnail for the photo
            ImageView thumbnailView = new ImageView(item.getImage());
            thumbnailView.setFitWidth(thumbnailSize);
            thumbnailView.setFitHeight(thumbnailSize);
            thumbnailView.setPreserveRatio(true);
            thumbnailView.setSmooth(true);
            thumbnailView.setCache(true);

            // Create a container for the thumbnail
            StackPane container = new StackPane(thumbnailView);
            container.getStyleClass().add("photo-gallery-thumbnail");
            container.setPrefSize(thumbnailSize, thumbnailSize);
            container.setMaxSize(thumbnailSize, thumbnailSize);
            container.setUserData(item);

            // Add selection styling
            if (item.isSelected()) {
                container.getStyleClass().add("selected");
            }

            // Add status indicator if needed
            if (item.getStatus() != null && !item.getStatus().isEmpty()) {
                Label statusLabel = new Label(item.getStatus());
                statusLabel.getStyleClass().add("photo-gallery-status-label");
                statusLabel.getStyleClass().add("status-" + item.getStatus().toLowerCase());
                StackPane.setAlignment(statusLabel, Pos.TOP_RIGHT);
                container.getChildren().add(statusLabel);
            }

            // Add caption if needed
            if (item.getCaption() != null && !item.getCaption().isEmpty()) {
                Label captionLabel = new Label(item.getCaption());
                captionLabel.getStyleClass().add("photo-gallery-caption-label");
                StackPane.setAlignment(captionLabel, Pos.BOTTOM_CENTER);
                container.getChildren().add(captionLabel);
            }

            // Add click handlers
            container.setOnMouseClicked(event -> handleThumbnailClick(event, item, container));

            // Add the thumbnail to the gallery
            galleryPane.getChildren().add(container);
        }
    }

    /**
     * Handles a click on a thumbnail.
     *
     * @param event the mouse event
     * @param item the photo item that was clicked
     * @param container the container that was clicked
     */
    private void handleThumbnailClick(MouseEvent event, PhotoItem item, StackPane container) {
        if (event.getClickCount() == 2) {
            // Double-click: zoom or callback
            if (onPhotoDoubleClicked != null) {
                onPhotoDoubleClicked.accept(item);
            } else {
                showZoomOverlay(item);
            }
        } else {
            // Single click: select
            setSelectedPhoto(item);
        }
    }

    /**
     * A class representing a photo item in the gallery.
     */
    public static class PhotoItem {
        private final StringProperty url = new SimpleStringProperty();
        private final StringProperty caption = new SimpleStringProperty();
        private final StringProperty status = new SimpleStringProperty();
        private final BooleanProperty selected = new SimpleBooleanProperty(false);
        private final ObjectProperty<Image> image = new SimpleObjectProperty<>();
        private final ObjectProperty<Object> userData = new SimpleObjectProperty<>();

        /**
         * Creates a new PhotoItem with the specified URL.
         *
         * @param url the URL of the photo
         */
        public PhotoItem(String url) {
            this.url.set(url);
            this.image.set(new Image(url, true)); // Use background loading
        }

        /**
         * Creates a new PhotoItem with the specified URL and caption.
         *
         * @param url the URL of the photo
         * @param caption the caption for the photo
         */
        public PhotoItem(String url, String caption) {
            this(url);
            this.caption.set(caption);
        }

        /**
         * Gets the URL of the photo.
         *
         * @return the URL
         */
        public String getUrl() {
            return url.get();
        }

        /**
         * Sets the URL of the photo.
         *
         * @param url the URL
         */
        public void setUrl(String url) {
            this.url.set(url);
        }

        /**
         * Gets the URL property.
         *
         * @return the URL property
         */
        public StringProperty urlProperty() {
            return url;
        }

        /**
         * Gets the caption of the photo.
         *
         * @return the caption
         */
        public String getCaption() {
            return caption.get();
        }

        /**
         * Sets the caption of the photo.
         *
         * @param caption the caption
         */
        public void setCaption(String caption) {
            this.caption.set(caption);
        }

        /**
         * Gets the caption property.
         *
         * @return the caption property
         */
        public StringProperty captionProperty() {
            return caption;
        }

        /**
         * Gets the status of the photo.
         *
         * @return the status
         */
        public String getStatus() {
            return status.get();
        }

        /**
         * Sets the status of the photo.
         *
         * @param status the status
         */
        public void setStatus(String status) {
            this.status.set(status);
        }

        /**
         * Gets the status property.
         *
         * @return the status property
         */
        public StringProperty statusProperty() {
            return status;
        }

        /**
         * Gets whether the photo is selected.
         *
         * @return true if the photo is selected, false otherwise
         */
        public boolean isSelected() {
            return selected.get();
        }

        /**
         * Sets whether the photo is selected.
         *
         * @param selected true to select the photo, false to deselect
         */
        public void setSelected(boolean selected) {
            this.selected.set(selected);
        }

        /**
         * Gets the selected property.
         *
         * @return the selected property
         */
        public BooleanProperty selectedProperty() {
            return selected;
        }

        /**
         * Gets the image of the photo.
         *
         * @return the image
         */
        public Image getImage() {
            return image.get();
        }

        /**
         * Sets the image of the photo.
         *
         * @param image the image
         */
        public void setImage(Image image) {
            this.image.set(image);
        }

        /**
         * Gets the image property.
         *
         * @return the image property
         */
        public ObjectProperty<Image> imageProperty() {
            return image;
        }

        /**
         * Gets the user data associated with the photo.
         *
         * @return the user data
         */
        public Object getUserData() {
            return userData.get();
        }

        /**
         * Sets the user data associated with the photo.
         *
         * @param userData the user data
         */
        public void setUserData(Object userData) {
            this.userData.set(userData);
        }

        /**
         * Gets the user data property.
         *
         * @return the user data property
         */
        public ObjectProperty<Object> userDataProperty() {
            return userData;
        }
    }
}
