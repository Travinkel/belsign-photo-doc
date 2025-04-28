package com.belman.belsign.domain.model.order.photodocument;

import java.util.Objects;
import java.util.UUID;

public class PhotoId {

    private final UUID value;

    public PhotoId(UUID value) {
        this.value = value;
    }

    public PhotoId() {
        this(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhotoId)) return false;
        PhotoId photoId = (PhotoId) o;
        return value.equals(photoId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
