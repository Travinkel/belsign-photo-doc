package com.belman.repository.persistence.memory;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.user.*;
import com.belman.service.usecase.security.BCryptPasswordHasher;

import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the UserRepository interface.
 * This implementation stores users in memory and is suitable for development and testing.
 * In a production environment, this would be replaced with a database-backed implementation.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<Username, UserId> userIdsByUsername = new HashMap<>();
    private final Map<EmailAddress, UserId> userIdsByEmail = new HashMap<>();
    private final Map<String, UserId> userIdsByPinCode = new HashMap<>();
    private final Map<String, UserId> userIdsByQrCodeHash = new HashMap<>();
    private final Map<UserId, UserBusiness> usersById = new HashMap<>();

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
        UserBusiness adminUser = UserBusiness.createNewUser(
                new Username("admin"),
                HashedPassword.fromPlainText("admin", passwordHasher),
                adminEmail
        );
        adminUser.addRole(UserRole.ADMIN);

        // Create admin user with password123 (for Android compatibility)
        UserBusiness adminUser2 = UserBusiness.createNewUser(
                new Username("admin"),
                HashedPassword.fromPlainText("password123", passwordHasher),
                adminEmail
        );
        adminUser2.addRole(UserRole.ADMIN);

        // Create production user
        EmailAddress productionEmail = new EmailAddress("production@belman.com");
        UserBusiness productionUser = UserBusiness.createNewUser(
                new Username("production"),
                HashedPassword.fromPlainText("production", passwordHasher),
                productionEmail
        );
        productionUser.addRole(UserRole.PRODUCTION);

        // Create QA user with both usernames
        EmailAddress qaEmail = new EmailAddress("qa_user1@belman.com");
        UserBusiness qaUser = UserBusiness.createNewUser(
                new Username("qa_user1"),
                HashedPassword.fromPlainText("qa", passwordHasher),
                qaEmail
        );
        qaUser.addRole(UserRole.QA);

        // Create QA user with qa_user username (for Android compatibility)
        EmailAddress qaUserEmail = new EmailAddress("qa_user@belman.com");
        UserBusiness qaUser2 = UserBusiness.createNewUser(
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

        // Add PIN code mappings for testing
        addPinCodeMapping("1234", productionUser.getId());

        // Add QR code hash mappings for testing
        addQrCodeHashMapping("scanner123hash", qaUser.getId());
    }

    /**
     * Helper method to save a user and add an email mapping.
     */
    private void saveWithEmail(UserBusiness user, EmailAddress email) {
        // Store the user by ID
        UserId userId = user.getId();
        usersById.put(userId, user);

        // Store the mapping from username to user ID
        userIdsByUsername.put(user.getUsername(), userId);

        // Store the mapping from email to user ID
        userIdsByEmail.put(email, userId);
    }

    @Override
    public Optional<UserBusiness> findByUsername(Username username) {
        UserId userId = userIdsByUsername.get(username);
        return Optional.ofNullable(userId != null ? usersById.get(userId) : null);
    }

    @Override
    public Optional<UserBusiness> findByEmail(EmailAddress email) {
        UserId userId = userIdsByEmail.get(email);
        return Optional.ofNullable(userId != null ? usersById.get(userId) : null);
    }

    @Override
    public Optional<UserBusiness> findById(UserId id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<UserBusiness> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public List<UserBusiness> findByRole(UserRole role) {
        return usersById.values().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toList());
    }

    @Override
    public UserBusiness save(UserBusiness user) {
        UserId userId = user.getId();
        Username username = user.getUsername();

        // Store the user by ID
        usersById.put(userId, user);

        // Store the mapping from username to user ID
        userIdsByUsername.put(username, userId);

        // Note: We can't update the email mapping here because we don't have access to the email.
        // The email mapping must be added separately using the addEmailMapping method.

        return user;
    }

    @Override
    public boolean deleteById(UserId id) {
        UserBusiness user = usersById.get(id);
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

    @Override
    public void delete(UserBusiness user) {
        if (user != null) {
            deleteById(user.getId());
        }
    }

    @Override
    public boolean existsById(UserId id) {
        return usersById.containsKey(id);
    }

    @Override
    public long count() {
        return usersById.size();
    }

    @Override
    public Optional<UserBusiness> findByPinCode(String pinCode) {
        UserId userId = userIdsByPinCode.get(pinCode);
        return Optional.ofNullable(userId != null ? usersById.get(userId) : null);
    }

    @Override
    public Optional<UserBusiness> findByQrCodeHash(String qrCodeHash) {
        UserId userId = userIdsByQrCodeHash.get(qrCodeHash);
        return Optional.ofNullable(userId != null ? usersById.get(userId) : null);
    }

    /**
     * Helper method to add an email mapping for a user.
     * This method must be called after saving a user if the email mapping needs to be updated.
     */
    public void addEmailMapping(EmailAddress email, UserId userId) {
        userIdsByEmail.put(email, userId);
    }

    /**
     * Helper method to add a PIN code mapping for a user.
     * This method must be called after saving a user if the PIN code mapping needs to be updated.
     */
    public void addPinCodeMapping(String pinCode, UserId userId) {
        userIdsByPinCode.put(pinCode, userId);
    }

    /**
     * Helper method to add a QR code hash mapping for a user.
     * This method must be called after saving a user if the QR code hash mapping needs to be updated.
     */
    public void addQrCodeHashMapping(String qrCodeHash, UserId userId) {
        userIdsByQrCodeHash.put(qrCodeHash, userId);
    }
}
