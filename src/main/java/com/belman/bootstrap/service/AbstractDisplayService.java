package com.belman.bootstrap.service;

/**
 * This class has been deprecated and replaced by the dynamic proxy implementation
 * in DisplayServiceFactory. The AbstractDisplayService was causing compilation errors
 * due to incompatible method signatures with the DisplayService interface.
 * 
 * @see com.belman.bootstrap.platform.DisplayServiceFactory
 * @deprecated Use DisplayServiceFactory.getDisplayService() instead
 */
@Deprecated
public abstract class AbstractDisplayService {
    // This class is now empty and deprecated.
    // All functionality has been moved to the dynamic proxy implementation in DisplayServiceFactory.
}
