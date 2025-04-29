package unit.domain.model.user;

import domain.model.user.Username;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UsernameTest {

    @Test
    void validUsernameShouldBeCreated() {
        Username username = new Username("production_worker");
        assertEquals("production_worker", username.getValue());
    }

    @Test
    void emptyUsernameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Username(""));
    }

    @Test
    void nullUsernameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Username(null));
    }
}
