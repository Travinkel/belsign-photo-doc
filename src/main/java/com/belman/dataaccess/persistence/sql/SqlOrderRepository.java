package com.belman.dataaccess.persistence.sql;

import com.belman.bootstrap.di.ServiceLocator;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerRepository;
import com.belman.domain.order.*;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.photo.PhotoId;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.specification.Specification;
import com.belman.domain.user.UserId;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserRepository;
import com.belman.domain.user.Username;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQL-based implementation of the OrderRepository interface.
 * This implementation stores orders in a SQL database.
 */
public class SqlOrderRepository implements OrderRepository {
    private static final Logger LOGGER = Logger.getLogger(SqlOrderRepository.class.getName());

    private final DataSource dataSource;

    /**
     * Creates a new SqlOrderRepository with the specified DataSource.
     *
     * @param dataSource the DataSource to use for database connections
     */
    public SqlOrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<OrderBusiness> findById(OrderId id) {
        String sql = "SELECT * FROM orderAggregates WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OrderBusiness order = mapResultSetToOrder(rs);
                    // Load photo IDs for the order
                    loadPhotos(order);
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order by id: " + id.id(), e);
        }
        return Optional.empty();
    }

    @Override
    public OrderBusiness save(OrderBusiness orderBusiness) {
        // Check if orderBusiness already exists
        String checkSql = "SELECT COUNT(*) FROM orderAggregates WHERE id = ?";
        boolean orderExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, orderBusiness.getId().id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    orderExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if orderBusiness exists: " + orderBusiness.getId().id(), e);
            throw new RuntimeException("Error checking if orderBusiness exists", e);
        }

        if (orderExists) {
            updateOrder(orderBusiness);
        } else {
            insertOrder(orderBusiness);
        }

        return orderBusiness;
    }

    @Override
    public void delete(OrderBusiness orderBusiness) {
        if (orderBusiness != null) {
            deleteById(orderBusiness.getId());
        }
    }

    @Override
    public boolean deleteById(OrderId id) {
        String sql = "DELETE FROM orderAggregates WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Order deleted successfully: " + id.id());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting order: " + id.id(), e);
        }

        return false;
    }

    @Override
    public List<OrderBusiness> findAll() {
        String sql = "SELECT * FROM orderAggregates";
        List<OrderBusiness> orderBusinesses = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                OrderBusiness order = mapResultSetToOrder(rs);
                // Load photo IDs for the order
                loadPhotos(order);
                orderBusinesses.add(order);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all orderBusinesses", e);
        }

        return orderBusinesses;
    }

    @Override
    public boolean existsById(OrderId id) {
        String sql = "SELECT COUNT(*) FROM orderAggregates WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if order exists: " + id.id(), e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM orderAggregates";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting orders", e);
        }

        return 0;
    }

    private void updateOrder(OrderBusiness orderBusiness) {
        String sql = "UPDATE orderAggregates SET order_number = ?, customer_id = ?, product_description = ?, " +
                     "delivery_information = ?, status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderBusiness.getOrderNumber() != null ? orderBusiness.getOrderNumber().value() : null);
            stmt.setString(2,
                    orderBusiness.getCustomerId() != null ? orderBusiness.getCustomerId().toString() : null);
            stmt.setString(3,
                    orderBusiness.getProductDescription() != null ? orderBusiness.getProductDescription().toString() :
                            null);
            stmt.setString(4, orderBusiness.getDeliveryInformation() != null ?
                    orderBusiness.getDeliveryInformation().toString() : null);
            stmt.setString(5, orderBusiness.getStatus().name());
            stmt.setString(6, orderBusiness.getId().id());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update order photo IDs
                updateOrderPhotoIds(conn, orderBusiness);
                LOGGER.info("OrderBusiness updated successfully: " + orderBusiness.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating orderBusiness: " + orderBusiness.getId().id(), e);
            throw new RuntimeException("Error updating orderBusiness", e);
        }
    }

    private void updateOrderPhotoIds(Connection conn, OrderBusiness orderBusiness) throws SQLException {
        // First, delete all existing photo documents for this order that are not in the current list
        // We don't actually want to delete photos, just update the list of IDs in the order
        // This is a placeholder for actual photo management logic

        // For each photo ID in the order, ensure it exists in the photo_documents table
        // This is simplified as the actual implementation would involve more complex photo management
        for (PhotoId photoId : orderBusiness.getPhotoIds()) {
            // Check if the photo exists
            String checkSql = "SELECT COUNT(*) FROM photo_documents WHERE id = ? AND order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                stmt.setString(1, photoId.id());
                stmt.setString(2, orderBusiness.getId().id());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        // Photo doesn't exist, so we would create it
                        // In a real implementation, this would involve more complex logic
                        LOGGER.info("Photo " + photoId.id() + " not found for order " + orderBusiness.getId().id());
                    }
                }
            }
        }
    }

    private void insertOrder(OrderBusiness orderBusiness) {
        String sql = "INSERT INTO orderAggregates (id, order_number, customer_id, product_description, " +
                     "delivery_information, status, created_by, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderBusiness.getId().id());
            stmt.setString(2, orderBusiness.getOrderNumber() != null ? orderBusiness.getOrderNumber().value() : null);
            stmt.setString(3,
                    orderBusiness.getCustomerId() != null ? orderBusiness.getCustomerId().toString() : null);
            stmt.setString(4,
                    orderBusiness.getProductDescription() != null ? orderBusiness.getProductDescription().toString() :
                            null);
            stmt.setString(5, orderBusiness.getDeliveryInformation() != null ?
                    orderBusiness.getDeliveryInformation().toString() : null);
            stmt.setString(6, orderBusiness.getStatus().name());
            stmt.setString(7, orderBusiness.getCreatedBy().toString()); //TODO: Use Reference Properly
            stmt.setTimestamp(8, java.sql.Timestamp.from(orderBusiness.getCreatedAt().value()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Insert photo IDs
                updateOrderPhotoIds(conn, orderBusiness);
                LOGGER.info("OrderBusiness inserted successfully: " + orderBusiness.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting orderBusiness: " + orderBusiness.getId().id(), e);
            throw new RuntimeException("Error inserting orderBusiness", e);
        }
    }

    private boolean photoExists(Connection conn, PhotoId photoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM photo_documents WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, photoId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void updatePhoto(Connection conn, PhotoDocument photo) throws SQLException {
        String sql = "UPDATE photo_documents SET image_path = ?, angle = ?, status = ?, " +
                     "reviewed_by = ?, reviewed_at = ?, review_comment = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, photo.getImagePath().path());

            // Store the angle - use named angle if available, otherwise use degrees
            // TODO: Broken
            String angleStr = photo.getTemplate().name();
            stmt.setString(2, angleStr);

            // Store the status
            stmt.setString(3, photo.getStatus().name());

            // Store the reviewed_by user ID if available
            if (photo.getReviewedBy() != null) {
                stmt.setString(4, photo.getReviewedBy().id().toString());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            // Store the reviewed_at timestamp if available
            if (photo.getReviewedAt() != null) {
                stmt.setTimestamp(5, java.sql.Timestamp.from(photo.getReviewedAt().toInstant()));
            } else {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            }

            // Store the review comment if available
            stmt.setString(6, photo.getReviewComment());

            // Where clause
            stmt.setString(7, photo.getPhotoId().toString());

            stmt.executeUpdate();
            LOGGER.info("Photo updated successfully: " + photo.getPhotoId());
        }
    }

    private void insertPhoto(Connection conn, PhotoDocument photo) throws SQLException {
        String sql =
                "INSERT INTO photo_documents (id, order_id, image_path, angle, status, uploaded_by, uploaded_at, " +
                "reviewed_by, reviewed_at, review_comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, photo.getPhotoId().toString());
            stmt.setString(2, photo.getOrderId().id());
            stmt.setString(3, photo.getImagePath().value());

            // Store the angle - use named angle if available, otherwise use degrees
            String angleStr = photo.getTemplate().name();
            stmt.setString(4, angleStr);

            // Store the status
            stmt.setString(5, photo.getStatus().name());

            // Store the uploaded_by user ID
            stmt.setString(6, photo.getUploadedBy().getId().id());

            // Store the uploaded_at timestamp
            stmt.setTimestamp(7, java.sql.Timestamp.from(photo.getUploadedAt().toInstant()));

            // Store the reviewed_by user ID if available
            if (photo.getReviewedBy() != null) {
                stmt.setString(8, photo.getReviewedBy().id().toString());
            } else {
                stmt.setNull(8, java.sql.Types.VARCHAR);
            }

            // Store the reviewed_at timestamp if available
            if (photo.getReviewedAt() != null) {
                stmt.setTimestamp(9, java.sql.Timestamp.from(photo.getReviewedAt().toInstant()));
            } else {
                stmt.setNull(9, java.sql.Types.TIMESTAMP);
            }

            // Store the review comment if available
            stmt.setString(10, photo.getReviewComment());

            stmt.executeUpdate();
            LOGGER.info("Photo inserted successfully: " + photo.getPhotoId());
        }
    }

    private OrderBusiness mapResultSetToOrder(ResultSet rs) throws SQLException {
        OrderId id = new OrderId(rs.getString("id"));

        // Get the created_by user ID and fetch the user
        UserId createdById = new UserId(rs.getString("created_by"));
        UserReference createdBy = fetchUser(createdById);

        // Get the creation timestamp
        java.sql.Timestamp sqlTimestamp = rs.getTimestamp("created_at");
        Timestamp createdAt = new Timestamp(sqlTimestamp.toInstant());

        // Create the orderBusiness with the required fields
        OrderBusiness orderBusiness = new OrderBusiness(id, createdBy, createdAt);

        // Set optional fields
        String orderNumberStr = rs.getString("order_number");
        if (orderNumberStr != null) {
            orderBusiness.setOrderNumber(new OrderNumber(orderNumberStr));
        }

        String customerId = rs.getString("customer_id");
        if (customerId != null) {
            CustomerBusiness customer = fetchCustomer(new CustomerId("dwd"));
            if (customer != null) {
                orderBusiness.setCustomerId(customer.getId());
            }
        }

        String productDescriptionStr = rs.getString("product_description");
        if (productDescriptionStr != null) {
            // This is simplified - in a real implementation, we would parse the product description properly
        }

        String deliveryInfoStr = rs.getString("delivery_information");
        if (deliveryInfoStr != null) {
            // This is simplified - in a real implementation, we would parse the delivery information properly
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            orderBusiness.setStatus(OrderStatus.valueOf(statusStr));
        }

        // Load photos for this orderBusiness
        loadPhotos(orderBusiness);

        return orderBusiness;
    }

    private UserReference fetchUser(UserId userId) {
        try {
            // Get the UserRepository from the ServiceLocator
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
            // Use the UserRepository to find the user by ID
            return userRepository.findById(userId)
                .map(user -> new UserReference(user.getId(), user.getUsername()))
                .orElse(null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching user: " + userId.id(), e);
            // Return a placeholder user reference in case of error
            return new UserReference(userId, new Username("unknown-user"));
        }
    }

    private CustomerBusiness fetchCustomer(CustomerId customerId) {
        try {
            // Get the CustomerRepository from the ServiceLocator
            CustomerRepository customerRepository = ServiceLocator.getService(CustomerRepository.class);
            // Use the CustomerRepository to find the customer by ID
            return customerRepository.findById(customerId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching customer: " + customerId.id(), e);
            // Log the error but don't crash the application
            LOGGER.warning("Using placeholder customer for ID: " + customerId.id());

            // Return a placeholder customer to avoid null pointer exceptions
            // In a real implementation, this would be a more sophisticated fallback
            return null; // This is still null for now as we'd need to create a proper CustomerBusiness constructor
        }
    }

    private void loadPhotos(OrderBusiness orderBusiness) {
        String sql = "SELECT id FROM photo_documents WHERE order_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderBusiness.getId().id());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String photoIdStr = rs.getString("photo_id");
                    if (photoIdStr != null && !photoIdStr.isEmpty()) {
                        PhotoId photoId = new PhotoId(photoIdStr);
                        orderBusiness.addPhotoId(photoId);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading photo IDs for orderBusiness: " + orderBusiness.getId().id(), e);
        }
    }

    // The mapResultSetToPhoto method has been removed as it's no longer needed.
    // Photos are now managed by the PhotoRepository directly.

    @Override
    public List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec) {
        // For simplicity, we'll load all orders and filter in memory
        // In a real implementation, this would translate the specification to SQL
        return findAll().stream()
                .filter(spec::isSatisfiedBy)
                .toList();
    }

    @Override
    public Optional<OrderBusiness> findByOrderNumber(OrderNumber orderNumber) {
        String sql = "SELECT * FROM orderAggregates WHERE order_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderNumber.value());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    OrderBusiness order = mapResultSetToOrder(rs);
                    // Load photo IDs for the order
                    loadPhotos(order);
                    return Optional.of(order);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order by order number: " + orderNumber.value(), e);
        }

        return Optional.empty();
    }

    /**
     * Creates a PhotoTemplate from a string representation.
     * Tries to match the string to a predefined template, or creates a custom template.
     *
     * @param angleStr the string representation of the template
     * @return a PhotoTemplate object
     */
    private PhotoTemplate createPhotoAngle(String angleStr) {
        if (angleStr == null || angleStr.isBlank()) {
            // Default to FRONT if no angle is specified
            return PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        }

        // Try to match with predefined templates
        switch (angleStr.toUpperCase()) {
            case "TOP_VIEW_OF_JOINT":
                return PhotoTemplate.TOP_VIEW_OF_JOINT;
            case "SIDE_VIEW_OF_WELD":
                return PhotoTemplate.SIDE_VIEW_OF_WELD;
            case "FRONT_VIEW_OF_ASSEMBLY":
                return PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
            case "BACK_VIEW_OF_ASSEMBLY":
                return PhotoTemplate.BACK_VIEW_OF_ASSEMBLY;
            case "LEFT_VIEW_OF_ASSEMBLY":
                return PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY;
            case "RIGHT_VIEW_OF_ASSEMBLY":
                return PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY;
            case "BOTTOM_VIEW_OF_ASSEMBLY":
                return PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY;
            case "CLOSE_UP_OF_WELD":
                return PhotoTemplate.CLOSE_UP_OF_WELD;
            case "ANGLED_VIEW_OF_JOINT":
                return PhotoTemplate.ANGLED_VIEW_OF_JOINT;
            case "OVERVIEW_OF_ASSEMBLY":
                return PhotoTemplate.OVERVIEW_OF_ASSEMBLY;
            default:
                // Create a custom template if no predefined template matches
                return new PhotoTemplate(angleStr, "Custom template: " + angleStr);
        }
    }
}
