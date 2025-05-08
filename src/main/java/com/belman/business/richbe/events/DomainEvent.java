package com.belman.business.richbe.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    Instant getOccurredOn();

    String getEventType();

}