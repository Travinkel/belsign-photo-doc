package com.belman.unit.dataaccess.repository.memory;

import com.belman.dataaccess.repository.memory.InMemoryPhotoTemplateRepository;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the InMemoryPhotoTemplateRepository class.
 */
public class InMemoryPhotoTemplateRepositoryTest {

    private InMemoryPhotoTemplateRepository repository;
    private LoggerFactory loggerFactory;

    @BeforeEach
    public void setUp() {
        // Mock the LoggerFactory
        loggerFactory = Mockito.mock(LoggerFactory.class);
        Mockito.when(loggerFactory.getLogger(Mockito.any())).thenReturn(Mockito.mock(com.belman.domain.services.Logger.class));

        // Create the repository
        repository = new InMemoryPhotoTemplateRepository(loggerFactory);
    }

    @Test
    public void testFindByOrderId_NoTemplatesAssociated_ReturnsEmptyList() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());

        // Act
        List<PhotoTemplate> templates = repository.findByOrderId(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertTrue(templates.isEmpty(), "Templates list should be empty when no templates are associated with the order");
        System.out.println("[DEBUG_LOG] Templates list is empty as expected when no templates are associated with the order");
    }

    @Test
    public void testFindByOrderId_TemplatesAssociated_ReturnsAssociatedTemplates() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        PhotoTemplate template1 = PhotoTemplate.TOP_VIEW_OF_JOINT;
        PhotoTemplate template2 = PhotoTemplate.SIDE_VIEW_OF_WELD;

        // Associate templates with the order
        repository.associateWithOrder(orderId, template1.name(), true);
        repository.associateWithOrder(orderId, template2.name(), true);

        // Act
        List<PhotoTemplate> templates = repository.findByOrderId(orderId);

        // Assert
        assertNotNull(templates, "Templates list should not be null");
        assertEquals(2, templates.size(), "Templates list should contain 2 templates");
        assertTrue(templates.contains(template1), "Templates list should contain template1");
        assertTrue(templates.contains(template2), "Templates list should contain template2");
        System.out.println("[DEBUG_LOG] Templates list contains the expected templates when templates are associated with the order");
    }

    @Test
    public void testAssociateWithOrder_ValidTemplate_ReturnsTrue() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        PhotoTemplate template = PhotoTemplate.TOP_VIEW_OF_JOINT;

        // Act
        boolean result = repository.associateWithOrder(orderId, template.name(), true);

        // Assert
        assertTrue(result, "associateWithOrder should return true for a valid template");
        System.out.println("[DEBUG_LOG] associateWithOrder returned true as expected for a valid template");
    }

    @Test
    public void testAssociateWithOrder_InvalidTemplate_ReturnsFalse() {
        // Arrange
        OrderId orderId = new OrderId(UUID.randomUUID().toString());
        String invalidTemplateId = "INVALID_TEMPLATE";

        // Act
        boolean result = repository.associateWithOrder(orderId, invalidTemplateId, true);

        // Assert
        assertFalse(result, "associateWithOrder should return false for an invalid template");
        System.out.println("[DEBUG_LOG] associateWithOrder returned false as expected for an invalid template");
    }
}
