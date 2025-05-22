package com.belman.domain.order;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the OrderNumber class.
 */
public class OrderNumberTest {

    @Test
    public void testConstructorWithNewFormat() {
        // Test with the new format (MM/YY-CUSTOMER-SEQUENCE)
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("01/23-123456-12345678", orderNumber.value());
    }

    @Test
    public void testConstructorWithLegacyFormat() {
        // Test with the legacy format (ORD-XX-YYMMDD-ABC-NNNN)
        OrderNumber orderNumber = new OrderNumber("ORD-01-230701-WLD-0001");
        assertEquals("ORD-01-230701-WLD-0001", orderNumber.value());
    }

    @Test
    public void testConstructorWithInvalidFormat() {
        // Test with an invalid format
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderNumber("INVALID-FORMAT");
        });
    }

    @Test
    public void testGetMonthYearWithNewFormat() {
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("01/23", orderNumber.getMonthYear());
    }

    @Test
    public void testGetMonthYearWithLegacyFormat() {
        OrderNumber orderNumber = new OrderNumber("ORD-01-230701-WLD-0001");
        assertEquals("07/23", orderNumber.getMonthYear());
    }

    @Test
    public void testGetCustomerIdentifierWithNewFormat() {
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("123456", orderNumber.getCustomerIdentifier());
    }

    @Test
    public void testGetCustomerIdentifierWithLegacyFormat() {
        OrderNumber orderNumber = new OrderNumber("ORD-01-230701-WLD-0001");
        assertEquals("WLD", orderNumber.getCustomerIdentifier());
    }

    @Test
    public void testGetSequenceNumberWithNewFormat() {
        OrderNumber orderNumber = new OrderNumber("01/23-123456-12345678");
        assertEquals("12345678", orderNumber.getSequenceNumber());
    }

    @Test
    public void testGetSequenceNumberWithLegacyFormat() {
        OrderNumber orderNumber = new OrderNumber("ORD-01-230701-WLD-0001");
        assertEquals("0001", orderNumber.getSequenceNumber());
    }
}