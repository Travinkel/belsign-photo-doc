package com.belman.presentation.usecases.login;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller for the user selection dialog.
 * This dialog shows a list of available users for development purposes.
 */
public class UserSelectionDialogController implements Initializable {

    @FXML
    private ListView<UserBusiness> activeUsersList;

    @FXML
    private ListView<UserBusiness> inactiveUsersList;

    @FXML
    private Button closeButton;

    private UserRepository userRepository;
    private LoginViewController loginViewController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Get the user repository from the service locator
            userRepository = ServiceLocator.getService(UserRepository.class);

            // Set up the list views
            setupListViews();

            // Load the users
            loadUsers();
        } catch (Exception e) {
            System.err.println("Error initializing UserSelectionDialogController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets up the list views with custom cell factories.
     */
    private void setupListViews() {
        // Set up the active users list view
        activeUsersList.setCellFactory(param -> new UserListCell());
        activeUsersList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                UserBusiness selectedUser = activeUsersList.getSelectionModel().getSelectedItem();
                if (selectedUser != null && loginViewController != null) {
                    loginViewController.setCredentials(selectedUser.getUsername().toString(), "password");
                    closeDialog();
                }
            }
        });

        // Set up the inactive users list view
        inactiveUsersList.setCellFactory(param -> new UserListCell());
        inactiveUsersList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                UserBusiness selectedUser = inactiveUsersList.getSelectionModel().getSelectedItem();
                if (selectedUser != null && loginViewController != null) {
                    loginViewController.setCredentials(selectedUser.getUsername().toString(), "password");
                    closeDialog();
                }
            }
        });
    }

    /**
     * Loads the users from the repository and populates the list views.
     */
    private void loadUsers() {
        try {
            // Get all users from the repository
            List<UserBusiness> allUsers = userRepository.findAll();

            // Filter active and inactive users
            List<UserBusiness> activeUsers = allUsers.stream()
                    .filter(user -> user.getStatus() == com.belman.domain.user.UserStatus.ACTIVE)
                    .collect(Collectors.toList());

            List<UserBusiness> inactiveUsers = allUsers.stream()
                    .filter(user -> user.getStatus() != com.belman.domain.user.UserStatus.ACTIVE)
                    .collect(Collectors.toList());

            // Update the list views
            activeUsersList.setItems(FXCollections.observableArrayList(activeUsers));
            inactiveUsersList.setItems(FXCollections.observableArrayList(inactiveUsers));
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the login view controller to allow communication between the dialog and the login view.
     *
     * @param controller the login view controller
     */
    public void setLoginViewController(LoginViewController controller) {
        this.loginViewController = controller;
    }

    /**
     * Handles the close button action.
     */
    @FXML
    private void handleClose() {
        closeDialog();
    }

    /**
     * Closes the dialog.
     */
    private void closeDialog() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Custom list cell for displaying users.
     */
    private static class UserListCell extends ListCell<UserBusiness> {
        @Override
        protected void updateItem(UserBusiness user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                // Display username and role (get first role if any)
                String roleText = user.getRoles().isEmpty() ? "No Role" : user.getRoles().iterator().next().toString();
                setText(user.getUsername() + " (" + roleText + ")");

                // Set style based on user status
                if (user.getStatus() == com.belman.domain.user.UserStatus.ACTIVE) {
                    setStyle("-fx-text-fill: -belman-blue;");
                } else {
                    setStyle("-fx-text-fill: -belman-grey-50;");
                }
            }
        }
    }
}