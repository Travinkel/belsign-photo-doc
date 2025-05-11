package com.belman.service.usecase;


import com.belman.common.exception.ApplicationException;

/**
 * Base interface for all use cases in the application layer.
 * <p>
 * Use cases represent application-specific business rules and orchestrate the flow of data
 * to and from entities in the domain layer. They contain application-specific business rules
 * and define how the application interacts with the domain model.
 * <p>
 * Use cases are the primary building blocks of the application layer and represent
 * a single, well-defined operation that the application can perform on behalf of a user.
 *
 * @param <I> the type of input parameters for the use case
 * @param <O> the type of output produced by the use case
 */
public interface UseCase<I, O> {

    /**
     * Executes the use case with the given input.
     *
     * @param input the input parameters for the use case
     * @return the output produced by the use case
     * @throws ApplicationException if an error occurs during execution
     */
    O execute(I input) throws ApplicationException;
}