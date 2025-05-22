package com.belman.dataaccess.persistence.sql;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.*;

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
 * SQL-based implementation of the UserRepository interface.
 * This implementation stores users in a SQL database.
 */
public class SqlUserRepository implements UserRepository {
    private static final Logger LOGGER = Logger.getLogger(SqlUserRepository.class.getName());

    private final DataSource dataSource;

    /**
     * Creates a new SqlUserRepository with the specified DataSource.
     *
     * @param dataSource the DataSource to use for database connections
     */
    public SqlUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<UserBusiness> findByUsername(Username username) {
        String sql = "SELECT * FROM USERS WHERE username = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.value());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username.value(), e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserBusiness> findByEmail(EmailAddress email) {
        String sql = "SELECT * FROM USERS WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.value());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by email: " + email.value(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<UserBusiness> findByRole(UserRole role) {
        String sql = "SELECT u.* FROM USERS u " +
                     "JOIN USER_ROLES ur ON u.user_id = ur.user_id " +
                     "WHERE ur.role = ?";
        List<UserBusiness> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding users by role: " + role, e);
        }

        return users;
    }

    @Override
    public Optional<UserBusiness> findByNfcId(String nfcId) {
        String sql = "SELECT * FROM USERS WHERE nfc_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nfcId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by NFC ID: " + nfcId, e);
        }

        return Optional.empty();
    }

    private UserBusiness mapResultSetToUser(ResultSet rs) throws SQLException {
        String userIdStr = rs.getString("user_id");
        System.out.println("[DEBUG_LOG] SqlUserRepository: Mapping user from ResultSet, user_id from DB: " + userIdStr);

        UserId id = new UserId(userIdStr);
        System.out.println("[DEBUG_LOG] SqlUserRepository: Created UserId object: " + id.id());

        String usernameStr = rs.getString("username");
        // Convert "quality" to "qa" for backward compatibility
        if ("quality".equals(usernameStr)) {
            usernameStr = "qa";
            System.out.println("[DEBUG_LOG] SqlUserRepository: Converting username 'quality' to 'qa' for backward compatibility");
        }
        Username username = new Username(usernameStr);
        System.out.println("[DEBUG_LOG] SqlUserRepository: Username from DB: " + usernameStr);

        HashedPassword password = new HashedPassword(rs.getString("password"));

        String fullName = rs.getString("name");
        PersonName name = null;
        if (fullName != null && !fullName.isBlank()) {
            // Split the full name into first and last name parts
            String[] nameParts = fullName.trim().split("\\s+", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "Unknown"; // Default last name if not provided
            name = new PersonName(firstName, lastName);
            System.out.println("[DEBUG_LOG] SqlUserRepository: Name from DB: " + fullName + ", parsed as: " + firstName + " " + lastName);
        } else {
            System.out.println("[DEBUG_LOG] SqlUserRepository: No name found in DB for user");
        }

        String emailStr = rs.getString("email");
        EmailAddress email = new EmailAddress(emailStr);
        System.out.println("[DEBUG_LOG] SqlUserRepository: Email from DB: " + emailStr);

        String statusStr = rs.getString("status");
        UserStatus status = UserStatus.valueOf(statusStr);
        System.out.println("[DEBUG_LOG] SqlUserRepository: Status from DB: " + statusStr);

        // Set approval state based on status
        ApprovalState approvalState;
        if (status == UserStatus.ACTIVE) {
            approvalState = ApprovalState.createApproved();
            System.out.println("[DEBUG_LOG] SqlUserRepository: User is ACTIVE, setting approval state to APPROVED");
        } else if (status == UserStatus.INACTIVE) {
            approvalState = ApprovalState.createRejected("User is inactive");
            System.out.println("[DEBUG_LOG] SqlUserRepository: User is INACTIVE, setting approval state to REJECTED");
        } else {
            approvalState = ApprovalState.createPendingState();
            System.out.println("[DEBUG_LOG] SqlUserRepository: User has status " + status + ", setting approval state to PENDING");
        }

        // Create a UserBusiness using the Builder pattern
        System.out.println("[DEBUG_LOG] SqlUserRepository: Creating UserBusiness object with Builder pattern");
        UserBusiness.Builder builder = new UserBusiness.Builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email)
                .approvalState(approvalState);

        if (name != null) {
            builder.name(name);
        }

        // Add NFC ID if present
        String nfcId = rs.getString("nfc_id");
        if (nfcId != null) {
            builder.nfcId(nfcId);
            System.out.println("[DEBUG_LOG] SqlUserRepository: NFC ID from DB: " + nfcId);
        }

        UserBusiness user = builder.build();
        System.out.println("[DEBUG_LOG] SqlUserRepository: UserBusiness object created: ID=" + user.getId().id() + ", Username=" + user.getUsername().value());

        // Load user roles
        System.out.println("[DEBUG_LOG] SqlUserRepository: Loading roles for user: " + user.getId().id());
        loadUserRoles(user);
        System.out.println("[DEBUG_LOG] SqlUserRepository: User roles loaded: " + user.getRoles());

        return user;
    }

    private void loadUserRoles(UserBusiness user) {
        String sql = "SELECT role FROM USER_ROLES WHERE user_id = ?";
        System.out.println("[DEBUG_LOG] SqlUserRepository: Loading roles for user with ID: " + user.getId().id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId().id());
            System.out.println("[DEBUG_LOG] SqlUserRepository: Executing SQL: " + sql + " with user_id = " + user.getId().id());

            try (ResultSet rs = stmt.executeQuery()) {
                int roleCount = 0;
                while (rs.next()) {
                    String roleStr = rs.getString("role");
                    // Convert "QUALITY" to "QA" for backward compatibility
                    if ("QUALITY".equals(roleStr)) {
                        roleStr = "QA";
                        System.out.println("[DEBUG_LOG] SqlUserRepository: Converting QUALITY role to QA for backward compatibility");
                    }
                    UserRole role = UserRole.valueOf(roleStr);
                    user.addRole(role);
                    roleCount++;
                    System.out.println("[DEBUG_LOG] SqlUserRepository: Added role to user: " + roleStr);
                }
                System.out.println("[DEBUG_LOG] SqlUserRepository: Loaded " + roleCount + " roles for user: " + user.getId().id());

                if (roleCount == 0) {
                    System.out.println("[DEBUG_LOG] SqlUserRepository: WARNING - No roles found for user: " + user.getId().id());
                }
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] SqlUserRepository: Error loading user roles: " + e.getMessage());
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error loading user roles: " + user.getId().id(), e);
        }
    }

    @Override
    public Optional<UserBusiness> findById(UserId id) {
        String sql = "SELECT * FROM USERS WHERE user_id = ?";
        System.out.println("[DEBUG_LOG] SqlUserRepository: Finding user by ID: " + id.id());

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());
            System.out.println("[DEBUG_LOG] SqlUserRepository: Executing SQL: " + sql + " with user_id = " + id.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("[DEBUG_LOG] SqlUserRepository: User found in database, mapping to UserBusiness");
                    UserBusiness user = mapResultSetToUser(rs);
                    System.out.println("[DEBUG_LOG] SqlUserRepository: Successfully retrieved user: " + 
                                      "ID=" + user.getId().id() + 
                                      ", Username=" + user.getUsername().value() + 
                                      ", Roles=" + user.getRoles());
                    return Optional.of(user);
                } else {
                    System.out.println("[DEBUG_LOG] SqlUserRepository: No user found with ID: " + id.id());
                }
            }
        } catch (SQLException e) {
            System.out.println("[DEBUG_LOG] SqlUserRepository: Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, "Error finding user by id: " + id.id(), e);
        }

        return Optional.empty();
    }

    @Override
    public UserBusiness save(UserBusiness user) {
        // Check if user already exists
        String checkSql = "SELECT COUNT(*) FROM USERS WHERE user_id = ?";
        boolean userExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, user.getId().id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists: " + user.getId().id(), e);
            throw new RuntimeException("Error checking if user exists", e);
        }

        if (userExists) {
            updateUser(user);
        } else {
            insertUser(user);
        }

        return user;
    }

    private void updateUser(UserBusiness user) {
        String sql = "UPDATE USERS SET username = ?, password = ?, name = ?, " +
                     "email = ?, status = ?, nfc_id = ? WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername().value());
            stmt.setString(2, user.getPassword().value());

            // Get name from user if available
            PersonName name = null;
            try {
                // This is a workaround since we don't know if getName() exists
                java.lang.reflect.Method getNameMethod = user.getClass().getMethod("getName");
                name = (PersonName) getNameMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getName() doesn't exist
            }

            if (name != null) {
                stmt.setString(3, name.firstName() + " " + name.lastName());
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            }

            stmt.setString(4, user.getEmail().value());

            // Get status from user if available
            UserStatus status = UserStatus.ACTIVE; // Default to ACTIVE
            try {
                // This is a workaround since we don't know if getStatus() exists
                java.lang.reflect.Method getStatusMethod = user.getClass().getMethod("getStatus");
                status = (UserStatus) getStatusMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getStatus() doesn't exist
            }

            stmt.setString(5, status.name());

            // Get NFC ID from user if available
            String nfcId = null;
            try {
                // This is a workaround since we don't know if getNfcId() exists
                java.lang.reflect.Method getNfcIdMethod = user.getClass().getMethod("getNfcId");
                nfcId = (String) getNfcIdMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getNfcId() doesn't exist
            }

            if (nfcId != null) {
                stmt.setString(6, nfcId);
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }

            stmt.setString(7, user.getId().id());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Update user roles (delete and re-insert)
                deleteUserRoles(conn, user.getId());
                insertUserRoles(conn, user);
                LOGGER.info("User updated successfully: " + user.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getId().id(), e);
            throw new RuntimeException("Error updating user", e);
        }
    }

    private void insertUser(UserBusiness user) {
        String sql = "INSERT INTO USERS (user_id, username, password, name, email, status, nfc_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId().id());
            stmt.setString(2, user.getUsername().value());
            stmt.setString(3, user.getPassword().value());

            // Get name from user if available
            PersonName name = null;
            try {
                // This is a workaround since we don't know if getName() exists
                java.lang.reflect.Method getNameMethod = user.getClass().getMethod("getName");
                name = (PersonName) getNameMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getName() doesn't exist
            }

            if (name != null) {
                stmt.setString(4, name.firstName() + " " + name.lastName());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            stmt.setString(5, user.getEmail().value());

            // Get status from user if available
            UserStatus status = UserStatus.ACTIVE; // Default to ACTIVE
            try {
                // This is a workaround since we don't know if getStatus() exists
                java.lang.reflect.Method getStatusMethod = user.getClass().getMethod("getStatus");
                status = (UserStatus) getStatusMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getStatus() doesn't exist
            }

            stmt.setString(6, status.name());

            // Get NFC ID from user if available
            String nfcId = null;
            try {
                // This is a workaround since we don't know if getNfcId() exists
                java.lang.reflect.Method getNfcIdMethod = user.getClass().getMethod("getNfcId");
                nfcId = (String) getNfcIdMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getNfcId() doesn't exist
            }

            if (nfcId != null) {
                stmt.setString(7, nfcId);
            } else {
                stmt.setNull(7, java.sql.Types.VARCHAR);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // Insert user roles
                insertUserRoles(conn, user);
                LOGGER.info("User inserted successfully: " + user.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting user: " + user.getId().id(), e);
            throw new RuntimeException("Error inserting user", e);
        }
    }

    private void deleteUserRoles(Connection conn, UserId userId) throws SQLException {
        String sql = "DELETE FROM USER_ROLES WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId.id());
            stmt.executeUpdate();
        }
    }

    private void insertUserRoles(Connection conn, UserBusiness user) throws SQLException {
        String sql = "INSERT INTO USER_ROLES (user_id, role) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (UserRole role : user.getRoles()) {
                stmt.setString(1, user.getId().id());
                stmt.setString(2, role.name());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    @Override
    public void delete(UserBusiness user) {
        if (user != null) {
            deleteById(user.getId());
        }
    }

    @Override
    public boolean deleteById(UserId id) {
        String sql = "DELETE FROM USERS WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("User deleted successfully: " + id.id());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + id.id(), e);
        }

        return false;
    }

    @Override
    public List<UserBusiness> findAll() {
        String sql = "SELECT * FROM USERS";
        List<UserBusiness> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all users", e);
        }

        return users;
    }

    @Override
    public boolean existsById(UserId id) {
        String sql = "SELECT COUNT(*) FROM USERS WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if user exists: " + id.id(), e);
        }

        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM USERS";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting users", e);
        }

        return 0;
    }
}
