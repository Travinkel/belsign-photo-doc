package com.belman.belsign.domain.model.user;

public class User {
    private final UUID
            userId;
    private final Username username;
    private final Role
            role;

    public User(UUID userId, Username username, Role role) {
        this.userId =
                userId;
        this.username = username;
        this.role = role;
    }


    public UUID getUserId() {
        return userId;
    }
    public Username getUsername() {
        return username;
    }
    public Role getRole() {
        return role;
    }
}
