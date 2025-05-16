package com.belman.integration.audit;

import com.belman.domain.audit.AuditFacade;
import com.belman.domain.audit.AuditRepository;
import com.belman.domain.audit.DefaultAuditFacade;
import com.belman.domain.audit.event.AuditEvent;
import com.belman.domain.common.base.BusinessObject;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerType;
import com.belman.domain.customer.events.CustomerCreatedEvent;
import com.belman.domain.customer.events.CustomerUpdatedEvent;
import com.belman.domain.services.Logger;
import com.belman.domain.user.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Integration test for the AuditFacade with InMemoryAuditRepository.
 * <p>
 * This test verifies that the AuditFacade and AuditRepository work together correctly.
 */
class AuditFacadeIntegrationTest {

    @Mock
    private Logger logger;

    private AuditRepository auditRepository;
    private AuditFacade auditFacade;
    private TestBusinessObject testBusinessObject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a real InMemoryAuditRepository
        auditRepository = new InMemoryAuditRepository();

        // Create the AuditFacade with the real repository and mock logger
        auditFacade = new DefaultAuditFacade(auditRepository, logger);

        // Set the AuditFacade on the BusinessObject class
        BusinessObject.setAuditFacade(auditFacade);

        // Create a test business object
        testBusinessObject = new TestBusinessObject("test-id");
    }

    @AfterEach
    void tearDown() {
        // Reset the static AuditFacade to avoid affecting other tests
        BusinessObject.setAuditFacade(null);

        // Clear the repository
        ((InMemoryAuditRepository) auditRepository).clear();
    }

    @Test
    void registerAuditEvent_shouldStoreEventInRepository() {
        // Arrange
        CustomerId customerId = new CustomerId("customer-123");
        UserId userId = UserId.newId();
        CustomerCreatedEvent event = new CustomerCreatedEvent(customerId, CustomerType.INDIVIDUAL, userId);

        // Act
        testBusinessObject.registerTestAuditEvent(event);

        // Assert
        List<AuditEvent> events = auditRepository.getEventsByEntity("Customer", customerId.toString());
        assertEquals(1, events.size());
        assertEquals("CustomerCreated", events.get(0).getEventType());

        // Verify logger was called
        verify(logger).debug(anyString(), eq(event.getEventType()));
    }

    @Test
    void registerAuditEvents_shouldStoreMultipleEventsInRepository() {
        // Arrange
        CustomerId customerId = new CustomerId("customer-123");
        UserId userId = UserId.newId();

        CustomerCreatedEvent createdEvent = new CustomerCreatedEvent(customerId, CustomerType.INDIVIDUAL, userId);

        Map<String, String> changedFields = new HashMap<>();
        changedFields.put("email", "newemail@example.com");
        CustomerUpdatedEvent updatedEvent = new CustomerUpdatedEvent(customerId, userId, changedFields);

        List<AuditEvent> events = List.of(createdEvent, updatedEvent);

        // Act
        testBusinessObject.registerTestAuditEvents(events);

        // Assert
        List<AuditEvent> storedEvents = auditRepository.getEventsByEntity("Customer", customerId.toString());
        assertEquals(2, storedEvents.size());

        // Verify logger was called
        verify(logger).debug(anyString(), eq(events.size()));
    }

    @Test
    void getEventsByEntity_shouldReturnEventsForEntity() {
        // Arrange
        CustomerId customerId1 = new CustomerId("customer-123");
        CustomerId customerId2 = new CustomerId("customer-456");
        UserId userId = UserId.newId();

        CustomerCreatedEvent event1 = new CustomerCreatedEvent(customerId1, CustomerType.INDIVIDUAL, userId);
        CustomerCreatedEvent event2 = new CustomerCreatedEvent(customerId2, CustomerType.COMPANY, userId);

        auditFacade.logEvent(event1);
        auditFacade.logEvent(event2);

        // Act
        List<AuditEvent> events = auditFacade.getEventsByEntity("Customer", customerId1.toString());

        // Assert
        assertEquals(1, events.size());
        assertEquals("CustomerCreated", events.get(0).getEventType());
        assertEquals(customerId1.toString(), ((CustomerCreatedEvent) events.get(0)).getCustomerId().toString());
    }

    @Test
    void getEventsByType_shouldReturnEventsOfType() {
        // Arrange
        CustomerId customerId = new CustomerId("customer-123");
        UserId userId = UserId.newId();

        CustomerCreatedEvent createdEvent = new CustomerCreatedEvent(customerId, CustomerType.INDIVIDUAL, userId);

        Map<String, String> changedFields = new HashMap<>();
        changedFields.put("email", "newemail@example.com");
        CustomerUpdatedEvent updatedEvent = new CustomerUpdatedEvent(customerId, userId, changedFields);

        auditFacade.logEvent(createdEvent);
        auditFacade.logEvent(updatedEvent);

        // Act
        List<AuditEvent> events = auditRepository.getEventsByType("CustomerCreated");

        // Assert
        assertEquals(1, events.size());
        assertEquals("CustomerCreated", events.get(0).getEventType());
    }

    @Test
    void getEventsByUser_shouldReturnEventsForUser() {
        // Arrange
        CustomerId customerId = new CustomerId("customer-123");
        UserId userId1 = UserId.newId();
        UserId userId2 = UserId.newId();

        CustomerCreatedEvent event1 = new CustomerCreatedEvent(customerId, CustomerType.INDIVIDUAL, userId1);

        Map<String, String> changedFields = new HashMap<>();
        changedFields.put("email", "newemail@example.com");
        CustomerUpdatedEvent event2 = new CustomerUpdatedEvent(customerId, userId2, changedFields);

        auditFacade.logEvent(event1);
        auditFacade.logEvent(event2);

        // Act
        List<AuditEvent> events = auditRepository.getEventsByUser(userId1.toString());

        // Assert
        assertEquals(1, events.size());
        assertEquals("CustomerCreated", events.get(0).getEventType());
        assertEquals(userId1.toString(), ((CustomerCreatedEvent) events.get(0)).getCreatedBy().toString());
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
}
