package com.belman.belsign.framework.orm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Query<T extends BaseEntity> {
    private final List<T> data;

    public Query(List<T> data) {
        this.data = data;
    }

    public Query<T> where(Predicate<T> predicate) {
        return new Query<>(data.stream().filter(predicate).collect(Collectors.toList()));
    }

    public <R> List<R> select(java.util.function.Function<T, R> mapper) {
        return data.stream().map(mapper).collect(Collectors.toList());
    }

    public ObservableList<T> toObservableList() {
        return FXCollections.observableArrayList(data);
    }
}
