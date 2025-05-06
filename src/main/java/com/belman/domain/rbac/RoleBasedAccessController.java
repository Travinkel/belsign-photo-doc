package com.belman.domain.rbac;

import com.belman.domain.aggregates.User;
import com.belman.domain.exceptions.AccessDeniedException;
import com.belman.domain.security.AuthenticationService;

import java.util.Optional;

/**
 * Controller for role-based access control.
 */
public class RoleBasedAccessController {

    private final AuthenticationService authenticationService;
    private final AccessPolicy accessPolicy;

    /**
     * Creates a new RoleBasedAccessController with the specified authentication service and access policy.
     * 
     * @param authenticationService the authentication service
     * @param accessPolicy the access policy
     */
    public RoleBasedAccessController(AuthenticationService authenticationService, AccessPolicy accessPolicy) {
        this.authenticationService = authenticationService;
        this.accessPolicy = accessPolicy;
    }

    /**
     * Checks if the current user has access based on the access policy.
     * 
     * @return true if the current user has access, false otherwise
     */
    public boolean hasAccess() {
        Optional<User> currentUser = authenticationService.getCurrentUser();
        return currentUser.isPresent() && accessPolicy.hasAccess(currentUser.get());
    }

    /**
     * Checks if the specified user has access based on the access policy.
     * 
     * @param user the user to check
     * @return true if the user has access, false otherwise
     */
    public boolean hasAccess(User user) {
        return user != null && accessPolicy.hasAccess(user);
    }

    /**
     * Gets the access policy used by this controller.
     * 
     * @return the access policy
     */
    public AccessPolicy getAccessPolicy() {
        return accessPolicy;
    }

    /**
     * Throws an exception if the current user does not have access.
     * 
     * @throws AccessDeniedException if the current user does not have access
     */
    public void checkAccess() throws AccessDeniedException {
        if (!hasAccess()) {
            throw new AccessDeniedException("Access denied. User does not have the required role.");
        }
    }

    /**
     * Throws an exception if the specified user does not have access.
     * 
     * @param user the user to check
     * @throws AccessDeniedException if the user does not have access
     */
    public void checkAccess(User user) throws AccessDeniedException {
        if (!hasAccess(user)) {
            throw new AccessDeniedException("Access denied. User does not have the required role.");
        }
    }
}
