package unit.domain.model.order;

import com.belman.belsign.domain.model.order.OrderNumber;
import com.belman.belsign.domain.model.order.Order;
import com.belman.belsign.domain.model.order.photodocument.PhotoDocument;
import com.belman.belsign.domain.model.order.photodocument.PhotoId;
import com.belman.belsign.domain.model.order.photodocument.ImagePath;
import com.belman.belsign.domain.model.shared.Timestamp;
import com.belman.belsign.domain.model.user.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private UUID orderId;
    private OrderNumber orderNumber;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        orderNumber = new OrderNumber("4/24-040000-37472826");
        order = new Order(orderId, orderNumber);
    }

    @Test
    void canCreateOrderWithIdAndOrderNumber() {
        assertEquals(orderId, order.getId());
        assertEquals(orderNumber, order.getOrderNumber());
    }

    @Test
    void addingPhotoDocumentIncreasesListSize() {
        PhotoDocument photoDocument = createSamplePhotoDocument();
        order.addPhotoDocument(photoDocument);

        List<PhotoDocument> photoDocuments = order.getPhotoDocuments();
        assertEquals(1, photoDocuments.size());
        assertTrue(photoDocuments.contains(photoDocument));
    }

    @Test
    void removingPhotoDocumentDecreasesListSize() {
        PhotoDocument photoDocument = createSamplePhotoDocument();
        order.addPhotoDocument(photoDocument);

        assertEquals(1, order.getPhotoDocuments().size());

        order.removePhotoDocument(photoDocument.getId());

        assertEquals(0, order.getPhotoDocuments().size());
    }

    @Test
    void getPhotoDocumentsReturnsImmutableList() {
        PhotoDocument photoDocument = createSamplePhotoDocument();
        order.addPhotoDocument(photoDocument);

        List<PhotoDocument> photos = order.getPhotoDocuments();
        assertThrows(UnsupportedOperationException.class, () -> photos.add(createSamplePhotoDocument()));
    }

    @Test
    void removingNonExistentPhotoDoesNothing() {
        order.removePhotoDocument(new PhotoId());
        assertEquals(0, order.getPhotoDocuments().size());
    }

    private PhotoDocument createSamplePhotoDocument() {
        return new PhotoDocument(
                new PhotoId(UUID.randomUUID()),
                new ImagePath(Path.of("src/main/resources/images/HighElves.png")),
                new Timestamp(Instant.now()),
                new Username("uploaderUser")
        );
    }

    /**
     * Test initialization of Order
     */
    @Test
    void testOrderInitialization() {
        UUID orderId = UUID.randomUUID();
        OrderNumber orderNumber = new OrderNumber("ORD-12345");

        Order order = new Order(orderId, orderNumber);

        assertNotNull(order, "Order should be initialized successfully.");
        assertEquals(orderId, order.getId(), "Order ID should match the provided ID.");
        assertEquals(orderNumber, order.getOrderNumber(), "Order number should match the provided order number.");
        assertTrue(order.getPhotoDocuments().isEmpty(), "PhotoDocuments list should be empty upon initialization.");
    }

    /**
     * Test adding a PhotoDocument to the order
     */
    @Test
    void testAddPhotoDocument() {
        UUID orderId = UUID.randomUUID();
        OrderNumber orderNumber = new OrderNumber("ORD-12345");
        Order order = new Order(orderId, orderNumber);

        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photoDocument = new PhotoDocument(photoId);

        order.addPhotoDocument(photoDocument);

        List<PhotoDocument> photoDocuments = order.getPhotoDocuments();
        assertEquals(1, photoDocuments.size(), "PhotoDocuments list should contain one document after adding.");
        assertEquals(photoDocument, photoDocuments.get(0), "The added PhotoDocument should match the expected object.");
    }

    /**
     * Test removing a PhotoDocument from the order
     */
    @Test
    void testRemovePhotoDocument() {
        UUID orderId = UUID.randomUUID();
        OrderNumber orderNumber = new OrderNumber("ORD-12345");
        Order order = new Order(orderId, orderNumber);

        PhotoId photoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photoDocument = new PhotoDocument(photoId);

        order.addPhotoDocument(photoDocument);
        assertEquals(1, order.getPhotoDocuments().size(), "PhotoDocuments list should initially contain one document.");

        order.removePhotoDocument(photoId);

        assertTrue(order.getPhotoDocuments().isEmpty(), "PhotoDocuments list should be empty after removing the document.");
    }

    /**
     * Test removing a PhotoDocument that does not exist
     */
    @Test
    void testRemovingNonExistentPhotoDocument() {
        UUID orderId = UUID.randomUUID();
        OrderNumber orderNumber = new OrderNumber("ORD-12345");
        Order order = new Order(orderId, orderNumber);

        PhotoId existingPhotoId = new PhotoId(UUID.randomUUID());
        PhotoDocument photoDocument = new PhotoDocument(existingPhotoId);
        order.addPhotoDocument(photoDocument);

        PhotoId nonExistentPhotoId = new PhotoId(UUID.randomUUID());

        order.removePhotoDocument(nonExistentPhotoId);

        assertEquals(1, order.getPhotoDocuments().size(), "PhotoDocuments list should remain unchanged when removing a non-existent document.");
        assertEquals(photoDocument, order.getPhotoDocuments().get(0), "The existing PhotoDocument should remain unaffected.");
    }

    /**
     * Test getting PhotoDocuments from the order
     */
    @Test
    void testGetPhotoDocuments() {
        UUID orderId = UUID.randomUUID();
        OrderNumber orderNumber = new OrderNumber("ORD-12345");
        Order order = new Order(orderId, orderNumber);

        PhotoId photoId1 = new PhotoId(UUID.randomUUID());
        PhotoId photoId2 = new PhotoId(UUID.randomUUID());

        PhotoDocument photoDocument1 = new PhotoDocument(photoId1);
        PhotoDocument photoDocument2 = new PhotoDocument(photoId2);

        order.addPhotoDocument(photoDocument1);
        order.addPhotoDocument(photoDocument2);

        List<PhotoDocument> photoDocuments = order.getPhotoDocuments();

        assertEquals(2, photoDocuments.size(), "PhotoDocuments list should contain two documents.");
        assertTrue(photoDocuments.contains(photoDocument1), "PhotoDocuments list should contain the first document.");
        assertTrue(photoDocuments.contains(photoDocument2), "PhotoDocuments list should contain the second document.");
    }

    /**
     * Test Order with a null PhotoDocument throws exception
     */
    @Test
    void testAddNullPhotoDocumentThrowsException() {
        UUID orderId = UUID.randomUUID();
        OrderNumber orderNumber = new OrderNumber("ORD-12345");
        Order order = new Order(orderId, orderNumber);

        assertThrows(NullPointerException.class, () -> {
            order.addPhotoDocument(null);
        }, "Adding a null PhotoDocument should throw NullPointerException.");
    }

    /**
     * Test initialization with null OrderNumber
     */
    @Test
    void testOrderInitializationWithNullOrderNumber() {
        assertThrows(NullPointerException.class, () -> {
            new Order(UUID.randomUUID(), null);
        }, "Creating an Order with null OrderNumber should throw NullPointerException.");
    }

    /**
     * Test initialization with null ID
     */
    @Test
    void testOrderInitializationWithNullId() {
        assertThrows(NullPointerException.class, () -> {
            new Order(null, new OrderNumber("ORD-12345"));
        }, "Creating an Order with null ID should throw NullPointerException.");
    }
}