package com.belman.domain.user;

/**
 * Defines the possible roles a user can have in the system.
 * Roles determine what actions a user is authorized to perform.
 *
 * <ul>
 *   <li>PRODUCTION: Users who can upload photos and create orders</li>
 *   <li>QA: Quality Assurance users who can review and approve/reject photos</li>
 *   <li>ADMIN: Administrators who can manage users and system settings</li>
 * </ul>
 * <p>
 * Users can have multiple roles simultaneously, allowing for flexible permission management.
 */
public enum UserRole {
    /**
     * Production workers who can upload photos and create orders.
     * This role is typically assigned to users working in the production area.
     */
    PRODUCTION,

    /**
     * Quality Assurance personnel who can review and approve/reject photos.
     * This role is responsible for ensuring the quality of photo documentation.
     */
    QA,

    /**
     * Administrators who can manage users and system settings.
     * This role has the highest level of access and can perform all operations.
     */
    ADMIN
}