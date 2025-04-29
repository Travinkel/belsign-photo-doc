package unit.domain.model.customer;

import com.belman.belsign.domain.model.customer.PersonName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PersonNameTest {

    @Test
    void validNameShouldBeCreated() {
        PersonName name = new PersonName("John", "Doe");
        assertEquals("John", name.firstName());
        assertEquals("Doe", name.lastName());
        assertEquals("John Doe", name.getFullName());
    }

    @Test
    void emptyFirstNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PersonName("", "Doe"));
    }

    @Test
    void nullFirstNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PersonName(null, "Doe"));
    }

    @Test
    void emptyLastNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PersonName("John", ""));
    }

    @Test
    void nullLastNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PersonName("John", null));
    }

    @Test
    void toStringShouldReturnFullName() {
        PersonName name = new PersonName("John", "Doe");
        assertEquals("John Doe", name.toString());
    }

    @Test
    void equalNamesShouldBeEqual() {
        PersonName name1 = new PersonName("John", "Doe");
        PersonName name2 = new PersonName("John", "Doe");
        
        assertEquals(name1, name2);
        assertEquals(name1.hashCode(), name2.hashCode());
    }

    @Test
    void differentNamesShouldNotBeEqual() {
        PersonName name1 = new PersonName("John", "Doe");
        PersonName name2 = new PersonName("Jane", "Doe");
        
        assertNotEquals(name1, name2);
    }
}