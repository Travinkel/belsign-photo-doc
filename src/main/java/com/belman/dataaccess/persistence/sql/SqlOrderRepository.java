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
        String sql = "SELECT * FROM ORDERS WHERE order_id = ?";
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Finding order by ID: " + id.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Executing SQL: " + sql + " with order_id = " + id.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Order found in database, mapping to OrderBusiness");
                    OrderBusiness order = mapResultSetToOrder(rs);

                    // Load photo IDs for the order
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Loading photos for order");
                    loadPhotos(order);

                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Successfully retrieved order: " + 
                                      "ID=" + order.getId().id() + 
                                      ", Number=" + (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") + 
                                      ", Status=" + order.getStatus() + 
                                      ", AssignedTo=" + (order.getAssignedTo() != null ? order.getAssignedTo().id().id() : "null"));
                    return Optional.of(order);
                } else {
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: No order found with ID: " + id.id());
                }
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Error finding order by ID: " + e.getMessage());
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error finding order by id: " + id.id(), e);
        }
        return Optional.empty();
    }

    @Override
    public OrderBusiness save(OrderBusiness orderBusiness) {
        // Check if orderBusiness already exists
        String checkSql = "SELECT COUNT(*) FROM ORDERS WHERE order_id = ?";
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
        String sql = "DELETE FROM ORDERS WHERE order_id = ?";

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
        String sql = "SELECT * FROM ORDERS";
        List<OrderBusiness> orderBusinesses = new ArrayList<>();

        System.out.println("[DEBUG_LOG] SqlOrderRepository: Executing findAll() to retrieve all orders");
        LOGGER.info("Loading orders from database");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("[DEBUG_LOG] SqlOrderRepository: SQL query executed: " + sql);
            LOGGER.fine("Executing SQL query: " + sql);

            int count = 0;
            int validOrderNumbers = 0;
            int invalidOrderNumbers = 0;

            while (rs.next()) {
                count++;
                String orderId = rs.getString("order_id");
                String orderNumberStr = rs.getString("order_number");

                System.out.println("[DEBUG_LOG] SqlOrderRepository: Processing order #" + count + 
                                  " from result set, ID=" + orderId + 
                                  ", Number=" + (orderNumberStr != null ? orderNumberStr : "null"));

                try {
                    OrderBusiness order = mapResultSetToOrder(rs);

                    // Validate order number
                    if (order.getOrderNumber() != null) {
                        validOrderNumbers++;
                        System.out.println("[DEBUG_LOG] SqlOrderRepository: Valid order number: " + order.getOrderNumber().value());
                    } else if (orderNumberStr != null) {
                        invalidOrderNumbers++;
                        System.out.println("[DEBUG_LOG] SqlOrderRepository: Invalid order number format: " + orderNumberStr);
                        LOGGER.warning("Order " + orderId + " has invalid order number format: " + orderNumberStr);
                    } else {
                        System.out.println("[DEBUG_LOG] SqlOrderRepository: Order has no order number");
                        LOGGER.warning("Order " + orderId + " has no order number");
                    }

                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Mapped order: ID=" + order.getId().id() + 
                                      ", Number=" + (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") + 
                                      ", Status=" + order.getStatus() + 
                                      ", AssignedTo=" + (order.getAssignedTo() != null ? order.getAssignedTo().id().id() : "null"));

                    // Load photo IDs for the order
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Loading photos for order: " + order.getId().id());
                    loadPhotos(order);

                    // Log detailed order information at trace level
                    LOGGER.fine("Loaded order: ID=" + order.getId().id() +
                               ", Number=" + (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") +
                               ", Status=" + order.getStatus() +
                               ", AssignedTo=" + (order.getAssignedTo() != null ? order.getAssignedTo().id().id() : "null"));

                    orderBusinesses.add(order);
                } catch (Exception e) {
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Error processing order #" + count + ": " + e.getMessage());
                    LOGGER.log(Level.WARNING, "Error processing order " + orderId + ": " + e.getMessage(), e);
                }
            }

            System.out.println("[DEBUG_LOG] SqlOrderRepository: Found " + orderBusinesses.size() + " total orders");
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Valid order numbers: " + validOrderNumbers + 
                              ", Invalid order numbers: " + invalidOrderNumbers);

            LOGGER.info("Loaded " + orderBusinesses.size() + " orders from database");
            LOGGER.info("Order number statistics: " + validOrderNumbers + " valid, " + 
                       invalidOrderNumbers + " invalid");

        } catch (SQLException e) {
            String errorMessage = "Error loading orders from database: " + e.getMessage();
            System.out.println("[DEBUG_LOG] SqlOrderRepository: " + errorMessage);
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, errorMessage, e);
        }

        return orderBusinesses;
    }

    @Override
    public boolean existsById(OrderId id) {
        String sql = "SELECT COUNT(*) FROM ORDERS WHERE order_id = ?";

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
        String sql = "SELECT COUNT(*) FROM ORDERS";

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
        String sql = "UPDATE ORDERS SET order_number = ?, customer_id = ?, product_description = ?, " +
                     "delivery_address = ?, status = ?, assigned_to = ? WHERE order_id = ?";

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

            // Set assigned_to
            if (orderBusiness.getAssignedTo() != null) {
                stmt.setString(6, orderBusiness.getAssignedTo().id().id());
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }

            stmt.setString(7, orderBusiness.getId().id());

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

        // For each photo ID in the order, ensure it exists in the PHOTOS table
        // This is simplified as the actual implementation would involve more complex photo management
        for (PhotoId photoId : orderBusiness.getPhotoIds()) {
            // Check if the photo exists
            String checkSql = "SELECT COUNT(*) FROM PHOTOS WHERE photo_id = ? AND order_id = ?";
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
        String sql = "INSERT INTO ORDERS (order_id, order_number, customer_id, product_description, " +
                     "delivery_address, status, created_by, created_at, assigned_to) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            // Set assigned_to
            if (orderBusiness.getAssignedTo() != null) {
                stmt.setString(9, orderBusiness.getAssignedTo().id().id());
            } else {
                stmt.setNull(9, java.sql.Types.VARCHAR);
            }

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
        String sql = "SELECT COUNT(*) FROM PHOTOS WHERE photo_id = ?";
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
        String sql = "UPDATE PHOTOS SET image_path = ?, template_id = ?, status = ?, " +
                     "reviewed_by = ?, reviewed_at = ?, comments = ? WHERE photo_id = ?";

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
                "INSERT INTO PHOTOS (photo_id, order_id, image_path, template_id, status, uploaded_by, uploaded_at, " +
                "reviewed_by, reviewed_at, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        String orderId = rs.getString("order_id");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Mapping ResultSet to OrderBusiness for order ID: " + orderId);

        OrderId id = new OrderId(orderId);

        // Get the created_by user ID and fetch the user
        String createdByIdStr = rs.getString("created_by");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Order created by user ID: " + createdByIdStr);

        UserId createdById = new UserId(createdByIdStr);
        UserReference createdBy = fetchUser(createdById);
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Fetched creator user: " + 
                          (createdBy != null ? createdBy.username().value() : "null"));

        // Get the creation timestamp
        java.sql.Timestamp sqlTimestamp = rs.getTimestamp("created_at");
        Timestamp createdAt = new Timestamp(sqlTimestamp.toInstant());
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Order creation timestamp: " + createdAt.value());

        // Create the orderBusiness with the required fields
        OrderBusiness orderBusiness = new OrderBusiness(id, createdBy, createdAt);
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Created base OrderBusiness object");

        // Set optional fields
        String orderNumberStr = rs.getString("order_number");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Order number from DB: " + orderNumberStr);
        if (orderNumberStr != null) {
            try {
                orderBusiness.setOrderNumber(new OrderNumber(orderNumberStr));
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Set order number: " + orderNumberStr);
            } catch (IllegalArgumentException e) {
                // Log a warning if the order number format is invalid
                System.out.println("[DEBUG_LOG] SqlOrderRepository: WARNING - Invalid order number format: " + orderNumberStr + ", " + e.getMessage());
                LOGGER.warning("Invalid order number format in database: " + orderNumberStr + ", " + e.getMessage());

                // Try to create a valid order number from the invalid one
                try {
                    // If it starts with "ORD-" but doesn't match the exact pattern, try to fix it
                    if (orderNumberStr.startsWith("ORD-")) {
                        // Extract parts and try to create a valid legacy format
                        String[] parts = orderNumberStr.split("-");
                        if (parts.length >= 5) {
                            // Ensure each part has the correct format
                            String prefix = "ORD";
                            String number = parts[1].length() == 2 ? parts[1] : String.format("%02d", Integer.parseInt(parts[1]));
                            String date = parts[2].length() == 6 ? parts[2] : "230101"; // Default date if invalid
                            String code = parts[3].length() == 3 ? parts[3].toUpperCase() : "XXX"; // Default code if invalid
                            String sequence = parts[4].length() == 4 ? parts[4] : "0001"; // Default sequence if invalid

                            String fixedOrderNumber = prefix + "-" + number + "-" + date + "-" + code + "-" + sequence;
                            System.out.println("[DEBUG_LOG] SqlOrderRepository: Attempting to fix order number: " + fixedOrderNumber);

                            try {
                                orderBusiness.setOrderNumber(new OrderNumber(fixedOrderNumber));
                                System.out.println("[DEBUG_LOG] SqlOrderRepository: Successfully fixed order number to: " + fixedOrderNumber);
                                LOGGER.info("Fixed invalid order number format: " + orderNumberStr + " -> " + fixedOrderNumber);
                            } catch (IllegalArgumentException ex) {
                                System.out.println("[DEBUG_LOG] SqlOrderRepository: Failed to fix order number: " + ex.getMessage());
                                // Leave order number as null
                            }
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Error trying to fix order number: " + ex.getMessage());
                    // Leave order number as null
                }
            }
        }

        String customerId = rs.getString("customer_id");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Customer ID from DB: " + customerId);
        if (customerId != null) {
            try {
                CustomerBusiness customer = fetchCustomer(new CustomerId(customerId));
                if (customer != null) {
                    orderBusiness.setCustomerId(customer.getId());
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Set customer ID: " + customer.getId().id());
                }
            } catch (Exception e) {
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Error fetching customer with ID: " + customerId + ", " + e.getMessage());
                // Set the customer ID directly from the database
                orderBusiness.setCustomerId(new CustomerId(customerId));
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Set customer ID directly: " + customerId);
            }
        }

        String productDescriptionStr = rs.getString("product_description");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Product description from DB: " + productDescriptionStr);
        if (productDescriptionStr != null) {
            // This is simplified - in a real implementation, we would parse the product description properly
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Product description parsing not implemented");
        }

        try {
            String deliveryAddressStr = rs.getString("delivery_address");
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Delivery address from DB: " + deliveryAddressStr);
            if (deliveryAddressStr != null) {
                // This is simplified - in a real implementation, we would parse the delivery information properly
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Delivery information parsing not implemented");
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Delivery address column not found or error accessing it: " + e.getMessage());
        }

        String statusStr = rs.getString("status");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Status from DB: " + statusStr);
        if (statusStr != null) {
            orderBusiness.setStatus(OrderStatus.valueOf(statusStr));
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Set order status: " + statusStr);
        }

        // Set assigned_to if available
        String assignedToId = rs.getString("assigned_to");
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Assigned to user ID from DB: " + assignedToId);

        if (assignedToId != null) {
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Fetching assigned user with ID: " + assignedToId);
            UserReference assignedTo = fetchUser(new UserId(assignedToId));

            if (assignedTo != null) {
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Setting order assignedTo: ID=" + 
                                  assignedTo.id().id() + ", Username=" + assignedTo.username().value());
                orderBusiness.setAssignedTo(assignedTo);
            } else {
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Failed to fetch assigned user, assignedTo will be null");
            }
        } else {
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Order is not assigned to any user");
        }

        System.out.println("[DEBUG_LOG] SqlOrderRepository: Completed mapping order: " + 
                          "ID=" + orderBusiness.getId().id() + 
                          ", Number=" + (orderBusiness.getOrderNumber() != null ? orderBusiness.getOrderNumber().value() : "null") + 
                          ", Status=" + orderBusiness.getStatus() + 
                          ", AssignedTo=" + (orderBusiness.getAssignedTo() != null ? 
                                           orderBusiness.getAssignedTo().id().id() : "null"));

        return orderBusiness;
    }

    private UserReference fetchUser(UserId userId) {
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Fetching user with ID: " + userId.id());

        try {
            // Get the UserRepository from the ServiceLocator
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Getting UserRepository from ServiceLocator");
            UserRepository userRepository = ServiceLocator.getService(UserRepository.class);

            // Use the UserRepository to find the user by ID
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Calling userRepository.findById() for user: " + userId.id());
            return userRepository.findById(userId)
               .map(user -> {
                   System.out.println("[DEBUG_LOG] SqlOrderRepository: User found: ID=" + user.getId().id() + 
                                     ", Username=" + user.getUsername().value() + 
                                     ", Roles=" + user.getRoles());
                   return new UserReference(user.getId(), user.getUsername());
               })
               .orElseGet(() -> {
                   System.out.println("[DEBUG_LOG] SqlOrderRepository: User not found with ID: " + userId.id());
                   return null;
               });
        } catch (Exception e) {
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Error fetching user: " + e.getMessage());
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error fetching user: " + userId.id(), e);

            // Return a placeholder user reference in case of error
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Creating placeholder user reference for ID: " + userId.id());
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
        String sql = "SELECT photo_id FROM PHOTOS WHERE order_id = ?";
        System.out.println("[DEBUG_LOG] SqlOrderRepository: Loading photos for order ID: " + orderBusiness.getId().id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderBusiness.getId().id());
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Executing SQL: " + sql + " with order_id = " + orderBusiness.getId().id());

            try (ResultSet rs = stmt.executeQuery()) {
                int photoCount = 0;
                while (rs.next()) {
                    String photoIdStr = rs.getString("photo_id");
                    System.out.println("[DEBUG_LOG] SqlOrderRepository: Found photo ID in database: " + photoIdStr);

                    if (photoIdStr != null && !photoIdStr.isEmpty()) {
                        PhotoId photoId = new PhotoId(photoIdStr);
                        orderBusiness.addPhotoId(photoId);
                        photoCount++;
                        System.out.println("[DEBUG_LOG] SqlOrderRepository: Added photo ID to order: " + photoIdStr);
                    } else {
                        System.out.println("[DEBUG_LOG] SqlOrderRepository: Skipping null or empty photo ID");
                    }
                }
                System.out.println("[DEBUG_LOG] SqlOrderRepository: Loaded " + photoCount + " photos for order ID: " + orderBusiness.getId().id());
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] SqlOrderRepository: Error loading photo IDs: " + e.getMessage());
            e.printStackTrace();
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
        String sql = "SELECT * FROM ORDERS WHERE order_number = ?";

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
                return PhotoTemplate.of(angleStr, "Custom template: " + angleStr);
        }
    }
}
