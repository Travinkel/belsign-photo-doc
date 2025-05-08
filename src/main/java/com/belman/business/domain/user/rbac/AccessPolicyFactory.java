package com.belman.business.domain.user.rbac;


import com.belman.business.domain.user.UserRole;

/**
 * Factory for creating common access policies.
 */
public class AccessPolicyFactory {

    /**
     * Creates an access policy that allows only administrators.
     * 
     * @return an access policy that allows only administrators
     */
    public AccessPolicy createAdminOnlyPolicy() {
        return new AccessPolicy(UserRole.ADMIN);
    }

    /**
     * Creates an access policy that allows only QA personnel.
     * 
     * @return an access policy that allows only QA personnel
     */
    public AccessPolicy createQAOnlyPolicy() {
        return new AccessPolicy(UserRole.QA);
    }

    /**
     * Creates an access policy that allows only production workers.
     * 
     * @return an access policy that allows only production workers
     */
    public AccessPolicy createProductionOnlyPolicy() {
        return new AccessPolicy(UserRole.PRODUCTION);
    }

    /**
     * Creates an access policy that allows QA personnel and administrators.
     * 
     * @return an access policy that allows QA personnel and administrators
     */
    public AccessPolicy createQAAndAdminPolicy() {
        return new AccessPolicy(UserRole.QA);
    }

    /**
     * Creates an access policy that allows production workers and QA personnel.
     * 
     * @return an access policy that allows production workers and QA personnel
     */
    public AccessPolicy createProductionAndQAPolicy() {
        return new AccessPolicy(UserRole.PRODUCTION);
    }

    /**
     * Creates an access policy that allows all roles.
     * 
     * @return an access policy that allows all roles
     */
    public AccessPolicy createAllRolesPolicy() {
        return new AccessPolicy(UserRole.PRODUCTION);
    }
}
