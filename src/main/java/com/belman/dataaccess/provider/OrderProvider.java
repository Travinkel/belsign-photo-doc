package com.belman.dataaccess.provider;

import com.belman.domain.order.OrderBusiness;

import java.util.List;

/**
 * Interface for providing orders from external sources.
 * Implementations of this interface can fetch orders from various sources,
 * such as files, databases, or APIs.
 */
public interface OrderProvider {

    /**
     * Fetches new orders from the external source.
     *
     * @return a list of new orders
     */
    List<OrderBusiness> fetchNewOrders();

    /**
     * Checks if there are new orders available.
     *
     * @return true if there are new orders available, false otherwise
     */
    boolean hasNewOrders();

    /**
     * Gets the name of this order provider.
     *
     * @return the name of this order provider
     */
    String getName();

    /**
     * Gets the description of this order provider.
     *
     * @return the description of this order provider
     */
    String getDescription();
}