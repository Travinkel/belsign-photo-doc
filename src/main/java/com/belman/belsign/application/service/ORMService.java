package com.belman.belsign.application.service;



import com.belman.belsign.framework.orm.BaseEntity;
import com.belman.belsign.framework.orm.Query;

import java.util.ArrayList;
import java.util.List;

public class ORMService<T extends BaseEntity> {
    private final List<T> database = new ArrayList<>();

    public void save(T entity) {
        database.add(entity);
    }

    public Query<T> query() {
        return new Query<>(database);
    }
}
