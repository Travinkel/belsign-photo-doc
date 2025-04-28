package unit.domain.model.user;

import com.belman.belsign.domain.model.user.EmailAddress;
import com.belman.belsign.domain.model.user.User;
import com.belman.belsign.domain.model.user.Username;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void userShouldBeCreatedWithUsernameAndEmail() {
        Username username = new Username("worker1");
        EmailAddress email = new EmailAddress("worker1@belman.dk");
        User user = new User(username, email);

        assertEquals(username, user.getUsername());
        assertEquals(email, user.getEmail());
    }

    @Test
    void addRoleShouldAddRoleToUser() {
        Username username = new Username("worker2");
        EmailAddress email = new EmailAddress("worker2@belman.dk");
        User user = new User(username, email);

        user.addRole(User.Role.PRODUCTION);

        assertTrue(user.getRoles().contains(User.Role.PRODUCTION));
    }
}
