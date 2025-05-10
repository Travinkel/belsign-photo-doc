package com.belman.domain.events;


public class AppLifecycleEvent extends AbstractDomainEvent {

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
                type.name(), getEventId());
    }

    public enum Type {
        APP_STARTED,
        APP_STOPPED,
        APP_PAUSED,
        APP_RESUMED,
        APP_RESTARTED
    }
}
