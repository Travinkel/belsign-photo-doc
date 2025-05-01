package dev.stefan.athomefx.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PlatformUtils class.
 * 
 * Note: These tests assume that the test environment does not have Gluon Attach available.
 * If Gluon Attach is available, the tests may fail depending on the actual platform.
 */
public class PlatformUtilsTest {

    @Test
    @DisplayName("Should detect desktop when Gluon Attach is not available")
    void shouldDetectDesktopWhenGluonAttachIsNotAvailable() {
        // In a test environment without Gluon Attach, isDesktop should return true
        assertTrue(PlatformUtils.isDesktop(), "Should detect desktop when Gluon Attach is not available");
    }

    @Test
    @DisplayName("Should not detect mobile when Gluon Attach is not available")
    void shouldNotDetectMobileWhenGluonAttachIsNotAvailable() {
        // In a test environment without Gluon Attach, isRunningOnMobile should return false
        assertFalse(PlatformUtils.isRunningOnMobile(), "Should not detect mobile when Gluon Attach is not available");
    }

    @Test
    @DisplayName("Should not detect Android when Gluon Attach is not available")
    void shouldNotDetectAndroidWhenGluonAttachIsNotAvailable() {
        // In a test environment without Gluon Attach, isAndroid should return false
        assertFalse(PlatformUtils.isAndroid(), "Should not detect Android when Gluon Attach is not available");
    }

    @Test
    @DisplayName("Should not detect iOS when Gluon Attach is not available")
    void shouldNotDetectIOSWhenGluonAttachIsNotAvailable() {
        // In a test environment without Gluon Attach, isIOS should return false
        assertFalse(PlatformUtils.isIOS(), "Should not detect iOS when Gluon Attach is not available");
    }

    @Test
    @DisplayName("Should have consistent results between isDesktop and isRunningOnMobile")
    void shouldHaveConsistentResultsBetweenIsDesktopAndIsRunningOnMobile() {
        // isDesktop should be the opposite of isRunningOnMobile
        assertEquals(!PlatformUtils.isRunningOnMobile(), PlatformUtils.isDesktop(), 
                "isDesktop should be the opposite of isRunningOnMobile");
    }
}