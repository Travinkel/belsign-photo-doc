package com.belman.business.domain.rbac;


import com.belman.business.domain.user.UserAggregate;
import com.belman.business.domain.user.UserRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines access policies based on user roles.
 * This class is used to check if a user has the required roles to perform an action.
 */
public class AccessPolicy {
    private final Set<UserRole> allowedRoles;

    /**
     * Creates a new AccessPolicy with the specified allowed roles.
     *
     * @param roles the roles that are allowed to access the resource
     */
    public AccessPolicy(UserRole... roles) {
        this.allowedRoles = roles.length > 0 ?
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList(roles))) : 
            Collections.emptySet();
    }

    /**
     * Checks if the user has any of the roles required by this policy.
     * 
     * @param user the user to check
     * @return true if the user has any of the required roles, false otherwise
     */
    public boolean hasAccess(UserAggregate user) {
        if (user == null) {
            return false;
        }

        // If no roles are specified, no access is granted
        if (allowedRoles.isEmpty()) {
            return false;
        }

        // Check if the user has any of the allowed roles
        Set<UserRole> userRoles = user.getRoles();
        for (UserRole role : allowedRoles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the roles allowed by this policy.
     * 
     * @return an unmodifiable set of the allowed roles
     */
    public Set<UserRole> getAllowedRoles() {
        return allowedRoles;
    }
}
