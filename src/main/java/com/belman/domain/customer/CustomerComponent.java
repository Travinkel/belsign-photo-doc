package com.belman.domain.customer;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.core.BusinessComponent;

import java.time.Instant;
import java.util.Objects;

/**
 * Business component representing a contact person for a customer.
 * This component is part of a customer business object and provides
 * contact information for a specific person within a customer organization.
 */
public class CustomerComponent extends BusinessComponent<CustomerId> {
    private final CustomerId customerId;
    private final String role;
    private PersonName name;
    private EmailAddress email;
    private PhoneNumber phoneNumber;
    private boolean isPrimary;
    private Instant lastModifiedAt;

    /**
     * Creates a new CustomerComponent with the specified details.
     *
     * @param builder the builder containing the component's properties
     */
    private CustomerComponent(Builder builder) {
        this.customerId = Objects.requireNonNull(builder.customerId, "customerId must not be null");
        this.role = Objects.requireNonNull(builder.role, "role must not be null");
        this.name = Objects.requireNonNull(builder.name, "name must not be null");
        this.email = Objects.requireNonNull(builder.email, "email must not be null");
        this.phoneNumber = builder.phoneNumber;
        this.isPrimary = builder.isPrimary;
        this.lastModifiedAt = Instant.now();
    }

    /**
     * Creates a new builder for constructing CustomerComponent instances.
     *
     * @return a new CustomerComponent builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the ID of the customer this component belongs to.
     *
     * @return the customer ID
     */
    @Override
    public CustomerId getId() {
        return customerId;
    }

    /**
     * Returns the role of this contact person within the customer organization.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the name of this contact person.
     *
     * @return the name
     */
    public PersonName getName() {
        return name;
    }

    /**
     * Sets the name of this contact person.
     *
     * @param name the new name
     * @throws NullPointerException if name is null
     */
    public void setName(PersonName name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        updateLastModifiedAt();
    }

    /**
     * Updates the last modified timestamp of this component.
     */
    protected void updateLastModifiedAt() {
        this.lastModifiedAt = Instant.now();
    }

    /**
     * Returns the email address of this contact person.
     *
     * @return the email address
     */
    public EmailAddress getEmail() {
        return email;
    }

    /**
     * Sets the email address of this contact person.
     *
     * @param email the new email address
     * @throws NullPointerException if email is null
     */
    public void setEmail(EmailAddress email) {
        this.email = Objects.requireNonNull(email, "email must not be null");
        updateLastModifiedAt();
    }

    /**
     * Returns the phone number of this contact person.
     *
     * @return the phone number, may be null if not set
     */
    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of this contact person.
     *
     * @param phoneNumber the new phone number, can be null to remove the phone number
     */
    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateLastModifiedAt();
    }

    /**
     * Returns whether this contact person is the primary contact for the customer.
     *
     * @return true if this is the primary contact, false otherwise
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Sets whether this contact person is the primary contact for the customer.
     *
     * @param primary true if this is the primary contact, false otherwise
     */
    public void setPrimary(boolean primary) {
        this.isPrimary = primary;
        updateLastModifiedAt();
    }

    /**
     * Gets the last modified timestamp of this component.
     *
     * @return the last modified timestamp
     */
    public Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    /**
     * Builder for creating CustomerComponent instances.
     * This class follows the Builder pattern to simplify the creation of complex CustomerComponent objects.
     */
    public static class Builder {
        private CustomerId customerId;
        private String role;
        private PersonName name;
        private EmailAddress email;
        private PhoneNumber phoneNumber;
        private boolean isPrimary;

        /**
         * Creates a new Builder instance.
         */
        private Builder() {
            // Default constructor
        }

        /**
         * Sets the customer ID.
         *
         * @param customerId the customer ID
         * @return this builder for method chaining
         */
        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        /**
         * Sets the role of this contact person.
         *
         * @param role the role
         * @return this builder for method chaining
         */
        public Builder role(String role) {
            this.role = role;
            return this;
        }

        /**
         * Sets the name of this contact person.
         *
         * @param name the name
         * @return this builder for method chaining
         */
        public Builder name(PersonName name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the email address of this contact person.
         *
         * @param email the email address
         * @return this builder for method chaining
         */
        public Builder email(EmailAddress email) {
            this.email = email;
            return this;
        }

        /**
         * Sets the phone number of this contact person.
         *
         * @param phoneNumber the phone number
         * @return this builder for method chaining
         */
        public Builder phoneNumber(PhoneNumber phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        /**
         * Sets whether this contact person is the primary contact.
         *
         * @param isPrimary true if this is the primary contact, false otherwise
         * @return this builder for method chaining
         */
        public Builder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        /**
         * Builds a new CustomerComponent instance with the properties set in this builder.
         *
         * @return a new CustomerComponent instance
         * @throws NullPointerException if any required property is null
         */
        public CustomerComponent build() {
            return new CustomerComponent(this);
        }
    }
}
