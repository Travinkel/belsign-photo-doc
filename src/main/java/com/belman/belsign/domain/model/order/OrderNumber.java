package com.belman.belsign.domain.model.order;

import java.util.Objects;

public class OrderNumber {
    private final String value;

    public OrderNumber(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Order number cannot be null or empty");
        }
        this.value = value.trim();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderNumber)) return false;

        OrderNumber that = (OrderNumber) o;

        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
