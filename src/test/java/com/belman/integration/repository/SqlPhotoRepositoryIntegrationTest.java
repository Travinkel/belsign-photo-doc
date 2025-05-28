package com.belman.integration.repository;

import com.belman.dataaccess.mapper.PhotoMapper;
import com.belman.dataaccess.repository.sql.SqlPhotoRepository;
import com.belman.dataaccess.repository.sql.util.SqlConnectionManager;
import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.order.OrderId;
import com.belman.domain.photo.Photo;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoRepository;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.services.Logger;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.Username;
import com.belman.integration.database.TestDatabaseUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration test for the SqlPhotoRepository.
 * This test verifies that the SqlPhotoRepository correctly interacts with the database.
 * 
 * This test class demonstrates:
 * - Integration testing of a repository with a real database
 * - AAA (Arrange-Act-Assert) pattern
 * - Mockito for mocking dependencies
 */
@ExtendWith(MockitoExtension.class)
public class SqlPhotoRepositoryIntegrationTest {

    private static DataSource dataSource;
    private PhotoRepository photoRepository;

    @Mock
    private LoggerFactory loggerFactory;

    @Mock
    private Logger logger;

    private UserBusiness testUser;

    private OrderId testOrderId;
    private PhotoTemplate testTemplate;
    private Photo testPhoto;

    /**
     * Custom implementation of PhotoMapper for testing.
     */
    private static class TestPhotoMapper implements PhotoMapper<ResultSet> {

        /**
         * Parse a timestamp string from the database into an Instant.
         * This handles various formats that might be returned by SQLite.
         */
        private Instant parseTimestamp(String timestamp) {
            try {
                // Try parsing as ISO format with Z
                if (timestamp.endsWith("Z")) {
                    return Instant.parse(timestamp);
                }

                // Try parsing as ISO format without Z
                if (timestamp.contains("T")) {
                    return Instant.parse(timestamp + "Z");
                }

                // Handle SQLite datetime format (YYYY-MM-DD HH:MM:SS)
                if (timestamp.contains(" ")) {
                    // Replace space with T and add Z for ISO format
                    return Instant.parse(timestamp.replace(" ", "T") + "Z");
                }

                // Default fallback
                return Instant.now();
            } catch (Exception e) {
                System.out.println("[DEBUG_LOG] Error parsing timestamp: " + timestamp + ", error: " + e.getMessage());
                return Instant.now();
            }
        }
        @Override
        public ResultSet toRecord(PhotoDocument entity) {
            // Not needed for this test
            return null;
        }

        @Override
        public PhotoDocument toEntity(ResultSet record) {
            // Not needed for this test
            return null;
        }

        @Override
        public PhotoDocument fromResultSet(ResultSet resultSet) throws SQLException {
            String photoId = resultSet.getString("photo_id");
            String orderId = resultSet.getString("order_id");
            String filePath = resultSet.getString("file_path");
            String templateType = resultSet.getString("template_type");
            String status = resultSet.getString("status");
            String createdBy = resultSet.getString("created_by");
            String createdAt = resultSet.getString("created_at");

            // Create a real user instead of a mock
            UserId userId = new UserId(createdBy);
            Username username = new Username("test-user");
            HashedPassword password = new HashedPassword("hashed-password");
            EmailAddress email = new EmailAddress("test@example.com");
            UserBusiness user = UserBusiness.createNewUser(username, password, email);

            // Determine the template based on the template type
            PhotoTemplate template;
            switch (templateType) {
                case "TOP_VIEW":
                case "TOP_VIEW_OF_JOINT":
                    template = PhotoTemplate.TOP_VIEW_OF_JOINT;
                    break;
                case "SIDE_VIEW":
                case "SIDE_VIEW_OF_WELD":
                    template = PhotoTemplate.SIDE_VIEW_OF_WELD;
                    break;
                case "FRONT_VIEW":
                case "FRONT_VIEW_OF_ASSEMBLY":
                    template = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
                    break;
                case "BACK_VIEW":
                case "BACK_VIEW_OF_ASSEMBLY":
                    template = PhotoTemplate.BACK_VIEW_OF_ASSEMBLY;
                    break;
                default:
                    template = PhotoTemplate.CUSTOM;
            }

            // Build and return a PhotoDocument
            return PhotoDocument.builder()
                    .photoId(new PhotoId(photoId))
                    .orderId(new OrderId(orderId))
                    .imagePath(new Photo(filePath))
                    .template(template)
                    .uploadedBy(user)
                    .uploadedAt(new Timestamp(parseTimestamp(createdAt)))
                    .build();
        }

        @Override
        public PhotoId toPhotoId(ResultSet record) {
            try {
                return new PhotoId(record.getString("photo_id"));
            } catch (SQLException e) {
                throw new RuntimeException("Error getting photo ID from result set", e);
            }
        }

        @Override
        public OrderId toOrderId(ResultSet record) {
            try {
                return new OrderId(record.getString("order_id"));
            } catch (SQLException e) {
                throw new RuntimeException("Error getting order ID from result set", e);
            }
        }

        @Override
        public List<ResultSet> findByOrderId(OrderId orderId) {
            // Not needed for this test
            return null;
        }

        @Override
        public List<ResultSet> findByStatus(com.belman.domain.user.ApprovalStatus status) {
            // Not needed for this test
            return null;
        }

        @Override
        public List<ResultSet> findByOrderIdAndStatus(OrderId orderId, com.belman.domain.user.ApprovalStatus status) {
            // Not needed for this test
            return null;
        }
    }

    @BeforeAll
    public static void setupDatabase() {
        // Initialize the test database
        dataSource = TestDatabaseUtil.initializeTestDatabase();
        assertNotNull(dataSource, "DataSource should not be null");
        System.out.println("[DEBUG_LOG] Test database initialized successfully for SqlPhotoRepository tests");
    }

    @BeforeEach
    void setUp() {
        // Reset the database before each test
        TestDatabaseUtil.resetTestDatabase();
        System.out.println("[DEBUG_LOG] Test database reset for new SqlPhotoRepository test");

        // Set up logger with lenient stub to avoid UnnecessaryStubbingException
        lenient().when(loggerFactory.getLogger(any())).thenReturn(logger);

        // Create a real user
        UserId userId = new UserId("test-user-id");
        Username username = new Username("test-user");
        HashedPassword password = new HashedPassword("hashed-password");
        EmailAddress email = new EmailAddress("test@example.com");
        testUser = UserBusiness.createNewUser(username, password, email);

        // Create a SqlConnectionManager
        SqlConnectionManager connectionManager = new SqlConnectionManager(loggerFactory, "jdbc:sqlite:src/test/resources/sqlitedb/testdb.db", new Properties());

        // Create a PhotoMapper
        PhotoMapper<ResultSet> photoMapper = new TestPhotoMapper();

        // Create the repository
        photoRepository = new SqlPhotoRepository(loggerFactory, connectionManager, photoMapper);

        // Set up test data
        testOrderId = new OrderId("test-order-id");
        testTemplate = PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        testPhoto = new Photo("/path/to/test/image.jpg");

        // Insert test data into the database
        insertTestData();
    }

    @AfterAll
    public static void tearDown() {
        // Shutdown the test database
        TestDatabaseUtil.shutdownTestDatabase();
        System.out.println("[DEBUG_LOG] Test database shut down after SqlPhotoRepository tests");
    }

    /**
     * Insert test data into the database for testing.
     */
    private void insertTestData() {
        try (Connection connection = dataSource.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            try {
                // Insert test user
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USERS (user_id, username, password, email, first_name, last_name, approved) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, testUser.getId().id());
                    stmt.setString(2, testUser.getUsername().value());
                    stmt.setString(3, "hashed-password");
                    stmt.setString(4, "test@example.com");
                    stmt.setString(5, "Test");
                    stmt.setString(6, "User");
                    stmt.setInt(7, 1);
                    stmt.executeUpdate();
                }

                // Insert user role
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)")) {
                    stmt.setString(1, testUser.getId().id());
                    stmt.setString(2, "PRODUCTION");
                    stmt.executeUpdate();
                }

                // Insert test order
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO ORDERS (order_id, order_number, description, status) " +
                        "VALUES (?, ?, ?, ?)")) {
                    stmt.setString(1, testOrderId.id());
                    stmt.setString(2, "TEST-ORDER-001");
                    stmt.setString(3, "Test Order for SqlPhotoRepository");
                    stmt.setString(4, "PENDING");
                    stmt.executeUpdate();
                }

                // Insert test photo
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
                    stmt.setString(1, "test-photo-id-1");
                    stmt.setString(2, testOrderId.id());
                    stmt.setString(3, "/path/to/test/image1.jpg");
                    stmt.setString(4, "FRONT_VIEW_OF_ASSEMBLY");
                    stmt.setString(5, "PENDING");
                    stmt.setString(6, testUser.getId().id());
                    stmt.executeUpdate();
                }

                // Insert another test photo with different status
                try (PreparedStatement stmt = connection.prepareStatement(
                        "INSERT INTO PHOTOS (photo_id, order_id, file_path, template_type, status, created_by, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, datetime('now'), datetime('now'))")) {
                    stmt.setString(1, "test-photo-id-2");
                    stmt.setString(2, testOrderId.id());
                    stmt.setString(3, "/path/to/test/image2.jpg");
                    stmt.setString(4, "SIDE_VIEW_OF_WELD");
                    stmt.setString(5, "APPROVED");
                    stmt.setString(6, testUser.getId().id());
                    stmt.executeUpdate();
                }

                // Commit the transaction
                connection.commit();
                System.out.println("[DEBUG_LOG] Test data inserted successfully");

                // Verify the data was inserted correctly
                try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PHOTOS WHERE photo_id = ?")) {
                    stmt.setString(1, "test-photo-id-1");
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("[DEBUG_LOG] Photo 1 found in database: " + rs.getString("photo_id"));
                        } else {
                            System.out.println("[DEBUG_LOG] Photo 1 NOT found in database!");
                        }
                    }
                }

                try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM PHOTOS")) {
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            System.out.println("[DEBUG_LOG] Total photos in database: " + rs.getInt(1));
                        }
                    }
                }
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                connection.rollback();
                System.out.println("[DEBUG_LOG] Error inserting test data: " + e.getMessage());
                throw e;
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            fail("Failed to insert test data: " + e.getMessage());
        }
    }

    /**
     * Test finding a photo by ID.
     */
    @Test
    @DisplayName("Find photo by ID")
    void testFindById() {
        // Arrange
        PhotoId photoId = new PhotoId("test-photo-id-1");

        // Debug: Check if the photo exists in the database
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PHOTOS WHERE photo_id = ?")) {
            stmt.setString(1, photoId.id());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG_LOG] Photo found in database before findById: " + rs.getString("photo_id"));
                } else {
                    System.out.println("[DEBUG_LOG] Photo NOT found in database before findById!");
                }
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] Error checking for photo: " + e.getMessage());
        }

        // Act
        Optional<PhotoDocument> result = photoRepository.findById(photoId);

        // Debug: Log the result
        System.out.println("[DEBUG_LOG] findById result: " + (result.isPresent() ? "found" : "not found"));

        // Assert
        assertTrue(result.isPresent(), "Photo should be found");
        assertEquals(photoId, result.get().getId(), "Photo ID should match");
        assertEquals(testOrderId.id(), result.get().getOrderId().id(), "Order ID should match");
        assertEquals("FRONT_VIEW_OF_ASSEMBLY", result.get().getTemplate().name(), "Template should match");
        assertEquals("PENDING", result.get().getStatus().name(), "Status should match");
    }

    /**
     * Test finding photos by order ID.
     */
    @Test
    @DisplayName("Find photos by order ID")
    void testFindByOrderId() {
        // Act
        List<PhotoDocument> result = photoRepository.findByOrderId(testOrderId);

        // Assert
        // We expect to find our 2 test photos plus any photos from the minimal schema
        assertTrue(result.size() >= 2, "Should find at least 2 photos for the order");
        assertTrue(result.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-1")), "Should contain first photo");
        assertTrue(result.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-2")), "Should contain second photo");
    }

    /**
     * Test finding photos by status.
     */
    @Test
    @DisplayName("Find photos by status")
    void testFindByStatus() {
        // Act
        List<PhotoDocument> pendingPhotos = photoRepository.findByStatus(PhotoDocument.ApprovalStatus.PENDING);
        List<PhotoDocument> approvedPhotos = photoRepository.findByStatus(PhotoDocument.ApprovalStatus.APPROVED);
        List<PhotoDocument> rejectedPhotos = photoRepository.findByStatus(PhotoDocument.ApprovalStatus.REJECTED);

        // Assert
        // We expect to find our test photos plus any photos from the minimal schema
        assertTrue(pendingPhotos.size() >= 1, "Should find at least 1 pending photo");
        assertTrue(approvedPhotos.size() >= 1, "Should find at least 1 approved photo");

        // Verify our test photos are in the results
        assertTrue(pendingPhotos.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-1")), "Should contain our pending test photo");
        assertTrue(approvedPhotos.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-2")), "Should contain our approved test photo");
    }

    /**
     * Test finding photos by order ID and status.
     */
    @Test
    @DisplayName("Find photos by order ID and status")
    void testFindByOrderIdAndStatus() {
        // Act
        List<PhotoDocument> pendingPhotos = photoRepository.findByOrderIdAndStatus(testOrderId, PhotoDocument.ApprovalStatus.PENDING);
        List<PhotoDocument> approvedPhotos = photoRepository.findByOrderIdAndStatus(testOrderId, PhotoDocument.ApprovalStatus.APPROVED);

        // Assert
        // We expect to find our test photos plus any photos from the minimal schema
        assertTrue(pendingPhotos.size() >= 1, "Should find at least 1 pending photo for the order");
        assertTrue(approvedPhotos.size() >= 1, "Should find at least 1 approved photo for the order");

        // Verify our test photos are in the results
        assertTrue(pendingPhotos.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-1")), "Should contain our pending test photo");
        assertTrue(approvedPhotos.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-2")), "Should contain our approved test photo");
    }

    /**
     * Test saving a new photo.
     */
    @Test
    @DisplayName("Save new photo")
    void testSaveNewPhoto() {
        // Arrange
        PhotoId photoId = new PhotoId("test-photo-id-new");
        PhotoDocument newPhoto = PhotoDocument.builder()
                .photoId(photoId)
                .orderId(testOrderId)
                .imagePath(new Photo("/path/to/test/new-image.jpg"))
                .template(PhotoTemplate.TOP_VIEW_OF_JOINT)
                .uploadedBy(testUser)
                .uploadedAt(new Timestamp(Instant.now()))
                .build();

        // Act
        PhotoDocument savedPhoto = photoRepository.save(newPhoto);

        // Assert
        assertNotNull(savedPhoto, "Saved photo should not be null");
        assertEquals(photoId, savedPhoto.getId(), "Photo ID should match");

        // Verify the photo was saved to the database
        Optional<PhotoDocument> retrievedPhoto = photoRepository.findById(photoId);
        assertTrue(retrievedPhoto.isPresent(), "Photo should be retrievable after saving");
        assertEquals(photoId, retrievedPhoto.get().getId(), "Retrieved photo ID should match");
        assertEquals(testOrderId.id(), retrievedPhoto.get().getOrderId().id(), "Retrieved order ID should match");
        assertEquals("TOP_VIEW_OF_JOINT", retrievedPhoto.get().getTemplate().name(), "Retrieved template should match");
    }

    /**
     * Test updating an existing photo.
     */
    @Test
    @DisplayName("Update existing photo")
    void testUpdatePhoto() {
        // Arrange
        PhotoId photoId = new PhotoId("test-photo-id-1");
        Optional<PhotoDocument> existingPhotoOpt = photoRepository.findById(photoId);
        assertTrue(existingPhotoOpt.isPresent(), "Photo should exist for update test");

        PhotoDocument existingPhoto = existingPhotoOpt.get();

        // Create an updated version of the photo with approved status
        PhotoDocument updatedPhoto = PhotoDocument.builder()
                .photoId(photoId)
                .orderId(existingPhoto.getOrderId())
                .imagePath(existingPhoto.getImagePath())
                .template(existingPhoto.getTemplate())
                .uploadedBy(existingPhoto.getUploadedBy())
                .uploadedAt(existingPhoto.getUploadedAt())
                .build();

        // Approve the photo
        updatedPhoto.approve(UserReference.from(testUser), new Timestamp(Instant.now()));

        // Act
        PhotoDocument savedPhoto = photoRepository.save(updatedPhoto);

        // Assert
        assertNotNull(savedPhoto, "Updated photo should not be null");
        assertEquals(PhotoDocument.ApprovalStatus.APPROVED, savedPhoto.getStatus(), "Status should be updated to APPROVED");

        // Verify the photo was updated in the database
        Optional<PhotoDocument> retrievedPhoto = photoRepository.findById(photoId);
        assertTrue(retrievedPhoto.isPresent(), "Photo should be retrievable after updating");
        assertEquals(PhotoDocument.ApprovalStatus.APPROVED, retrievedPhoto.get().getStatus(), "Retrieved status should be APPROVED");
    }

    /**
     * Test deleting a photo.
     */
    @Test
    @DisplayName("Delete photo")
    void testDeletePhoto() {
        // Arrange
        PhotoId photoId = new PhotoId("test-photo-id-1");
        Optional<PhotoDocument> existingPhotoOpt = photoRepository.findById(photoId);
        assertTrue(existingPhotoOpt.isPresent(), "Photo should exist for delete test");

        // Act
        photoRepository.delete(existingPhotoOpt.get());

        // Assert
        Optional<PhotoDocument> deletedPhotoOpt = photoRepository.findById(photoId);
        assertFalse(deletedPhotoOpt.isPresent(), "Photo should not exist after deletion");
    }

    /**
     * Test checking if a photo exists by ID.
     */
    @Test
    @DisplayName("Check if photo exists by ID")
    void testExistsById() {
        // Arrange
        PhotoId existingPhotoId = new PhotoId("test-photo-id-1");
        PhotoId nonExistingPhotoId = new PhotoId("non-existing-photo-id");

        // Act & Assert
        assertTrue(photoRepository.existsById(existingPhotoId), "Existing photo should be found");
        assertFalse(photoRepository.existsById(nonExistingPhotoId), "Non-existing photo should not be found");
    }

    /**
     * Test counting photos.
     */
    @Test
    @DisplayName("Count photos")
    void testCount() {
        // Act
        long count = photoRepository.count();

        // Assert
        // We expect to count our 2 test photos plus any photos from the minimal schema
        assertEquals(6, count, "Should count 6 photos (2 test photos + 4 from minimal schema)");
    }

    /**
     * Test finding all photos.
     */
    @Test
    @DisplayName("Find all photos")
    void testFindAll() {
        // Act
        List<PhotoDocument> allPhotos = photoRepository.findAll();

        // Assert
        // We expect to find our 2 test photos plus any photos from the minimal schema
        assertTrue(allPhotos.size() >= 2, "Should find at least 2 photos");
        assertTrue(allPhotos.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-1")), "Should contain first photo");
        assertTrue(allPhotos.stream().anyMatch(p -> p.getId().id().equals("test-photo-id-2")), "Should contain second photo");
    }
}
