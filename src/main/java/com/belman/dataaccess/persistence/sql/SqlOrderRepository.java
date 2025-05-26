package com.belman.dataaccess.persistence.sql;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQL-based implementation of the OrderRepository interface.
 * This implementation stores orders in a SQL database.
 */
public class SqlOrderRepository implements OrderRepository {
    private static final Logger LOGGER = Logger.getLogger(SqlOrderRepository.class.getName());

    private final DataSource dataSource;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    /**
     * Creates a new SqlOrderRepository with the specified dependencies.
     *
     * @param dataSource the DataSource to use for database connections
     * @param userRepository the repository for user data
     * @param customerRepository the repository for customer data
     */
    public SqlOrderRepository(DataSource dataSource, UserRepository userRepository, CustomerRepository customerRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<OrderBusiness> findById(OrderId id) {
        String orderId = id.id();

        // Check cache first
        synchronized (orderCache) {
            OrderBusiness cachedOrder = orderCache.get(orderId);
            if (cachedOrder != null) {
                LOGGER.fine("Cache hit: Order found in cache with ID: " + orderId);
                return Optional.of(cachedOrder);
            }
        }

        LOGGER.fine("Cache miss: Order not found in cache with ID: " + orderId);
        String sql = "SELECT * FROM ORDERS WHERE order_id = ?";
        LOGGER.fine("Finding order by ID: " + orderId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);
            LOGGER.fine("Executing SQL: " + sql + " with order_id = " + orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    LOGGER.fine("Order found in database, mapping to OrderBusiness");
                    OrderBusiness order = mapResultSetToOrder(rs);

                    // Load photo IDs for the order
                    LOGGER.fine("Loading photos for order");
                    loadPhotos(order);

                    // Cache the order
                    synchronized (orderCache) {
                        orderCache.put(orderId, order);
                    }

                    LOGGER.fine("Successfully retrieved order: " + 
                               "ID=" + order.getId().id() + 
                               ", Number=" + (order.getOrderNumber() != null ? order.getOrderNumber().value() : "null") + 
                               ", Status=" + order.getStatus() + 
                               ", AssignedTo=" + (order.getAssignedTo() != null ? order.getAssignedTo().id().id() : "null"));
                    return Optional.of(order);
                } else {
                    LOGGER.fine("No order found with ID: " + orderId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding order by id: " + orderId, e);
        }
        return Optional.empty();
    }

    @Override
    public OrderBusiness save(OrderBusiness orderBusiness) {
        String orderId = orderBusiness.getId().id();

        // Check if orderBusiness already exists
        String checkSql = "SELECT COUNT(*) FROM ORDERS WHERE order_id = ?";
        boolean orderExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    orderExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if orderBusiness exists: " + orderId, e);
            throw new RuntimeException("Error checking if orderBusiness exists", e);
        }

        if (orderExists) {
            updateOrder(orderBusiness);
        } else {
            insertOrder(orderBusiness);
        }

        // Update the cache
        synchronized (orderCache) {
            orderCache.put(orderId, orderBusiness);
            LOGGER.fine("Updated order cache for ID: " + orderId);
        }

        // Update the photo ID cache
        synchronized (photoIdCache) {
            photoIdCache.put(orderId, new ArrayList<>(orderBusiness.getPhotoIds()));
            LOGGER.fine("Updated photo ID cache for order ID: " + orderId);
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
        String orderId = id.id();
        String sql = "DELETE FROM ORDERS WHERE order_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Remove from caches
                synchronized (orderCache) {
                    orderCache.remove(orderId);
                }

                synchronized (photoIdCache) {
                    photoIdCache.remove(orderId);
                }

                LOGGER.info("Order deleted successfully: " + orderId + " (removed from cache)");
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting order: " + orderId, e);
        }

        return false;
    }

    @Override
    public List<OrderBusiness> findAll() {
        // Default to first page with default page size
        return findAll(0, 50);
    }

    /**
     * Finds all orders with pagination support.
     *
     * @param page the page number (0-based)
     * @param pageSize the number of items per page
     * @return a list of orders for the specified page
     */
    public List<OrderBusiness> findAll(int page, int pageSize) {
        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (pageSize <= 0) {
            pageSize = 100;
        }

        // Calculate offset
        int offset = page * pageSize;

        String sql = "SELECT * FROM ORDERS ORDER BY created_at DESC LIMIT ? OFFSET ?";
        List<OrderBusiness> orderBusinesses = new ArrayList<>();

        LOGGER.info("Loading orders from database (page=" + page + ", pageSize=" + pageSize + ")");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pageSize);
            stmt.setInt(2, offset);

            LOGGER.fine("Executing SQL query: " + sql + " with params: pageSize=" + pageSize + ", offset=" + offset);

            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                int validOrderNumbers = 0;
                int invalidOrderNumbers = 0;

                while (rs.next()) {
                    count++;
                    String orderId = rs.getString("order_id");
                    String orderNumberStr = rs.getString("order_number");

                    try {
                        OrderBusiness order = mapResultSetToOrder(rs);

                        // Validate order number
                        if (order.getOrderNumber() != null) {
                            validOrderNumbers++;
                        } else if (orderNumberStr != null) {
                            invalidOrderNumbers++;
                            LOGGER.warning("Order " + orderId + " has invalid order number format: " + orderNumberStr);
                        } else {
                            LOGGER.warning("Order " + orderId + " has no order number");
                        }

                        // Add the order to the list (without loading photos yet)
                        orderBusinesses.add(order);
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Error processing order " + orderId + ": " + e.getMessage(), e);
                    }
                }

                // Batch load photos for all orders at once (more efficient than loading individually)
                if (!orderBusinesses.isEmpty()) {
                    loadPhotosForOrders(orderBusinesses);
                }

                LOGGER.info("Loaded " + orderBusinesses.size() + " orders from database (page " + page + ")");
                LOGGER.fine("Order number statistics: " + validOrderNumbers + " valid, " + 
                           invalidOrderNumbers + " invalid");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading orders from database: " + e.getMessage(), e);
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
        LOGGER.fine("Mapping ResultSet to OrderBusiness for order ID: " + orderId);

        OrderId id = new OrderId(orderId);

        // Get the created_by user ID and fetch the user
        String createdByIdStr = rs.getString("created_by");
        UserId createdById = new UserId(createdByIdStr);

        // Use the cached version to avoid repeated database queries
        UserReference createdBy = fetchUserCached(createdById);

        // Get the creation timestamp
        java.sql.Timestamp sqlTimestamp = rs.getTimestamp("created_at");
        Timestamp createdAt = new Timestamp(sqlTimestamp.toInstant());

        // Create the orderBusiness with the required fields
        OrderBusiness orderBusiness = new OrderBusiness(id, createdBy, createdAt);

        // Set optional fields
        setOrderNumber(orderBusiness, rs.getString("order_number"));
        setCustomerId(orderBusiness, rs.getString("customer_id"));
        setProductDescription(orderBusiness, rs.getString("product_description"));
        setDeliveryAddress(orderBusiness, rs);
        setOrderStatus(orderBusiness, rs.getString("status"));
        setAssignedTo(orderBusiness, rs.getString("assigned_to"));

        LOGGER.fine("Completed mapping order: ID=" + orderBusiness.getId().id() + 
                   ", Number=" + (orderBusiness.getOrderNumber() != null ? orderBusiness.getOrderNumber().value() : "null") + 
                   ", Status=" + orderBusiness.getStatus());

        return orderBusiness;
    }

    /**
     * Sets the order number on the order business object.
     * Attempts to fix invalid order numbers.
     *
     * @param orderBusiness the order business object
     * @param orderNumberStr the order number string from the database
     */
    private void setOrderNumber(OrderBusiness orderBusiness, String orderNumberStr) {
        if (orderNumberStr == null) {
            return;
        }

        try {
            orderBusiness.setOrderNumber(new OrderNumber(orderNumberStr));
        } catch (IllegalArgumentException e) {
            // Log a warning if the order number format is invalid
            LOGGER.warning("Invalid order number format in database: " + orderNumberStr + ", " + e.getMessage());
            attemptToFixOrderNumber(orderBusiness, orderNumberStr);
        }
    }

    /**
     * Attempts to fix an invalid order number by converting it to the new format.
     *
     * @param orderBusiness the order business object
     * @param orderNumberStr the invalid order number string
     */
    private void attemptToFixOrderNumber(OrderBusiness orderBusiness, String orderNumberStr) {
        try {
            // If it starts with "ORD-", convert it to the new format (MM/YY-CUSTOMER-SEQUENCE)
            if (orderNumberStr.startsWith("ORD-")) {
                // Extract parts from the legacy format
                String[] parts = orderNumberStr.split("-");
                if (parts.length >= 5) {
                    // Extract date part (YYMMDD) from the legacy format
                    String date = parts[2];
                    if (date.length() >= 6) {
                        String yy = date.substring(0, 2);
                        String mm = date.substring(2, 4);

                        // Use the code part as the customer ID, padded to 6 digits
                        String code = parts[3];
                        String customerId = String.format("%06d", Math.abs(code.hashCode() % 1000000));

                        // Use the sequence part, padded to 8 digits
                        String sequence = parts[4];
                        String paddedSequence = String.format("%08d", Integer.parseInt(sequence));

                        // Create a new order number in the format MM/YY-CUSTOMER-SEQUENCE
                        String newFormatOrderNumber = mm + "/" + yy + "-" + customerId + "-" + paddedSequence;

                        try {
                            orderBusiness.setOrderNumber(new OrderNumber(newFormatOrderNumber));
                            LOGGER.info("Converted legacy order number to new format: " + orderNumberStr + " -> " + newFormatOrderNumber);
                        } catch (IllegalArgumentException ex) {
                            // If conversion fails, generate a completely new order number
                            String generatedOrderNumber = OrderNumber.generate("123456").value();
                            orderBusiness.setOrderNumber(new OrderNumber(generatedOrderNumber));
                            LOGGER.info("Generated new order number: " + orderNumberStr + " -> " + generatedOrderNumber);
                        }
                    } else {
                        // If date part is invalid, generate a completely new order number
                        String generatedOrderNumber = OrderNumber.generate("123456").value();
                        orderBusiness.setOrderNumber(new OrderNumber(generatedOrderNumber));
                        LOGGER.info("Generated new order number: " + orderNumberStr + " -> " + generatedOrderNumber);
                    }
                } else {
                    // If parts are invalid, generate a completely new order number
                    String generatedOrderNumber = OrderNumber.generate("123456").value();
                    orderBusiness.setOrderNumber(new OrderNumber(generatedOrderNumber));
                    LOGGER.info("Generated new order number: " + orderNumberStr + " -> " + generatedOrderNumber);
                }
            }
        } catch (Exception ex) {
            // If any exception occurs, generate a completely new order number
            try {
                String generatedOrderNumber = OrderNumber.generate("123456").value();
                orderBusiness.setOrderNumber(new OrderNumber(generatedOrderNumber));
                LOGGER.info("Generated new order number due to exception: " + ex.getMessage() + " -> " + generatedOrderNumber);
            } catch (Exception e) {
                // Leave order number as null
                LOGGER.warning("Failed to generate new order number: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the customer ID on the order business object.
     *
     * @param orderBusiness the order business object
     * @param customerId the customer ID string from the database
     */
    private void setCustomerId(OrderBusiness orderBusiness, String customerId) {
        if (customerId == null) {
            return;
        }

        try {
            CustomerBusiness customer = fetchCustomer(new CustomerId(customerId));
            if (customer != null) {
                orderBusiness.setCustomerId(customer.getId());
            }
        } catch (Exception e) {
            // Set the customer ID directly from the database
            orderBusiness.setCustomerId(new CustomerId(customerId));
        }
    }

    /**
     * Sets the product description on the order business object.
     *
     * @param orderBusiness the order business object
     * @param productDescriptionStr the product description string from the database
     */
    private void setProductDescription(OrderBusiness orderBusiness, String productDescriptionStr) {
        if (productDescriptionStr != null) {
            // This is simplified - in a real implementation, we would parse the product description properly
        }
    }

    /**
     * Sets the delivery address on the order business object.
     *
     * @param orderBusiness the order business object
     * @param rs the result set containing the delivery address
     */
    private void setDeliveryAddress(OrderBusiness orderBusiness, ResultSet rs) {
        try {
            String deliveryAddressStr = rs.getString("delivery_address");
            if (deliveryAddressStr != null) {
                // This is simplified - in a real implementation, we would parse the delivery information properly
            }
        } catch (SQLException e) {
            LOGGER.warning("Delivery address column not found or error accessing it: " + e.getMessage());
        }
    }

    /**
     * Sets the order status on the order business object.
     *
     * @param orderBusiness the order business object
     * @param statusStr the status string from the database
     */
    private void setOrderStatus(OrderBusiness orderBusiness, String statusStr) {
        if (statusStr != null) {
            orderBusiness.setStatus(OrderStatus.valueOf(statusStr));
        }
    }

    /**
     * Sets the assigned to user on the order business object.
     *
     * @param orderBusiness the order business object
     * @param assignedToId the assigned to user ID string from the database
     */
    private void setAssignedTo(OrderBusiness orderBusiness, String assignedToId) {
        if (assignedToId != null) {
            // Use the cached version to avoid repeated database queries
            UserReference assignedTo = fetchUserCached(new UserId(assignedToId));
            if (assignedTo != null) {
                orderBusiness.setAssignedTo(assignedTo);
            }
        }
    }

    /**
     * Fetches a user by ID.
     * This method uses a cache to avoid repeated database queries for the same user.
     * 
     * @param userId the ID of the user to fetch
     * @return a UserReference for the user, or null if not found
     */
    private UserReference fetchUser(UserId userId) {
        LOGGER.fine("Fetching user with ID: " + userId.id());

        try {
            // Use the injected UserRepository to find the user by ID
            return userRepository.findById(userId)
               .map(user -> new UserReference(user.getId(), user.getUsername()))
               .orElseGet(() -> {
                   LOGGER.warning("User not found with ID: " + userId.id());
                   return null;
               });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error fetching user: " + userId.id(), e);

            // Return a placeholder user reference in case of error
            return new UserReference(userId, new Username("unknown-user"));
        }
    }

    /**
     * Cache configuration
     */
    // Cache for user references to avoid repeated database queries
    private static final Map<String, UserReference> userCache = new HashMap<>();

    // Cache for orders to avoid repeated database queries
    // Using a LinkedHashMap with access ordering to implement a simple LRU cache
    private static final Map<String, OrderBusiness> orderCache = new LinkedHashMap<String, OrderBusiness>(100, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, OrderBusiness> eldest) {
            return size() > 100; // Limit cache size to 100 entries
        }
    };

    // Cache for photo IDs by order ID to avoid repeated database queries
    private static final Map<String, List<PhotoId>> photoIdCache = new HashMap<>();

    // Maximum age for cached items in milliseconds (5 minutes)
    private static final long CACHE_EXPIRY_MS = 5 * 60 * 1000;

    /**
     * Fetches a user by ID using the cache.
     * 
     * @param userId the ID of the user to fetch
     * @return a UserReference for the user, or null if not found
     */
    private UserReference fetchUserCached(UserId userId) {
        String userIdStr = userId.id();

        // Check if the user is already in the cache
        if (userCache.containsKey(userIdStr)) {
            return userCache.get(userIdStr);
        }

        // Fetch the user and add to cache
        UserReference user = fetchUser(userId);
        if (user != null) {
            userCache.put(userIdStr, user);
        }

        return user;
    }

    /**
     * Fetches multiple users by ID in a single batch operation.
     * This reduces the number of database queries needed when loading many users.
     * 
     * @param userIds the list of user IDs to fetch
     * @return a map of user IDs to UserReferences
     */
    private Map<String, UserReference> fetchUsers(List<UserId> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        // Create a set of unique user IDs that aren't already in the cache
        List<UserId> uniqueUserIds = userIds.stream()
            .filter(id -> !userCache.containsKey(id.id()))
            .distinct()
            .toList();

        if (uniqueUserIds.isEmpty()) {
            // All users are already in the cache
            Map<String, UserReference> result = new HashMap<>();
            for (UserId userId : userIds) {
                UserReference user = userCache.get(userId.id());
                if (user != null) {
                    result.put(userId.id(), user);
                }
            }
            return result;
        }

        // Use the injected UserRepository
        // Since UserRepository doesn't have a findAllById method, we'll use findAll and filter
        List<com.belman.domain.user.UserBusiness> allUsers = userRepository.findAll();

        // Filter to only the users we need
        Set<String> uniqueUserIdStrings = uniqueUserIds.stream()
            .map(UserId::id)
            .collect(java.util.stream.Collectors.toSet());

        List<com.belman.domain.user.UserBusiness> users = allUsers.stream()
            .filter(user -> uniqueUserIdStrings.contains(user.getId().id()))
            .toList();

        // Add all fetched users to the cache
        for (com.belman.domain.user.UserBusiness user : users) {
            UserReference userRef = new UserReference(user.getId(), user.getUsername());
            userCache.put(user.getId().id(), userRef);
        }

        // Create the result map
        Map<String, UserReference> result = new HashMap<>();
        for (UserId userId : userIds) {
            UserReference user = userCache.get(userId.id());
            if (user != null) {
                result.put(userId.id(), user);
            }
        }

        return result;
    }

    private CustomerBusiness fetchCustomer(CustomerId customerId) {
        try {
            // Use the injected CustomerRepository to find the customer by ID
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

    /**
     * Loads photos for a single order.
     * Uses a cache to avoid repeated database queries.
     * 
     * @param orderBusiness the order to load photos for
     */
    private void loadPhotos(OrderBusiness orderBusiness) {
        String orderId = orderBusiness.getId().id();

        // Check cache first
        synchronized (photoIdCache) {
            List<PhotoId> cachedPhotoIds = photoIdCache.get(orderId);
            if (cachedPhotoIds != null) {
                LOGGER.fine("Cache hit: Photo IDs found in cache for order ID: " + orderId);
                for (PhotoId photoId : cachedPhotoIds) {
                    orderBusiness.addPhotoId(photoId);
                }
                return;
            }
        }

        LOGGER.fine("Cache miss: Photo IDs not found in cache for order ID: " + orderId);
        String sql = "SELECT photo_id FROM PHOTOS WHERE order_id = ?";
        LOGGER.fine("Loading photos from database for order ID: " + orderId);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                int photoCount = 0;
                List<PhotoId> photoIds = new ArrayList<>();

                while (rs.next()) {
                    String photoIdStr = rs.getString("photo_id");

                    if (photoIdStr != null && !photoIdStr.isEmpty()) {
                        PhotoId photoId = new PhotoId(photoIdStr);
                        orderBusiness.addPhotoId(photoId);
                        photoIds.add(photoId);
                        photoCount++;
                    }
                }

                // Cache the photo IDs
                synchronized (photoIdCache) {
                    photoIdCache.put(orderId, photoIds);
                }

                LOGGER.fine("Loaded " + photoCount + " photos for order ID: " + orderId);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading photo IDs for order: " + orderId, e);
        }
    }

    /**
     * Loads photos for multiple orders in a single batch operation.
     * This reduces the number of database queries needed when loading many orders.
     * Uses cache where possible to avoid database queries.
     * 
     * @param orders the list of orders to load photos for
     */
    private void loadPhotosForOrders(List<OrderBusiness> orders) {
        if (orders == null || orders.isEmpty()) {
            return;
        }

        // Create a map of order IDs to orders for quick lookup
        Map<String, OrderBusiness> orderMap = new HashMap<>();
        List<String> orderIdsToLoad = new ArrayList<>();

        // First check which orders already have photos in the cache
        for (OrderBusiness order : orders) {
            String orderId = order.getId().id();
            orderMap.put(orderId, order);

            // Check if photos for this order are in the cache
            synchronized (photoIdCache) {
                List<PhotoId> cachedPhotoIds = photoIdCache.get(orderId);
                if (cachedPhotoIds != null) {
                    // Use cached photo IDs
                    LOGGER.fine("Cache hit: Using cached photo IDs for order " + orderId);
                    for (PhotoId photoId : cachedPhotoIds) {
                        order.addPhotoId(photoId);
                    }
                } else {
                    // Need to load from database
                    orderIdsToLoad.add(orderId);
                }
            }
        }

        // If all orders were in the cache, we're done
        if (orderIdsToLoad.isEmpty()) {
            LOGGER.fine("All photos for " + orders.size() + " orders were found in cache");
            return;
        }

        // Build the SQL query for orders that need to be loaded from the database
        StringBuilder orderIdList = new StringBuilder();
        for (int i = 0; i < orderIdsToLoad.size(); i++) {
            if (i > 0) {
                orderIdList.append(",");
            }
            orderIdList.append("'").append(orderIdsToLoad.get(i)).append("'");
        }

        // Query to get all photos for the specified orders in one batch
        String sql = "SELECT photo_id, order_id FROM PHOTOS WHERE order_id IN (" + orderIdList.toString() + ")";

        LOGGER.fine("Batch loading photos for " + orderIdsToLoad.size() + " orders from database");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Count photos loaded per order for logging
            Map<String, Integer> photoCountByOrder = new HashMap<>();

            // Map to collect photo IDs by order ID for caching
            Map<String, List<PhotoId>> photoIdsByOrder = new HashMap<>();

            while (rs.next()) {
                String photoIdStr = rs.getString("photo_id");
                String orderIdStr = rs.getString("order_id");

                if (photoIdStr != null && !photoIdStr.isEmpty() && orderIdStr != null) {
                    OrderBusiness order = orderMap.get(orderIdStr);
                    if (order != null) {
                        PhotoId photoId = new PhotoId(photoIdStr);
                        order.addPhotoId(photoId);

                        // Collect photo IDs for caching
                        photoIdsByOrder.computeIfAbsent(orderIdStr, k -> new ArrayList<>()).add(photoId);

                        // Update photo count for this order
                        photoCountByOrder.put(orderIdStr, 
                            photoCountByOrder.getOrDefault(orderIdStr, 0) + 1);
                    }
                }
            }

            // Update the cache with the loaded photo IDs
            synchronized (photoIdCache) {
                for (Map.Entry<String, List<PhotoId>> entry : photoIdsByOrder.entrySet()) {
                    photoIdCache.put(entry.getKey(), entry.getValue());
                }
            }

            // Log summary of photos loaded
            LOGGER.fine("Batch loaded photos for " + orderIdsToLoad.size() + " orders from database. " +
                       "Total photos: " + photoCountByOrder.values().stream().mapToInt(Integer::intValue).sum());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error batch loading photos for orders: " + e.getMessage(), e);
        }
    }

    // The mapResultSetToPhoto method has been removed as it's no longer needed.
    // Photos are now managed by the PhotoRepository directly.

    @Override
    public List<OrderBusiness> findBySpecification(Specification<OrderBusiness> spec) {
        // Check if the specification can be translated to SQL
        if (spec instanceof com.belman.domain.specification.SqlSpecification) {
            return findBySqlSpecification((com.belman.domain.specification.SqlSpecification<OrderBusiness>) spec);
        }

        // Fall back to in-memory filtering for non-SQL specifications
        // But use pagination to avoid loading the entire database
        List<OrderBusiness> allOrders = new ArrayList<>();
        int page = 0;
        int pageSize = 50; // Reduced page size for better memory efficiency
        List<OrderBusiness> pageOfOrders;
        int maxPages = 20; // Limit the number of pages to process to avoid excessive database queries
        int processedPages = 0;

        do {
            pageOfOrders = findAll(page, pageSize);
            List<OrderBusiness> matchingOrders = pageOfOrders.stream()
                    .filter(spec::isSatisfiedBy)
                    .toList();

            allOrders.addAll(matchingOrders);

            LOGGER.fine("Processed page " + page + ": found " + matchingOrders.size() + 
                       " matching orders out of " + pageOfOrders.size());

            page++;
            processedPages++;

            // Stop if we've processed the maximum number of pages or if we got fewer results than the page size
            // (indicating we've reached the end of the data)
        } while (!pageOfOrders.isEmpty() && pageOfOrders.size() == pageSize && processedPages < maxPages);

        if (processedPages >= maxPages && !pageOfOrders.isEmpty()) {
            LOGGER.warning("Reached maximum number of pages (" + maxPages + 
                          ") when filtering by specification. Results may be incomplete.");
        }

        return allOrders;
    }

    /**
     * Finds orders using a SQL specification.
     * This method translates the specification to a SQL WHERE clause for efficient database filtering.
     *
     * @param sqlSpec the SQL specification
     * @return a list of orders that satisfy the specification
     */
    private List<OrderBusiness> findBySqlSpecification(com.belman.domain.specification.SqlSpecification<OrderBusiness> sqlSpec) {
        String whereClause = sqlSpec.toSqlClause();
        List<Object> parameters = sqlSpec.getParameters();

        String sql = "SELECT * FROM ORDERS";
        if (whereClause != null && !whereClause.isEmpty()) {
            sql += " WHERE " + whereClause;
        }

        List<OrderBusiness> orderBusinesses = new ArrayList<>();

        LOGGER.info("Finding orders by SQL specification: " + whereClause);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            LOGGER.fine("Executing SQL query: " + sql);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        OrderBusiness order = mapResultSetToOrder(rs);
                        loadPhotos(order);
                        orderBusinesses.add(order);
                    } catch (Exception e) {
                        String orderId = rs.getString("order_id");
                        LOGGER.log(Level.WARNING, "Error processing order " + orderId + ": " + e.getMessage(), e);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding orders by specification: " + e.getMessage(), e);
        }

        return orderBusinesses;
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
