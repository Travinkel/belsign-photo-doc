package com.belman.business.richbe.user.factory;

import com.belman.business.richbe.common.EmailAddress;
import com.belman.business.richbe.common.PersonName;
import com.belman.business.richbe.common.PhoneNumber;
import com.belman.business.richbe.security.HashedPassword;
import com.belman.business.richbe.user.ApprovalState;
import com.belman.business.richbe.user.UserAggregate;
import com.belman.business.richbe.user.UserId;
import com.belman.business.richbe.user.Username;

import java.util.HashSet;

public class UserAggregateFactory {

    // Create User with minimum required properties
    public static UserAggregate createUser(Username username, HashedPassword password, EmailAddress email) {
        validateRequiredFields(username, password, email);
        return UserAggregate.createNewUser(username, password, email);
    }

    // Create User with full properties including PersonName
    public static UserAggregate createUserWithName(Username username, HashedPassword password,
                                                   PersonName name, EmailAddress email) {
        validateRequiredFields(username, password, email);
        validateName(name);
        return new UserAggregate.Builder()
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .build();
    }

    // Create User with all details manually defined (useful for updates or clone operations)
    public static UserAggregate createFullUser(UserId userId, Username username,
                                               HashedPassword password, PersonName name,
                                               EmailAddress email, PhoneNumber phoneNumber) {
        validateRequiredFields(username, password, email);
        validateUserId(userId);
        validateName(name);
        return UserAggregate.reconstitute(
                userId,
                username,
                password,
                name,
                email,
                phoneNumber,
                ApprovalState.createPendingState(),
                new HashSet<>()
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
