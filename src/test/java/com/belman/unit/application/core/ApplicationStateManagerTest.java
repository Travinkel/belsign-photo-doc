package com.belman.unit.application.core;

import com.belman.business.core.ApplicationStateManager;
import com.belman.business.richbe.events.ApplicationBackgroundedEvent;
import com.belman.business.richbe.events.ApplicationPausedEvent;
import com.belman.business.richbe.events.ApplicationResumedEvent;
import com.belman.business.richbe.events.ApplicationStateEvent;
import com.belman.business.richbe.events.ApplicationStateEvent.ApplicationState;
import com.belman.business.richbe.events.ApplicationStoppedEvent;
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

    private List<ApplicationStateEvent> receivedEvents;
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
        assertTrue(receivedEvents.get(0) instanceof ApplicationResumedEvent);
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
        assertTrue(receivedEvents.get(0) instanceof ApplicationResumedEvent);
        assertTrue(receivedEvents.get(1) instanceof ApplicationPausedEvent);
        assertTrue(receivedEvents.get(2) instanceof ApplicationBackgroundedEvent);
        assertTrue(receivedEvents.get(3) instanceof ApplicationResumedEvent);
        assertTrue(receivedEvents.get(4) instanceof ApplicationStoppedEvent);
    }
}
