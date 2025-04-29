package unit.domain.model.user;

import com.belman.belsign.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void constructorShouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new UserId(null));
    }

    @Test
    void newIdShouldCreateUniqueId() {
        UserId id1 = UserId.newId();
        UserId id2 = UserId.newId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void toUUIDShouldReturnCorrectUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);
        
        assertEquals(uuid, userId.toUUID());
    }

    @Test
    void equalUserIdsShouldBeEqual() {
        UUID uuid = UUID.randomUUID();
        UserId id1 = new UserId(uuid);
        UserId id2 = new UserId(uuid);
        
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void differentUserIdsShouldNotBeEqual() {
        UserId id1 = new UserId(UUID.randomUUID());
        UserId id2 = new UserId(UUID.randomUUID());
        
        assertNotEquals(id1, id2);
    }
}