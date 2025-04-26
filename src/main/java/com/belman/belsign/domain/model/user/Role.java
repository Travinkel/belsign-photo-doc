package com.belman.belsign.domain.model.user;

public enum Role {
    USER,
    ADMIN,
    SUPER_ADMIN;

    public static Role fromString(String role) {
        return Role.valueOf(role.toUpperCase());
    }
}
