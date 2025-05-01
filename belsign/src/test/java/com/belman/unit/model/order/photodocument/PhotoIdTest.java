package com.belman.unit.model.order.photodocument;

import com.belman.domain.valueobjects.PhotoId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PhotoIdTest {

    @Test
    void constructorShouldAcceptValidUUID() {
        UUID uuid = UUID.randomUUID();
        PhotoId photoId = new PhotoId(uuid);
        assertEquals(uuid, photoId.value());
    }

    @Test
    void constructorShouldRejectNullUUID() {
        assertThrows(NullPointerException.class, () -> new PhotoId(null));
    }

    @Test
    void newIdShouldCreateRandomId() {
        PhotoId id1 = PhotoId.newId();
        PhotoId id2 = PhotoId.newId();

        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void equalPhotoIdsShouldBeEqual() {
        UUID uuid = UUID.randomUUID();
        PhotoId id1 = new PhotoId(uuid);
        PhotoId id2 = new PhotoId(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void differentPhotoIdsShouldNotBeEqual() {
        PhotoId id1 = new PhotoId(UUID.randomUUID());
        PhotoId id2 = new PhotoId(UUID.randomUUID());

        assertNotEquals(id1, id2);
    }
}
