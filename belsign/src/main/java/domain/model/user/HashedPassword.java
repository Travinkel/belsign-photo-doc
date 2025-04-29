package domain.model.user;

/**
 * Represents a securely hashed password.
 * Cannot be null or empty.
 */
public record HashedPassword(String value) {
    /**
     * Creates a new HashedPassword instance.
     *
     * @param value the hashed password value
     * @throws IllegalArgumentException if the value is null or empty
     */
    public HashedPassword {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hashed password must not be null or blank");
        }
    }
    /**
     * Checks if this hashed password matches another hashed password.
     *
     * @param other the other hashed password to compare with
     * @return true if the passwords match, false otherwise
     */
    public boolean matches(HashedPassword other) {
        return this.value.equals(other.value);
    }
}
