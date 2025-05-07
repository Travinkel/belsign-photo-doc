package com.belman.presentation.views.photoupload;

import com.belman.domain.order.photo.PhotoDocument;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A touch-friendly list cell for displaying photo information.
 * This cell provides a more structured and visually appealing layout
 * with better touch targets and visual hierarchy.
 */
public class TouchFriendlyPhotoListCell extends ListCell<PhotoDocument> {

    private final HBox container;
    private final Circle statusIndicator;
    private final Label photoIdLabel;
    private final Label angleLabel;
    private final Label statusLabel;
    private final Label uploaderLabel;
    private final Label timestampLabel;

    public TouchFriendlyPhotoListCell() {
        // Create the status indicator circle
        statusIndicator = new Circle(12);
        statusIndicator.setStroke(Color.LIGHTGRAY);
        statusIndicator.setStrokeWidth(1);

        // Create labels with appropriate styling
        photoIdLabel = new Label();
        photoIdLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        photoIdLabel.setWrapText(true);

        angleLabel = new Label();
        angleLabel.setWrapText(true);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        uploaderLabel = new Label();
        uploaderLabel.setWrapText(true);

        timestampLabel = new Label();
        timestampLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -belman-grey-50;");
        timestampLabel.setWrapText(true);

        // Create a VBox for the main content
        VBox content = new VBox(5);
        content.getChildren().addAll(photoIdLabel, angleLabel, statusLabel, uploaderLabel, timestampLabel);
        HBox.setHgrow(content, Priority.ALWAYS);

        // Create the main container
        container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(8));
        container.getChildren().addAll(statusIndicator, content);
        
        // Set the graphic to null initially
        setGraphic(null);
    }

    @Override
    protected void updateItem(PhotoDocument item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Update the status indicator color based on the photo status
            switch (item.getStatus()) {
                case APPROVED:
                    statusIndicator.setFill(Color.web("#338d71")); // Green
                    break;
                case REJECTED:
                    statusIndicator.setFill(Color.web("#d32f2f")); // Red
                    break;
                case PENDING:
                default:
                    statusIndicator.setFill(Color.web("#ffa000")); // Amber
                    break;
            }

            // Update the labels with photo information
            photoIdLabel.setText("Photo ID: " + item.getPhotoId().value());
            angleLabel.setText("Angle: " + item.getAngle().degrees() + "°");
            statusLabel.setText("Status: " + item.getStatus().name());
            uploaderLabel.setText("Uploaded by: " + item.getUploadedBy().getUsername().value());
            timestampLabel.setText("Uploaded at: " + item.getUploadedAt().toInstant().toString());

            // Set the graphic to the container
            setGraphic(container);
        }
    }
}