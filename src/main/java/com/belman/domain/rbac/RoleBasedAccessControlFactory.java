package com.belman.domain.rbac;

import com.belman.domain.services.AuthenticationService;

/**
 * Factory for creating role-based access control components.
 */
public class RoleBasedAccessControlFactory {

    private final AuthenticationService authenticationService;
    private final AccessPolicyFactory accessPolicyFactory;

    /**
     * Creates a new RoleBasedAccessControlFactory with the specified services.
     * 
     * @param authenticationService the authentication service
     * @param accessPolicyFactory the access policy factory
     */
    public RoleBasedAccessControlFactory(AuthenticationService authenticationService, AccessPolicyFactory accessPolicyFactory) {
        this.authenticationService = authenticationService;
        this.accessPolicyFactory = accessPolicyFactory;
    }

    /**
     * Creates a role-based access controller for admin-only operations.
     * 
     * @return a role-based access controller for admin-only operations
     */
    public RoleBasedAccessController createAdminAccessController() {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicyFactory.createAdminOnlyPolicy()
        );
    }

    /**
     * Creates a role-based access controller for QA-only operations.
     * 
     * @return a role-based access controller for QA-only operations
     */
    public RoleBasedAccessController createQAAccessController() {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicyFactory.createQAOnlyPolicy()
        );
    }

    /**
     * Creates a role-based access controller for production-only operations.
     * 
     * @return a role-based access controller for production-only operations
     */
    public RoleBasedAccessController createProductionAccessController() {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicyFactory.createProductionOnlyPolicy()
        );
    }

    /**
     * Creates a role-based access controller for QA and admin operations.
     * 
     * @return a role-based access controller for QA and admin operations
     */
    public RoleBasedAccessController createQAAndAdminAccessController() {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicyFactory.createQAAndAdminPolicy()
        );
    }

    /**
     * Creates a role-based access controller for production and QA operations.
     * 
     * @return a role-based access controller for production and QA operations
     */
    public RoleBasedAccessController createProductionAndQAAccessController() {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicyFactory.createProductionAndQAPolicy()
        );
    }

    /**
     * Creates a role-based access controller for operations accessible to all roles.
     * 
     * @return a role-based access controller for operations accessible to all roles
     */
    public RoleBasedAccessController createAllRolesAccessController() {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicyFactory.createAllRolesPolicy()
        );
    }

    /**
     * Creates a role-based access controller with a custom access policy.
     * 
     * @param accessPolicy the custom access policy
     * @return a role-based access controller with the specified access policy
     */
    public RoleBasedAccessController createCustomAccessController(AccessPolicy accessPolicy) {
        return new RoleBasedAccessController(
            authenticationService,
            accessPolicy
        );
    }
}
