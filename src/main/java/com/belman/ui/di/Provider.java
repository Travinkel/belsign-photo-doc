package com.belman.ui.di;

/**
 * Interface for a dependency provider.
 * This is part of the Dependency Injection pattern.
 *
 * @param <T> the type of the dependency
 */
@FunctionalInterface
public interface Provider<T> {
    /**
     * Gets the dependency.
     *
     * @return the dependency
     */
    T get();
}