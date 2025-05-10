package com.belman.domain.order.photo.policy;

import com.belman.domain.order.ProductDescription;
import com.belman.domain.order.photo.PhotoTemplate;

import java.util.*;

/**
 * Default implementation of IPhotoQualityService that defines quality requirements
 * for photos based on product type.
 * <p>
 * This policy encapsulates business rules about which types of photos are required
 * for different products, minimum quality standards, and annotation requirements.
 */
public class PhotoQualityPolicy implements IPhotoQualityService {

    // Map of product categories to required photo templates
    private final Map<String, Set<PhotoTemplate>> requiredTemplates;

    // Map of product categories to minimum number of photos required
    private final Map<String, Integer> minimumPhotoCounts;

    // Map of product categories to annotation requirements
    private final Map<String, Boolean> annotationRequirements;

    /**
     * Creates a new PhotoQualityPolicy with default settings.
     */
    public PhotoQualityPolicy() {
        this.requiredTemplates = initializeRequiredTemplates();
        this.minimumPhotoCounts = initializeMinimumPhotoCounts();
        this.annotationRequirements = initializeAnnotationRequirements();
    }

    private Map<String, Set<PhotoTemplate>> initializeRequiredTemplates() {
        Map<String, Set<PhotoTemplate>> templateMap = new HashMap<>();

        // Standard products require front, left, right, and back photos
        templateMap.put("STANDARD", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
        ));

        // Simple products only require front and back photos
        templateMap.put("SIMPLE", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY
        ));

        // Complex products require photos from all standard angles plus top and bottom
        templateMap.put("COMPLEX", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY
        ));

        // Custom products may require additional templates
        templateMap.put("CUSTOM", Set.of(
                PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY,
                PhotoTemplate.BACK_VIEW_OF_ASSEMBLY,
                PhotoTemplate.TOP_VIEW_OF_JOINT,
                PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY,
                PhotoTemplate.CUSTOM
        ));

        return Collections.unmodifiableMap(templateMap);
    }

    private Map<String, Integer> initializeMinimumPhotoCounts() {
        Map<String, Integer> countMap = new HashMap<>();
        countMap.put("STANDARD", 4);
        countMap.put("SIMPLE", 2);
        countMap.put("COMPLEX", 6);
        countMap.put("CUSTOM", 8);

        return Collections.unmodifiableMap(countMap);
    }

    private Map<String, Boolean> initializeAnnotationRequirements() {
        Map<String, Boolean> requirementsMap = new HashMap<>();
        requirementsMap.put("STANDARD", true);
        requirementsMap.put("SIMPLE", false);
        requirementsMap.put("COMPLEX", true);
        requirementsMap.put("CUSTOM", true);

        return Collections.unmodifiableMap(requirementsMap);
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
        return requiredTemplates.getOrDefault(category, Collections.emptySet());
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
        return minimumPhotoCounts.getOrDefault(category, 2); // Default to 2 photos minimum
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
        return annotationRequirements.getOrDefault(category, false);
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
