package com.belman.presentation.base;

/**
 * Interface for view models that support logout functionality.
 * This interface should be implemented by view models that need to handle user logout.
 */
public interface LogoutCapable {
    
    /**
     * Logs out the current user.
     * Implementations should handle the logout process, including:
     * - Clearing user session data
     * - Navigating to the login screen
     * - Any other cleanup required
     */
    void logout();
}