package com.belman.application.usecase.help;

import com.belman.domain.user.UserBusiness;

import java.util.List;
import java.util.Optional;

/**
 * Service for help and support.
 * Provides methods for accessing documentation and submitting feedback.
 */
public interface HelpService {
    /**
     * Gets the help documentation for a specific context.
     *
     * @param context the context for which to get documentation
     * @return the documentation for the context
     */
    String getDocumentation(String context);

    /**
     * Gets all available help contexts.
     *
     * @return a list of all available help contexts
     */
    List<String> getAvailableContexts();

    /**
     * Searches the documentation for a specific term.
     *
     * @param searchTerm the term to search for
     * @return a list of documentation entries that match the search term
     */
    List<String> searchDocumentation(String searchTerm);

    /**
     * Submits feedback.
     *
     * @param feedback    the feedback to submit
     * @param submittedBy the user who submitted the feedback
     * @param context     the context in which the feedback was submitted
     * @return true if the feedback was submitted successfully, false otherwise
     */
    boolean submitFeedback(String feedback, UserBusiness submittedBy, String context);

    /**
     * Submits feedback with a screenshot.
     *
     * @param feedback    the feedback to submit
     * @param screenshot  the screenshot to attach
     * @param submittedBy the user who submitted the feedback
     * @param context     the context in which the feedback was submitted
     * @return true if the feedback was submitted successfully, false otherwise
     */
    boolean submitFeedbackWithScreenshot(String feedback, byte[] screenshot, UserBusiness submittedBy, String context);

    /**
     * Gets the tutorial for a specific context.
     *
     * @param context the context for which to get the tutorial
     * @return an Optional containing the tutorial for the context if available, or empty if not available
     */
    Optional<String> getTutorial(String context);

    /**
     * Gets all available tutorial contexts.
     *
     * @return a list of all available tutorial contexts
     */
    List<String> getAvailableTutorialContexts();

    /**
     * Marks a tutorial as completed for a user.
     *
     * @param context the context of the tutorial
     * @param user    the user who completed the tutorial
     * @return true if the tutorial was marked as completed successfully, false otherwise
     */
    boolean markTutorialCompleted(String context, UserBusiness user);

    /**
     * Checks if a tutorial has been completed by a user.
     *
     * @param context the context of the tutorial
     * @param user    the user to check
     * @return true if the tutorial has been completed by the user, false otherwise
     */
    boolean isTutorialCompleted(String context, UserBusiness user);
}