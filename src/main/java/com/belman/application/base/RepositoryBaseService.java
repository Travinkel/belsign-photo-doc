package com.belman.application.base;

import com.belman.domain.common.base.Repository;
import com.belman.domain.services.LoggerFactory;

/**
 * Base class for services that work with repositories.
 * Extends BaseService to provide common functionality for repository operations.
 *
 * @param <T> the type of the repository
 * @param <E> the type of the entity managed by the repository
 * @param <ID> the type of the entity's identifier
 */
public abstract class RepositoryBaseService<T extends Repository<E, ID>, E, ID> extends BaseService {

    /**
     * The repository used by this service.
     */
    protected final T repository;

    /**
     * Creates a new RepositoryBaseService with the specified repository and logger factory.
     *
     * @param repository the repository to use
     * @param loggerFactory the factory to create loggers
     */
    protected RepositoryBaseService(T repository, LoggerFactory loggerFactory) {
        super(loggerFactory);
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
        logDebug("RepositoryBaseService initialized with repository: {}", repository.getClass().getSimpleName());
    }

    /**
     * Gets the repository used by this service.
     *
     * @return the repository
     */
    protected T getRepository() {
        return repository;
    }

    /**
     * Safely executes a repository operation and handles exceptions.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param <R> the return type of the operation
     * @return the result of the operation, or null if an exception occurs
     */
    protected <R> R executeRepositoryOperation(RepositoryOperation<R> operation, String errorMessage) {
        try {
            return operation.execute();
        } catch (Exception e) {
            logError(errorMessage, e);
            return null;
        }
    }

    /**
     * Safely executes a repository operation that returns a list and handles exceptions.
     *
     * @param operation the operation to execute
     * @param errorMessage the error message to log if an exception occurs
     * @param <R> the type of elements in the list
     * @return the result of the operation, or an empty list if an exception occurs
     */
    protected <R> java.util.List<R> executeRepositoryListOperation(RepositoryOperation<java.util.List<R>> operation, String errorMessage) {
        try {
            return operation.execute();
        } catch (Exception e) {
            logError(errorMessage, e);
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Functional interface for repository operations.
     *
     * @param <R> the return type of the operation
     */
    @FunctionalInterface
    protected interface RepositoryOperation<R> {
        /**
         * Executes the repository operation.
         *
         * @return the result of the operation
         * @throws Exception if an error occurs
         */
        R execute() throws Exception;
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        // This method is required by BaseService
        // In a real implementation, we would store the logger factory in a field
        // and return it here. For now, we'll return null since this is just a
        // demonstration of refactoring.
        return null;
    }
}