package com.belman.presentation.usecases.admin.components;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.domain.user.Username;
import com.belman.application.usecase.security.BCryptPasswordHasher;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.HashSet;
import java.util.Set;

/**
 * A dialog for creating and editing users.
 * This dialog provides a form for entering user details and handles validation.
 */
public class UserDialog {

    private final Stage dialogStage;
    private final Label titleLabel;
    private final Label errorLabel;
    private final Button saveButton;
    private final Button cancelButton;
    private final HBox buttonBox;
    
    // Form fields
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final TextField emailField;
    private final ComboBox<UserRole> roleComboBox;
    private final TextField firstNameField;
    private final TextField lastNameField;
    private final TextField phoneNumberField;
    private final TextField nfcIdField;
    
    private UserBusiness result = null;
    private final UserBusiness existingUser;
    private final PasswordHasher passwordHasher;

    /**
     * Creates a new UserDialog for creating a new user.
     *
     * @param title the dialog title
     */
    public UserDialog(String title) {
        this(title, null);
    }

    /**
     * Creates a new UserDialog for editing an existing user.
     *
     * @param title the dialog title
     * @param user the user to edit, or null if creating a new user
     */
    public UserDialog(String title, UserBusiness user) {
        this.existingUser = user;
        this.passwordHasher = new BCryptPasswordHasher();
        
        // Create the dialog stage
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setResizable(false);

        // Create the title label
        titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextAlignment(TextAlignment.CENTER);
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setPadding(new Insets(15));
        titleLabel.setStyle("-fx-background-color: #004b88; -fx-text-fill: white;");

        // Create the form fields
        usernameField = new TextField();
        usernameField.setPromptText("Username (min 3 characters)");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        
        emailField = new TextField();
        emailField.setPromptText("Email address");
        
        roleComboBox = new ComboBox<>(FXCollections.observableArrayList(UserRole.values()));
        roleComboBox.setPromptText("Select role");
        
        firstNameField = new TextField();
        firstNameField.setPromptText("First name (optional)");
        
        lastNameField = new TextField();
        lastNameField.setPromptText("Last name (optional)");
        
        phoneNumberField = new TextField();
        phoneNumberField.setPromptText("Phone number (optional)");
        
        nfcIdField = new TextField();
        nfcIdField.setPromptText("NFC ID (optional, for production workers)");
        
        // If editing an existing user, populate the fields
        if (existingUser != null) {
            usernameField.setText(existingUser.getUsername().value());
            usernameField.setDisable(true); // Username cannot be changed
            
            emailField.setText(existingUser.getEmail().value());
            
            if (existingUser.getRoles().size() > 0) {
                roleComboBox.setValue(existingUser.getRoles().iterator().next());
            }
            
            if (existingUser.getName() != null) {
                firstNameField.setText(existingUser.getName().firstName());
                lastNameField.setText(existingUser.getName().lastName());
            }
            
            if (existingUser.getPhoneNumber() != null) {
                phoneNumberField.setText(existingUser.getPhoneNumber().value());
            }
            
            if (existingUser.getNfcId() != null) {
                nfcIdField.setText(existingUser.getNfcId());
            }
        }
        
        // Create the form layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(20));
        
        // Add form fields to the grid
        int row = 0;
        formGrid.add(new Label("Username:"), 0, row);
        formGrid.add(usernameField, 1, row);
        
        row++;
        formGrid.add(new Label("Password:"), 0, row);
        formGrid.add(passwordField, 1, row);
        
        row++;
        formGrid.add(new Label("Email:"), 0, row);
        formGrid.add(emailField, 1, row);
        
        row++;
        formGrid.add(new Label("Role:"), 0, row);
        formGrid.add(roleComboBox, 1, row);
        
        row++;
        formGrid.add(new Label("First Name:"), 0, row);
        formGrid.add(firstNameField, 1, row);
        
        row++;
        formGrid.add(new Label("Last Name:"), 0, row);
        formGrid.add(lastNameField, 1, row);
        
        row++;
        formGrid.add(new Label("Phone Number:"), 0, row);
        formGrid.add(phoneNumberField, 1, row);
        
        row++;
        formGrid.add(new Label("NFC ID:"), 0, row);
        formGrid.add(nfcIdField, 1, row);
        
        // Create the error label
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setPadding(new Insets(10, 20, 0, 20));
        
        // Create the content box
        VBox contentBox = new VBox();
        contentBox.getChildren().addAll(formGrid, errorLabel);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setStyle("-fx-background-color: white;");
        
        // Create the buttons
        saveButton = new Button("Save");
        saveButton.setMinWidth(120);
        saveButton.setMinHeight(48);
        saveButton.setFont(Font.font("System", 16));
        saveButton.setOnAction(e -> handleSave());
        
        cancelButton = new Button("Cancel");
        cancelButton.setMinWidth(120);
        cancelButton.setMinHeight(48);
        cancelButton.setFont(Font.font("System", 16));
        cancelButton.setOnAction(e -> dialogStage.close());
        
        // Create the button box
        buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(cancelButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(15));
        buttonBox.setStyle("-fx-background-color: #f2f2f2;");
        
        // Create the main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(titleLabel);
        mainLayout.setCenter(contentBox);
        mainLayout.setBottom(buttonBox);
        mainLayout.setMinWidth(450);
        mainLayout.setMinHeight(500);
        
        // Create the scene
        Scene scene = new Scene(mainLayout);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);
    }
    
    /**
     * Handles the save button click.
     * Validates the form fields and creates or updates the user.
     */
    private void handleSave() {
        try {
            // Validate required fields
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String email = emailField.getText().trim();
            UserRole role = roleComboBox.getValue();
            
            if (username.isEmpty()) {
                errorLabel.setText("Username is required");
                return;
            }
            
            if (existingUser == null && password.isEmpty()) {
                errorLabel.setText("Password is required for new users");
                return;
            }
            
            if (email.isEmpty()) {
                errorLabel.setText("Email is required");
                return;
            }
            
            if (role == null) {
                errorLabel.setText("Role is required");
                return;
            }
            
            // Create value objects
            Username usernameObj;
            try {
                usernameObj = new Username(username);
            } catch (IllegalArgumentException e) {
                errorLabel.setText("Invalid username: " + e.getMessage());
                return;
            }
            
            EmailAddress emailObj;
            try {
                emailObj = new EmailAddress(email);
            } catch (IllegalArgumentException e) {
                errorLabel.setText("Invalid email: " + e.getMessage());
                return;
            }
            
            // Optional fields
            PersonName nameObj = null;
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            if (!firstName.isEmpty() && !lastName.isEmpty()) {
                try {
                    nameObj = new PersonName(firstName, lastName);
                } catch (IllegalArgumentException e) {
                    errorLabel.setText("Invalid name: " + e.getMessage());
                    return;
                }
            }
            
            PhoneNumber phoneObj = null;
            String phone = phoneNumberField.getText().trim();
            if (!phone.isEmpty()) {
                try {
                    phoneObj = new PhoneNumber(phone);
                } catch (IllegalArgumentException e) {
                    errorLabel.setText("Invalid phone number: " + e.getMessage());
                    return;
                }
            }
            
            String nfcId = nfcIdField.getText().trim();
            if (nfcId.isEmpty()) {
                nfcId = null;
            }
            
            // Create or update the user
            if (existingUser == null) {
                // Create a new user
                HashedPassword hashedPassword = HashedPassword.fromPlainText(password, passwordHasher);
                
                UserBusiness.Builder builder = new UserBusiness.Builder()
                        .id(UserId.newId())
                        .username(usernameObj)
                        .password(hashedPassword)
                        .email(emailObj)
                        .addRole(role);
                
                if (nameObj != null) {
                    builder.name(nameObj);
                }
                
                if (phoneObj != null) {
                    builder.phoneNumber(phoneObj);
                }
                
                if (nfcId != null) {
                    builder.nfcId(nfcId);
                }
                
                result = builder.build();
            } else {
                // Update the existing user
                UserBusiness.Builder builder = new UserBusiness.Builder()
                        .id(existingUser.getId())
                        .username(usernameObj)
                        .password(existingUser.getPassword())
                        .email(emailObj)
                        .approvalState(existingUser.getApprovalState())
                        .addRole(role);
                
                // Update password if provided
                if (!password.isEmpty()) {
                    HashedPassword hashedPassword = HashedPassword.fromPlainText(password, passwordHasher);
                    builder.password(hashedPassword);
                }
                
                if (nameObj != null) {
                    builder.name(nameObj);
                }
                
                if (phoneObj != null) {
                    builder.phoneNumber(phoneObj);
                }
                
                if (nfcId != null) {
                    builder.nfcId(nfcId);
                }
                
                result = builder.build();
            }
            
            // Close the dialog
            dialogStage.close();
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }
    
    /**
     * Shows the dialog and waits for the user to close it.
     *
     * @return the created or updated user, or null if the dialog was cancelled
     */
    public UserBusiness showAndWait() {
        dialogStage.showAndWait();
        return result;
    }
}