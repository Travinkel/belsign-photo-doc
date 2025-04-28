package com.belman.belsign.domain.model.user;

/**
 * This enum has been deprecated and replaced by User.Role.
 * Use User.Role instead for all role-related functionality.
 * @deprecated Use {@link User.Role} instead
 */
@Deprecated
public enum Role {
    /**
     * @deprecated Use {@link User.Role#PRODUCTION} instead
     */
    @Deprecated
    USER,

    /**
     * @deprecated Use {@link User.Role#ADMIN} instead
     */
    @Deprecated
    ADMIN,

    /**
     * @deprecated Use {@link User.Role#ADMIN} instead
     */
    @Deprecated
    SUPER_ADMIN;

    /**
     * @deprecated Use {@link User.Role#valueOf(String)} instead
     */
    @Deprecated
    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
