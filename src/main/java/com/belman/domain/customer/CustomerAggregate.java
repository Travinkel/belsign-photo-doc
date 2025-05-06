package com.belman.domain.customer;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;

import java.util.Objects;

/**
 * Aggregate root representing a customer in the BelSign system.
 * <p>
 * The Customer aggregate encapsulates all customer-related information,
 * including contact details and company information. Customers are
 * associated with orders and are essential for the order fulfillment process.
 */
public class CustomerAggregate {
    private final CustomerId id;
    private Company company;
    private PersonName contactPerson;
    private EmailAddress contactEmail;
    private PhoneNumber contactPhone;
    private boolean active;
    private CustomerType customerType;

    /**
     * Creates a new Customer with the specified ID and basic information.
     *
     * @param id            the unique identifier for this customer
     * @param company       the customer's company details
     * @param contactPerson the name of the primary contact person
     * @param contactEmail  the email address of the primary contact person
     */
    public CustomerAggregate(CustomerId id, Company company, PersonName contactPerson, EmailAddress contactEmail) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.company = Objects.requireNonNull(company, "company must not be null");
        this.contactPerson = Objects.requireNonNull(contactPerson, "contactPerson must not be null");
        this.contactEmail = Objects.requireNonNull(contactEmail, "contactEmail must not be null");
        this.active = true;
        this.customerType = CustomerType.REGULAR;
    }

    /**
     * Creates a new Customer with all details.
     *
     * @param id            the unique identifier for this customer
     * @param company       the customer's company details
     * @param contactPerson the name of the primary contact person
     * @param contactEmail  the email address of the primary contact person
     * @param contactPhone  the phone number of the primary contact person
     * @param customerType  the type of customer (REGULAR, VIP, etc.)
     * @param active        whether the customer is active
     */
    public CustomerAggregate(CustomerId id, Company company, PersonName contactPerson,
                             EmailAddress contactEmail, PhoneNumber contactPhone,
                             CustomerType customerType, boolean active) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.company = Objects.requireNonNull(company, "company must not be null");
        this.contactPerson = Objects.requireNonNull(contactPerson, "contactPerson must not be null");
        this.contactEmail = Objects.requireNonNull(contactEmail, "contactEmail must not be null");
        this.contactPhone = contactPhone; // Optional, can be null
        this.customerType = Objects.requireNonNull(customerType, "customerType must not be null");
        this.active = active;
    }

    /**
     * Returns the unique identifier for this customer.
     */
    public CustomerId getId() {
        return id;
    }

    /**
     * Returns the company details for this customer.
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Sets or updates the company details for this customer.
     *
     * @param company the new company details
     * @throws NullPointerException if company is null
     */
    public void setCompany(Company company) {
        this.company = Objects.requireNonNull(company, "company must not be null");
    }

    /**
     * Returns the name of the primary contact person for this customer.
     */
    public PersonName getContactPerson() {
        return contactPerson;
    }

    /**
     * Sets or updates the name of the primary contact person for this customer.
     *
     * @param contactPerson the new contact person
     * @throws NullPointerException if contactPerson is null
     */
    public void setContactPerson(PersonName contactPerson) {
        this.contactPerson = Objects.requireNonNull(contactPerson, "contactPerson must not be null");
    }

    /**
     * Returns the email address of the primary contact person for this customer.
     */
    public EmailAddress getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets or updates the email address of the primary contact person for this customer.
     *
     * @param contactEmail the new contact email
     * @throws NullPointerException if contactEmail is null
     */
    public void setContactEmail(EmailAddress contactEmail) {
        this.contactEmail = Objects.requireNonNull(contactEmail, "contactEmail must not be null");
    }

    /**
     * Returns the phone number of the primary contact person for this customer.
     * May be null if no phone number is provided.
     */
    public PhoneNumber getContactPhone() {
        return contactPhone;
    }

    /**
     * Sets or updates the phone number of the primary contact person for this customer.
     * Can be null if no phone number is provided.
     *
     * @param contactPhone the new contact phone number, or null if none
     */
    public void setContactPhone(PhoneNumber contactPhone) {
        this.contactPhone = contactPhone;
    }

    /**
     * Returns whether this customer is active.
     * Inactive customers are retained in the system but are not available for new orders.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets or updates whether this customer is active.
     *
     * @param active true if the customer should be active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Activates this customer, allowing them to place orders.
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Deactivates this customer, preventing them from placing new orders.
     * Existing orders are not affected.
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * Returns the type of this customer (REGULAR, VIP, etc.).
     */
    public CustomerType getCustomerType() {
        return customerType;
    }

    /**
     * Sets or updates the type of this customer.
     *
     * @param customerType the new customer type
     * @throws NullPointerException if customerType is null
     */
    public void setCustomerType(CustomerType customerType) {
        this.customerType = Objects.requireNonNull(customerType, "customerType must not be null");
    }

    /**
     * Upgrades this customer to VIP status.
     */
    public void upgradeToVip() {
        this.customerType = CustomerType.VIP;
    }
}