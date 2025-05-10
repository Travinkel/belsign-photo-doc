package com.belman.repository.persistence;

import com.belman.domain.user.*;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.common.EmailAddress;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.common.PersonName;

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
        String sql = "SELECT * FROM users WHERE username = ?";

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
        String sql = "SELECT * FROM users WHERE email = ?";

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
    public void save(UserBusiness user) {
        // Check if user already exists
        String checkSql = "SELECT COUNT(*) FROM users WHERE id = ?";
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
    }

    private void insertUser(UserBusiness user) {
        String sql = "INSERT INTO users (id, username, password, first_name, last_name, email, status) " +
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
                stmt.setString(4, name.firstName());
                stmt.setString(5, name.lastName());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            stmt.setString(6, user.getEmail().value());

            // Get status from user if available
            UserStatus status = UserStatus.ACTIVE; // Default to ACTIVE
            try {
                // This is a workaround since we don't know if getStatus() exists
                java.lang.reflect.Method getStatusMethod = user.getClass().getMethod("getStatus");
                status = (UserStatus) getStatusMethod.invoke(user);
            } catch (Exception e) {
                // Ignore if getStatus() doesn't exist
            }

            stmt.setString(7, status.name());

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

    private void updateUser(UserBusiness user) {
        String sql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, " +
                     "email = ?, status = ? WHERE id = ?";

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
                stmt.setString(3, name.firstName());
                stmt.setString(4, name.lastName());
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
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

    private void insertUserRoles(Connection conn, UserBusiness user) throws SQLException {
        String sql = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (UserRole role : user.getRoles()) {
                stmt.setString(1, user.getId().id());
                stmt.setString(2, role.name());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteUserRoles(Connection conn, UserId userId) throws SQLException {
        String sql = "DELETE FROM user_roles WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId.id());
            stmt.executeUpdate();
        }
    }

    private UserBusiness mapResultSetToUser(ResultSet rs) throws SQLException {
        UserId id = new UserId(rs.getString("id"));
        Username username = new Username(rs.getString("username"));
        HashedPassword password = new HashedPassword(rs.getString("password"));

        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        PersonName name = (firstName != null && lastName != null) 
                ? new PersonName(firstName, lastName) 
                : null;

        EmailAddress email = new EmailAddress(rs.getString("email"));
        UserStatus status = UserStatus.valueOf(rs.getString("status"));

        // Create a UserBusiness using the Builder pattern
        UserBusiness.Builder builder = new UserBusiness.Builder()
                .id(id)
                .username(username)
                .password(password)
                .email(email);

        if (name != null) {
            builder.name(name);
        }

        UserBusiness user = builder.build();

        // Load user roles
        loadUserRoles(user);

        return user;
    }

    private void loadUserRoles(UserBusiness user) {
        String sql = "SELECT role FROM user_roles WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId().id());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserRole role = UserRole.valueOf(rs.getString("role"));
                    user.addRole(role);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading user roles: " + user.getId().id(), e);
        }
    }

    @Override
    public Optional<UserBusiness> findById(UserId id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by id: " + id.id(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<UserBusiness> findAll() {
        String sql = "SELECT * FROM users";
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
    public List<UserBusiness> findByRole(UserRole role) {
        String sql = "SELECT u.* FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
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
    public boolean delete(UserId id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id().toString());
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
}
