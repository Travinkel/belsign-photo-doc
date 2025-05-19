package com.belman.dataaccess.mapper;

import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.specification.Specification;

import java.util.List;

/**
 * Interface for mapping between OrderBusiness entities and database records.
 *
 * @param <D> the type of the database record
 */
public interface OrderMapper<D> extends EntityMapper<OrderBusiness, D> {

    /**
     * Maps a database record to an OrderId.
     *
     * @param record the database record
     * @return the OrderId
     */
    OrderId toOrderId(D record);

    /**
     * Maps a database record to an OrderNumber.
     *
     * @param record the database record
     * @return the OrderNumber
     */
    OrderNumber toOrderNumber(D record);

    /**
     * Finds database records that satisfy the given specification.
     *
     * @param spec the specification to filter orders
     * @return a list of database records that satisfy the specification
     */
    List<D> findBySpecification(Specification<OrderBusiness> spec);

    /**
     * Finds a database record by its order number.
     *
     * @param orderNumber the order number
     * @return the database record, or null if not found
     */
    D findByOrderNumber(OrderNumber orderNumber);
}