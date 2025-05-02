package com.belman.unit.backbone.core.events;

import com.belman.backbone.core.events.AbstractDomainEvent;
import com.belman.backbone.core.events.ViewShownEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ViewShownEvent class.
 * These tests verify that the ViewShownEvent correctly extends AbstractDomainEvent
 * and provides the expected behavior.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewShownEventTest {

    @Test
    void constructor_shouldSetViewName() {
        // Arrange
        String viewName = "TestView";

        // Act
        ViewShownEvent event = new ViewShownEvent(viewName);

        // Assert
        assertEquals(viewName, event.getViewName(), "View name should be set correctly");
    }

    @Test
    void constructor_withNullViewName_shouldAcceptNullViewName() {
        // Act
        ViewShownEvent event = new ViewShownEvent(null);

        // Assert
        assertNull(event.getViewName(), "View name should be null");
    }

    @Test
    void getEventType_shouldReturnClassName() {
        // Arrange
        ViewShownEvent event = new ViewShownEvent("TestView");

        // Act
        String eventType = event.getEventType();

        // Assert
        assertEquals("ViewShownEvent", eventType, "Event type should be the class name");
    }

    @Test
    void viewShownEvent_shouldExtendAbstractDomainEvent() {
        // Arrange
        ViewShownEvent event = new ViewShownEvent("TestView");

        // Assert
        assertTrue(event instanceof AbstractDomainEvent, "ViewShownEvent should extend AbstractDomainEvent");
    }

    @Test
    void toString_shouldIncludeViewName() {
        // Arrange
        String viewName = "TestView";
        ViewShownEvent event = new ViewShownEvent(viewName);

        // Act
        String result = event.toString();

        // Assert
        assertTrue(result.contains(viewName), "toString should include view name");
        assertTrue(result.contains("ViewShownEvent"), "toString should include event type");
        assertTrue(result.contains(event.getEventId().toString()), "toString should include event ID");
        assertTrue(result.contains(event.getTimestamp().toString()), "toString should include timestamp");
    }
}