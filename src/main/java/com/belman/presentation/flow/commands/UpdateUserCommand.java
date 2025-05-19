package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.application.usecase.admin.AdminService;
import com.belman.application.usecase.security.BCryptPasswordHasher;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Command for updating an existing user.
 * <p>
 * This command updates a user's details using the AdminService and UserRepository.
 */
public class UpdateUserCommand implements Command<UserBusiness> {
    
    @Inject
    private AdminService adminService;
    
    @Inject
    private com.belman.domain.user.UserRepository userRepository;
    
    private final UserId userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String nfcId;
    private final String password;
    private final UserRole role;
    
    private UserBusiness originalUser;
    private UserBusiness updatedUser;
    private final PasswordHasher passwordHasher;
    
    /**
     * Creates a new UpdateUserCommand with the specified user details.
     *
     * @param userId      the ID of the user to update
     * @param email       the new email address
     * @param firstName   the new first name (optional)
     * @param lastName    the new last name (optional)
     * @param phoneNumber the new phone number (optional)
     * @param nfcId       the new NFC ID (optional)
     * @param password    the new password (optional, if null or empty, the password will not be changed)
     * @param role        the new role (if null, the role will not be changed)
     */
    public UpdateUserCommand(UserId userId, String email, String firstName, String lastName, 
                             String phoneNumber, String nfcId, String password, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.nfcId = nfcId;
        this.password = password;
        this.role = role;
        this.passwordHasher = new BCryptPasswordHasher();
    }
    
    @Override
    public CompletableFuture<UserBusiness> execute() {
        return CompletableFuture.supplyAsync(() -> {
            // Validate required parameters
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or blank");
            }
            
            // Find the user
            Optional<UserBusiness> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User not found: " + userId.id());
            }
            
            // Store the original user for undo
            originalUser = userOpt.get();
            
            // Create value objects
            EmailAddress emailObj = new EmailAddress(email);
            
            PersonName nameObj = null;
            if (firstName != null && !firstName.isBlank() && lastName != null && !lastName.isBlank()) {
                nameObj = new PersonName(firstName, lastName);
            } else if (originalUser.getName() != null) {
                nameObj = originalUser.getName();
            }
            
            PhoneNumber phoneObj = null;
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                phoneObj = new PhoneNumber(phoneNumber);
            } else if (originalUser.getPhoneNumber() != null) {
                phoneObj = originalUser.getPhoneNumber();
            }
            
            // Create a builder with the original user's data
            UserBusiness.Builder builder = new UserBusiness.Builder()
                    .id(originalUser.getId())
                    .username(originalUser.getUsername())
                    .password(originalUser.getPassword())
                    .email(emailObj)
                    .approvalState(originalUser.getApprovalState());
            
            // Set the name if provided or if the original user had one
            if (nameObj != null) {
                builder.name(nameObj);
            }
            
            // Set the phone number if provided or if the original user had one
            if (phoneObj != null) {
                builder.phoneNumber(phoneObj);
            }
            
            // Set the NFC ID if provided or if the original user had one
            if (nfcId != null && !nfcId.isBlank()) {
                builder.nfcId(nfcId);
            } else if (originalUser.getNfcId() != null) {
                builder.nfcId(originalUser.getNfcId());
            }
            
            // Update the password if provided
            if (password != null && !password.isBlank()) {
                HashedPassword hashedPassword = HashedPassword.fromPlainText(password, passwordHasher);
                builder.password(hashedPassword);
            }
            
            // Add roles
            if (role != null) {
                // If a specific role is provided, use only that role
                builder.addRole(role);
            } else {
                // Otherwise, keep the original roles
                for (UserRole existingRole : originalUser.getRoles()) {
                    builder.addRole(existingRole);
                }
            }
            
            // Build the updated user
            updatedUser = builder.build();
            
            // Save the updated user
            userRepository.save(updatedUser);
            
            return updatedUser;
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo: no user was updated"));
        }
        
        return CompletableFuture.runAsync(() -> {
            // Restore the original user
            userRepository.save(originalUser);
        });
    }
    
    @Override
    public boolean canUndo() {
        return originalUser != null && updatedUser != null;
    }
    
    @Override
    public String getDescription() {
        return "Update user: " + (originalUser != null ? originalUser.getUsername().value() : userId.id());
    }
}