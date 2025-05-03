package com.belman.unit.application.core;

import com.belman.application.core.ApplicationStateManager;
import com.belman.application.core.GluonLifecycleManager;
import com.belman.domain.shared.ApplicationStateEvent.ApplicationState;
import com.gluonhq.attach.lifecycle.LifecycleEvent;
import com.gluonhq.attach.lifecycle.LifecycleService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the GluonLifecycleManager class.
 */
public class GluonLifecycleManagerTest {

    @Mock
    private MobileApplication mockApplication;

    @Mock
    private LifecycleService mockLifecycleService;

    private AtomicReference<Runnable> pauseHandler = new AtomicReference<>();
    private AtomicReference<Runnable> resumeHandler = new AtomicReference<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock the LifecycleService.create() method
        mockStatic(LifecycleService.class);
        when(LifecycleService.create()).thenReturn(Optional.of(mockLifecycleService));
        
        // Capture the handlers registered for lifecycle events
        doAnswer(invocation -> {
            LifecycleEvent event = invocation.getArgument(0);
            Runnable handler = invocation.getArgument(1);
            
            if (event == LifecycleEvent.PAUSE) {
                pauseHandler.set(handler);
            } else if (event == LifecycleEvent.RESUME) {
                resumeHandler.set(handler);
            }
            
            return null;
        }).when(mockLifecycleService).addListener(any(LifecycleEvent.class), any(Runnable.class));
        
        // Initialize the ApplicationStateManager
        ApplicationStateManager.initialize();
    }

    @Test
    void initialize_shouldRegisterLifecycleHandlers() {
        // When
        GluonLifecycleManager.initialize();
        
        // Then
        verify(mockLifecycleService).addListener(eq(LifecycleEvent.PAUSE), any(Runnable.class));
        verify(mockLifecycleService).addListener(eq(LifecycleEvent.RESUME), any(Runnable.class));
    }

    @Test
    void pauseEvent_shouldTransitionToApplicationPausedState() {
        // Given
        GluonLifecycleManager.initialize();
        ApplicationStateManager.transitionTo(ApplicationState.ACTIVE);
        
        // When
        pauseHandler.get().run();
        
        // Then
        assertEquals(ApplicationState.PAUSED, ApplicationStateManager.getCurrentState());
    }

    @Test
    void resumeEvent_shouldTransitionToApplicationActiveState() {
        // Given
        GluonLifecycleManager.initialize();
        ApplicationStateManager.transitionTo(ApplicationState.PAUSED);
        
        // When
        resumeHandler.get().run();
        
        // Then
        assertEquals(ApplicationState.ACTIVE, ApplicationStateManager.getCurrentState());
    }

    @Test
    void init_shouldInitializeLifecycleManager() {
        // When
        GluonLifecycleManager.init(mockApplication);
        
        // Then
        assertEquals(ApplicationState.ACTIVE, ApplicationStateManager.getCurrentState());
    }

    @Test
    void init_withNullApplication_shouldNotInitialize() {
        // Given
        ApplicationStateManager.transitionTo(ApplicationState.STARTING);
        
        // When
        GluonLifecycleManager.init(null);
        
        // Then
        assertEquals(ApplicationState.STARTING, ApplicationStateManager.getCurrentState());
    }
}