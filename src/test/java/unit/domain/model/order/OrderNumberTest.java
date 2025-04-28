package unit.domain.model.order;

import com.belman.belsign.domain.model.order.OrderNumber;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderNumberTest {

    @Test
    void validOrderNumberShouldBeCreated() {
        OrderNumber orderNumber = new OrderNumber("01/24-123456-20240428");
        assertEquals("01/24-123456-20240428", orderNumber.value());
    }

    @Test
    void invalidOrderNumberShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new OrderNumber("invalid-ordernumber"));
        assertThrows(IllegalArgumentException.class, () -> new OrderNumber("123456")); // Forkert format
    }

    @Test
    void nullOrderNumberShouldThrowException() {
        assertThrows(NullPointerException.class, () -> new OrderNumber(null));
    }
}
