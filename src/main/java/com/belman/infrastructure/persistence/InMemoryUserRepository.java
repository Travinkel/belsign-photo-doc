package com.belman.infrastructure.persistence;

import com.belman.domain.aggregates.User;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.security.PasswordHasher;
import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.PersonName;
import com.belman.domain.valueobjects.Username;
import com.belman.domain.valueobjects.UserId;
import com.belman.infrastructure.security.BCryptPasswordHasher;

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
    private final Map<Username, User> usersByUsername = new HashMap<>();
    private final Map<EmailAddress, User> usersByEmail = new HashMap<>();
    private final Map<UserId, User> usersById = new HashMap<>();

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
        User adminUser = new User(
            UserId.newId(),
            new Username("admin"),
            HashedPassword.fromPlainText("admin", passwordHasher),
            new PersonName("Admin", "User"),
            new EmailAddress("admin@belman.com")
        );
        adminUser.addRole(User.Role.ADMIN);

        // Create admin user with password123 (for Android compatibility)
        User adminUser2 = new User(
            UserId.newId(),
            new Username("admin"),
            HashedPassword.fromPlainText("password123", passwordHasher),
            new PersonName("Admin", "User"),
            new EmailAddress("admin@belman.com")
        );
        adminUser2.addRole(User.Role.ADMIN);

        // Create production user
        User productionUser = new User(
            UserId.newId(),
            new Username("production"),
            HashedPassword.fromPlainText("production", passwordHasher),
            new PersonName("Production", "User"),
            new EmailAddress("production@belman.com")
        );
        productionUser.addRole(User.Role.PRODUCTION);

        // Create QA user with both usernames
        User qaUser = new User(
            UserId.newId(),
            new Username("qa"),
            HashedPassword.fromPlainText("qa", passwordHasher),
            new PersonName("QA", "User"),
            new EmailAddress("qa@belman.com")
        );
        qaUser.addRole(User.Role.QA);

        // Create QA user with qa_user username (for Android compatibility)
        User qaUser2 = new User(
            UserId.newId(),
            new Username("qa_user"),
            HashedPassword.fromPlainText("qa", passwordHasher),
            new PersonName("QA", "User"),
            new EmailAddress("qa_user@belman.com")
        );
        qaUser2.addRole(User.Role.QA);

        // Save users
        save(adminUser);
        save(adminUser2);
        save(productionUser);
        save(qaUser);
        save(qaUser2);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return Optional.ofNullable(usersByEmail.get(email));
    }

    @Override
    public void save(User user) {
        usersByUsername.put(user.getUsername(), user);
        usersByEmail.put(user.getEmail(), user);
        usersById.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return Optional.ofNullable(usersById.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersById.values());
    }

    @Override
    public List<User> findByRole(User.Role role) {
        return usersById.values().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(UserId id) {
        User user = usersById.get(id);
        if (user != null) {
            usersById.remove(id);
            usersByUsername.remove(user.getUsername());
            usersByEmail.remove(user.getEmail());
            return true;
        }
        return false;
    }
}
