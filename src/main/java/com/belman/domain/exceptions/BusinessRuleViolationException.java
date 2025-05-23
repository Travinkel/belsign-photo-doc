package com.belman.domain.exceptions;

/**
 * Exception thrown when a business rule is violated.
 * This is a business exception since business rules are part of the business logic.
 */
public class BusinessRuleViolationException extends BusinessException {

    /**
     * Creates a new BusinessRuleViolationException with the specified message.
     *
     * @param message the detail message
     */
    public BusinessRuleViolationException(String message) {
        super(message);
    }

    /**
     * Creates a new BusinessRuleViolationException with the specified rule and reason.
     *
     * @param rule   the business rule that was violated
     * @param reason the reason why the rule was violated
     */
    public BusinessRuleViolationException(String rule, String reason) {
        super("Business rule violated: " + rule + ". Reason: " + reason);
    }

    /**
     * Creates a new BusinessRuleViolationException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
