package com.belman.unit.test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A simple test class to demonstrate the testing process.
 * This test verifies basic string operations.
 */
public class SimpleTest {

    /**
     * Test that string concatenation works correctly.
     */
    @Test
    public void testStringConcatenation() {
        String part1 = "Hello";
        String part2 = "World";
        String result = part1 + " " + part2;
        
        assertEquals("Hello World", result, "String concatenation should work correctly");
        System.out.println("[DEBUG_LOG] String concatenation test passed");
    }

    /**
     * Test that string length calculation works correctly.
     */
    @Test
    public void testStringLength() {
        String text = "Belman Photo Documentation";
        int length = text.length();
        
        assertEquals(26, length, "String length should be calculated correctly");
        System.out.println("[DEBUG_LOG] String length test passed");
    }
}