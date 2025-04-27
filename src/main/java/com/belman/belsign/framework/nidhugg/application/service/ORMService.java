package com.belman.belsign.framework.nidhugg.application.service;



import com.belman.belsign.framework.nidhugg.domain.BaseEntity;
import com.belman.belsign.framework.nidhugg.domain.Query;

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
