package com.belman.business.domain.order;

/**
 * Value object representing a description of a product in an order.
 * This is specific to the order bounded context.
 */
public record ProductDescription(String name, String specifications, String notes) {

    /**
     * Creates a new ProductDescription with the specified name, specifications, and notes.
     *
     * @param name           the name of the product
     * @param specifications the specifications of the product
     * @param notes          additional notes about the product (can be null)
     * @throws IllegalArgumentException if name or specifications is null or empty
     */
    public ProductDescription {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank");
        }

        if (specifications == null || specifications.isBlank()) {
            throw new IllegalArgumentException("Product specifications must not be null or blank");
        }

        // notes can be null
    }

    /**
     * Creates a new ProductDescription with the specified name and specifications but no notes.
     *
     * @param name           the name of the product
     * @param specifications the specifications of the product
     * @return a new ProductDescription with the specified name and specifications
     * @throws IllegalArgumentException if name or specifications is null or empty
     */
    public static ProductDescription create(String name, String specifications) {
        return new ProductDescription(name, specifications, null);
    }

    /**
     * Returns a string representation of this product description.
     *
     * @return a string representation of this product description
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Product: ").append(name);
        sb.append(", Specifications: ").append(specifications);

        if (notes != null && !notes.isBlank()) {
            sb.append(", Notes: ").append(notes);
        }

        return sb.toString();
    }
}