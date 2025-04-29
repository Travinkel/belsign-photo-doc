package unit.domain.model.customer;


import domain.model.customer.*;
import domain.model.user.EmailAddress;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void individualCustomerShouldBeCreated() {
        CustomerId id = CustomerId.newId();
        PersonName name = new PersonName("John", "Doe");
        EmailAddress email = new EmailAddress("john.doe@example.com");
        
        Customer customer = Customer.individual(id, name, email);
        
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.INDIVIDUAL, customer.getType());
        assertEquals(name, customer.getPersonName());
        assertEquals(email, customer.getEmail());
        assertNull(customer.getPhoneNumber());
        assertTrue(customer.isIndividual());
        assertFalse(customer.isCompany());
        assertEquals("John Doe", customer.getName());
    }
    
    @Test
    void individualCustomerWithPhoneNumberShouldBeCreated() {
        CustomerId id = CustomerId.newId();
        PersonName name = new PersonName("John", "Doe");
        EmailAddress email = new EmailAddress("john.doe@example.com");
        PhoneNumber phone = new PhoneNumber("+45 12345678");
        
        Customer customer = Customer.individual(id, name, email, phone);
        
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.INDIVIDUAL, customer.getType());
        assertEquals(name, customer.getPersonName());
        assertEquals(email, customer.getEmail());
        assertEquals(phone, customer.getPhoneNumber());
    }
    
    @Test
    void companyCustomerShouldBeCreated() {
        CustomerId id = CustomerId.newId();
        Company company = new Company("Belman A/S", "12345678", "Oddesundvej 18");
        EmailAddress email = new EmailAddress("info@belman.dk");
        
        Customer customer = Customer.company(id, company, email);
        
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.COMPANY, customer.getType());
        assertEquals(company, customer.getCompany());
        assertEquals(email, customer.getEmail());
        assertNull(customer.getPhoneNumber());
        assertTrue(customer.isCompany());
        assertFalse(customer.isIndividual());
        assertEquals("Belman A/S", customer.getName());
    }
    
    @Test
    void companyCustomerWithPhoneNumberShouldBeCreated() {
        CustomerId id = CustomerId.newId();
        Company company = new Company("Belman A/S", "12345678", "Oddesundvej 18");
        EmailAddress email = new EmailAddress("info@belman.dk");
        PhoneNumber phone = new PhoneNumber("+45 12345678");
        
        Customer customer = Customer.company(id, company, email, phone);
        
        assertEquals(id, customer.getId());
        assertEquals(CustomerType.COMPANY, customer.getType());
        assertEquals(company, customer.getCompany());
        assertEquals(email, customer.getEmail());
        assertEquals(phone, customer.getPhoneNumber());
    }
    
    @Test
    void getPersonNameShouldThrowExceptionForCompanyCustomer() {
        Customer customer = Customer.company(
            CustomerId.newId(),
            new Company("Belman A/S", "12345678", "Oddesundvej 18"),
            new EmailAddress("info@belman.dk")
        );
        
        assertThrows(IllegalStateException.class, customer::getPersonName);
    }
    
    @Test
    void setPersonNameShouldThrowExceptionForCompanyCustomer() {
        Customer customer = Customer.company(
            CustomerId.newId(),
            new Company("Belman A/S", "12345678", "Oddesundvej 18"),
            new EmailAddress("info@belman.dk")
        );
        
        PersonName name = new PersonName("John", "Doe");
        assertThrows(IllegalStateException.class, () -> customer.setPersonName(name));
    }
    
    @Test
    void getCompanyShouldThrowExceptionForIndividualCustomer() {
        Customer customer = Customer.individual(
            CustomerId.newId(),
            new PersonName("John", "Doe"),
            new EmailAddress("john.doe@example.com")
        );
        
        assertThrows(IllegalStateException.class, customer::getCompany);
    }
    
    @Test
    void setCompanyShouldThrowExceptionForIndividualCustomer() {
        Customer customer = Customer.individual(
            CustomerId.newId(),
            new PersonName("John", "Doe"),
            new EmailAddress("john.doe@example.com")
        );
        
        Company company = new Company("Belman A/S", "12345678", "Oddesundvej 18");
        assertThrows(IllegalStateException.class, () -> customer.setCompany(company));
    }
    
    @Test
    void setEmailShouldUpdateEmail() {
        Customer customer = Customer.individual(
            CustomerId.newId(),
            new PersonName("John", "Doe"),
            new EmailAddress("john.doe@example.com")
        );
        
        EmailAddress newEmail = new EmailAddress("new.email@example.com");
        customer.setEmail(newEmail);
        
        assertEquals(newEmail, customer.getEmail());
    }
    
    @Test
    void setPhoneNumberShouldUpdatePhoneNumber() {
        Customer customer = Customer.individual(
            CustomerId.newId(),
            new PersonName("John", "Doe"),
            new EmailAddress("john.doe@example.com")
        );
        
        PhoneNumber phone = new PhoneNumber("+45 12345678");
        customer.setPhoneNumber(phone);
        
        assertEquals(phone, customer.getPhoneNumber());
    }
}