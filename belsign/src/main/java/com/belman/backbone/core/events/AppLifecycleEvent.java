package com.belman.backbone.core.events;

import java.time.Instant;
import java.util.UUID;

public class AppLifecycleEvent extends AbstractDomainEvent {

    public enum Type {
        APP_STARTED,
        APP_STOPPED,
        APP_PAUSED,
        APP_RESUMED,
        APP_RESTARTED
    }

    private final Type type;

    public AppLifecycleEvent(Type type) {
        super();
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String getEventType() {
        return "AppLifecycleEvent:" + type.name();
    }

    @Override
    public String toString() {
        return String.format("AppLifecycleEvent[type=%s, timestamp=%s, id=%s]",
                type.name(), getTimestamp(), getEventId());
    }
}
