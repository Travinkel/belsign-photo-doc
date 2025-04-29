package com.belman.belsign.domain.model.customer;

import java.util.Objects;

/**
 * Value object representing a company.
 */
public record Company(String name, String registrationNumber, String address) {
    /**
     * Creates a Company with the specified name, registration number, and address.
     * 
     * @param name the company name
     * @param registrationNumber the company registration number (can be null)
     * @param address the company address (can be null)
     * @throws IllegalArgumentException if name is blank
     */
    public Company {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Company name cannot be empty");
        }
    }
    
    /**
     * Creates a Company with just a name.
     * 
     * @param name the company name
     * @return a new Company with the specified name
     */
    public static Company withName(String name) {
        return new Company(name, null, null);
    }
    
    /**
     * @return a string representation of the company
     */
    @Override
    public String toString() {
        return name;
    }
}