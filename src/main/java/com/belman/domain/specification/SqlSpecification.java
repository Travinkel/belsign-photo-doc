package com.belman.domain.specification;

import java.util.List;

/**
 * Interface for specifications that can be translated to SQL WHERE clauses.
 * This allows for more efficient database queries by pushing filtering to the database level.
 *
 * @param <T> the type of entity this specification applies to
 */
public interface SqlSpecification<T> extends Specification<T> {
    
    /**
     * Converts this specification to a SQL WHERE clause.
     * 
     * @return a SQL WHERE clause (without the "WHERE" keyword)
     */
    String toSqlClause();
    
    /**
     * Gets the parameters for the SQL WHERE clause.
     * 
     * @return a list of parameter values in the order they appear in the SQL clause
     */
    List<Object> getParameters();
}