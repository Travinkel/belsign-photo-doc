package com.belman.unit.backbone.core.events;

import com.belman.domain.shared.AbstractDomainEvent;
import com.belman.domain.shared.ViewHiddenEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ViewHiddenEvent class.
 * These tests verify that the ViewHiddenEvent correctly extends AbstractDomainEvent
 * and provides the expected behavior.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ViewHiddenEventTest {

    @Test
    void constructor_shouldSetViewName() {
        // Arrange
        String viewName = "TestView";

        // Act
        ViewHiddenEvent event = new ViewHiddenEvent(viewName);

        // Assert
        assertEquals(viewName, event.getViewName(), "View name should be set correctly");
    }

    @Test
    void constructor_withNullViewName_shouldAcceptNullViewName() {
        // Act
        ViewHiddenEvent event = new ViewHiddenEvent(null);

        // Assert
        assertNull(event.getViewName(), "View name should be null");
    }

    @Test
    void getEventType_shouldReturnClassName() {
        // Arrange
        ViewHiddenEvent event = new ViewHiddenEvent("TestView");

        // Act
        String eventType = event.getEventType();

        // Assert
        assertEquals("ViewHiddenEvent", eventType, "Event type should be the class name");
    }

    @Test
    void viewHiddenEvent_shouldExtendAbstractDomainEvent() {
        // Arrange
        ViewHiddenEvent event = new ViewHiddenEvent("TestView");

        // Assert
        assertTrue(event instanceof AbstractDomainEvent, "ViewHiddenEvent should extend AbstractDomainEvent");
    }

    @Test
    void toString_shouldIncludeViewName() {
        // Arrange
        String viewName = "TestView";
        ViewHiddenEvent event = new ViewHiddenEvent(viewName);

        // Act
        String result = event.toString();

        // Assert
        assertTrue(result.contains(viewName), "toString should include view name");
        assertTrue(result.contains("ViewHiddenEvent"), "toString should include event type");
        assertTrue(result.contains(event.getEventId().toString()), "toString should include event ID");
        assertTrue(result.contains(event.getTimestamp().toString()), "toString should include timestamp");
    }
}