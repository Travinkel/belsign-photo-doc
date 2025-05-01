package com.belman.unit.model.user;

import com.belman.domain.valueobjects.EmailAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailAddressTest {

    @Test
    void validEmailShouldBeCreated() {
        EmailAddress email = new EmailAddress("worker@belman.dk");
        assertEquals("worker@belman.dk", email.getValue());
    }

    @Test
    void invalidEmailShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress("invalid-email"));
    }

    @Test
    void nullEmailShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new EmailAddress(null));
    }
}
