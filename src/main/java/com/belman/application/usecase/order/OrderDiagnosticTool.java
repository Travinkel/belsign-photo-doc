package com.belman.application.usecase.order;

import com.belman.application.base.BaseService;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.order.OrderRepository;
import com.belman.domain.services.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Diagnostic tool for checking the database for order data issues.
 * This tool can be used to identify and report issues with order data in the database.
 */
public class OrderDiagnosticTool extends BaseService {

    private final OrderRepository orderRepository;
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new OrderDiagnosticTool with the specified dependencies.
     *
     * @param loggerFactory   the logger factory
     * @param orderRepository the order repository
     */
    public OrderDiagnosticTool(LoggerFactory loggerFactory, OrderRepository orderRepository) {
        super(loggerFactory);
        this.orderRepository = orderRepository;
        this.loggerFactory = loggerFactory;
    }

    @Override
    protected LoggerFactory getLoggerFactory() {
        return loggerFactory;
    }

    /**
     * Checks the database for order data issues and returns a report.
     *
     * @return a report of the issues found
     */
    public DiagnosticReport checkOrderData() {
        logInfo("Starting order data diagnostic check");
        DiagnosticReport report = new DiagnosticReport();

        try {
            // Get all orders from the repository
            List<OrderBusiness> orders = orderRepository.findAll();
            report.totalOrders = orders.size();
            logInfo("Found " + orders.size() + " orders in the database");

            // Check each order for issues
            for (OrderBusiness order : orders) {
                checkOrder(order, report);
            }

            // Log summary
            logInfo("Order data diagnostic check completed");
            logInfo("Total orders: " + report.totalOrders);
            logInfo("Orders with missing order numbers: " + report.ordersWithMissingOrderNumbers.size());
            logInfo("Orders with invalid order numbers: " + report.ordersWithInvalidOrderNumbers.size());
            logInfo("Orders with missing customer IDs: " + report.ordersWithMissingCustomerIds.size());
            logInfo("Orders with missing product descriptions: " + report.ordersWithMissingProductDescriptions.size());
            logInfo("Orders with missing delivery information: " + report.ordersWithMissingDeliveryInformation.size());
            logInfo("Orders with missing created by: " + report.ordersWithMissingCreatedBy.size());
            logInfo("Orders with missing created at: " + report.ordersWithMissingCreatedAt.size());
        } catch (Exception e) {
            logError("Error checking order data", e);
            report.errorMessage = e.getMessage();
        }

        return report;
    }

    /**
     * Checks a single order for issues and updates the report.
     *
     * @param order  the order to check
     * @param report the report to update
     */
    private void checkOrder(OrderBusiness order, DiagnosticReport report) {
        // Check order number
        if (order.getOrderNumber() == null) {
            report.ordersWithMissingOrderNumbers.add(order.getId());
        } else {
            try {
                // Validate order number format
                new OrderNumber(order.getOrderNumber().value());
            } catch (IllegalArgumentException e) {
                report.ordersWithInvalidOrderNumbers.add(order.getId());
            }
        }

        // Check customer ID
        if (order.getCustomerId() == null) {
            report.ordersWithMissingCustomerIds.add(order.getId());
        }

        // Check product description
        if (order.getProductDescription() == null) {
            report.ordersWithMissingProductDescriptions.add(order.getId());
        }

        // Check delivery information
        if (order.getDeliveryInformation() == null) {
            report.ordersWithMissingDeliveryInformation.add(order.getId());
        }

        // Check created by
        if (order.getCreatedBy() == null) {
            report.ordersWithMissingCreatedBy.add(order.getId());
        }

        // Check created at
        if (order.getCreatedAt() == null) {
            report.ordersWithMissingCreatedAt.add(order.getId());
        }
    }

    /**
     * Attempts to fix issues with order data in the database.
     *
     * @param report the diagnostic report containing the issues to fix
     * @return a report of the fixes applied
     */
    public FixReport fixOrderData(DiagnosticReport report) {
        logInfo("Starting order data fix");
        FixReport fixReport = new FixReport();

        try {
            // Fix orders with invalid order numbers
            for (OrderId orderId : report.ordersWithInvalidOrderNumbers) {
                Optional<OrderBusiness> orderOpt = orderRepository.findById(orderId);
                if (orderOpt.isPresent()) {
                    OrderBusiness order = orderOpt.get();
                    String orderNumberStr = order.getOrderNumber().value();
                    logInfo("Attempting to fix invalid order number: " + orderNumberStr + " for order ID: " + orderId.id());

                    try {
                        // Try to create a valid order number from the invalid one
                        if (orderNumberStr.startsWith("ORD-")) {
                            // Extract parts and try to create a valid legacy format
                            String[] parts = orderNumberStr.split("-");
                            if (parts.length >= 5) {
                                // Ensure each part has the correct format
                                String prefix = "ORD";
                                String number = parts[1].length() == 2 ? parts[1] : String.format("%02d", Integer.parseInt(parts[1]));
                                String date = parts[2].length() == 6 ? parts[2] : "230101"; // Default date if invalid
                                String code = parts[3].length() == 3 ? parts[3].toUpperCase() : "XXX"; // Default code if invalid
                                String sequence = parts[4].length() == 4 ? parts[4] : "0001"; // Default sequence if invalid

                                String fixedOrderNumber = prefix + "-" + number + "-" + date + "-" + code + "-" + sequence;
                                logInfo("Fixed order number: " + orderNumberStr + " -> " + fixedOrderNumber);

                                // Update the order with the fixed order number
                                order.setOrderNumber(new OrderNumber(fixedOrderNumber));
                                orderRepository.save(order);
                                fixReport.fixedOrderNumbers++;
                            }
                        }
                    } catch (Exception e) {
                        logError("Error fixing order number: " + orderNumberStr, e);
                        fixReport.errorMessages.add("Error fixing order number: " + orderNumberStr + " - " + e.getMessage());
                    }
                }
            }

            // Log summary
            logInfo("Order data fix completed");
            logInfo("Fixed order numbers: " + fixReport.fixedOrderNumbers);
            logInfo("Errors: " + fixReport.errorMessages.size());
        } catch (Exception e) {
            logError("Error fixing order data", e);
            fixReport.errorMessages.add("Error fixing order data: " + e.getMessage());
        }

        return fixReport;
    }

    /**
     * Report of the issues found during the diagnostic check.
     */
    public static class DiagnosticReport {
        public int totalOrders = 0;
        public List<OrderId> ordersWithMissingOrderNumbers = new ArrayList<>();
        public List<OrderId> ordersWithInvalidOrderNumbers = new ArrayList<>();
        public List<OrderId> ordersWithMissingCustomerIds = new ArrayList<>();
        public List<OrderId> ordersWithMissingProductDescriptions = new ArrayList<>();
        public List<OrderId> ordersWithMissingDeliveryInformation = new ArrayList<>();
        public List<OrderId> ordersWithMissingCreatedBy = new ArrayList<>();
        public List<OrderId> ordersWithMissingCreatedAt = new ArrayList<>();
        public String errorMessage = null;

        /**
         * Checks if there are any issues in the report.
         *
         * @return true if there are any issues, false otherwise
         */
        public boolean hasIssues() {
            return !ordersWithMissingOrderNumbers.isEmpty() ||
                   !ordersWithInvalidOrderNumbers.isEmpty() ||
                   !ordersWithMissingCustomerIds.isEmpty() ||
                   !ordersWithMissingProductDescriptions.isEmpty() ||
                   !ordersWithMissingDeliveryInformation.isEmpty() ||
                   !ordersWithMissingCreatedBy.isEmpty() ||
                   !ordersWithMissingCreatedAt.isEmpty() ||
                   errorMessage != null;
        }
    }

    /**
     * Report of the fixes applied during the fix operation.
     */
    public static class FixReport {
        public int fixedOrderNumbers = 0;
        public List<String> errorMessages = new ArrayList<>();

        /**
         * Checks if there were any errors during the fix operation.
         *
         * @return true if there were any errors, false otherwise
         */
        public boolean hasErrors() {
            return !errorMessages.isEmpty();
        }
    }
}
