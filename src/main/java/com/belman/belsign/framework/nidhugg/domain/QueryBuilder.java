package com.belman.belsign.framework.nidhugg.domain;

import com.belman.belsign.framework.nidhugg.application.service.ORMService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class QueryBuilder<T extends BaseEntity> {

    private final ORMService<T> ormService;
    private Predicate<T> predicate;

    public QueryBuilder(ORMService ormService) {
        this.ormService = ormService;
        this.predicate = entity -> true; // Default predicate that matches all entities
    }

    public QueryBuilder where(Predicate<T> condition) {
        if (predicate != null) {
            predicate = condition;
        } else {
            predicate = predicate.and(condition);
        }
        return this;
    }

    public QueryBuilder orWhere(Predicate<T> condition) {
        if (predicate != null) {
            predicate = condition;
        } else {
            predicate = predicate.or(condition);
        }
        return this;
    }

    public List<T> find() {
        if (predicate == null) {
            return new ArrayList<>(ormService.findAll());
        }
        return ormService.findAll(predicate);
    }
}
