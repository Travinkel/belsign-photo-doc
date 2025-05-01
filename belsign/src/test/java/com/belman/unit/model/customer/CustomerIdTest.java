package com.belman.unit.model.customer;

import com.belman.domain.valueobjects.CustomerId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerIdTest {

    @Test
    void constructorShouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new CustomerId(null));
    }

    @Test
    void newIdShouldCreateUniqueId() {
        CustomerId id1 = CustomerId.newId();
        CustomerId id2 = CustomerId.newId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void toUUIDShouldReturnCorrectUUID() {
        UUID uuid = UUID.randomUUID();
        CustomerId customerId = new CustomerId(uuid);
        
        assertEquals(uuid, customerId.toUUID());
    }

    @Test
    void equalCustomerIdsShouldBeEqual() {
        UUID uuid = UUID.randomUUID();
        CustomerId id1 = new CustomerId(uuid);
        CustomerId id2 = new CustomerId(uuid);
        
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void differentCustomerIdsShouldNotBeEqual() {
        CustomerId id1 = new CustomerId(UUID.randomUUID());
        CustomerId id2 = new CustomerId(UUID.randomUUID());
        
        assertNotEquals(id1, id2);
    }
}