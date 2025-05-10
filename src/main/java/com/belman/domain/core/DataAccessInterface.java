package com.belman.domain.core;

import java.util.List;
import java.util.Optional;

/**
 * Generic data access interface for business objects.
 * <p>
 * This interface defines the standard operations to be executed on a data access component.
 * Data access interfaces mediate between the business and data mapping layers using a collection-like
 * interface for accessing business objects.
 *
 * @param <T>  the type of the business object this interface manages
 * @param <ID> the type of the identifier of the business object
 */
public interface DataAccessInterface<T extends BusinessObject<ID>, ID> {

    /**
     * Finds a business object by its unique identifier.
     *
     * @param id the identifier of the business object to find
     * @return an Optional containing the found business object, or empty if not found
     */
    Optional<T> findById(ID id);

    /**
     * Saves a business object to the data store. If the business object already exists,
     * it will be updated; otherwise, it will be created.
     * <p>
     * Any audit events registered with the business object will be published upon
     * successful save.
     *
     * @param businessObject the business object to save
     * @return the saved business object (may be updated with generated values)
     */
    T save(T businessObject);

    /**
     * Deletes a business object from the data store.
     *
     * @param businessObject the business object to delete
     */
    void delete(T businessObject);

    /**
     * Deletes a business object from the data store by its identifier.
     *
     * @param id the identifier of the business object to delete
     * @return true if the business object was deleted, false if not found
     */
    boolean deleteById(ID id);

    /**
     * Finds all business objects managed by this data access interface.
     * <p>
     * Note: Use with caution as this might return a large number of objects.
     *
     * @return a list of all business objects
     */
    List<T> findAll();

    /**
     * Checks if a business object with the given identifier exists.
     *
     * @param id the identifier to check
     * @return true if a business object with the given identifier exists, false otherwise
     */
    boolean existsById(ID id);

    /**
     * Counts the number of business objects managed by this data access interface.
     *
     * @return the number of business objects
     */
    long count();
}