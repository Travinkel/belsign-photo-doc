package com.belman.domain.shared;

/**
 * A type-safe key for the state store.
 * <p>
 * This class provides a way to create type-safe keys for the state store,
 * which helps prevent type errors and provides better IDE support.
 *
 * @param <T> the type of the value associated with this key
 */
public final class StateKey<T> {
    private final String key;
    private final Class<T> type;

    /**
     * Creates a new StateKey with the specified key and type.
     *
     * @param key  the key string
     * @param type the class of the value type
     */
    private StateKey(String key, Class<T> type) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.key = key;
        this.type = type;
    }

    /**
     * Creates a new StateKey with the specified key and type.
     *
     * @param key  the key string
     * @param type the class of the value type
     * @param <T>  the type of the value
     * @return a new StateKey
     */
    public static <T> StateKey<T> of(String key, Class<T> type) {
        return new StateKey<>(key, type);
    }

    /**
     * Creates a StateKey for unknown type scenarios.
     *
     * @param key  the key string
     * @return a new StateKey with a null type
     */
    public static StateKey<Object> forUnknownType(String key) {
        return new StateKey<>(key, Object.class);
    }

    /**
     * Gets the key string.
     *
     * @return the key string
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the type class.
     *
     * @return the type class
     */
    public Class<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateKey<?> stateKey = (StateKey<?>) o;
        return key.equals(stateKey.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return "StateKey{" +
                "key='" + key + '\'' +
                ", type=" + type.getSimpleName() +
                '}';
    }
}