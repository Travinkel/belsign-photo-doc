package unit.domain.model.user;

import domain.model.user.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStatusTest {

    @Test
    void shouldHaveCorrectValues() {
        assertEquals(4, UserStatus.values().length);
        assertEquals(UserStatus.ACTIVE, UserStatus.valueOf("ACTIVE"));
        assertEquals(UserStatus.INACTIVE, UserStatus.valueOf("INACTIVE"));
        assertEquals(UserStatus.LOCKED, UserStatus.valueOf("LOCKED"));
        assertEquals(UserStatus.PENDING, UserStatus.valueOf("PENDING"));
    }
    
    @Test
    void shouldBeComparable() {
        assertNotEquals(UserStatus.ACTIVE, UserStatus.INACTIVE);
        assertNotEquals(UserStatus.ACTIVE, UserStatus.LOCKED);
        assertNotEquals(UserStatus.ACTIVE, UserStatus.PENDING);
    }
}