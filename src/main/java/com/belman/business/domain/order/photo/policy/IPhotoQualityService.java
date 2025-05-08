package com.belman.business.domain.order.photo.policy;

import com.belman.business.domain.order.ProductDescription;
import com.belman.business.domain.order.photo.PhotoTemplate;

import java.util.Set;

/**
 * Interface defining the contract for photo quality services.
 */
public interface IPhotoQualityService {

    /**
     * Gets the required photo templates for a given product.
     *
     * @param productDescription the product to get photo requirements for
     * @return a set of required photo templates
     */
    Set<PhotoTemplate> getRequiredTemplates(ProductDescription productDescription);

    /**
     * Gets the minimum number of photos required for a given product.
     *
     * @param productDescription the product to get photo requirements for
     * @return the minimum number of photos required
     */
    int getMinimumPhotoCount(ProductDescription productDescription);

    /**
     * Determines if annotations are required for photos of a given product.
     *
     * @param productDescription the product to check annotation requirements for
     * @return true if annotations are required, false otherwise
     */
    boolean requiresAnnotations(ProductDescription productDescription);
}
