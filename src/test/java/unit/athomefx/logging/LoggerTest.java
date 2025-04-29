package unit.athomefx.logging;

import com.belman.belsign.framework.athomefx.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class LoggerTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private Logger logger;
    
    @BeforeEach
    void setUp() {
        // Redirect System.out to capture log output
        System.setOut(new PrintStream(outContent));
        
        // Create a logger for this class
        logger = Logger.getLogger(LoggerTest.class);
        
        // Set the minimum log level to TRACE to test all levels
        Logger.setMinimumLevel(Logger.Level.TRACE);
    }
    
    @AfterEach
    void tearDown() {
        // Restore System.out
        System.setOut(originalOut);
    }
    
    @Test
    void testTraceLogging() {
        logger.trace("This is a trace message");
        assertTrue(outContent.toString().contains("[TRACE] unit.athomefx.logging.LoggerTest - This is a trace message"));
    }
    
    @Test
    void testDebugLogging() {
        logger.debug("This is a debug message");
        assertTrue(outContent.toString().contains("[DEBUG] unit.athomefx.logging.LoggerTest - This is a debug message"));
    }
    
    @Test
    void testInfoLogging() {
        logger.info("This is an info message");
        assertTrue(outContent.toString().contains("[INFO] unit.athomefx.logging.LoggerTest - This is an info message"));
    }
    
    @Test
    void testWarnLogging() {
        logger.warn("This is a warning message");
        assertTrue(outContent.toString().contains("[WARN] unit.athomefx.logging.LoggerTest - This is a warning message"));
    }
    
    @Test
    void testErrorLogging() {
        logger.error("This is an error message");
        assertTrue(outContent.toString().contains("[ERROR] unit.athomefx.logging.LoggerTest - This is an error message"));
    }
    
    @Test
    void testParameterizedLogging() {
        logger.info("User {} logged in from {}", "john.doe", "192.168.1.1");
        assertTrue(outContent.toString().contains("[INFO] unit.athomefx.logging.LoggerTest - User john.doe logged in from 192.168.1.1"));
    }
    
    @Test
    void testExceptionLogging() {
        Exception e = new RuntimeException("Test exception");
        logger.error("An error occurred", e);
        String output = outContent.toString();
        assertTrue(output.contains("[ERROR] unit.athomefx.logging.LoggerTest - An error occurred: Test exception"));
        assertTrue(output.contains("java.lang.RuntimeException: Test exception"));
    }
    
    @Test
    void testLogLevelFiltering() {
        // Set the minimum log level to INFO
        Logger.setMinimumLevel(Logger.Level.INFO);
        
        // These should not be logged
        logger.trace("This trace message should not be logged");
        logger.debug("This debug message should not be logged");
        
        // These should be logged
        logger.info("This info message should be logged");
        logger.warn("This warning message should be logged");
        logger.error("This error message should be logged");
        
        String output = outContent.toString();
        assertFalse(output.contains("This trace message should not be logged"));
        assertFalse(output.contains("This debug message should not be logged"));
        assertTrue(output.contains("This info message should be logged"));
        assertTrue(output.contains("This warning message should be logged"));
        assertTrue(output.contains("This error message should be logged"));
    }
}