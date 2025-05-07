package com.belman.domain.order.photo.policy;

import com.belman.domain.order.ProductDescription;
import com.belman.domain.order.photo.PhotoAngle;

import java.util.*;

/**
 * Domain policy that defines photo documentation requirements for different product types.
 * This policy encapsulates business rules about which types of photos are required
 * for specific product categories and types.
 */
public class ProductPhotoRequirementPolicy {

    // Map of product categories to required photo angles
    private static final Map<String, Set<PhotoAngle>> REQUIRED_ANGLES;

    // Map of product categories to minimum number of photos required
    private static final Map<String, Integer> MINIMUM_PHOTO_COUNT;

    static {
        // Initialize photo angle requirements by product category
        Map<String, Set<PhotoAngle>> angleMap = new HashMap<>();

        // Standard products require front, left, right, and back photos
        Set<PhotoAngle> standardAngles = new HashSet<>();
        standardAngles.add(PhotoAngle.FRONT);
        standardAngles.add(PhotoAngle.LEFT);
        standardAngles.add(PhotoAngle.RIGHT);
        standardAngles.add(PhotoAngle.BACK);
        angleMap.put("STANDARD", Collections.unmodifiableSet(standardAngles));

        // Simple products only require front and back photos
        Set<PhotoAngle> simpleAngles = new HashSet<>();
        simpleAngles.add(PhotoAngle.FRONT);
        simpleAngles.add(PhotoAngle.BACK);
        angleMap.put("SIMPLE", Collections.unmodifiableSet(simpleAngles));

        // Complex products require photos from all standard angles plus top and bottom
        Set<PhotoAngle> complexAngles = new HashSet<>();
        complexAngles.add(PhotoAngle.FRONT);
        complexAngles.add(PhotoAngle.LEFT);
        complexAngles.add(PhotoAngle.RIGHT);
        complexAngles.add(PhotoAngle.BACK);
        complexAngles.add(PhotoAngle.TOP);
        complexAngles.add(PhotoAngle.BOTTOM);
        angleMap.put("COMPLEX", Collections.unmodifiableSet(complexAngles));

        REQUIRED_ANGLES = Collections.unmodifiableMap(angleMap);

        // Initialize minimum photo counts by product category
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("STANDARD", 4);
        countMap.put("SIMPLE", 2);
        countMap.put("COMPLEX", 6);
        countMap.put("CUSTOM", 8);

        MINIMUM_PHOTO_COUNT = Collections.unmodifiableMap(countMap);
    }

    /**
     * Gets the required photo angles for a given product.
     *
     * @param productDescription the product to get photo requirements for
     * @return an unmodifiable set of required photo angles
     */
    public Set<PhotoAngle> getRequiredAngles(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        return REQUIRED_ANGLES.getOrDefault(category, Collections.emptySet());
    }

    /**
     * Gets the minimum number of photos required for a given product.
     *
     * @param productDescription the product to get photo requirements for
     * @return the minimum number of photos required
     */
    public int getMinimumPhotoCount(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        return MINIMUM_PHOTO_COUNT.getOrDefault(category, 2); // Default to 2 photos minimum
    }

    /**
     * Determines the product category based on product description.
     * This method applies business rules to categorize products.
     *
     * @param productDescription the product description to categorize
     * @return the product category
     */
    private String determineProductCategory(ProductDescription productDescription) {
        // In a real implementation, this would examine product attributes
        // to determine the correct category.
        // This is a simplified example.

        // Combine name, specifications and notes for categorization
        String combined = String.join(" ",
                productDescription.name(),
                productDescription.specifications(),
                productDescription.notes() != null ? productDescription.notes() : "").toLowerCase();

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