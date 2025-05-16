package com.belman.ui.usecases.authentication.logout;

import com.belman.common.logging.EmojiLogger;
import com.belman.ui.base.BaseView;
import com.gluonhq.charm.glisten.control.AppBar;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * View for the logout screen.
 */
public class LogoutView extends BaseView<LogoutViewModel> {
    private static final EmojiLogger logger = EmojiLogger.getLogger(LogoutView.class);

    private Button logoutButton;
    private Button cancelButton;
    private Label errorMessageLabel;
    private ProgressIndicator logoutProgressIndicator;

    /**
     * Constructor for the logout view.
     */
    public LogoutView() {
        super();
        logger.debug("LogoutView constructor called");

        // Create UI components
        createUI();
    }

    /**
     * Creates the UI components for the logout view.
     */
    private void createUI() {
        // Create a VBox to hold all components
        VBox root = new VBox(20);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);

        // Create a title label
        Label titleLabel = new Label("Logout");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Create a message label
        Label messageLabel = new Label("Are you sure you want to logout?");
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setWrapText(true);

        // Create the logout button
        logoutButton = new Button("Logout");
        logoutButton.setPrefWidth(200);

        // Create the cancel button
        cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(200);

        // Create the error message label
        errorMessageLabel = new Label();
        errorMessageLabel.setTextAlignment(TextAlignment.CENTER);
        errorMessageLabel.setWrapText(true);
        errorMessageLabel.getStyleClass().add("error-label");

        // Create the progress indicator
        logoutProgressIndicator = new ProgressIndicator();
        logoutProgressIndicator.setVisible(false);

        // Add all components to the root VBox
        root.getChildren().addAll(
                titleLabel,
                messageLabel,
                logoutButton,
                cancelButton,
                errorMessageLabel,
                logoutProgressIndicator
        );

        // Set the root node
        getChildren().add(root);
    }

    @Override
    public boolean shouldShowAppBar() {
        return false;
    }

    @Override
    public void updateAppBar(AppBar appBar) {
        appBar.setVisible(false);
    }
}
