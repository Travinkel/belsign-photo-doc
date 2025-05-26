package com.belman.domain.order;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the OrderNumber class.
 */
public class OrderNumberTest {

    @Test
    public void testConstructorWithValidFormat() {
        // Test with the format (ORD-XX-YYMMDD-ZZZ-NNNN)
        OrderNumber orderNumber = new OrderNumber("ORD-78-230625-PIP-0003");
        assertEquals("ORD-78-230625-PIP-0003", orderNumber.value());
    }

    @Test
    public void testConstructorWithInvalidFormat() {
        // Test with an invalid format
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderNumber("INVALID-FORMAT");
        });

        // Test with the old format (should now be invalid)
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderNumber("01/23-123456-12345678");
        });
    }

    @Test
    public void testGetProjectIdentifier() {
        OrderNumber orderNumber = new OrderNumber("ORD-78-230625-PIP-0003");
        assertEquals("78", orderNumber.getProjectIdentifier());
    }

    @Test
    public void testGetDateCode() {
        OrderNumber orderNumber = new OrderNumber("ORD-78-230625-PIP-0003");
        assertEquals("230625", orderNumber.getDateCode());
    }

    @Test
    public void testGetProjectCode() {
        OrderNumber orderNumber = new OrderNumber("ORD-78-230625-PIP-0003");
        assertEquals("PIP", orderNumber.getProjectCode());
    }

    @Test
    public void testGetSequenceNumber() {
        OrderNumber orderNumber = new OrderNumber("ORD-78-230625-PIP-0003");
        assertEquals("0003", orderNumber.getSequenceNumber());
    }
}
