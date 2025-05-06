package com.belman.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value object representing a monetary amount.
 * <p>
 * This class is immutable and thread-safe. It handles monetary amounts
 * with appropriate precision and rounding, and ensures that operations
 * between different currencies are not allowed.
 */
public final class Money {

    private final BigDecimal amount;
    private final Currency currency;

    // Some common currencies for convenience
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency EUR = Currency.getInstance("EUR");
    public static final Currency GBP = Currency.getInstance("GBP");
    public static final Currency JPY = Currency.getInstance("JPY");
    public static final Currency DKK = Currency.getInstance("DKK");

    /**
     * Creates a new Money instance with the specified amount and currency.
     *
     * @param amount   the monetary amount
     * @param currency the currency
     * @throws NullPointerException     if amount or currency is null
     * @throws IllegalArgumentException if amount is negative
     */
    public Money(BigDecimal amount, Currency currency) {
        this.amount = Objects.requireNonNull(amount, "amount must not be null")
                .setScale(getScale(currency), RoundingMode.HALF_EVEN);
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
    }

    /**
     * Determines the appropriate scale for a given currency.
     * <p>
     * For most currencies, this will be the default fraction digits defined by the currency.
     * For currencies where the smallest unit is not a cent (e.g., JPY), this might be 0.
     *
     * @param currency the currency to determine scale for
     * @return the appropriate scale for the currency
     */
    private static int getScale(Currency currency) {
        return currency.getDefaultFractionDigits();
    }

    /**
     * Factory method to create a Money instance with zero amount in the specified currency.
     *
     * @param currency the currency
     * @return a new Money instance with zero amount in the specified currency
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Factory method to create a Money instance with the specified amount in the specified currency.
     *
     * @param amount   the monetary amount
     * @param currency the currency
     * @return a new Money instance with the specified amount in the specified currency
     */
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    /**
     * Factory method to create a Money instance with the specified amount in the specified currency.
     *
     * @param amount   the monetary amount as a double
     * @param currency the currency
     * @return a new Money instance with the specified amount in the specified currency
     */
    public static Money of(double amount, Currency currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    /**
     * Returns the amount of this Money.
     *
     * @return the monetary amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Returns the currency of this Money.
     *
     * @return the currency
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Adds another Money instance to this one.
     *
     * @param other the Money to add
     * @return a new Money instance representing the sum
     * @throws IllegalArgumentException if the currencies are different
     */
    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtracts another Money instance from this one.
     *
     * @param other the Money to subtract
     * @return a new Money instance representing the difference
     * @throws IllegalArgumentException if the currencies are different or the result would be negative
     */
    public Money subtract(Money other) {
        assertSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount cannot be negative");
        }
        return new Money(result, this.currency);
    }

    /**
     * Multiplies this Money by a factor.
     *
     * @param factor the factor to multiply by
     * @return a new Money instance representing the product
     */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor)
                .setScale(getScale(this.currency), RoundingMode.HALF_EVEN), this.currency);
    }

    /**
     * Multiplies this Money by a factor.
     *
     * @param factor the factor to multiply by
     * @return a new Money instance representing the product
     */
    public Money multiply(double factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    /**
     * Divides this Money by a divisor.
     *
     * @param divisor the divisor to divide by
     * @return a new Money instance representing the quotient
     * @throws ArithmeticException if divisor is zero
     */
    public Money divide(BigDecimal divisor) {
        return new Money(this.amount.divide(divisor, getScale(this.currency), RoundingMode.HALF_EVEN),
                this.currency);
    }

    /**
     * Divides this Money by a divisor.
     *
     * @param divisor the divisor to divide by
     * @return a new Money instance representing the quotient
     * @throws ArithmeticException if divisor is zero
     */
    public Money divide(double divisor) {
        return divide(BigDecimal.valueOf(divisor));
    }

    /**
     * Compares this Money with another.
     *
     * @param other the Money to compare with
     * @return -1, 0, or 1 if this Money is less than, equal to, or greater than other
     * @throws IllegalArgumentException if the currencies are different
     */
    public int compareTo(Money other) {
        assertSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    /**
     * Asserts that the given Money has the same currency as this one.
     *
     * @param other the Money to check
     * @throws IllegalArgumentException if the currencies are different
     */
    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Currency mismatch: " + this.currency + " != " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return amount.toString() + " " + currency.getCurrencyCode();
    }
}