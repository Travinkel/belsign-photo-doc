package com.belman.domain.rbac;

import com.belman.presentation.core.BaseService;
import com.belman.domain.aggregates.User.Role;

/**
 * Factory for creating common access policies.
 * This class is Gluon-aware and uses the backbone framework.
 */
public class AccessPolicyFactory extends BaseService {
    
    /**
     * Creates an access policy that allows only administrators.
     * 
     * @return an access policy that allows only administrators
     */
    public AccessPolicy createAdminOnlyPolicy() {
        return new AccessPolicy(Role.ADMIN);
    }
    
    /**
     * Creates an access policy that allows only QA personnel.
     * 
     * @return an access policy that allows only QA personnel
     */
    public AccessPolicy createQAOnlyPolicy() {
        return new AccessPolicy(Role.QA);
    }
    
    /**
     * Creates an access policy that allows only production workers.
     * 
     * @return an access policy that allows only production workers
     */
    public AccessPolicy createProductionOnlyPolicy() {
        return new AccessPolicy(Role.PRODUCTION);
    }
    
    /**
     * Creates an access policy that allows QA personnel and administrators.
     * 
     * @return an access policy that allows QA personnel and administrators
     */
    public AccessPolicy createQAAndAdminPolicy() {
        return new AccessPolicy(Role.QA, Role.ADMIN);
    }
    
    /**
     * Creates an access policy that allows production workers and QA personnel.
     * 
     * @return an access policy that allows production workers and QA personnel
     */
    public AccessPolicy createProductionAndQAPolicy() {
        return new AccessPolicy(Role.PRODUCTION, Role.QA);
    }
    
    /**
     * Creates an access policy that allows all roles.
     * 
     * @return an access policy that allows all roles
     */
    public AccessPolicy createAllRolesPolicy() {
        return new AccessPolicy(Role.PRODUCTION, Role.QA, Role.ADMIN);
    }
}