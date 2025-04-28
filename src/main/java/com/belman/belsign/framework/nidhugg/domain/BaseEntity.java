package com.belman.belsign.framework.nidhugg.domain;

import java.util.UUID;

public abstract class BaseEntity {

    private UUID id;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
    }

    protected BaseEntity(UUID id) {
        this.id = id == null ? UUID.randomUUID() : id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
