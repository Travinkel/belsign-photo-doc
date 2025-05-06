package com.belman.domain.order.policy;

import com.belman.domain.common.Money;
import com.belman.domain.order.OrderAggregate;
import com.belman.domain.order.ProductDescription;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Domain policy that encapsulates pricing rules for orders.
 * <p>
 * This policy defines how prices are calculated based on product characteristics,
 * order details, and business rules. It follows the Policy Pattern from Domain-Driven Design,
 * which encapsulates complex business rules that might otherwise pollute entities or services.
 */
public class OrderPricingPolicy {

    private final Currency defaultCurrency;
    private final Map<String, BigDecimal> productCategoryPrices;
    private final BigDecimal photoDocumentationFee;

    /**
     * Creates a new OrderPricingPolicy with the specified parameters.
     *
     * @param defaultCurrency       the default currency to use for prices
     * @param photoDocumentationFee the fee for photo documentation services
     */
    public OrderPricingPolicy(Currency defaultCurrency, BigDecimal photoDocumentationFee) {
        this.defaultCurrency = Objects.requireNonNull(defaultCurrency, "defaultCurrency must not be null");
        this.photoDocumentationFee = Objects.requireNonNull(photoDocumentationFee,
                "photoDocumentationFee must not be null");

        if (photoDocumentationFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("photoDocumentationFee must not be negative");
        }

        this.productCategoryPrices = new HashMap<>();
        initializeDefaultPrices();
    }

    private void initializeDefaultPrices() {
        // Initialize default prices for product categories
        productCategoryPrices.put("STANDARD", BigDecimal.valueOf(100));
        productCategoryPrices.put("SIMPLE", BigDecimal.valueOf(50));
        productCategoryPrices.put("COMPLEX", BigDecimal.valueOf(200));
        productCategoryPrices.put("CUSTOM", BigDecimal.valueOf(300));
    }

    /**
     * Sets the base price for a specific product category.
     *
     * @param category the product category
     * @param price    the base price for the category
     * @throws IllegalArgumentException if price is negative
     */
    public void setProductCategoryPrice(String category, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must not be negative");
        }
        productCategoryPrices.put(category.toUpperCase(), price);
    }

    /**
     * Calculates the base price for a product based on its description.
     *
     * @param productDescription the product description
     * @return the base price for the product
     */
    public Money calculateBasePrice(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        BigDecimal basePrice = productCategoryPrices.getOrDefault(category, BigDecimal.valueOf(100));

        return Money.of(basePrice, defaultCurrency);
    }

    /**
     * Calculates the photo documentation fee based on product complexity.
     *
     * @param productDescription the product description
     * @return the photo documentation fee
     */
    public Money calculatePhotoDocumentationFee(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        BigDecimal multiplier = BigDecimal.ONE;

        // Apply different multipliers based on product category
        if ("COMPLEX".equals(category)) {
            multiplier = BigDecimal.valueOf(1.5);
        } else if ("CUSTOM".equals(category)) {
            multiplier = BigDecimal.valueOf(2.0);
        } else if ("SIMPLE".equals(category)) {
            multiplier = BigDecimal.valueOf(0.75);
        }

        return Money.of(photoDocumentationFee.multiply(multiplier), defaultCurrency);
    }

    /**
     * Calculates the total price for an order based on its product and characteristics.
     *
     * @param order the order to calculate the price for
     * @return the total price for the order
     */
    public Money calculateTotalPrice(OrderAggregate order) {
        Objects.requireNonNull(order, "order must not be null");
        Objects.requireNonNull(order.getProductDescription(), "order product description must not be null");

        Money basePrice = calculateBasePrice(order.getProductDescription());
        Money photoFee = calculatePhotoDocumentationFee(order.getProductDescription());

        return basePrice.add(photoFee);
    }

    /**
     * Determines the product category based on product description.
     * This method applies business rules to categorize products.
     *
     * @param productDescription the product description to categorize
     * @return the product category
     */
    private String determineProductCategory(ProductDescription productDescription) {
        String name = productDescription.name().toLowerCase();
        String specifications = productDescription.specifications().toLowerCase();
        String notes = productDescription.notes() != null ? productDescription.notes().toLowerCase() : "";

        String combined = name + " " + specifications + " " + notes;

        if (combined.contains("custom") || combined.contains("special order")) {
            return "CUSTOM";
        } else if (combined.contains("complex") || combined.contains("assembly")) {
            return "COMPLEX";
        } else if (combined.contains("simple") || combined.contains("basic")) {
            return "SIMPLE";
        } else {
            return "STANDARD";
        }
    }
}