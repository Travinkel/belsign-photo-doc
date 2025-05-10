package com.belman.domain.core;

/**
 * Interface marker for domain services.
 * <p>
 * Domain services represent operations that conceptually do not belong to any entity or value object.
 * They express significant business processes or transformations in the domain that operate on multiple
 * aggregates or work with complex business rules.
 * <p>
 * A domain service:
 * - Is stateless
 * - Has domain significance and forms part of the ubiquitous language
 * - Operates on domain entities and value objects
 * - Does not have persistence responsibilities
 * - Works exclusively with domain concepts
 */
public interface IDomainService {
}