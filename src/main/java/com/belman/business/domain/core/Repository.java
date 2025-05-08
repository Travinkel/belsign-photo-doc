package com.belman.business.domain.core;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for domain aggregates.
 * <p>
 * This interface defines the standard operations to be executed on a repository.
 * Repositories mediate between the domain and data mapping layers using a collection-like
 * interface for accessing domain objects.
 *
 * @param <T>  the type of the aggregate root entity this repository manages
 * @param <ID> the type of the identifier of the aggregate root
 */
public interface Repository<T extends AggregateRoot<ID>, ID> {

    /**
     * Finds an aggregate by its unique identifier.
     *
     * @param id the identifier of the aggregate to find
     * @return an Optional containing the found aggregate, or empty if not found
     */
    Optional<T> findById(ID id);

    /**
     * Saves an aggregate to the repository. If the aggregate already exists,
     * it will be updated; otherwise, it will be created.
     * <p>
     * Any domain events registered with the aggregate will be published upon
     * successful save.
     *
     * @param aggregate the aggregate to save
     * @return the saved aggregate (may be updated with generated values)
     */
    T save(T aggregate);

    /**
     * Deletes an aggregate from the repository.
     *
     * @param aggregate the aggregate to delete
     */
    void delete(T aggregate);

    /**
     * Deletes an aggregate from the repository by its identifier.
     *
     * @param id the identifier of the aggregate to delete
     * @return true if the aggregate was deleted, false if not found
     */
    boolean deleteById(ID id);

    /**
     * Finds all aggregates managed by this repository.
     * <p>
     * Note: Use with caution as this might return a large number of objects.
     *
     * @return a list of all aggregates
     */
    List<T> findAll();

    /**
     * Checks if an aggregate with the given identifier exists.
     *
     * @param id the identifier to check
     * @return true if an aggregate with the given identifier exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Counts the number of aggregates managed by this repository.
     *
     * @return the number of aggregates
     */
    long count();
}