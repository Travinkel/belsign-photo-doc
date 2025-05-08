package com.belman.data.persistence;

import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.user.UserId;
import com.belman.business.richbe.user.Username;
import com.belman.business.richbe.user.UserRepository;
import com.belman.business.richbe.user.UserRole;
import com.belman.business.richbe.security.HashedPassword;
import com.belman.business.richbe.security.PasswordHasher;

import com.belman.data.security.BCryptPasswordHasher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the UserRepository interface.
 * This implementation stores users in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<Username, UserId> userIdsByUsername = new HashMap<>();
    private final Map<EmailAddress, UserId> userIdsByEmail = new HashMap<>();
    private final Map<UserId, UserAggregate> usersById = new HashMap<>();

    /**
     * Creates a new InMemoryUserRepository with default users.
     */
    public InMemoryUserRepository() {
        // Create default admin user
        createDefaultUsers();
    }

    private void createDefaultUsers() {
        // Create password hasher
        PasswordHasher passwordHasher = new BCryptPasswordHasher();

        // Create admin user with both old and new credentials
        EmailAddress adminEmail = new EmailAddress("admin@belman.com");
        UserAggregate adminUser = UserAggregate.createNewUser(
                new Username("admin"),
                HashedPassword.fromPlainText("admin", passwordHasher),
                adminEmail
        );
        adminUser.addRole(UserRole.ADMIN);

        // Create admin user with password123 (for Android compatibility)
        UserAggregate adminUser2 = UserAggregate.createNewUser(
                new Username("admin"),
                HashedPassword.fromPlainText("password123", passwordHasher),
                adminEmail
        );
        adminUser2.addRole(UserRole.ADMIN);

        // Create production user
        EmailAddress productionEmail = new EmailAddress("production@belman.com");
        UserAggregate productionUser = UserAggregate.createNewUser(
                new Username("production"),
                HashedPassword.fromPlainText("production", passwordHasher),
                productionEmail
        );
        productionUser.addRole(UserRole.PRODUCTION);

        // Create QA user with both usernames
        EmailAddress qaEmail = new EmailAddress("qa@belman.com");
        UserAggregate qaUser = UserAggregate.createNewUser(
                new Username("qa"),
                HashedPassword.fromPlainText("qa", passwordHasher),
                qaEmail
        );
        qaUser.addRole(UserRole.QA);

        // Create QA user with qa_user username (for Android compatibility)
        EmailAddress qaUserEmail = new EmailAddress("qa_user@belman.com");
        UserAggregate qaUser2 = UserAggregate.createNewUser(
                new Username("qa_user"),
                HashedPassword.fromPlainText("qa", passwordHasher),
                qaUserEmail
        );
        qaUser2.addRole(UserRole.QA);

        // Save users and add email mappings
        saveWithEmail(adminUser, adminEmail);
        saveWithEmail(adminUser2, adminEmail);
        saveWithEmail(productionUser, productionEmail);
        saveWithEmail(qaUser, qaEmail);
        saveWithEmail(qaUser2, qaUserEmail);
    }

    /**
     * Helper method to save a user and add an email mapping.
     */
    private void saveWithEmail(UserAggregate user, EmailAddress email) {
        // Store the user by ID
        UserId userId = user.getId();
        usersById.put(userId, user);
        
        // Store the mapping from username to user ID
        userIdsByUsername.put(user.getUsername(), userId);
        
        // Store the mapping from email to user ID
        userIdsByEmail.put(email, userId);
    }

    @Override
    public Optional<UserAggregate> findByUsername(Username username) {
        UserId userId = userIdsByUsername.get(username);
        return Optional.ofNullable(userId != null ? usersById.get(userId) : null);
    }

    @Override
    public Optional<UserAggregate> findByEmail(EmailAddress email) {
        UserId userId = userIdsByEmail.get(email);
        return Optional.ofNullable(userId != null ? usersById.get(userId) : null);
    }

    @Override
    public void save(UserAggregate user) {
        UserId userId = user.getId();
        Username username = user.getUsername();
        
        // Store the user by ID
        usersById.put(userId, user);
        
        // Store the mapping from username to user ID
        userIdsByUsername.put(username, userId);
        
        // Note: We can't update the email mapping here because we don't have access to the email.
        // The email mapping must be added separately using the addEmailMapping method.
    }

    @Override
    public Optional<UserAggregate> findById(UserId id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<UserAggregate> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public List<UserAggregate> findByRole(UserRole role) {
        return usersById.values().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(UserId id) {
        UserAggregate user = usersById.get(id);
        if (user != null) {
            // Remove the user from the ID map
            usersById.remove(id);
            
            // Remove the username mapping
            userIdsByUsername.remove(user.getUsername());
            
            // Remove the email mapping
            // Since we don't have a getEmail() method, we need to find the email by user ID
            for (EmailAddress email : userIdsByEmail.keySet()) {
                if (userIdsByEmail.get(email).equals(id)) {
                    userIdsByEmail.remove(email);
                    break;
                }
            }
            
            return true;
        }
        return false;
    }
    
    /**
     * Helper method to add an email mapping for a user.
     * This method must be called after saving a user if the email mapping needs to be updated.
     */
    public void addEmailMapping(EmailAddress email, UserId userId) {
        userIdsByEmail.put(email, userId);
    }
}