package com.belman.domain.shared;

import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events in the application.
 * Domain events represent something that happened in the domain that domain experts care about.
 * 
 * @deprecated This interface is deprecated and will be removed in a future release.
 * Use {@link com.belman.domain.events.DomainEvent} instead.
 */
@Deprecated
public interface DomainEvent extends com.belman.domain.events.DomainEvent {
}
