package com.belman.unit.model.customer;

import com.belman.domain.valueobjects.Company;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompanyTest {

    @Test
    void validCompanyShouldBeCreated() {
        Company company = new Company("Belman A/S", "12345678", "Oddesundvej 18, 6715 Esbjerg N");
        assertEquals("Belman A/S", company.name());
        assertEquals("12345678", company.registrationNumber());
        assertEquals("Oddesundvej 18, 6715 Esbjerg N", company.address());
    }

    @Test
    void companyShouldBeCreatedWithNullRegistrationNumber() {
        Company company = new Company("Belman A/S", null, "Oddesundvej 18, 6715 Esbjerg N");
        assertEquals("Belman A/S", company.name());
        assertNull(company.registrationNumber());
        assertEquals("Oddesundvej 18, 6715 Esbjerg N", company.address());
    }

    @Test
    void companyShouldBeCreatedWithNullAddress() {
        Company company = new Company("Belman A/S", "12345678", null);
        assertEquals("Belman A/S", company.name());
        assertEquals("12345678", company.registrationNumber());
        assertNull(company.address());
    }

    @Test
    void withNameShouldCreateCompanyWithJustName() {
        Company company = Company.withName("Belman A/S");
        assertEquals("Belman A/S", company.name());
        assertNull(company.registrationNumber());
        assertNull(company.address());
    }

    @Test
    void nullNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Company(null, "12345678", "Address"));
    }

    @Test
    void emptyNameShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Company("", "12345678", "Address"));
    }

    @Test
    void toStringShouldReturnName() {
        Company company = new Company("Belman A/S", "12345678", "Address");
        assertEquals("Belman A/S", company.toString());
    }

    @Test
    void equalCompaniesShouldBeEqual() {
        Company company1 = new Company("Belman A/S", "12345678", "Address");
        Company company2 = new Company("Belman A/S", "12345678", "Address");
        
        assertEquals(company1, company2);
        assertEquals(company1.hashCode(), company2.hashCode());
    }

    @Test
    void differentCompaniesShouldNotBeEqual() {
        Company company1 = new Company("Belman A/S", "12345678", "Address");
        Company company2 = new Company("Other Company", "12345678", "Address");
        
        assertNotEquals(company1, company2);
    }
}