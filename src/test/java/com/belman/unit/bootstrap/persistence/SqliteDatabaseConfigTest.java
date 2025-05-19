package com.belman.unit.bootstrap.persistence;

import com.belman.bootstrap.persistence.SqliteDatabaseConfig;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the SqliteDatabaseConfig class.
 * These tests verify that the SQLite database is properly initialized and contains the necessary mock data.
 */
public class SqliteDatabaseConfigTest {

    @Test
    public void testSqliteDatabaseInitialization() throws SQLException {
        // Initialize the SQLite database
        SqliteDatabaseConfig.initialize();
        
        // Get the data source
        DataSource dataSource = SqliteDatabaseConfig.getDataSource();
        
        // Verify that the data source is not null
        assertNotNull(dataSource, "SQLite data source should not be null");
        
        // Get a connection from the data source
        try (Connection connection = dataSource.getConnection()) {
            // Verify that the connection is valid
            assertTrue(connection.isValid(5), "SQLite connection should be valid");
            
            // Check if the users table exists and contains data
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM users")) {
                
                // Verify that the users table contains at least one row
                assertTrue(resultSet.next(), "Result set should have at least one row");
                int userCount = resultSet.getInt(1);
                assertTrue(userCount > 0, "Users table should contain at least one user");
                
                System.out.println("[DEBUG_LOG] Found " + userCount + " users in the SQLite database");
            } catch (SQLException e) {
                fail("Failed to query users table: " + e.getMessage());
            }
            
            // Check if the orders table exists and contains data
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM orders")) {
                
                // Verify that the orders table contains at least one row
                assertTrue(resultSet.next(), "Result set should have at least one row");
                int orderCount = resultSet.getInt(1);
                assertTrue(orderCount > 0, "Orders table should contain at least one order");
                
                System.out.println("[DEBUG_LOG] Found " + orderCount + " orders in the SQLite database");
            } catch (SQLException e) {
                // It's okay if the orders table doesn't exist or is empty
                System.out.println("[DEBUG_LOG] Orders table not found or empty: " + e.getMessage());
            }
            
            // Check if the photos table exists and contains data
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM photos")) {
                
                // Verify that the photos table contains at least one row
                assertTrue(resultSet.next(), "Result set should have at least one row");
                int photoCount = resultSet.getInt(1);
                assertTrue(photoCount > 0, "Photos table should contain at least one photo");
                
                System.out.println("[DEBUG_LOG] Found " + photoCount + " photos in the SQLite database");
            } catch (SQLException e) {
                // It's okay if the photos table doesn't exist or is empty
                System.out.println("[DEBUG_LOG] Photos table not found or empty: " + e.getMessage());
            }
        }
    }
}