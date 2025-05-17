package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserRole;
import com.belman.application.usecase.admin.AdminService;

import java.util.concurrent.CompletableFuture;

/**
 * Command for creating a new user.
 * <p>
 * This command uses the AdminService to create a new user with the specified details.
 */
public class CreateUserCommand implements Command<UserBusiness> {
    
    @Inject
    private AdminService adminService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final UserRole[] roles;
    private UserBusiness createdUser;
    
    /**
     * Creates a new CreateUserCommand with the specified user details.
     *
     * @param username  the username
     * @param password  the password
     * @param firstName the first name
     * @param lastName  the last name
     * @param email     the email address
     * @param roles     the roles to assign to the user
     */
    public CreateUserCommand(String username, String password, String firstName, String lastName, String email,
                             UserRole[] roles) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;
    }
    
    @Override
    public CompletableFuture<UserBusiness> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username cannot be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank");
            }
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email cannot be null or blank");
            }
            
            // Create the user
            createdUser = adminService.createUser(username, password, firstName, lastName, email, roles);
            
            return createdUser;
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo: no user was created"));
        }
        
        return CompletableFuture.runAsync(() -> {
            // Delete the user
            boolean deleted = adminService.deleteUser(createdUser.getId());
            if (!deleted) {
                throw new RuntimeException("Failed to delete user: " + createdUser.getId().id());
            }
        });
    }
    
    @Override
    public boolean canUndo() {
        return createdUser != null;
    }
    
    @Override
    public String getDescription() {
        return "Create user: " + username;
    }
}