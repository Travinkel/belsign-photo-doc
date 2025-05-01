package com.belman.unit.repository;

import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.repositories.UserRepository;
import com.belman.domain.valueobjects.Username;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

class InMemoryUserRepositoryTest {

    static class InMemoryUserRepository implements UserRepository {
        private User user;

        @Override
        public Optional<User> findByUsername(Username username) {
            if (user != null && user.getUsername().equals(username)) {
                return Optional.of(user);
            }
            return Optional.empty();
        }

        @Override
        public Optional<User> findByEmail(EmailAddress email) {
            if (user != null && user.getEmail().equals(email)) {
                return Optional.of(user);
            }
            return Optional.empty();
        }

        @Override
        public void save(User user) {
            this.user = user;
        }
    }

    @Test
    void savedUserShouldBeFoundByUsernameAndEmail() {
        InMemoryUserRepository repo = new InMemoryUserRepository();
        Username username = new Username("admin1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("admin1@belman.dk");
        User user = new User(username, password, email);
        repo.save(user);

        assertEquals(user, repo.findByUsername(username).orElseThrow());
        assertEquals(user, repo.findByEmail(email).orElseThrow());
    }
}
