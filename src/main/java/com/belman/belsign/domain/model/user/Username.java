package com.belman.belsign.domain.model.user;

import java.util.Objects;

public record Username(String value) {
    public Username {
        Objects.requireNonNull(value, "Username cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
