package com.belman.domain.order;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the OrderNumber class.
 */
public class OrderNumberTest {

    @Test
    public void testConstructorWithValidFormat() {
        // Test with the format (MM/YY-CUSTOMER-SEQUENCE)
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("01/23-123456-12345678", orderNumber.value());
    }

    @Test
    public void testConstructorWithInvalidFormat() {
        // Test with an invalid format
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderNumber("INVALID-FORMAT");
        });

        // Test with the legacy format (should now be invalid)
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderNumber("ORD-01-230701-WLD-0001");
        });
    }

    @Test
    public void testGetMonthYear() {
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("01/23", orderNumber.getMonthYear());
    }

    @Test
    public void testGetCustomerIdentifier() {
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("123456", orderNumber.getCustomerIdentifier());
    }

    @Test
    public void testGetSequenceNumber() {
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("12345678", orderNumber.getSequenceNumber());
    }
}
