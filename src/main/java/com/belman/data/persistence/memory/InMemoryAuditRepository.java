package com.belman.data.persistence.memory;

import com.belman.domain.audit.AuditRepository;
import com.belman.domain.audit.event.AuditEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the AuditRepository interface.
 * <p>
 * This class provides a simple in-memory implementation for storing and retrieving
 * audit events. It's useful for testing and development purposes.
 * <p>
 * Note: This implementation is not suitable for production use as it doesn't
 * persist audit events across application restarts.
 */
public class InMemoryAuditRepository implements AuditRepository {

    // Map of event ID to event
    private final Map<UUID, AuditEvent> events = new ConcurrentHashMap<>();

    // Map of entity type and ID to list of event IDs
    private final Map<String, List<UUID>> entityEvents = new ConcurrentHashMap<>();

    // Map of event type to list of event IDs
    private final Map<String, List<UUID>> eventTypeEvents = new ConcurrentHashMap<>();

    // Map of user ID to list of event IDs
    private final Map<String, List<UUID>> userEvents = new ConcurrentHashMap<>();

    /**
     * Clears all stored events.
     * This method is primarily useful for testing.
     */
    public void clear() {
        events.clear();
        entityEvents.clear();
        eventTypeEvents.clear();
        userEvents.clear();
    }

    /**
     * Returns the total number of stored events.
     *
     * @return the number of events
     */
    public int getEventCount() {
        return events.size();
    }    @Override
    public void store(AuditEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        // Store the event
        events.put(event.getEventId(), event);

        // Extract entity information from the event if available
        extractAndStoreEntityInfo(event);

        // Store by event type
        eventTypeEvents.computeIfAbsent(event.getEventType(), k -> new ArrayList<>())
                .add(event.getEventId());

        // Extract and store user information if available
        extractAndStoreUserInfo(event);
    }



    @Override
    public void storeAll(List<AuditEvent> events) {
        Objects.requireNonNull(events, "events must not be null");
        events.forEach(this::store);
    }

    @Override
    public List<AuditEvent> getEventsByEntity(String entityType, String entityId) {
        Objects.requireNonNull(entityType, "entityType must not be null");
        Objects.requireNonNull(entityId, "entityId must not be null");

        String key = entityType + ":" + entityId;
        List<UUID> eventIds = entityEvents.getOrDefault(key, Collections.emptyList());
        return eventIds.stream()
                .map(events::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> getEventsByType(String eventType) {
        Objects.requireNonNull(eventType, "eventType must not be null");

        List<UUID> eventIds = eventTypeEvents.getOrDefault(eventType, Collections.emptyList());
        return eventIds.stream()
                .map(events::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuditEvent> getEventsByUser(String userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        List<UUID> eventIds = userEvents.getOrDefault(userId, Collections.emptyList());
        return eventIds.stream()
                .map(events::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * Extracts entity information from the event and stores it in the entityEvents map.
     * This method uses reflection to check if the event has getEntityType() and getEntityId() methods.
     *
     * @param event the event to extract entity information from
     */
    private void extractAndStoreEntityInfo(AuditEvent event) {
        try {
            // Try to get entity type and ID using reflection
            String entityType = getPropertyValue(event, "getEntityType");
            String entityId = getPropertyValue(event, "getEntityId");

            if (entityType != null && entityId != null) {
                String key = entityType + ":" + entityId;
                entityEvents.computeIfAbsent(key, k -> new ArrayList<>())
                        .add(event.getEventId());
            }
        } catch (Exception e) {
            // Ignore if the event doesn't have entity information
        }
    }

    /**
     * Extracts user information from the event and stores it in the userEvents map.
     * This method uses reflection to check if the event has getUserId() method.
     *
     * @param event the event to extract user information from
     */
    private void extractAndStoreUserInfo(AuditEvent event) {
        try {
            // Try to get user ID using reflection
            String userId = getPropertyValue(event, "getUserId");

            if (userId != null) {
                userEvents.computeIfAbsent(userId, k -> new ArrayList<>())
                        .add(event.getEventId());
            }
        } catch (Exception e) {
            // Ignore if the event doesn't have user information
        }
    }

    /**
     * Gets a property value from an object using reflection.
     *
     * @param obj        the object to get the property from
     * @param methodName the name of the method to call
     * @return the property value as a string, or null if the method doesn't exist or returns null
     */
    private String getPropertyValue(Object obj, String methodName) {
        try {
            java.lang.reflect.Method method = obj.getClass().getMethod(methodName);
            Object result = method.invoke(obj);
            return result != null ? result.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
