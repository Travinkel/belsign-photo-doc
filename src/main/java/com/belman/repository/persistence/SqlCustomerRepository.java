package com.belman.repository.persistence;

import com.belman.domain.customer.CustomerAggregate;
import com.belman.domain.customer.CustomerType;
import com.belman.domain.customer.CustomerRepository;
import com.belman.domain.specification.Specification;
import com.belman.domain.customer.Company;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SQL-based implementation of the CustomerRepository interface.
 * This implementation stores customers in a SQL database.
 */
public class SqlCustomerRepository implements CustomerRepository {
    private static final Logger LOGGER = Logger.getLogger(SqlCustomerRepository.class.getName());

    private final DataSource dataSource;

    /**
     * Creates a new SqlCustomerRepository with the specified DataSource.
     * 
     * @param dataSource the DataSource to use for database connections
     */
    public SqlCustomerRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CustomerAggregate findById(CustomerId id) {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id.id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding customer by id: " + id.id(), e);
        }

        return null;
    }

    @Override
    public List<CustomerAggregate> findAll() {
        String sql = "SELECT * FROM customers";
        List<CustomerAggregate> customers = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all customers", e);
        }

        return customers;
    }

    @Override
    public List<CustomerAggregate> findBySpecification(Specification<CustomerAggregate> spec) {
        // For simplicity, we'll load all customers and filter in memory
        // In a real implementation, this would translate the specification to SQL
        return findAll().stream()
                .filter(spec::isSatisfiedBy)
                .toList();
    }

    @Override
    public void save(CustomerAggregate customer) {
        // Check if customer already exists
        String checkSql = "SELECT COUNT(*) FROM customers WHERE id = ?";
        boolean customerExists = false;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkSql)) {

            stmt.setString(1, customer.getId().id().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    customerExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if customer exists: " + customer.getId().id(), e);
            throw new RuntimeException("Error checking if customer exists", e);
        }

        if (customerExists) {
            updateCustomer(customer);
        } else {
            insertCustomer(customer);
        }
    }

    @Override
    public void delete(CustomerAggregate customer) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getId().id().toString());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Customer deleted successfully: " + customer.getId().id());
            } else {
                LOGGER.warning("No customer found to delete with id: " + customer.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting customer: " + customer.getId().id(), e);
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    private void insertCustomer(CustomerAggregate customer) {
        String sql = "INSERT INTO customers (id, type, person_first_name, person_last_name, company_name, " +
                     "email, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getId().id().toString());
            stmt.setString(2, customer.getType().name());

            // Set person name or company name based on customer type
            if (customer.isIndividual()) {
                PersonName personName = customer.getPersonName();
                stmt.setString(3, personName.firstName());
                stmt.setString(4, personName.lastName());
                stmt.setNull(5, java.sql.Types.VARCHAR); // No company name
            } else {
                stmt.setNull(3, java.sql.Types.VARCHAR); // No first name
                stmt.setNull(4, java.sql.Types.VARCHAR); // No last name
                stmt.setString(5, customer.getCompany().toString());
            }

            stmt.setString(6, customer.getEmail().value());

            // Set phone number if available
            if (customer.getPhoneNumber() != null) {
                stmt.setString(7, customer.getPhoneNumber().toString());
            } else {
                stmt.setNull(7, java.sql.Types.VARCHAR);
            }

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Customer inserted successfully: " + customer.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting customer: " + customer.getId().id(), e);
            throw new RuntimeException("Error inserting customer", e);
        }
    }

    private void updateCustomer(CustomerAggregate customer) {
        String sql = "UPDATE customers SET type = ?, person_first_name = ?, person_last_name = ?, " +
                     "company_name = ?, email = ?, phone_number = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getType().name());

            // Set person name or company name based on customer type
            if (customer.isIndividual()) {
                PersonName personName = customer.getPersonName();
                stmt.setString(2, personName.firstName());
                stmt.setString(3, personName.lastName());
                stmt.setNull(4, java.sql.Types.VARCHAR); // No company name
            } else {
                stmt.setNull(2, java.sql.Types.VARCHAR); // No first name
                stmt.setNull(3, java.sql.Types.VARCHAR); // No last name
                stmt.setString(4, customer.getCompany().toString());
            }

            stmt.setString(5, customer.getEmail().value());

            // Set phone number if available
            if (customer.getPhoneNumber() != null) {
                stmt.setString(6, customer.getPhoneNumber().toString());
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }

            stmt.setString(7, customer.getId().id().toString());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                LOGGER.info("Customer updated successfully: " + customer.getId().id());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating customer: " + customer.getId().id(), e);
            throw new RuntimeException("Error updating customer", e);
        }
    }

    private CustomerAggregate mapResultSetToCustomer(ResultSet rs) throws SQLException {
        CustomerId id = new CustomerId(rs.getString("id"));
        CustomerType type = CustomerType.valueOf(rs.getString("type"));
        EmailAddress email = new EmailAddress(rs.getString("email"));

        // Create phone number if available
        String phoneNumberStr = rs.getString("phone_number");
        PhoneNumber phoneNumber = phoneNumberStr != null ? new PhoneNumber(phoneNumberStr) : null;

        CustomerAggregate customer;

        if (type == CustomerType.INDIVIDUAL) {
            String firstName = rs.getString("person_first_name");
            String lastName = rs.getString("person_last_name");
            PersonName personName = new PersonName(firstName, lastName);

            if (phoneNumber != null) {
                customer = CustomerAggregate.individual(id, personName, email, phoneNumber);
            } else {
                customer = CustomerAggregate.individual(id, personName, email);
            }
        } else {
            String companyName = rs.getString("company_name");
            String registrationNumber = rs.getString("company_registration_number");
            String companyAddress = rs.getString("company_address");

            // Use empty strings for missing values
            registrationNumber = registrationNumber != null ? registrationNumber : "";
            companyAddress = companyAddress != null ? companyAddress : "";

            Company company = new Company(companyName, registrationNumber, companyAddress);

            if (phoneNumber != null) {
                customer = CustomerAggregate.company(id, company, email, phoneNumber);
            } else {
                customer = CustomerAggregate.company(id, company, email);
            }
        }

        return customer;
    }
}
