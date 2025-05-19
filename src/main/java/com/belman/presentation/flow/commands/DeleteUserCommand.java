package com.belman.presentation.flow.commands;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.application.usecase.admin.AdminService;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Command for deleting a user.
 * <p>
 * This command uses the AdminService to delete a user with the specified ID.
 */
public class DeleteUserCommand implements Command<Boolean> {
    
    @Inject
    private AdminService adminService;
    
    @Inject
    private com.belman.domain.user.UserRepository userRepository;
    
    @Inject
    private SessionContext sessionContext;
    
    private final UserId userId;
    private UserBusiness deletedUser;
    
    /**
     * Creates a new DeleteUserCommand with the specified user ID.
     *
     * @param userId the ID of the user to delete
     */
    public DeleteUserCommand(UserId userId) {
        this.userId = userId;
    }
    
    @Override
    public CompletableFuture<Boolean> execute() {
        return CompletableFuture.supplyAsync(() -> {
            // Validate required parameters
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }
            
            // Check if the user is the currently logged-in user
            Optional<UserBusiness> currentUser = sessionContext.getUser();
            if (currentUser.isPresent() && currentUser.get().getId().equals(userId)) {
                throw new IllegalArgumentException("Cannot delete the currently logged-in user");
            }
            
            // Find the user to store for undo
            Optional<UserBusiness> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User not found: " + userId.id());
            }
            
            // Store the user for undo
            deletedUser = userOpt.get();
            
            // Delete the user
            boolean deleted = adminService.deleteUser(userId);
            
            if (!deleted) {
                throw new RuntimeException("Failed to delete user: " + userId.id());
            }
            
            return deleted;
        });
    }
    
    @Override
    public CompletableFuture<Void> undo() {
        if (!canUndo()) {
            return CompletableFuture.failedFuture(
                    new UnsupportedOperationException("Cannot undo: no user was deleted"));
        }
        
        return CompletableFuture.runAsync(() -> {
            // Restore the deleted user
            userRepository.save(deletedUser);
        });
    }
    
    @Override
    public boolean canUndo() {
        return deletedUser != null;
    }
    
    @Override
    public String getDescription() {
        return "Delete user: " + (deletedUser != null ? deletedUser.getUsername().value() : userId.id());
    }
}