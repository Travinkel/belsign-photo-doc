package domain.model.customer;


import domain.model.user.EmailAddress;

import java.util.Objects;

/**
 * Entity representing a customer who can receive QC reports.
 * Can represent either an individual person or a company.
 */
public class Customer {
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
    public static Customer individual(CustomerId id, PersonName personName, EmailAddress email) {
        return new Customer(id, CustomerType.INDIVIDUAL, personName, null, email, null);
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
    public static Customer individual(CustomerId id, PersonName personName, EmailAddress email, PhoneNumber phoneNumber) {
        return new Customer(id, CustomerType.INDIVIDUAL, personName, null, email, phoneNumber);
    }

    /**
     * Creates a new company Customer with the specified ID, company, and email.
     * 
     * @param id the unique identifier for this customer
     * @param company the company
     * @param email the customer's email address
     * @return a new company customer
     */
    public static Customer company(CustomerId id, Company company, EmailAddress email) {
        return new Customer(id, CustomerType.COMPANY, null, company, email, null);
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
    public static Customer company(CustomerId id, Company company, EmailAddress email, PhoneNumber phoneNumber) {
        return new Customer(id, CustomerType.COMPANY, null, company, email, phoneNumber);
    }

    /**
     * Private constructor used by factory methods.
     */
    private Customer(CustomerId id, CustomerType type, PersonName personName, Company company, 
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
}
