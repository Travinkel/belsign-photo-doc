package com.belman.unit.business.audit;

import com.belman.business.audit.AuditFacade;
import com.belman.business.audit.AuditRepository;
import com.belman.business.audit.DefaultAuditFacade;
import com.belman.business.richbe.events.AuditEvent;
import com.belman.business.richbe.events.BaseAuditEvent;
import com.belman.business.richbe.order.photo.PhotoId;
import com.belman.business.richbe.services.Logger;
import com.belman.business.richbe.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultAuditFacadeTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private Logger logger;

    private AuditFacade auditFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        auditFacade = new DefaultAuditFacade(auditRepository, logger);
    }

    @Test
    void logEvent_shouldDelegateToRepository() {
        // Arrange
        AuditEvent event = new TestAuditEvent();

        // Act
        auditFacade.logEvent(event);

        // Assert
        verify(auditRepository).store(event);
        verify(logger).debug(anyString(), eq(event.getEventType()));
    }

    @Test
    void logBatch_shouldDelegateToRepository() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(
                new TestAuditEvent(),
                new TestAuditEvent()
        );

        // Act
        auditFacade.logBatch(events);

        // Assert
        verify(auditRepository).storeAll(events);
        verify(logger).debug(anyString(), eq(events.size()));
    }

    @Test
    void logBatch_withEmptyList_shouldNotCallRepository() {
        // Arrange
        List<AuditEvent> events = List.of();

        // Act
        auditFacade.logBatch(events);

        // Assert
        verify(auditRepository, never()).storeAll(any());
        verify(logger).debug(anyString());
    }

    @Test
    void logPhotoApproved_shouldCreateAndStoreEvent() {
        // Arrange
        PhotoId photoId = mock(PhotoId.class);
        UserId approverId = mock(UserId.class);

        // Act
        auditFacade.logPhotoApproved(photoId, approverId);

        // Assert
        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRepository).store(eventCaptor.capture());
        
        AuditEvent capturedEvent = eventCaptor.getValue();
        assertEquals("PhotoApproved", capturedEvent.getEventType());
        assertNotNull(capturedEvent.getEventId());
        assertNotNull(capturedEvent.getOccurredOn());
    }

    @Test
    void logPhotoRejected_shouldCreateAndStoreEvent() {
        // Arrange
        PhotoId photoId = mock(PhotoId.class);
        UserId rejecterId = mock(UserId.class);
        String reason = "Poor quality";

        // Act
        auditFacade.logPhotoRejected(photoId, rejecterId, reason);

        // Assert
        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRepository).store(eventCaptor.capture());
        
        AuditEvent capturedEvent = eventCaptor.getValue();
        assertEquals("PhotoRejected", capturedEvent.getEventType());
        assertNotNull(capturedEvent.getEventId());
        assertNotNull(capturedEvent.getOccurredOn());
    }

    @Test
    void getEventsByEntity_shouldDelegateToRepository() {
        // Arrange
        String entityType = "Photo";
        String entityId = "123";
        List<AuditEvent> expectedEvents = List.of(new TestAuditEvent());
        when(auditRepository.getEventsByEntity(entityType, entityId)).thenReturn(expectedEvents);

        // Act
        List<AuditEvent> actualEvents = auditFacade.getEventsByEntity(entityType, entityId);

        // Assert
        assertEquals(expectedEvents, actualEvents);
        verify(auditRepository).getEventsByEntity(entityType, entityId);
    }

    /**
     * Test implementation of AuditEvent for testing purposes.
     */
    private static class TestAuditEvent extends BaseAuditEvent {
        public TestAuditEvent() {
            super(UUID.randomUUID(), Instant.now());
        }

        @Override
        public String getEventType() {
            return "TestEvent";
        }
    }
}