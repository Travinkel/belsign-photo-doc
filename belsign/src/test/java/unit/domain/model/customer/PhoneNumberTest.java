package unit.domain.model.customer;

import domain.model.customer.PhoneNumber;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @Test
    void validPhoneNumberShouldBeCreated() {
        PhoneNumber phone = new PhoneNumber("+45 12345678");
        assertEquals("+45 12345678", phone.value());
    }

    @Test
    void simplePhoneNumberShouldBeCreated() {
        PhoneNumber phone = new PhoneNumber("12345678");
        assertEquals("12345678", phone.value());
    }

    @Test
    void phoneNumberWithParenthesesShouldBeCreated() {
        PhoneNumber phone = new PhoneNumber("+45 (0) 12345678");
        assertEquals("+45 (0) 12345678", phone.value());
    }

    @Test
    void nullPhoneNumberShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber(null));
    }

    @Test
    void invalidPhoneNumberShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("abc"));
    }

    @Test
    void tooShortPhoneNumberShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new PhoneNumber("123"));
    }

    @Test
    void getFormattedShouldReturnValue() {
        PhoneNumber phone = new PhoneNumber("+45 12345678");
        assertEquals("+45 12345678", phone.getFormatted());
    }

    @Test
    void toStringShouldReturnValue() {
        PhoneNumber phone = new PhoneNumber("+45 12345678");
        assertEquals("+45 12345678", phone.toString());
    }

    @Test
    void equalPhoneNumbersShouldBeEqual() {
        PhoneNumber phone1 = new PhoneNumber("+45 12345678");
        PhoneNumber phone2 = new PhoneNumber("+45 12345678");
        
        assertEquals(phone1, phone2);
        assertEquals(phone1.hashCode(), phone2.hashCode());
    }

    @Test
    void differentPhoneNumbersShouldNotBeEqual() {
        PhoneNumber phone1 = new PhoneNumber("+45 12345678");
        PhoneNumber phone2 = new PhoneNumber("+45 87654321");
        
        assertNotEquals(phone1, phone2);
    }
}