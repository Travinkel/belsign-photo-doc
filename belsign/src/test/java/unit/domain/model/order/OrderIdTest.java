package unit.domain.model.order;

import domain.model.order.OrderId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderIdTest {

    @Test
    void constructorShouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new OrderId(null));
    }

    @Test
    void newIdShouldCreateUniqueId() {
        OrderId id1 = OrderId.newId();
        OrderId id2 = OrderId.newId();
        
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void toUUIDShouldReturnCorrectUUID() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = new OrderId(uuid);
        
        assertEquals(uuid, orderId.toUUID());
    }

    @Test
    void equalOrderIdsShouldBeEqual() {
        UUID uuid = UUID.randomUUID();
        OrderId id1 = new OrderId(uuid);
        OrderId id2 = new OrderId(uuid);
        
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void differentOrderIdsShouldNotBeEqual() {
        OrderId id1 = new OrderId(UUID.randomUUID());
        OrderId id2 = new OrderId(UUID.randomUUID());
        
        assertNotEquals(id1, id2);
    }
}