package com.belman.data.persistence;

import com.belman.domain.aggregates.User;
import com.belman.domain.aggregates.User.Role;
import com.belman.domain.enums.UserStatus;
import com.belman.business.domain.user.UserRepository;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.valueobjects.PersonName;
import com.belman.domain.valueobjects.UserId;
import com.belman.domain.valueobjects.Username;

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
    public Optional<User> findByUsername(Username username) {
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
    public Optional<User> findByEmail(EmailAddress email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.getValue());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by email: " + email.getValue(), e);
        }

        return Optional.empty();
    }

    @Override
    public void save(User user) {
        // Check if user already exists
        String checkSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        boolean userExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, user.getId().id().toString());

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

    private void insertUser(User user) {
        String sql = "INSERT INTO users (id, username, password, first_name, last_name, email, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId().id().toString());
            stmt.setString(2, user.getUsername().value());
            stmt.setString(3, user.getPassword().value());

            if (user.getName() != null) {
                stmt.setString(4, user.getName().firstName());
                stmt.setString(5, user.getName().lastName());
            } else {
                stmt.setNull(4, java.sql.Types.VARCHAR);
                stmt.setNull(5, java.sql.Types.VARCHAR);
            }

            stmt.setString(6, user.getEmail().getValue());
            stmt.setString(7, user.getStatus().name());

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

    private void updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, first_name = ?, last_name = ?, " +
                     "email = ?, status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername().value());
            stmt.setString(2, user.getPassword().value());

            if (user.getName() != null) {
                stmt.setString(3, user.getName().firstName());
                stmt.setString(4, user.getName().lastName());
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR);
                stmt.setNull(4, java.sql.Types.VARCHAR);
            }

            stmt.setString(5, user.getEmail().getValue());
            stmt.setString(6, user.getStatus().name());
            stmt.setString(7, user.getId().id().toString());

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

    private void insertUserRoles(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO user_roles (user_id, role) VALUES (?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (User.Role role : user.getRoles()) {
                stmt.setString(1, user.getId().id().toString());
                stmt.setString(2, role.name());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteUserRoles(Connection conn, UserId userId) throws SQLException {
        String sql = "DELETE FROM user_roles WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId.id().toString());
            stmt.executeUpdate();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UserId id = new UserId(UUID.fromString(rs.getString("id")));
        Username username = new Username(rs.getString("username"));
        HashedPassword password = new HashedPassword(rs.getString("password"));

        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        PersonName name = (firstName != null && lastName != null) 
                ? new PersonName(firstName, lastName) 
                : null;

        EmailAddress email = new EmailAddress(rs.getString("email"));
        UserStatus status = UserStatus.valueOf(rs.getString("status"));

        User user = new User(id, username, password, name, email);
        user.setStatus(status);

        // Load user roles
        loadUserRoles(user);

        return user;
    }

    private void loadUserRoles(User user) {
        String sql = "SELECT role FROM user_roles WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getId().id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User.Role role = User.Role.valueOf(rs.getString("role"));
                    user.addRole(role);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading user roles: " + user.getId().id(), e);
        }
    }

    @Override
    public Optional<User> findById(UserId id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id().toString());

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
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

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
    public List<User> findByRole(Role role) {
        String sql = "SELECT u.* FROM users u " +
                     "JOIN user_roles ur ON u.id = ur.user_id " +
                     "WHERE ur.role = ?";
        List<User> users = new ArrayList<>();

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
