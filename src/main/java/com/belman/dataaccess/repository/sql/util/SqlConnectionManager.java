package com.belman.dataaccess.repository.sql.util;

import com.belman.domain.services.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Manages SQL database connections.
 * This class provides methods for creating and managing database connections.
 */
public class SqlConnectionManager {

    private final LoggerFactory loggerFactory;
    private final String jdbcUrl;
    private final Properties connectionProperties;

    /**
     * Creates a new SqlConnectionManager with the specified JDBC URL and connection properties.
     *
     * @param loggerFactory       the logger factory
     * @param jdbcUrl             the JDBC URL
     * @param connectionProperties the connection properties
     */
    public SqlConnectionManager(LoggerFactory loggerFactory, String jdbcUrl, Properties connectionProperties) {
        this.loggerFactory = loggerFactory;
        this.jdbcUrl = jdbcUrl;
        this.connectionProperties = connectionProperties;
    }

    /**
     * Creates a new SqlConnectionManager with the specified JDBC URL, username, and password.
     *
     * @param loggerFactory the logger factory
     * @param jdbcUrl       the JDBC URL
     * @param username      the database username
     * @param password      the database password
     */
    public SqlConnectionManager(LoggerFactory loggerFactory, String jdbcUrl, String username, String password) {
        this.loggerFactory = loggerFactory;
        this.jdbcUrl = jdbcUrl;
        this.connectionProperties = new Properties();
        this.connectionProperties.setProperty("user", username);
        this.connectionProperties.setProperty("password", password);
    }

    /**
     * Gets a new database connection.
     *
     * @return a new database connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(jdbcUrl, connectionProperties);
        } catch (SQLException e) {
            loggerFactory.getLogger(SqlConnectionManager.class).error("Error getting database connection", e);
            throw e;
        }
    }

    /**
     * Closes a database connection.
     *
     * @param connection the connection to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                loggerFactory.getLogger(SqlConnectionManager.class).error("Error closing database connection", e);
            }
        }
    }

    /**
     * Begins a transaction on the specified connection.
     *
     * @param connection the connection
     * @throws SQLException if a database access error occurs
     */
    public void beginTransaction(Connection connection) throws SQLException {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            loggerFactory.getLogger(SqlConnectionManager.class).error("Error beginning transaction", e);
            throw e;
        }
    }

    /**
     * Commits a transaction on the specified connection.
     *
     * @param connection the connection
     * @throws SQLException if a database access error occurs
     */
    public void commitTransaction(Connection connection) throws SQLException {
        try {
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            loggerFactory.getLogger(SqlConnectionManager.class).error("Error committing transaction", e);
            throw e;
        }
    }

    /**
     * Rolls back a transaction on the specified connection.
     *
     * @param connection the connection
     */
    public void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                loggerFactory.getLogger(SqlConnectionManager.class).error("Error rolling back transaction", e);
            }
        }
    }
}