package com.belman.domain.order.photo.policy;

import com.belman.domain.order.ProductDescription;
import com.belman.domain.order.photo.PhotoTemplate;

import java.util.*;

/**
 * Implementation of IPhotoQualityService that defines photo documentation
 * requirements for different product types based on predefined rules.
 * <p>
 * This policy ensures that critical areas such as joints and welds are documented
 * according to Belman's quality control standards.
 */
public class ProductPhotoRequirementPolicy implements IPhotoQualityService {

    // Map of product categories to required photo templates
    private static final Map<String, Set<PhotoTemplate>> REQUIRED_TEMPLATES;

    // Map of product categories to minimum number of photos required
    private static final Map<String, Integer> MINIMUM_PHOTO_COUNTS;

    // Map of product categories to annotation requirements
    private static final Map<String, Boolean> ANNOTATION_REQUIREMENTS;

    static {
        // Initialize required photo templates by product category
        Map<String, Set<PhotoTemplate>> templateMap = new HashMap<>();
        templateMap.put("STANDARD", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.SIDE_VIEW_OF_WELD,
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
        ));
        templateMap.put("SIMPLE", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
        ));
        templateMap.put("COMPLEX", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.SIDE_VIEW_OF_WELD,
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
        ));
        templateMap.put("CUSTOM", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.SIDE_VIEW_OF_WELD,
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                PhotoTemplate.CUSTOM
        ));
        REQUIRED_TEMPLATES = Collections.unmodifiableMap(templateMap);

        // Initialize minimum photo counts by product category
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("STANDARD", 4);
        countMap.put("SIMPLE", 2);
        countMap.put("COMPLEX", 5);
        countMap.put("CUSTOM", 6);
        MINIMUM_PHOTO_COUNTS = Collections.unmodifiableMap(countMap);

        // Initialize annotation requirements by product category
        Map<String, Boolean> annotationMap = new HashMap<>();
        annotationMap.put("STANDARD", true);
        annotationMap.put("SIMPLE", false);
        annotationMap.put("COMPLEX", true);
        annotationMap.put("CUSTOM", true);
        ANNOTATION_REQUIREMENTS = Collections.unmodifiableMap(annotationMap);
    }

    /**
     * Gets the required photo templates for a given product.
     *
     * @param productDescription the product to get photo requirements for
     * @return an unmodifiable set of required photo templates
     */
    @Override
    public Set<PhotoTemplate> getRequiredTemplates(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        return REQUIRED_TEMPLATES.getOrDefault(category, Collections.emptySet());
    }

    /**
     * Gets the minimum number of photos required for a given product.
     *
     * @param productDescription the product to get photo requirements for
     * @return the minimum number of photos required
     */
    @Override
    public int getMinimumPhotoCount(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        return MINIMUM_PHOTO_COUNTS.getOrDefault(category, 2); // Default to 2 photos minimum
    }

    /**
     * Determines if annotations are required for photos of a given product.
     *
     * @param productDescription the product to check annotation requirements for
     * @return true if annotations are required, false otherwise
     */
    @Override
    public boolean requiresAnnotations(ProductDescription productDescription) {
        Objects.requireNonNull(productDescription, "productDescription must not be null");

        String category = determineProductCategory(productDescription);
        return ANNOTATION_REQUIREMENTS.getOrDefault(category, false);
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
