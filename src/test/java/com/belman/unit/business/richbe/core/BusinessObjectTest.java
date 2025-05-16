package com.belman.unit.business.richbe.core;

import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.audit.event.BaseAuditEvent;
import com.belman.domain.common.base.BusinessObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

class BusinessObjectTest {

    @Mock
    private AuditFacade auditFacade;

    private TestBusinessObject businessObject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        BusinessObject.setAuditFacade(auditFacade);
        businessObject = new TestBusinessObject("test-id");
    }

    @AfterEach
    void tearDown() {
        // Reset the static AuditFacade to avoid affecting other tests
        BusinessObject.setAuditFacade(null);
    }

    @Test
    void registerAuditEvent_shouldDelegateToAuditFacade() {
        // Arrange
        AuditEvent event = new TestAuditEvent();

        // Act
        businessObject.registerTestAuditEvent(event);

        // Assert
        verify(auditFacade).logEvent(event);
    }

    @Test
    void registerAuditEvents_shouldDelegateToAuditFacade() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(
                new TestAuditEvent(),
                new TestAuditEvent()
        );

        // Act
        businessObject.registerTestAuditEvents(events);

        // Assert
        verify(auditFacade).logBatch(events);
    }

    @Test
    void registerAuditEvent_withNullAuditFacade_shouldThrowException() {
        // Arrange
        BusinessObject.setAuditFacade(null);
        AuditEvent event = new TestAuditEvent();

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> businessObject.registerTestAuditEvent(event));
    }

    @Test
    void registerAuditEvents_withNullAuditFacade_shouldThrowException() {
        // Arrange
        BusinessObject.setAuditFacade(null);
        List<AuditEvent> events = Arrays.asList(
                new TestAuditEvent(),
                new TestAuditEvent()
        );

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> businessObject.registerTestAuditEvents(events));
    }

    /**
     * Test implementation of BusinessObject for testing purposes.
     */
    private static class TestBusinessObject extends BusinessObject<String> {
        private final String id;

        public TestBusinessObject(String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        public void registerTestAuditEvent(AuditEvent event) {
            registerAuditEvent(event);
        }

        public void registerTestAuditEvents(List<AuditEvent> events) {
            registerAuditEvents(events);
        }
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
