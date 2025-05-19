package com.belman.dataaccess.mapper;

import com.belman.domain.services.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of the EntityMapper interface.
 * Provides common functionality for all mappers.
 *
 * @param <T> the type of the domain entity
 * @param <D> the type of the database record
 */
public abstract class BaseEntityMapper<T, D> implements EntityMapper<T, D> {

    protected final LoggerFactory loggerFactory;

    /**
     * Creates a new BaseEntityMapper with the specified logger factory.
     *
     * @param loggerFactory the logger factory
     */
    protected BaseEntityMapper(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    /**
     * Maps a ResultSet to a list of domain entities.
     *
     * @param resultSet the ResultSet
     * @return a list of domain entities
     * @throws SQLException if a database access error occurs
     */
    public List<T> fromResultSetToList(ResultSet resultSet) throws SQLException {
        List<T> entities = new ArrayList<>();
        while (resultSet.next()) {
            entities.add(fromResultSet(resultSet));
        }
        return entities;
    }

    /**
     * Gets a string value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the string value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected String getStringOrNull(ResultSet resultSet, String columnName) throws SQLException {
        String value = resultSet.getString(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets an integer value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the integer value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected Integer getIntegerOrNull(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a long value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the long value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected Long getLongOrNull(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a double value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the double value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected Double getDoubleOrNull(ResultSet resultSet, String columnName) throws SQLException {
        double value = resultSet.getDouble(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a boolean value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the boolean value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected Boolean getBooleanOrNull(ResultSet resultSet, String columnName) throws SQLException {
        boolean value = resultSet.getBoolean(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a timestamp value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the timestamp value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected java.sql.Timestamp getTimestampOrNull(ResultSet resultSet, String columnName) throws SQLException {
        java.sql.Timestamp value = resultSet.getTimestamp(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a date value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the date value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected java.sql.Date getDateOrNull(ResultSet resultSet, String columnName) throws SQLException {
        java.sql.Date value = resultSet.getDate(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a time value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the time value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected java.sql.Time getTimeOrNull(ResultSet resultSet, String columnName) throws SQLException {
        java.sql.Time value = resultSet.getTime(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Gets a byte array value from a ResultSet, returning null if the value is null.
     *
     * @param resultSet the ResultSet
     * @param columnName the column name
     * @return the byte array value, or null if the value is null
     * @throws SQLException if a database access error occurs
     */
    protected byte[] getBytesOrNull(ResultSet resultSet, String columnName) throws SQLException {
        byte[] value = resultSet.getBytes(columnName);
        return resultSet.wasNull() ? null : value;
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param e the exception
     */
    protected void logError(String message, Exception e) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).error(message, e);
        } else {
            System.err.println(message + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
