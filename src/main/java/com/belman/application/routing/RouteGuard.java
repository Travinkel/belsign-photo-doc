package com.belman.application.routing;

import java.util.function.Supplier;

/**
 * Interface for registering route guards.
 * This interface allows the infrastructure layer to register guards for routes
 * without directly depending on the presentation layer.
 */
public interface RouteGuard {
    
    /**
     * Registers a guard for a specific route.
     * 
     * @param routeName the name of the route to guard
     * @param guardCondition a supplier that returns true if access is allowed, false otherwise
     */
    void registerGuard(String routeName, Supplier<Boolean> guardCondition);
}