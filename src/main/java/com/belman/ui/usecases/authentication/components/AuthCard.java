package com.belman.ui.usecases.authentication.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A card-like container for authentication forms.
 * This component provides a consistent look and feel for authentication forms.
 */
public class AuthCard extends VBox {
    
    /**
     * Creates a new AuthCard with the specified children.
     *
     * @param children the children to add to the card
     */
    public AuthCard(Node... children) {
        super(20, children);
        initialize();
    }
    
    /**
     * Creates a new empty AuthCard.
     */
    public AuthCard() {
        super(20);
        initialize();
    }
    
    /**
     * Initializes the card with the appropriate styles and properties.
     */
    private void initialize() {
        // Add the auth-card style class
        getStyleClass().add("auth-card");
        
        // Set default properties
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setMaxWidth(450);
        
        // Add a drop shadow effect
        setEffect(new javafx.scene.effect.DropShadow(10, Color.rgb(0, 0, 0, 0.2)));
        
        // Add a background with rounded corners
        setBackground(new javafx.scene.layout.Background(
                new javafx.scene.layout.BackgroundFill(
                        Color.WHITE,
                        new javafx.scene.layout.CornerRadii(10),
                        Insets.EMPTY
                )
        ));
        
        // Add a border
        setBorder(new javafx.scene.layout.Border(
                new javafx.scene.layout.BorderStroke(
                        Color.rgb(200, 200, 200),
                        javafx.scene.layout.BorderStrokeStyle.SOLID,
                        new javafx.scene.layout.CornerRadii(10),
                        new javafx.scene.layout.BorderWidths(1)
                )
        ));
    }
    
    /**
     * Sets the title of the card.
     * This adds a title label at the top of the card.
     *
     * @param title the title text
     * @return this card for method chaining
     */
    public AuthCard setTitle(String title) {
        // Create a title label
        javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(title);
        titleLabel.getStyleClass().add("auth-card-title");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Add the title label at the beginning of the children list
        getChildren().add(0, titleLabel);
        
        return this;
    }
    
    /**
     * Sets the width of the card.
     *
     * @param width the width in pixels
     * @return this card for method chaining
     */
    public AuthCard setCardWidth(double width) {
        setPrefWidth(width);
        setMaxWidth(width);
        return this;
    }
    
    /**
     * Adds a divider to the card.
     *
     * @return this card for method chaining
     */
    public AuthCard addDivider() {
        Rectangle divider = new Rectangle(getWidth() - 60, 1);
        divider.setFill(Color.rgb(200, 200, 200));
        getChildren().add(divider);
        return this;
    }
}