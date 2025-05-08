package com.belman.business.richbe.user;

/**
 * Value object representing a reference to a user from another bounded context.
 * This follows the Domain-Driven Design principle of using references
 * across bounded contexts rather than passing entire aggregates.
 */
public record UserReference(UserId id, Username username) {

    /**
     * Creates a new UserReference with the specified ID and username.
     *
     * @param id       the user ID
     * @param username the username
     * @throws NullPointerException if id or username is null
     */
    public UserReference {
        if (id == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        if (username == null) {
            throw new IllegalArgumentException("Username must not be null");
        }
    }

    /**
     * Creates a UserReference from a UserAggregate.
     * This factory method makes it easier to create references to existing users.
     *
     * @param user the user to create a reference for
     * @return a new UserReference for the specified user
     * @throws NullPointerException if user is null
     */
    public static UserReference from(UserAggregate user) {
        if (user == null) {
            throw new NullPointerException("UserAggregate must not be null");
        }
        return new UserReference(user.getId(), user.getUsername());
    }
}
