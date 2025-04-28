package com.belman.belsign.domain.model.shared;

import java.time.Instant;
import java.util.Objects;

public record Timestamp(Instant value) {

    public Timestamp {
        Objects.requireNonNull(value, "Timestamp must not be null");
    }

    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
