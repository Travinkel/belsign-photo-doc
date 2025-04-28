package unit.domain.model.customer;

import com.belman.belsign.domain.model.customer.Customer;
import com.belman.belsign.domain.model.customer.CustomerId;
import com.belman.belsign.domain.model.user.EmailAddress;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void customerShouldBeCreatedWithRequiredFields() {
        // Arrange
        CustomerId id = new CustomerId(UUID.randomUUID());
        String name = "John Doe";
        EmailAddress email = new EmailAddress("john.doe@example.com");
        
        // Act
        Customer customer = new Customer(id, name, email);
        
        // Assert
        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertNull(customer.getCompany());
        assertNull(customer.getPhoneNumber());
    }
    
    @Test
    void customerShouldBeCreatedWithAllFields() {
        // Arrange
        CustomerId id = new CustomerId(UUID.randomUUID());
        String name = "John Doe";
        EmailAddress email = new EmailAddress("john.doe@example.com");
        String company = "Acme Inc.";
        String phoneNumber = "+1 123-456-7890";
        
        // Act
        Customer customer = new Customer(id, name, email, company, phoneNumber);
        
        // Assert
        assertEquals(id, customer.getId());
        assertEquals(name, customer.getName());
        assertEquals(email, customer.getEmail());
        assertEquals(company, customer.getCompany());
        assertEquals(phoneNumber, customer.getPhoneNumber());
    }
    
    @Test
    void customerShouldUpdateMutableFields() {
        // Arrange
        Customer customer = new Customer(
            new CustomerId(UUID.randomUUID()),
            "John Doe",
            new EmailAddress("john.doe@example.com")
        );
        
        // Act
        String newName = "Jane Doe";
        EmailAddress newEmail = new EmailAddress("jane.doe@example.com");
        String newCompany = "Beta Corp.";
        String newPhoneNumber = "+1 987-654-3210";
        
        customer.setName(newName);
        customer.setEmail(newEmail);
        customer.setCompany(newCompany);
        customer.setPhoneNumber(newPhoneNumber);
        
        // Assert
        assertEquals(newName, customer.getName());
        assertEquals(newEmail, customer.getEmail());
        assertEquals(newCompany, customer.getCompany());
        assertEquals(newPhoneNumber, customer.getPhoneNumber());
    }
    
    @Test
    void customerShouldRejectNullRequiredFields() {
        // Arrange
        CustomerId id = new CustomerId(UUID.randomUUID());
        String name = "John Doe";
        EmailAddress email = new EmailAddress("john.doe@example.com");
        
        // Assert
        assertThrows(NullPointerException.class, () -> new Customer(null, name, email));
        assertThrows(NullPointerException.class, () -> new Customer(id, null, email));
        assertThrows(NullPointerException.class, () -> new Customer(id, name, null));
        
        Customer customer = new Customer(id, name, email);
        assertThrows(NullPointerException.class, () -> customer.setName(null));
        assertThrows(NullPointerException.class, () -> customer.setEmail(null));
    }
}