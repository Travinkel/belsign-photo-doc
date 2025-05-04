package com.belman.domain.rbac;

import com.belman.application.core.BaseService;
import com.belman.domain.aggregates.User;
import com.belman.domain.services.AuthenticationService;

import java.util.Optional;

/**
 * Manager for role-based access control.
 * This class is responsible for checking if users have the required roles to access certain features.
 */
public class RoleBasedAccessManager extends BaseService {
    
    private final AuthenticationService authenticationService;
    private final AccessPolicy accessPolicy;
    
    /**
     * Creates a new RoleBasedAccessManager with the specified authentication service and access policy.
     * 
     * @param authenticationService the authentication service
     * @param accessPolicy the access policy
     */
    public RoleBasedAccessManager(AuthenticationService authenticationService, AccessPolicy accessPolicy) {
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
     * Gets the access policy used by this manager.
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