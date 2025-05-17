package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserRole;
import com.belman.application.usecase.admin.AdminService;

import java.util.concurrent.CompletableFuture;

/**
 * Command for assigning a role to a user.
 * <p>
 * This command uses the AdminService to assign a role to a user.
 */
public class AssignRoleCommand implements Command<Boolean> {
    
    @Inject
    private AdminService adminService;
    
    @Inject
    private SessionContext sessionContext;
    
    private final UserId userId;
    private final UserRole role;
    
    /**
     * Creates a new AssignRoleCommand for the specified user and role.
     *
     * @param userId the ID of the user to assign the role to
     * @param role   the role to assign
     */
    public AssignRoleCommand(UserId userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
    
    @Override
    public CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            if (role == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }
            
            // Assign the role to the user
            return adminService.assignRole(userId, role);
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo: role assignment cannot be undone"));
        }
        
        return CompletableFuture.runAsync(() -> {
            // Remove the role from the user
            boolean removed = adminService.removeRole(userId, role);
            if (!removed) {
                throw new RuntimeException("Failed to remove role: " + role + " from user: " + userId.id());
            }
        });
    }
    
    @Override
    public boolean canUndo() {
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Assign role: " + role + " to user: " + userId.id();
    }
}