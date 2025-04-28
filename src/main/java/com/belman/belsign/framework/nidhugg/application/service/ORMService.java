package com.belman.belsign.framework.nidhugg.application.service;

import com.belman.belsign.framework.nidhugg.domain.BaseEntity;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ORMService<T extends BaseEntity> {

    private final Map<UUID, T> database = new HashMap<>();

    public void save(T entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        database.put(entity.getId(), entity);
    }

    public Optional<T> findById(UUID id) {
        return Optional.ofNullable(database.get(id));
    }

    public List<T> findAll() {
        return new ArrayList<>(database.values());
    }

    public List<T> findAll(Predicate<T> filter) {
        return database.values()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    public void update(T entity) {
        if (entity.getId() == null || !database.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity must exist to be updated.");
        }
        database.put(entity.getId(), entity);
    }

    public void delete(T entity) {
        if (entity.getId() == null || !database.containsKey(entity.getId())) {
            throw new IllegalArgumentException("Entity must exist to be deleted.");
        }
        database.remove(entity.getId());
    }
}
