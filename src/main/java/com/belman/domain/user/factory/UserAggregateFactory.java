package com.belman.domain.user.factory;

import com.belman.domain.common.EmailAddress;
import com.belman.domain.common.PersonName;
import com.belman.domain.common.PhoneNumber;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserAggregate;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;
import com.belman.domain.user.UserStatus;

public class UserAggregateFactory {

    // Create User with minimum required properties
    public static UserAggregate createUser(Username username, HashedPassword password, EmailAddress email) {
        validateRequiredFields(username, password, email);
        return new UserAggregate(
                username,
                password,
                email
        );
    }

    // Create User with full properties including PersonName
    public static UserAggregate createUserWithName(Username username, HashedPassword password,
                                                   PersonName name, EmailAddress email) {
        validateRequiredFields(username, password, email);
        validateName(name);
        return new UserAggregate(
                username,
                password,
                email,
                name
        );
    }

    // Create User with all details manually defined (useful for updates or clone operations)
    public static UserAggregate createFullUser(UserId userId, Username username,
                                               HashedPassword password, PersonName name,
                                               EmailAddress email, PhoneNumber phoneNumber) {
        validateRequiredFields(username, password, email);
        validateUserId(userId);
        validateName(name);
        return new UserAggregate(
                userId,
                username,
                password,
                name,
                email,
                phoneNumber,
                ApprovalState.createPending()
        );
    }

    // Validate required fields
    private static void validateRequiredFields(Username username, HashedPassword password, EmailAddress email) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null.");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }
        if (email == null) {
            throw new IllegalArgumentException("Email address cannot be null.");
        }
    }

    // Validate UserId
    private static void validateUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null.");
        }
    }

    // Validate PersonName
    private static void validateName(PersonName name) {
        if (name == null) {
            throw new IllegalArgumentException("PersonName cannot be null.");
        }
    }
}
