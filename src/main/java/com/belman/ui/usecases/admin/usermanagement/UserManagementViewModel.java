package com.belman.ui.usecases.admin.usermanagement;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.common.di.Inject;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.security.AuthenticationService;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.*;
import com.belman.ui.base.BaseViewModel;
import com.belman.ui.navigation.Router;
import com.belman.ui.usecases.authentication.login.LoginView;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.*;

/**
 * View model for the user management view.
 * Handles business logic for managing user accounts.
 */
public class UserManagementViewModel extends BaseViewModel<UserManagementViewModel> {

    // Properties for the user list
    private final ObservableList<UserBusiness> users = FXCollections.observableArrayList();
    private final FilteredList<UserBusiness> filteredUsers = new FilteredList<>(users);
    // Properties for the search filter
    private final StringProperty searchFilter = new SimpleStringProperty("");
    // Properties for the selected user
    private final ObjectProperty<UserBusiness> selectedUser = new SimpleObjectProperty<>();
    private final BooleanProperty userSelected = new SimpleBooleanProperty(false);
    // Properties for the user form
    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty firstName = new SimpleStringProperty("");
    private final StringProperty lastName = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final ObjectProperty<UserStatus> status = new SimpleObjectProperty<>(UserStatus.ACTIVE);
    private final BooleanProperty adminRole = new SimpleBooleanProperty(false);
    private final BooleanProperty qaRole = new SimpleBooleanProperty(false);
    private final BooleanProperty productionRole = new SimpleBooleanProperty(false);
    private final StringProperty password = new SimpleStringProperty("");
    // Property for error messages
    private final StringProperty errorMessage = new SimpleStringProperty("");
    @Inject
    private UserRepository userRepository;
    // Flag for new user creation
    private boolean isNewUser = false;

    @Override
    public void onShow() {
        // Load users when the view is shown
        loadUsers();
    }

    /**
     * Loads all users from the repository.
     */
    public void loadUsers() {
        try {
            List<UserBusiness> allUsers = userRepository.findAll();
            users.setAll(allUsers);
            filterUsers();
        } catch (Exception e) {
            errorMessage.set("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Filters the users based on the search filter.
     */
    public void filterUsers() {
        String filter = searchFilter.get().toLowerCase(Locale.ROOT);

        if (filter == null || filter.isEmpty()) {
            filteredUsers.setPredicate(user -> true);
        } else {
            filteredUsers.setPredicate(user -> {
                // Check if username contains filter
                if (user.getUsername().value().toLowerCase(Locale.ROOT).contains(filter)) {
                    return true;
                }

                // Check if first name contains filter
                if (user.getName() != null && user.getName().firstName().toLowerCase(Locale.ROOT).contains(filter)) {
                    return true;
                }

                // Check if last name contains filter
                if (user.getName() != null && user.getName().lastName().toLowerCase(Locale.ROOT).contains(filter)) {
                    return true;
                }

                // Check if email contains filter
                return user.getEmail() != null && user.getEmail().value().toLowerCase(Locale.ROOT).contains(filter);
            });
        }
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    /**
     * Creates a new user and prepares the form for input.
     */
    public void createNewUser() {
        // Clear the selection
        selectedUser.set(null);
        userSelected.set(true);
        isNewUser = true;

        // Clear form fields
        username.set("");
        firstName.set("");
        lastName.set("");
        email.set("");
        status.set(UserStatus.ACTIVE);
        adminRole.set(false);
        qaRole.set(false);
        productionRole.set(false);
        password.set("");

        // Clear error message
        errorMessage.set("");
    }

    /**
     * Saves the current user.
     *
     * @return true if the user was saved successfully, false otherwise
     */
    public boolean saveUser() {
        try {
            // Validate input
            if (username.get() == null || username.get().isEmpty()) {
                errorMessage.set("Username is required");
                return false;
            }

            if (email.get() == null || email.get().isEmpty()) {
                errorMessage.set("Email is required");
                return false;
            }

            if (isNewUser && (password.get() == null || password.get().isEmpty())) {
                errorMessage.set("Password is required for new users");
                return false;
            }

            // Create or update user
            UserBusiness user;
            if (isNewUser) {
                // Create new user
                Username usernameObj = new Username(username.get());
                EmailAddress emailObj = new EmailAddress(email.get());
                HashedPassword passwordObj = new HashedPassword(password.get());

                // Create a set of roles
                Set<UserRole> roles = new HashSet<>();
                if (adminRole.get()) roles.add(UserRole.ADMIN);
                if (qaRole.get()) roles.add(UserRole.QA);
                if (productionRole.get()) roles.add(UserRole.PRODUCTION);

                // Create the user
                UserId userId = new UserId(UUID.randomUUID().toString());
                PersonName personName = new PersonName(firstName.get(), lastName.get());

                // Use the builder pattern to create the user
                UserBusiness.Builder builder = new UserBusiness.Builder()
                        .id(userId)
                        .username(usernameObj)
                        .password(passwordObj)
                        .name(personName)
                        .email(emailObj);

                // Add roles
                for (UserRole role : roles) {
                    builder.addRole(role);
                }

                user = builder.build();
            } else {
                // Since UserBusiness doesn't have setter methods for most properties,
                // we need to create a new UserBusiness with the updated values
                UserId userId = selectedUser.get().getId();
                Username usernameObj = new Username(username.get());
                HashedPassword passwordObj = selectedUser.get().getPassword(); // Keep the existing password
                PersonName personName = new PersonName(firstName.get(), lastName.get());
                EmailAddress emailObj = new EmailAddress(email.get());

                // Create a set of roles
                Set<UserRole> roles = new HashSet<>();
                if (adminRole.get()) roles.add(UserRole.ADMIN);
                if (qaRole.get()) roles.add(UserRole.QA);
                if (productionRole.get()) roles.add(UserRole.PRODUCTION);

                // Use the builder pattern to create the user
                UserBusiness.Builder builder = new UserBusiness.Builder()
                        .id(userId)
                        .username(usernameObj)
                        .password(passwordObj)
                        .name(personName)
                        .email(emailObj);

                // Add roles
                for (UserRole role : roles) {
                    builder.addRole(role);
                }

                // Build the updated user
                user = builder.build();

                // Note: We can't update the password directly as there's no setPassword method
                // In a real application, we would need to create a new User object or use a
                // specialized service to update the password
            }

            // Save the user
            userRepository.save(user);

            // Reload users
            loadUsers();

            // Select the saved user
            selectUser(user);

            return true;
        } catch (Exception e) {
            errorMessage.set("Error saving user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Selects a user and populates the form fields.
     *
     * @param user the user to select
     */
    public void selectUser(UserBusiness user) {
        selectedUser.set(user);
        userSelected.set(true);
        isNewUser = false;

        // Populate form fields
        username.set(user.getUsername().value());
        if (user.getName() != null) {
            firstName.set(user.getName().firstName());
            lastName.set(user.getName().lastName());
        } else {
            firstName.set("");
            lastName.set("");
        }
        email.set(user.getEmail() != null ? user.getEmail().value() : "");

        // Convert ApprovalStatus to UserStatus
        ApprovalStatus approvalStatus = user.getApprovalState().getStatusEnum();
        if (approvalStatus == ApprovalStatus.APPROVED) {
            status.set(UserStatus.ACTIVE);
        } else if (approvalStatus == ApprovalStatus.REJECTED) {
            status.set(UserStatus.SUSPENDED);
        } else {
            status.set(UserStatus.PENDING);
        }

        // Set role checkboxes
        Set<UserRole> roles = user.getRoles();
        adminRole.set(roles.contains(UserRole.ADMIN));
        qaRole.set(roles.contains(UserRole.QA));
        productionRole.set(roles.contains(UserRole.PRODUCTION));

        // Clear password field
        password.set("");

        // Clear error message
        errorMessage.set("");
    }

    /**
     * Resets the password for the selected user.
     */
    public void resetPassword() {
        if (selectedUser.get() == null) {
            errorMessage.set("No user selected");
            return;
        }

        // Generate a random password
        String newPassword = generateRandomPassword();

        // Set the password field
        password.set(newPassword);

        // Show the password in the error message
        errorMessage.set("New password: " + newPassword);
    }

    /**
     * Generates a random password.
     *
     * @return a random password
     */
    private String generateRandomPassword() {
        // Generate a random password with 8 characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    // Getters for properties

    /**
     * Cancels the current edit operation.
     */
    public void cancelEdit() {
        if (selectedUser.get() != null) {
            // Reselect the user to reset the form
            selectUser(selectedUser.get());
        } else {
            // Clear the form
            userSelected.set(false);
            isNewUser = false;

            // Clear form fields
            username.set("");
            firstName.set("");
            lastName.set("");
            email.set("");
            status.set(UserStatus.ACTIVE);
            adminRole.set(false);
            qaRole.set(false);
            productionRole.set(false);
            password.set("");

            // Clear error message
            errorMessage.set("");
        }
    }

    public ObservableList<UserBusiness> getUsersProperty() {
        return filteredUsers;
    }

    public StringProperty searchFilterProperty() {
        return searchFilter;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty emailProperty() {
        return email;
    }

    public ObjectProperty<UserStatus> statusProperty() {
        return status;
    }

    public BooleanProperty adminRoleProperty() {
        return adminRole;
    }

    public BooleanProperty qaRoleProperty() {
        return qaRole;
    }

    public BooleanProperty productionRoleProperty() {
        return productionRole;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public BooleanProperty userSelectedProperty() {
        return userSelected;
    }

    /**
     * Logs out the current user and navigates to the login view.
     */
    public void logout() {
        try {
            // Get the AuthenticationService instance
            AuthenticationService authService = ServiceLocator.getService(AuthenticationService.class);

            // Log out the user
            if (authService != null) {
                authService.logout();
            }

            // Navigate to the login view
            Router.navigateTo(LoginView.class);
        } catch (Exception e) {
            errorMessage.set("Error logging out: " + e.getMessage());
        }
    }
}