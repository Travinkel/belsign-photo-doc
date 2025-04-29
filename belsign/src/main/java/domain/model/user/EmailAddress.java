package domain.model.user;

import java.util.regex.Pattern;

/**
 * Value object for an email address.
 */
public record EmailAddress(String value) {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    /**
     * Creates an EmailAddress with the specified value.
     * 
     * @param value the email address string
     * @throws IllegalArgumentException if the email address is invalid
     */
    public EmailAddress {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    /**
     * @return the email address string
     * @deprecated Use value() instead
     */
    @Deprecated
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
