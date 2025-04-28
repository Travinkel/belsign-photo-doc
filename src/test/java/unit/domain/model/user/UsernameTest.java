package unit.domain.model.user;

import com.belman.belsign.domain.model.user.Username;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void canCreateUsernameWithValidString() {
        Username username = new Username("productionWorker");
        assertEquals("productionWorker", username.value());
    }

    @Test
    void creatingUsernameWithNullThrowsException() {
        assertThrows(NullPointerException.class, () -> new Username(null));
    }

    @Test
    void creatingUsernameWithEmptyStringThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Username(""));
    }

    @Test
    void usernamesWithSameValueAreEqual() {
        Username username1 = new Username("QAUser");
        Username username2 = new Username("QAUser");

        assertEquals(username1, username2);
        assertEquals(username1.hashCode(), username2.hashCode());
    }

    @Test
    void toStringReturnsUsernameValue() {
        Username username = new Username("adminUser");
        assertEquals("adminUser", username.toString());
    }
}
