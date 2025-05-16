package com.belman.domain.user.factory;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.PersonName;
import com.belman.domain.common.valueobjects.PhoneNumber;
import com.belman.domain.security.HashedPassword;
import com.belman.domain.user.ApprovalState;
import com.belman.domain.user.UserBusiness;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;

import java.util.HashSet;

public class UserBusinessFactory {

    // Create User with minimum required properties
    public static UserBusiness createUser(Username username, HashedPassword password, EmailAddress email) {
        validateRequiredFields(username, password, email);
        return UserBusiness.createNewUser(username, password, email);
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

    // Create User with full properties including PersonName
    public static UserBusiness createUserWithName(Username username, HashedPassword password,
                                                  PersonName name, EmailAddress email) {
        validateRequiredFields(username, password, email);
        validateName(name);
        return new UserBusiness.Builder()
                .username(username)
                .password(password)
                .email(email)
                .name(name)
                .build();
    }

    // Validate PersonName
    private static void validateName(PersonName name) {
        if (name == null) {
            throw new IllegalArgumentException("PersonName cannot be null.");
        }
    }

    // Create User with all details manually defined (useful for updates or clone operations)
    public static UserBusiness createFullUser(UserId userId, Username username,
                                              HashedPassword password, PersonName name,
                                              EmailAddress email, PhoneNumber phoneNumber) {
        validateRequiredFields(username, password, email);
        validateUserId(userId);
        validateName(name);
        return UserBusiness.reconstitute(
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

    // Validate UserId
    private static void validateUserId(UserId userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null.");
        }
    }
}
