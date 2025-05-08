package com.belman.business.richbe.customer;



import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.common.PersonName;
import com.belman.business.richbe.common.PhoneNumber;

import java.util.Objects;

/**
 * Entity representing a customer who can receive QC reports.
 * Can represent either an individual person or a company.
 */
public class CustomerAggregate {
    private final CustomerId id;
    private final CustomerType type;
    private PersonName personName;
    private Company company;
    private EmailAddress email;
    private PhoneNumber phoneNumber;

    /**
     * Creates a new individual Customer with the specified ID, name, and email.
     * 
     * @param id the unique identifier for this customer
     * @param personName the person's name
     * @param email the customer's email address
     * @return a new individual customer
     */
    public static CustomerAggregate individual(CustomerId id, PersonName personName, EmailAddress email) {
        return new CustomerAggregate(id, CustomerType.INDIVIDUAL, personName, null, email, null);
    }

    /**
     * Creates a new individual Customer with the specified ID, name, email, and phone number.
     * 
     * @param id the unique identifier for this customer
     * @param personName the person's name
     * @param email the customer's email address
     * @param phoneNumber the customer's phone number
     * @return a new individual customer
     */
    public static CustomerAggregate individual(CustomerId id, PersonName personName, EmailAddress email, PhoneNumber phoneNumber) {
        return new CustomerAggregate(id, CustomerType.INDIVIDUAL, personName, null, email, phoneNumber);
    }

    /**
     * Creates a new company Customer with the specified ID, company, and email.
     * 
     * @param id the unique identifier for this customer
     * @param company the company
     * @param email the customer's email address
     * @return a new company customer
     */
    public static CustomerAggregate company(CustomerId id, Company company, EmailAddress email) {
        return new CustomerAggregate(id, CustomerType.COMPANY, null, company, email, null);
    }

    /**
     * Creates a new company Customer with the specified ID, company, email, and phone number.
     * 
     * @param id the unique identifier for this customer
     * @param company the company
     * @param email the customer's email address
     * @param phoneNumber the customer's phone number
     * @return a new company customer
     */
    public static CustomerAggregate company(CustomerId id, Company company, EmailAddress email, PhoneNumber phoneNumber) {
        return new CustomerAggregate(id, CustomerType.COMPANY, null, company, email, phoneNumber);
    }

    /**
     * Private constructor used by factory methods.
     */
    private CustomerAggregate(CustomerId id, CustomerType type, PersonName personName, Company company,
                              EmailAddress email, PhoneNumber phoneNumber) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");

        if (type == CustomerType.INDIVIDUAL) {
            this.personName = Objects.requireNonNull(personName, "personName must not be null for individual customers");
        } else {
            this.company = Objects.requireNonNull(company, "company must not be null for company customers");
        }

        this.phoneNumber = phoneNumber;
    }

    public CustomerId getId() {
        return id;
    }

    public CustomerType getType() {
        return type;
    }

    public boolean isIndividual() {
        return type == CustomerType.INDIVIDUAL;
    }

    public boolean isCompany() {
        return type == CustomerType.COMPANY;
    }

    public PersonName getPersonName() {
        if (!isIndividual()) {
            throw new IllegalStateException("Person name is only available for individual customers");
        }
        return personName;
    }

    public void setPersonName(PersonName personName) {
        if (!isIndividual()) {
            throw new IllegalStateException("Cannot set person name for company customers");
        }
        this.personName = Objects.requireNonNull(personName, "personName must not be null");
    }

    public Company getCompany() {
        if (!isCompany()) {
            throw new IllegalStateException("Company is only available for company customers");
        }
        return company;
    }

    public void setCompany(Company company) {
        if (!isCompany()) {
            throw new IllegalStateException("Cannot set company for individual customers");
        }
        this.company = Objects.requireNonNull(company, "company must not be null");
    }

    /**
     * @return the customer's name (person name or company name)
     */
    public String getName() {
        return isIndividual() ? personName.toString() : company.toString();
    }

    public EmailAddress getEmail() {
        return email;
    }

    public void setEmail(EmailAddress email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Builder class for creating Customer instances.
     */
    public static class Builder {
        private CustomerId id;
        private CustomerType type;
        private PersonName personName;
        private Company company;
        private EmailAddress email;
        private PhoneNumber phoneNumber;

        public Builder withId(CustomerId id) {
            this.id = id;
            return this;
        }

        public Builder withType(CustomerType type) {
            this.type = type;
            return this;
        }

        public Builder withPersonName(PersonName personName) {
            this.personName = personName;
            return this;
        }

        public Builder withCompany(Company company) {
            this.company = company;
            return this;
        }

        public Builder withEmail(EmailAddress email) {
            this.email = email;
            return this;
        }

        public Builder withPhoneNumber(PhoneNumber phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public CustomerAggregate build() {
            if (type == CustomerType.INDIVIDUAL) {
                return new CustomerAggregate(id, type, personName, null, email, phoneNumber);
            } else if (type == CustomerType.COMPANY) {
                return new CustomerAggregate(id, type, null, company, email, phoneNumber);
            } else {
                throw new IllegalStateException("Invalid customer type");
            }
        }
    }
}
