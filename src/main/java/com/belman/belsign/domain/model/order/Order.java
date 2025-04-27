package com.belman.belsign.domain.model.order;

import java.util.UUID;

public class Order {
    private UUID id;
    private OrderNumber orderNumber;

    public OrderNumber getOrderNumber() {
        return orderNumber;
    }
}
