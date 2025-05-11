package com.belman.domain.core;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface for business components.
 * <p>
 * This interface defines the standard operations to be executed on a repository.
 * Repositories mediate between the business and data mapping layers using a collection-like
 * interface for accessing business components.
 *
 * @param <T>  the type of the business component this repository manages
 * @param <ID> the type of the identifier of the business component
 */
public interface ComponentRepository<T extends BusinessComponent<ID>, ID> {

    /**
     * Finds a business component by its unique identifier.
     *
     * @param id the identifier of the business component to find
     * @return an Optional containing the found business component, or empty if not found
     */
    Optional<T> findById(ID id);

    /**
     * Saves a business component to the repository. If the business component already exists,
     * it will be updated; otherwise, it will be created.
     *
     * @param component the business component to save
     * @return the saved business component (may be updated with generated values)
     */
    T save(T component);

    /**
     * Deletes a business component from the repository.
     *
     * @param component the business component to delete
     */
    void delete(T component);

    /**
     * Deletes a business component from the repository by its identifier.
     *
     * @param id the identifier of the business component to delete
     * @return true if the business component was deleted, false if not found
     */
    boolean deleteById(ID id);

    /**
     * Finds all business components managed by this repository.
     * <p>
     * Note: Use with caution as this might return a large number of objects.
     *
     * @return a list of all business components
     */
    List<T> findAll();

    /**
     * Checks if a business component with the given identifier exists.
     *
     * @param id the identifier to check
     * @return true if a business component with the given identifier exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Counts the number of business components managed by this repository.
     *
     * @return the number of business components
     */
    long count();
}