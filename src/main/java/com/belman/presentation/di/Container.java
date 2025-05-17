package com.belman.presentation.di;

/**
 * Interface for a dependency injection container.
 * This is part of the Dependency Injection pattern.
 */
public interface Container {
    /**
     * Registers a dependency with the container.
     *
     * @param <T>            the type of the dependency
     * @param type           the class of the dependency
     * @param implementation the implementation of the dependency
     */
    <T> void register(Class<T> type, T implementation);

    /**
     * Registers a dependency with the container using a provider.
     *
     * @param <T>      the type of the dependency
     * @param type     the class of the dependency
     * @param provider the provider for the dependency
     */
    <T> void register(Class<T> type, Provider<T> provider);

    /**
     * Gets a dependency from the container.
     *
     * @param <T>  the type of the dependency
     * @param type the class of the dependency
     * @return the dependency
     * @throws IllegalArgumentException if the dependency is not registered
     */
    <T> T get(Class<T> type);

    /**
     * Checks if a dependency is registered with the container.
     *
     * @param <T>  the type of the dependency
     * @param type the class of the dependency
     * @return true if the dependency is registered, false otherwise
     */
    <T> boolean contains(Class<T> type);
}