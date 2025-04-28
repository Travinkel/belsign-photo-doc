package com.belman.belsign.domain.model.customer;

import com.belman.belsign.domain.model.user.EmailAddress;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a customer who can receive QC reports.
 */
public class Customer {
    private final CustomerId id;
    private String name;
    private EmailAddress email;
    private String company;
    private String phoneNumber;

    /**
     * Creates a new Customer with the specified ID, name, and email.
     * 
     * @param id the unique identifier for this customer
     * @param name the customer's name
     * @param email the customer's email address
     */
    public Customer(CustomerId id, String name, EmailAddress email) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    /**
     * Creates a new Customer with the specified ID, name, email, company, and phone number.
     * 
     * @param id the unique identifier for this customer
     * @param name the customer's name
     * @param email the customer's email address
     * @param company the customer's company
     * @param phoneNumber the customer's phone number
     */
    public Customer(CustomerId id, String name, EmailAddress email, String company, String phoneNumber) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.company = company;
        this.phoneNumber = phoneNumber;
    }

    public CustomerId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public EmailAddress getEmail() {
        return email;
    }

    public void setEmail(EmailAddress email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}