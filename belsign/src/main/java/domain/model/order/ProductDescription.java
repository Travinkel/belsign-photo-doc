package domain.model.order;

import java.util.Objects;

/**
 * Value object representing a description of a product in an order.
 */
public record ProductDescription(String name, String description, String specifications) {
    /**
     * Creates a ProductDescription with the specified name, description, and specifications.
     * 
     * @param name the product name
     * @param description the product description
     * @param specifications the product specifications
     * @throws IllegalArgumentException if name is blank
     */
    public ProductDescription {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
    }
    
    /**
     * Creates a ProductDescription with just a name.
     * 
     * @param name the product name
     * @return a new ProductDescription with the specified name
     */
    public static ProductDescription withName(String name) {
        return new ProductDescription(name, null, null);
    }
    
    /**
     * Creates a ProductDescription with a name and description.
     * 
     * @param name the product name
     * @param description the product description
     * @return a new ProductDescription with the specified name and description
     */
    public static ProductDescription withNameAndDescription(String name, String description) {
        return new ProductDescription(name, description, null);
    }
    
    /**
     * @return a string representation of the product description
     */
    @Override
    public String toString() {
        return name + (description != null ? ": " + description : "");
    }
}