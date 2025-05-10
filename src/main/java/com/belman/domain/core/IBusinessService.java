package com.belman.domain.core;

/**
 * Interface marker for business services.
 * <p>
 * Business services represent operations that conceptually do not belong to any specific business component or data object.
 * They express significant business processes or transformations in the business that operate on multiple
 * business objects or work with complex business rules.
 * <p>
 * A business service:
 * - Is stateless
 * - Has business significance and forms part of the common business language
 * - Operates on business components and data objects
 * - Does not have persistence responsibilities
 * - Works exclusively with business concepts
 */
public interface IBusinessService {
}