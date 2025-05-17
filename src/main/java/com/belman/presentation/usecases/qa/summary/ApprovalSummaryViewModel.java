package com.belman.presentation.usecases.qa.summary;

import com.belman.common.di.Inject;
import com.belman.common.session.SessionContext;
import com.belman.presentation.base.BaseViewModel;
import com.belman.presentation.navigation.Router;
import com.belman.presentation.usecases.qa.dashboard.QADashboardView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

/**
 * ViewModel for the approval summary view.
 * Provides data and operations for displaying the result of the QA review process.
 */
public class ApprovalSummaryViewModel extends BaseViewModel<ApprovalSummaryViewModel> {
    private final StringProperty orderNumber = new SimpleStringProperty("");
    private final StringProperty summaryMessage = new SimpleStringProperty("");
    private final StringProperty comment = new SimpleStringProperty("");
    private final BooleanProperty approved = new SimpleBooleanProperty(false);

    @Inject
    private SessionContext sessionContext;

    /**
     * Default constructor for use by the ViewLoader.
     */
    public ApprovalSummaryViewModel() {
        // Default constructor
    }

    @Override
    public void initialize() {
        super.initialize();

        // Set default summary message
        summaryMessage.set("Order review completed");
    }

    @Override
    public void onShow() {
        // Get parameters from the navigation context

        // Get order number
        String orderNumberStr = Router.getParameter("orderNumber");
        if (orderNumberStr != null) {
            orderNumber.set(orderNumberStr);
        }

        // Get approval status
        Boolean approvedValue = Router.getParameter("approved");
        if (approvedValue != null) {
            approved.set(approvedValue);

            // Update summary message based on approval status
            if (approved.get()) {
                summaryMessage.set("Order " + orderNumber.get() + " has been approved");
            } else {
                summaryMessage.set("Order " + orderNumber.get() + " has been rejected");
            }
        }

        // Get comment
        String commentStr = Router.getParameter("comment");
        if (commentStr != null) {
            comment.set(commentStr);
        }
    }

    /**
     * Navigates back to the QA dashboard.
     */
    public void navigateToDashboard() {
        Router.navigateTo(QADashboardView.class);
    }

    /**
     * Gets the order number property.
     *
     * @return the order number property
     */
    public StringProperty orderNumberProperty() {
        return orderNumber;
    }

    /**
     * Gets the summary message property.
     *
     * @return the summary message property
     */
    public StringProperty summaryMessageProperty() {
        return summaryMessage;
    }

    /**
     * Gets the comment property.
     *
     * @return the comment property
     */
    public StringProperty commentProperty() {
        return comment;
    }

    /**
     * Gets the approved property.
     *
     * @return the approved property
     */
    public BooleanProperty approvedProperty() {
        return approved;
    }
}
