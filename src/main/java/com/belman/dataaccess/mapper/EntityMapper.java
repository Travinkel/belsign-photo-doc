package com.belman.dataaccess.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface for mapping between database records and domain entities.
 *
 * @param <T> the type of the domain entity
 * @param <D> the type of the database record
 */
public interface EntityMapper<T, D> {

    /**
     * Maps a domain entity to a database record.
     *
     * @param entity the domain entity
     * @return the database record
     */
    D toRecord(T entity);

    /**
     * Maps a database record to a domain entity.
     *
     * @param record the database record
     * @return the domain entity
     */
    T toEntity(D record);

    /**
     * Maps a ResultSet row to a domain entity.
     *
     * @param resultSet the ResultSet
     * @return the domain entity
     * @throws SQLException if a database access error occurs
     */
    T fromResultSet(ResultSet resultSet) throws SQLException;

    /**
     * Maps a list of database records to a list of domain entities.
     *
     * @param records the database records
     * @return the domain entities
     */
    default List<T> toEntities(List<D> records) {
        return records.stream()
                .map(this::toEntity)
                .toList();
    }

    /**
     * Maps a list of domain entities to a list of database records.
     *
     * @param entities the domain entities
     * @return the database records
     */
    default List<D> toRecords(List<T> entities) {
        return entities.stream()
                .map(this::toRecord)
                .toList();
    }
}