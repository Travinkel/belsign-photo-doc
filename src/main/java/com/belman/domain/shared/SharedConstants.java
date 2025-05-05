package com.belman.domain.shared;

/**
 * Contains constants that are shared across the domain layer.
 * This class is part of the domain layer and contains constants that are used by multiple domain components.
 */
public final class SharedConstants {
    
    // Private constructor to prevent instantiation
    private SharedConstants() {
        throw new AssertionError("SharedConstants is a utility class and should not be instantiated");
    }
    
    /**
     * Default values for domain objects
     */
    public static final class Defaults {
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_ORDER_NUMBER_LENGTH = 20;
        public static final int MAX_PHOTO_DESCRIPTION_LENGTH = 500;
    }
    
    /**
     * Regular expressions for validation
     */
    public static final class Regex {
        public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
        public static final String ORDER_NUMBER_PATTERN = "^[A-Z0-9-]+$";
        public static final String USERNAME_PATTERN = "^[A-Za-z0-9_]+$";
    }
    
    /**
     * Error messages for domain validation
     */
    public static final class ErrorMessages {
        public static final String INVALID_EMAIL = "Invalid email address";
        public static final String INVALID_ORDER_NUMBER = "Invalid order number";
        public static final String INVALID_USERNAME = "Invalid username";
        public static final String PASSWORD_TOO_SHORT = "Password must be at least %d characters long";
    }
}