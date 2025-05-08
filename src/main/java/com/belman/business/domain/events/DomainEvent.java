package com.belman.business.domain.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    Instant getOccurredOn();

    String getEventType();

}