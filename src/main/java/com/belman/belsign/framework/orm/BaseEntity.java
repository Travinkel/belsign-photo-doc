package com.belman.belsign.framework.orm;

import java.lang.reflect.Field;

public abstract class BaseEntity {
    public String getTableName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public String[] getColumnNames() {
        Field[] fields = this.getClass().getDeclaredFields();
        return java.util.Arrays.stream(fields)
                .map(Field::getName)
                .toArray(String[]::new);
    }
}
