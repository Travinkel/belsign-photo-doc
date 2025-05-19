package com.belman.dataaccess.repository;

import com.belman.domain.common.base.Repository;
import com.belman.domain.services.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Abstract base implementation of the Repository interface.
 * Provides common functionality for all repository implementations.
 *
 * @param <T>  the type of the aggregate root entity this repository manages
 * @param <ID> the type of the identifier of the aggregate root
 */
public abstract class BaseRepository<T, ID> implements Repository<T, ID> {

    protected final LoggerFactory loggerFactory;

    /**
     * Constructor with logger factory.
     * 
     * @param loggerFactory the logger factory to use
     */
    protected BaseRepository(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            logDebug("Finding aggregate by ID: " + id);
            return doFindById(id).map(this::createCopy);
        } catch (Exception e) {
            logError("Error finding aggregate by ID: " + id, e);
            throw new RuntimeException("Error finding aggregate by ID: " + id, e);
        }
    }

    @Override
    public T save(T aggregate) {
        try {
            validateAggregate(aggregate);
            T processedAggregate = beforeSave(aggregate);
            ID id = getId(processedAggregate);
            logDebug("Saving aggregate with ID: " + id);
            T savedAggregate = doSave(processedAggregate);
            return afterSave(createCopy(savedAggregate));
        } catch (Exception e) {
            logError("Error saving aggregate", e);
            throw new RuntimeException("Error saving aggregate", e);
        }
    }

    @Override
    public void delete(T aggregate) {
        try {
            validateAggregate(aggregate);
            beforeDelete(aggregate);
            ID id = getId(aggregate);
            logDebug("Deleting aggregate with ID: " + id);
            doDelete(aggregate);
            afterDelete(aggregate);
        } catch (Exception e) {
            logError("Error deleting aggregate", e);
            throw new RuntimeException("Error deleting aggregate", e);
        }
    }

    @Override
    public boolean deleteById(ID id) {
        try {
            logDebug("Deleting aggregate by ID: " + id);
            Optional<T> aggregate = findById(id);
            if (aggregate.isPresent()) {
                delete(aggregate.get());
                return true;
            }
            return false;
        } catch (Exception e) {
            logError("Error deleting aggregate by ID: " + id, e);
            throw new RuntimeException("Error deleting aggregate by ID: " + id, e);
        }
    }

    @Override
    public List<T> findAll() {
        try {
            logDebug("Finding all aggregates");
            return doFindAll().stream()
                    .map(this::createCopy)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logError("Error finding all aggregates", e);
            throw new RuntimeException("Error finding all aggregates", e);
        }
    }

    @Override
    public boolean existsById(ID id) {
        try {
            logDebug("Checking if aggregate exists by ID: " + id);
            return doExistsById(id);
        } catch (Exception e) {
            logError("Error checking if aggregate exists by ID: " + id, e);
            throw new RuntimeException("Error checking if aggregate exists by ID: " + id, e);
        }
    }

    @Override
    public long count() {
        try {
            logDebug("Counting aggregates");
            return doCount();
        } catch (Exception e) {
            logError("Error counting aggregates", e);
            throw new RuntimeException("Error counting aggregates", e);
        }
    }

    /**
     * Performs the actual database operation to find an aggregate by ID.
     * 
     * @param id the ID of the aggregate to find
     * @return an Optional containing the found aggregate, or empty if not found
     */
    protected abstract Optional<T> doFindById(ID id);

    /**
     * Performs the actual database operation to save an aggregate.
     * 
     * @param aggregate the aggregate to save
     * @return the saved aggregate
     */
    protected abstract T doSave(T aggregate);

    /**
     * Performs the actual database operation to delete an aggregate.
     * 
     * @param aggregate the aggregate to delete
     */
    protected abstract void doDelete(T aggregate);

    /**
     * Performs the actual database operation to find all aggregates.
     * 
     * @return a list of all aggregates
     */
    protected abstract List<T> doFindAll();

    /**
     * Performs the actual database operation to check if an aggregate exists by ID.
     * 
     * @param id the ID to check
     * @return true if an aggregate with the given ID exists, false otherwise
     */
    protected abstract boolean doExistsById(ID id);

    /**
     * Performs the actual database operation to count aggregates.
     * 
     * @return the number of aggregates
     */
    protected abstract long doCount();

    /**
     * Extracts the ID from an aggregate.
     *
     * @param aggregate the aggregate
     * @return the ID of the aggregate
     */
    protected abstract ID getId(T aggregate);

    /**
     * Creates a deep copy of an aggregate to prevent external modification.
     * This is important for maintaining the integrity of the aggregate root pattern.
     *
     * @param aggregate the aggregate to copy
     * @return a deep copy of the aggregate
     */
    protected abstract T createCopy(T aggregate);

    /**
     * Validates an aggregate before saving.
     * Implementations can override this method to add validation logic.
     *
     * @param aggregate the aggregate to validate
     * @throws IllegalArgumentException if the aggregate is invalid
     */
    protected void validateAggregate(T aggregate) {
        if (aggregate == null) {
            throw new IllegalArgumentException("Aggregate cannot be null");
        }
    }

    /**
     * Handles any pre-save operations for an aggregate.
     * Implementations can override this method to add custom logic.
     *
     * @param aggregate the aggregate to process
     * @return the processed aggregate
     */
    protected T beforeSave(T aggregate) {
        return aggregate;
    }

    /**
     * Handles any post-save operations for an aggregate.
     * Implementations can override this method to add custom logic.
     *
     * @param aggregate the saved aggregate
     * @return the processed aggregate
     */
    protected T afterSave(T aggregate) {
        return aggregate;
    }

    /**
     * Handles any pre-delete operations for an aggregate.
     * Implementations can override this method to add custom logic.
     *
     * @param aggregate the aggregate to process
     */
    protected void beforeDelete(T aggregate) {
        // Default implementation does nothing
    }

    /**
     * Handles any post-delete operations for an aggregate.
     * Implementations can override this method to add custom logic.
     *
     * @param aggregate the deleted aggregate
     */
    protected void afterDelete(T aggregate) {
        // Default implementation does nothing
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param e       the exception
     */
    protected void logError(String message, Exception e) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).error(message, e);
        } else {
            System.err.println(message + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Logs an info message.
     *
     * @param message the info message
     */
    protected void logInfo(String message) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).info(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Logs a debug message.
     *
     * @param message the debug message
     */
    protected void logDebug(String message) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).debug(message);
        } else {
            System.out.println("[DEBUG] " + message);
        }
    }
}
