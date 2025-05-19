package com.belman.dataaccess.repository.sql.util;

import com.belman.domain.services.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Utility class for executing SQL queries.
 * This class provides methods for executing SQL queries and handling results.
 */
public class SqlQueryExecutor {

    private final LoggerFactory loggerFactory;
    private final SqlConnectionManager connectionManager;

    /**
     * Creates a new SqlQueryExecutor with the specified connection manager.
     *
     * @param loggerFactory     the logger factory
     * @param connectionManager the connection manager
     */
    public SqlQueryExecutor(LoggerFactory loggerFactory, SqlConnectionManager connectionManager) {
        this.loggerFactory = loggerFactory;
        this.connectionManager = connectionManager;
    }

    /**
     * Executes a query and maps the results to a list of objects.
     *
     * @param <T>           the type of objects to return
     * @param sql           the SQL query
     * @param resultMapper  the function to map ResultSet rows to objects
     * @param params        the query parameters
     * @return a list of objects
     * @throws SQLException if a database access error occurs
     */
    public <T> List<T> executeQuery(String sql, Function<ResultSet, T> resultMapper, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<T> results = new ArrayList<>();

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(sql);
            setParameters(statement, params);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                results.add(resultMapper.apply(resultSet));
            }

            return results;
        } catch (SQLException e) {
            loggerFactory.getLogger(SqlQueryExecutor.class).error("Error executing query: " + sql, e);
            throw e;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }

    /**
     * Executes a query and maps the first result to an object.
     *
     * @param <T>           the type of object to return
     * @param sql           the SQL query
     * @param resultMapper  the function to map ResultSet rows to objects
     * @param params        the query parameters
     * @return an Optional containing the object if found, or empty if not found
     * @throws SQLException if a database access error occurs
     */
    public <T> Optional<T> executeQueryForObject(String sql, Function<ResultSet, T> resultMapper, Object... params) throws SQLException {
        List<T> results = executeQuery(sql, resultMapper, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Executes an update query (INSERT, UPDATE, DELETE).
     *
     * @param sql    the SQL query
     * @param params the query parameters
     * @return the number of rows affected
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(sql);
            setParameters(statement, params);
            return statement.executeUpdate();
        } catch (SQLException e) {
            loggerFactory.getLogger(SqlQueryExecutor.class).error("Error executing update: " + sql, e);
            throw e;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    /**
     * Executes an insert query and returns the generated key.
     *
     * @param <T>           the type of the generated key
     * @param sql           the SQL query
     * @param keyMapper     the function to map the generated key
     * @param params        the query parameters
     * @return an Optional containing the generated key if available, or empty if not available
     * @throws SQLException if a database access error occurs
     */
    public <T> Optional<T> executeInsert(String sql, Function<ResultSet, T> keyMapper, Object... params) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = connectionManager.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameters(statement, params);
            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();

            if (generatedKeys.next()) {
                return Optional.of(keyMapper.apply(generatedKeys));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            loggerFactory.getLogger(SqlQueryExecutor.class).error("Error executing insert: " + sql, e);
            throw e;
        } finally {
            closeResources(connection, statement, generatedKeys);
        }
    }

    /**
     * Executes a batch update.
     *
     * @param sql           the SQL query
     * @param batchParams   the list of batch parameters
     * @return an array of update counts
     * @throws SQLException if a database access error occurs
     */
    public int[] executeBatch(String sql, List<Object[]> batchParams) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = connectionManager.getConnection();
            connectionManager.beginTransaction(connection);
            statement = connection.prepareStatement(sql);

            for (Object[] params : batchParams) {
                setParameters(statement, params);
                statement.addBatch();
            }

            int[] results = statement.executeBatch();
            connectionManager.commitTransaction(connection);
            return results;
        } catch (SQLException e) {
            if (connection != null) {
                connectionManager.rollbackTransaction(connection);
            }
            loggerFactory.getLogger(SqlQueryExecutor.class).error("Error executing batch: " + sql, e);
            throw e;
        } finally {
            closeResources(connection, statement, null);
        }
    }

    /**
     * Sets parameters on a prepared statement.
     *
     * @param statement the prepared statement
     * @param params    the parameters
     * @throws SQLException if a database access error occurs
     */
    private void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    /**
     * Closes database resources.
     *
     * @param connection the connection
     * @param statement  the statement
     * @param resultSet  the result set
     */
    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                loggerFactory.getLogger(SqlQueryExecutor.class).error("Error closing result set", e);
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                loggerFactory.getLogger(SqlQueryExecutor.class).error("Error closing statement", e);
            }
        }

        if (connection != null) {
            connectionManager.closeConnection(connection);
        }
    }
}