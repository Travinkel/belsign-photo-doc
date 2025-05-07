package com.belman.infrastructure.persistence;

import com.belman.domain.aggregates.User;
import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.photo.PhotoDocument;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.specification.Specification;
import com.belman.domain.valueobjects.CustomerId;
import com.belman.domain.valueobjects.DeliveryInformation;
import com.belman.domain.valueobjects.OrderId;
import com.belman.domain.valueobjects.OrderNumber;
import com.belman.domain.valueobjects.PhotoId;
import com.belman.domain.valueobjects.ProductDescription;
import com.belman.domain.valueobjects.Timestamp;
import com.belman.domain.valueobjects.UserId;
import com.belman.domain.valueobjects.PhotoAngle;
import com.belman.domain.valueobjects.ImagePath;
import com.belman.domain.user.UserRepository;
import com.belman.domain.customer.CustomerRepository;
import com.belman.application.core.ServiceLocator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    public OrderAggregate findById(OrderId id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order by id: " + id.id(), e);
        }

        return null;
    }

    @Override
    public List<OrderAggregate> findAll() {
        String sql = "SELECT * FROM orders";
        List<OrderAggregate> orderAggregates = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orderAggregates.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all orderAggregates", e);
        }

        return orderAggregates;
    }

    @Override
    public List<OrderAggregate> findBySpecification(Specification<OrderAggregate> spec) {
        // For simplicity, we'll load all orders and filter in memory
        // In a real implementation, this would translate the specification to SQL
        return findAll().stream()
                .filter(spec::isSatisfiedBy)
                .toList();
    }

    @Override
    public void save(OrderAggregate orderAggregate) {
        // Check if orderAggregate already exists
        String checkSql = "SELECT COUNT(*) FROM orders WHERE id = ?";
        boolean orderExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, orderAggregate.getId().id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    orderExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if orderAggregate exists: " + orderAggregate.getId().id(), e);
            throw new RuntimeException("Error checking if orderAggregate exists", e);
        }

        if (orderExists) {
            updateOrder(orderAggregate);
        } else {
            insertOrder(orderAggregate);
        }
    }

    @Override
    public Optional<OrderAggregate> findByOrderNumber(OrderNumber orderNumber) {
        String sql = "SELECT * FROM orders WHERE order_number = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderNumber.value());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order by order number: " + orderNumber.value(), e);
        }

        return Optional.empty();
    }

    private void insertOrder(OrderAggregate orderAggregate) {
        String sql = "INSERT INTO orders (id, order_number, customer_id, product_description, " +
                     "delivery_information, status, created_by, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderAggregate.getId().id().toString());
            stmt.setString(2, orderAggregate.getOrderNumber() != null ? orderAggregate.getOrderNumber().value() : null);
            stmt.setString(3, orderAggregate.getCustomer() != null ? orderAggregate.getCustomer().getId().id().toString() : null);
            stmt.setString(4, orderAggregate.getProductDescription() != null ? orderAggregate.getProductDescription().toString() : null);
            stmt.setString(5, orderAggregate.getDeliveryInformation() != null ? orderAggregate.getDeliveryInformation().toString() : null);
            stmt.setString(6, orderAggregate.getStatus().name());
            stmt.setString(7, orderAggregate.getCreatedBy().getId().id().toString());
            stmt.setTimestamp(8, java.sql.Timestamp.from(orderAggregate.getCreatedAt().toInstant()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Insert photos
                for (PhotoDocument photo : orderAggregate.getPhotos()) {
                    insertPhoto(conn, photo);
                }
                LOGGER.info("OrderAggregate inserted successfully: " + orderAggregate.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting orderAggregate: " + orderAggregate.getId().id(), e);
            throw new RuntimeException("Error inserting orderAggregate", e);
        }
    }

    private void updateOrder(OrderAggregate orderAggregate) {
        String sql = "UPDATE orders SET order_number = ?, customer_id = ?, product_description = ?, " +
                     "delivery_information = ?, status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderAggregate.getOrderNumber() != null ? orderAggregate.getOrderNumber().value() : null);
            stmt.setString(2, orderAggregate.getCustomer() != null ? orderAggregate.getCustomer().getId().id().toString() : null);
            stmt.setString(3, orderAggregate.getProductDescription() != null ? orderAggregate.getProductDescription().toString() : null);
            stmt.setString(4, orderAggregate.getDeliveryInformation() != null ? orderAggregate.getDeliveryInformation().toString() : null);
            stmt.setString(5, orderAggregate.getStatus().name());
            stmt.setString(6, orderAggregate.getId().id().toString());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update photos (this is simplified - in a real implementation, we would need to handle photo updates more carefully)
                for (PhotoDocument photo : orderAggregate.getPhotos()) {
                    if (photoExists(conn, photo.getPhotoId())) {
                        updatePhoto(conn, photo);
                    } else {
                        insertPhoto(conn, photo);
                    }
                }
                LOGGER.info("OrderAggregate updated successfully: " + orderAggregate.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating orderAggregate: " + orderAggregate.getId().id(), e);
            throw new RuntimeException("Error updating orderAggregate", e);
        }
    }

    private boolean photoExists(Connection conn, PhotoId photoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM photo_documents WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, photoId.value().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private void insertPhoto(Connection conn, PhotoDocument photo) throws SQLException {
        String sql = "INSERT INTO photo_documents (id, order_id, image_path, angle, status, uploaded_by, uploaded_at, " +
                     "reviewed_by, reviewed_at, review_comment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, photo.getPhotoId().value().toString());
            stmt.setString(2, photo.getOrderId().id().toString());
            stmt.setString(3, photo.getImagePath().path());

            // Store the angle - use named angle if available, otherwise use degrees
            String angleStr = photo.getAngle().isNamedAngle() ? 
                photo.getAngle().namedAngle().name() : 
                String.valueOf(photo.getAngle().degrees());
            stmt.setString(4, angleStr);

            // Store the status
            stmt.setString(5, photo.getStatus().name());

            // Store the uploaded_by user ID
            stmt.setString(6, photo.getUploadedBy().getId().id().toString());

            // Store the uploaded_at timestamp
            stmt.setTimestamp(7, java.sql.Timestamp.from(photo.getUploadedAt().toInstant()));

            // Store the reviewed_by user ID if available
            if (photo.getReviewedBy() != null) {
                stmt.setString(8, photo.getReviewedBy().getId().id().toString());
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
            LOGGER.info("Photo inserted successfully: " + photo.getPhotoId().value());
        }
    }

    private void updatePhoto(Connection conn, PhotoDocument photo) throws SQLException {
        String sql = "UPDATE photo_documents SET image_path = ?, angle = ?, status = ?, " +
                     "reviewed_by = ?, reviewed_at = ?, review_comment = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, photo.getImagePath().path());

            // Store the angle - use named angle if available, otherwise use degrees
            String angleStr = photo.getAngle().isNamedAngle() ? 
                photo.getAngle().namedAngle().name() : 
                String.valueOf(photo.getAngle().degrees());
            stmt.setString(2, angleStr);

            // Store the status
            stmt.setString(3, photo.getStatus().name());

            // Store the reviewed_by user ID if available
            if (photo.getReviewedBy() != null) {
                stmt.setString(4, photo.getReviewedBy().getId().id().toString());
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
            stmt.setString(7, photo.getPhotoId().value().toString());

            stmt.executeUpdate();
            LOGGER.info("Photo updated successfully: " + photo.getPhotoId().value());
        }
    }

    private OrderAggregate mapResultSetToOrder(ResultSet rs) throws SQLException {
        OrderId id = new OrderId(UUID.fromString(rs.getString("id")));

        // Get the created_by user ID and fetch the user
        UserId createdById = new UserId(UUID.fromString(rs.getString("created_by")));
        User createdBy = fetchUser(createdById);

        // Get the creation timestamp
        java.sql.Timestamp sqlTimestamp = rs.getTimestamp("created_at");
        Timestamp createdAt = new Timestamp(sqlTimestamp.toInstant());

        // Create the orderAggregate with the required fields
        OrderAggregate orderAggregate = new OrderAggregate(id, createdBy, createdAt);

        // Set optional fields
        String orderNumberStr = rs.getString("order_number");
        if (orderNumberStr != null) {
            orderAggregate.setOrderNumber(new OrderNumber(orderNumberStr));
        }

        String customerId = rs.getString("customer_id");
        if (customerId != null) {
            CustomerAggregate customer = fetchCustomer(new CustomerId(UUID.fromString(customerId)));
            if (customer != null) {
                orderAggregate.setCustomer(customer);
            }
        }

        String productDescriptionStr = rs.getString("product_description");
        if (productDescriptionStr != null) {
            // This is simplified - in a real implementation, we would parse the product description properly
            orderAggregate.setProductDescription(ProductDescription.withName(productDescriptionStr));
        }

        String deliveryInfoStr = rs.getString("delivery_information");
        if (deliveryInfoStr != null) {
            // This is simplified - in a real implementation, we would parse the delivery information properly
            orderAggregate.setDeliveryInformation(DeliveryInformation.basic(deliveryInfoStr, Timestamp.now()));
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            orderAggregate.setStatus(OrderStatus.valueOf(statusStr));
        }

        // Load photos for this orderAggregate
        loadPhotos(orderAggregate);

        return orderAggregate;
    }

    private User fetchUser(UserId userId) {
        try {
            // Get the UserRepository from the ServiceLocator
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);
            // Use the UserRepository to find the user by ID
            return userRepository.findById(userId).orElse(null);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching user: " + userId.id(), e);
        }
        return null;
    }

    private CustomerAggregate fetchCustomer(CustomerId customerId) {
        try {
            // Get the CustomerRepository from the ServiceLocator
            CustomerRepository customerRepository = ServiceLocator.getService(CustomerRepository.class);
            // Use the CustomerRepository to find the customer by ID
            return customerRepository.findById(customerId);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching customer: " + customerId.id(), e);
        }
        return null;
    }

    private void loadPhotos(OrderAggregate orderAggregate) {
        String sql = "SELECT * FROM photo_documents WHERE order_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderAggregate.getId().id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PhotoDocument photo = mapResultSetToPhoto(rs);
                    if (photo != null) {
                        orderAggregate.addPhoto(photo);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading photos for orderAggregate: " + orderAggregate.getId().id(), e);
        }
    }

    private PhotoDocument mapResultSetToPhoto(ResultSet rs) throws SQLException {
        // This is a simplified implementation - in a real implementation, we would map all fields properly
        try {
            PhotoId photoId = new PhotoId(UUID.fromString(rs.getString("id")));
            OrderId orderId = new OrderId(UUID.fromString(rs.getString("order_id")));
            String imagePath = rs.getString("image_path");
            String angle = rs.getString("angle");
            String status = rs.getString("status");

            // Get the uploaded_by user ID and fetch the user
            UserId uploadedById = new UserId(UUID.fromString(rs.getString("uploaded_by")));
            User uploadedBy = fetchUser(uploadedById);

            // Get the upload timestamp
            java.sql.Timestamp sqlTimestamp = rs.getTimestamp("uploaded_at");
            Timestamp uploadedAt = new Timestamp(sqlTimestamp.toInstant());

            // Create the photo document
            PhotoDocument photo = new PhotoDocument(
                photoId, 
                createPhotoAngle(angle), 
                new ImagePath(imagePath), 
                uploadedBy, 
                uploadedAt
            );

            // Set the order ID
            photo.assignToOrder(orderId);

            return photo;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error mapping result set to photo", e);
            return null;
        }
    }

    /**
     * Creates a PhotoAngle from a string representation.
     * Tries to parse the string as a named angle first, then as a custom angle (degrees).
     * 
     * @param angleStr the string representation of the angle
     * @return a PhotoAngle object
     */
    private PhotoAngle createPhotoAngle(String angleStr) {
        if (angleStr == null || angleStr.isBlank()) {
            // Default to FRONT if no angle is specified
            return new PhotoAngle(PhotoAngle.NamedAngle.FRONT);
        }

        // Try to parse as a named angle
        try {
            PhotoAngle.NamedAngle namedAngle = PhotoAngle.NamedAngle.valueOf(angleStr.toUpperCase());
            return new PhotoAngle(namedAngle);
        } catch (IllegalArgumentException e) {
            // Not a named angle, try to parse as degrees
            try {
                double degrees = Double.parseDouble(angleStr.replace("°", ""));
                return new PhotoAngle(degrees);
            } catch (NumberFormatException ex) {
                // Neither a named angle nor a valid number, default to FRONT
                LOGGER.warning("Invalid angle format: " + angleStr + ", defaulting to FRONT");
                return new PhotoAngle(PhotoAngle.NamedAngle.FRONT);
            }
        }
    }
}
