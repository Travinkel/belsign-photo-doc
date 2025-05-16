package com.belman.unit.application.core;

import com.belman.domain.audit.event.*;
import com.belman.domain.audit.event.ApplicationStateAuditEvent.ApplicationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ApplicationStateManager class.
 */
public class ApplicationStateManagerTest {

    private List<ApplicationStateAuditEvent> receivedEvents;
    private AtomicBoolean backgroundTaskExecuted;
    private AtomicBoolean foregroundTaskExecuted;
    private AtomicBoolean shutdownTaskExecuted;

    @BeforeEach
    void setUp() {
        receivedEvents = new ArrayList<>();
        backgroundTaskExecuted = new AtomicBoolean(false);
        foregroundTaskExecuted = new AtomicBoolean(false);
        shutdownTaskExecuted = new AtomicBoolean(false);

        // Reset the ApplicationStateManager to its initial state
        ApplicationStateManager.initialize();

        // Register a listener to capture events
        ApplicationStateManager.addStateChangeListener(receivedEvents::add);

        // Register test tasks
        ApplicationStateManager.registerBackgroundTask(() -> backgroundTaskExecuted.set(true));
        ApplicationStateManager.registerForegroundTask(() -> foregroundTaskExecuted.set(true));
        ApplicationStateManager.registerShutdownTask(() -> shutdownTaskExecuted.set(true));
    }

    @Test
    void initialize_shouldSetStateToStarting() {
        // When initialized in setUp()

        // Then
        assertEquals(ApplicationState.STARTING, ApplicationStateManager.getCurrentState());
    }

    @Test
    void transitionTo_shouldChangeState() {
        // When
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);

        // Then
        assertEquals(ApplicationState.ACTIVE, ApplicationStateManager.getCurrentState());
    }

    @Test
    void transitionTo_shouldPublishEvent() {
        // When
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);

        // Then
        assertEquals(1, receivedEvents.size());
        assertInstanceOf(ApplicationResumedEvent.class, receivedEvents.get(0));
    }

    @Test
    void transitionTo_shouldNotPublishEventForSameState() {
        // Given
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);
        receivedEvents.clear();

        // When
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);

        // Then
        assertEquals(0, receivedEvents.size());
    }

    @Test
    void transitionToBackground_shouldExecuteBackgroundTasks() {
        // When
        ApplicationStateManager.transitionTo(ApplicationState.BACKGROUND);

        // Then
        assertTrue(backgroundTaskExecuted.get());
    }

    @Test
    void transitionToActive_shouldExecuteForegroundTasks() {
        // When
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);

        // Then
        assertTrue(foregroundTaskExecuted.get());
    }

    @Test
    void transitionToStopping_shouldExecuteShutdownTasks() {
        // When
        ApplicationStateManager.transitionTo(ApplicationState.STOPPING);

        // Then
        assertTrue(shutdownTaskExecuted.get());
    }

    @Test
    void stateTransitions_shouldFollowCorrectSequence() {
        // When
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);
        ApplicationStateManager.transitionTo(ApplicationState.PAUSED);
        ApplicationStateManager.transitionTo(ApplicationState.BACKGROUND);
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);
        ApplicationStateManager.transitionTo(ApplicationState.STOPPING);

        // Then
        assertEquals(5, receivedEvents.size());
        assertInstanceOf(ApplicationResumedEvent.class, receivedEvents.get(0));
        assertInstanceOf(ApplicationPausedEvent.class, receivedEvents.get(1));
        assertInstanceOf(ApplicationBackgroundedEvent.class, receivedEvents.get(2));
        assertInstanceOf(ApplicationResumedEvent.class, receivedEvents.get(3));
        assertInstanceOf(ApplicationStoppedEvent.class, receivedEvents.get(4));
    }
}
