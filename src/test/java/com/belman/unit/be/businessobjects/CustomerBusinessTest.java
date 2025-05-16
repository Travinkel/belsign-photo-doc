package com.belman.unit.be.businessobjects;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.customer.Company;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.customer.CustomerType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CustomerBusiness class.
 */
public class CustomerBusinessTest {

    @Test
    @DisplayName("Should create an individual customer with required fields")
    void testCreateIndividualCustomerWithRequiredFields() {
        // Arrange
        CustomerId id = CustomerId.newId();
        PersonName personName = new PersonName("John", "Doe");
        EmailAddress email = new EmailAddress("john.doe@example.com");

        // Act
        CustomerBusiness customer = CustomerBusiness.individual(id, personName, email);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.INDIVIDUAL, customer.getType());
        assertEquals(personName, customer.getPersonName());
        assertEquals(email, customer.getEmail());
        assertNull(customer.getPhoneNumber());
        assertTrue(customer.isIndividual());
        assertFalse(customer.isCompany());
        assertEquals("John Doe", customer.getName());
    }

    @Test
    @DisplayName("Should create an individual customer with all fields")
    void testCreateIndividualCustomerWithAllFields() {
        // Arrange
        CustomerId id = CustomerId.newId();
        PersonName personName = new PersonName("John", "Doe");
        EmailAddress email = new EmailAddress("john.doe@example.com");
        PhoneNumber phoneNumber = new PhoneNumber("+1234567890");

        // Act
        CustomerBusiness customer = CustomerBusiness.individual(id, personName, email, phoneNumber);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.INDIVIDUAL, customer.getType());
        assertEquals(personName, customer.getPersonName());
        assertEquals(email, customer.getEmail());
        assertEquals(phoneNumber, customer.getPhoneNumber());
        assertTrue(customer.isIndividual());
        assertFalse(customer.isCompany());
        assertEquals("John Doe", customer.getName());
    }

    @Test
    @DisplayName("Should create a company customer with required fields")
    void testCreateCompanyCustomerWithRequiredFields() {
        // Arrange
        CustomerId id = CustomerId.newId();
        Company company = new Company("Acme Inc.", "123 Main St", "REG-12345");
        EmailAddress email = new EmailAddress("info@acme.com");

        // Act
        CustomerBusiness customer = CustomerBusiness.company(id, company, email);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.COMPANY, customer.getType());
        assertEquals(company, customer.getCompany());
        assertEquals(email, customer.getEmail());
        assertNull(customer.getPhoneNumber());
        assertFalse(customer.isIndividual());
        assertTrue(customer.isCompany());
        assertEquals("Acme Inc.", customer.getName());
    }

    @Test
    @DisplayName("Should create a company customer with all fields")
    void testCreateCompanyCustomerWithAllFields() {
        // Arrange
        CustomerId id = CustomerId.newId();
        Company company = new Company("Acme Inc.", "123 Main St", "REG-12345");
        EmailAddress email = new EmailAddress("info@acme.com");
        PhoneNumber phoneNumber = new PhoneNumber("+1234567890");

        // Act
        CustomerBusiness customer = CustomerBusiness.company(id, company, email, phoneNumber);

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.COMPANY, customer.getType());
        assertEquals(company, customer.getCompany());
        assertEquals(email, customer.getEmail());
        assertEquals(phoneNumber, customer.getPhoneNumber());
        assertFalse(customer.isIndividual());
        assertTrue(customer.isCompany());
        assertEquals("Acme Inc.", customer.getName());
    }

    @Test
    @DisplayName("Should create a customer using the Builder pattern")
    void testCreateCustomerUsingBuilder() {
        // Arrange
        CustomerId id = CustomerId.newId();
        Company company = new Company("Acme Inc.", "123 Main St", "REG-12345");
        EmailAddress email = new EmailAddress("info@acme.com");
        PhoneNumber phoneNumber = new PhoneNumber("+1234567890");

        // Act
        CustomerBusiness customer = new CustomerBusiness.Builder()
                .withId(id)
                .withType(CustomerType.COMPANY)
                .withCompany(company)
                .withEmail(email)
                .withPhoneNumber(phoneNumber)
                .build();

        // Assert
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.COMPANY, customer.getType());
        assertEquals(company, customer.getCompany());
        assertEquals(email, customer.getEmail());
        assertEquals(phoneNumber, customer.getPhoneNumber());
        assertFalse(customer.isIndividual());
        assertTrue(customer.isCompany());
        assertEquals("Acme Inc.", customer.getName());
    }

    @Test
    @DisplayName("Should update customer properties")
    void testUpdateCustomerProperties() {
        // Arrange
        CustomerId id = CustomerId.newId();
        PersonName personName = new PersonName("John", "Doe");
        EmailAddress email = new EmailAddress("john.doe@example.com");
        CustomerBusiness customer = CustomerBusiness.individual(id, personName, email);

        // New values
        PersonName newPersonName = new PersonName("Jane", "Doe");
        EmailAddress newEmail = new EmailAddress("jane.doe@example.com");
        PhoneNumber newPhoneNumber = new PhoneNumber("+9876543210");

        // Act
        customer.setPersonName(newPersonName);
        customer.setEmail(newEmail);
        customer.setPhoneNumber(newPhoneNumber);

        // Assert
        assertEquals(newPersonName, customer.getPersonName());
        assertEquals(newEmail, customer.getEmail());
        assertEquals(newPhoneNumber, customer.getPhoneNumber());
        assertEquals("Jane Doe", customer.getName());
    }

    @Test
    @DisplayName("Should update company information")
    void testUpdateCompanyInformation() {
        // Arrange
        CustomerId id = CustomerId.newId();
        Company company = new Company("Acme Inc.", "123 Main St", "REG-12345");
        EmailAddress email = new EmailAddress("info@acme.com");
        CustomerBusiness customer = CustomerBusiness.company(id, company, email);

        // New company
        Company newCompany = new Company("Acme Corp", "456 Oak St", "REG-67890");

        // Act
        customer.setCompany(newCompany);

        // Assert
        assertEquals(newCompany, customer.getCompany());
        assertEquals("Acme Corp", customer.getName());
    }
}