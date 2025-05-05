package com.belman.infrastructure.persistence;

import com.belman.domain.aggregates.Order;
import com.belman.domain.aggregates.User;
import com.belman.domain.entities.Customer;
import com.belman.domain.entities.PhotoDocument;
import com.belman.domain.enums.OrderStatus;
import com.belman.domain.repositories.OrderRepository;
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
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.repositories.CustomerRepository;
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
    public Order findById(OrderId id) {
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
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all orders", e);
        }

        return orders;
    }

    @Override
    public List<Order> findBySpecification(Specification<Order> spec) {
        // For simplicity, we'll load all orders and filter in memory
        // In a real implementation, this would translate the specification to SQL
        return findAll().stream()
                .filter(spec::isSatisfiedBy)
                .toList();
    }

    @Override
    public void save(Order order) {
        // Check if order already exists
        String checkSql = "SELECT COUNT(*) FROM orders WHERE id = ?";
        boolean orderExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, order.getId().id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    orderExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if order exists: " + order.getId().id(), e);
            throw new RuntimeException("Error checking if order exists", e);
        }

        if (orderExists) {
            updateOrder(order);
        } else {
            insertOrder(order);
        }
    }

    @Override
    public Optional<Order> findByOrderNumber(OrderNumber orderNumber) {
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

    private void insertOrder(Order order) {
        String sql = "INSERT INTO orders (id, order_number, customer_id, product_description, " +
                     "delivery_information, status, created_by, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getId().id().toString());
            stmt.setString(2, order.getOrderNumber() != null ? order.getOrderNumber().value() : null);
            stmt.setString(3, order.getCustomer() != null ? order.getCustomer().getId().id().toString() : null);
            stmt.setString(4, order.getProductDescription() != null ? order.getProductDescription().toString() : null);
            stmt.setString(5, order.getDeliveryInformation() != null ? order.getDeliveryInformation().toString() : null);
            stmt.setString(6, order.getStatus().name());
            stmt.setString(7, order.getCreatedBy().getId().id().toString());
            stmt.setTimestamp(8, java.sql.Timestamp.from(order.getCreatedAt().toInstant()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Insert photos
                for (PhotoDocument photo : order.getPhotos()) {
                    insertPhoto(conn, photo);
                }
                LOGGER.info("Order inserted successfully: " + order.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting order: " + order.getId().id(), e);
            throw new RuntimeException("Error inserting order", e);
        }
    }

    private void updateOrder(Order order) {
        String sql = "UPDATE orders SET order_number = ?, customer_id = ?, product_description = ?, " +
                     "delivery_information = ?, status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getOrderNumber() != null ? order.getOrderNumber().value() : null);
            stmt.setString(2, order.getCustomer() != null ? order.getCustomer().getId().id().toString() : null);
            stmt.setString(3, order.getProductDescription() != null ? order.getProductDescription().toString() : null);
            stmt.setString(4, order.getDeliveryInformation() != null ? order.getDeliveryInformation().toString() : null);
            stmt.setString(5, order.getStatus().name());
            stmt.setString(6, order.getId().id().toString());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update photos (this is simplified - in a real implementation, we would need to handle photo updates more carefully)
                for (PhotoDocument photo : order.getPhotos()) {
                    if (photoExists(conn, photo.getPhotoId())) {
                        updatePhoto(conn, photo);
                    } else {
                        insertPhoto(conn, photo);
                    }
                }
                LOGGER.info("Order updated successfully: " + order.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating order: " + order.getId().id(), e);
            throw new RuntimeException("Error updating order", e);
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

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        OrderId id = new OrderId(UUID.fromString(rs.getString("id")));

        // Get the created_by user ID and fetch the user
        UserId createdById = new UserId(UUID.fromString(rs.getString("created_by")));
        User createdBy = fetchUser(createdById);

        // Get the creation timestamp
        java.sql.Timestamp sqlTimestamp = rs.getTimestamp("created_at");
        Timestamp createdAt = new Timestamp(sqlTimestamp.toInstant());

        // Create the order with the required fields
        Order order = new Order(id, createdBy, createdAt);

        // Set optional fields
        String orderNumberStr = rs.getString("order_number");
        if (orderNumberStr != null) {
            order.setOrderNumber(new OrderNumber(orderNumberStr));
        }

        String customerId = rs.getString("customer_id");
        if (customerId != null) {
            Customer customer = fetchCustomer(new CustomerId(UUID.fromString(customerId)));
            if (customer != null) {
                order.setCustomer(customer);
            }
        }

        String productDescriptionStr = rs.getString("product_description");
        if (productDescriptionStr != null) {
            // This is simplified - in a real implementation, we would parse the product description properly
            order.setProductDescription(ProductDescription.withName(productDescriptionStr));
        }

        String deliveryInfoStr = rs.getString("delivery_information");
        if (deliveryInfoStr != null) {
            // This is simplified - in a real implementation, we would parse the delivery information properly
            order.setDeliveryInformation(DeliveryInformation.basic(deliveryInfoStr, Timestamp.now()));
        }

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            order.setStatus(OrderStatus.valueOf(statusStr));
        }

        // Load photos for this order
        loadPhotos(order);

        return order;
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

    private Customer fetchCustomer(CustomerId customerId) {
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

    private void loadPhotos(Order order) {
        String sql = "SELECT * FROM photo_documents WHERE order_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, order.getId().id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PhotoDocument photo = mapResultSetToPhoto(rs);
                    if (photo != null) {
                        order.addPhoto(photo);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading photos for order: " + order.getId().id(), e);
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
