package com.belman.domain.photo;

/**
 * Enumeration of fields that can be required for a photo template.
 * These represent different types of information or characteristics
 * that a photo must have based on its template.
 */
public enum RequiredField {
    /**
     * Indicates that the photo must have at least one annotation.
     * Annotations provide additional context or notes about specific areas of the photo.
     */
    ANNOTATIONS,

    /**
     * Indicates that the photo must have metadata.
     * Metadata includes technical information such as resolution, file size, and image format.
     */
    METADATA,

    /**
     * Indicates that the photo must include measurement annotations.
     * Measurement annotations provide size or dimension information about elements in the photo.
     */
    MEASUREMENTS,

    /**
     * Indicates that the photo must mark any defects or issues.
     * Defect markings highlight problems or quality concerns in the photographed item.
     */
    DEFECT_MARKING,

    /**
     * Indicates that the photo must include reference points.
     * Reference points help establish scale or position in the photo.
     */
    REFERENCE_POINTS,

    /**
     * Indicates that the photo must include a timestamp.
     * Timestamps provide evidence of when the photo was taken.
     */
    TIMESTAMP,

    /**
     * Indicates that the photo must include location information.
     * Location information provides context about where the photo was taken.
     */
    LOCATION
}