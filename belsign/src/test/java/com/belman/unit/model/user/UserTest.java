package com.belman.unit.model.user;


import com.belman.domain.valueobjects.EmailAddress;
import com.belman.domain.valueobjects.HashedPassword;
import com.belman.domain.aggregates.User;
import com.belman.domain.valueobjects.Username;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void userShouldBeCreatedWithUsernameAndEmail() {
        Username username = new Username("worker1");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User user = new User(username, password, email);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
    }

    @Test
    void addRoleShouldAddRoleToUser() {
        Username username = new Username("worker2");
        HashedPassword password = new HashedPassword("hashedPassword123");
        EmailAddress email = new EmailAddress("worker2@belman.dk");
        User user = new User(username, password, email);

        user.addRole(User.Role.PRODUCTION);

        assertTrue(user.getRoles().contains(User.Role.PRODUCTION));
    }
}
