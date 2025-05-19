package com.belman.dataaccess.provider;

import com.belman.domain.common.valueobjects.EmailAddress;
import com.belman.domain.common.valueobjects.Timestamp;
import com.belman.domain.customer.Company;
import com.belman.domain.customer.CustomerBusiness;
import com.belman.domain.customer.CustomerId;
import com.belman.domain.order.OrderBusiness;
import com.belman.domain.order.OrderId;
import com.belman.domain.order.OrderNumber;
import com.belman.domain.photo.PhotoTemplate;
import com.belman.domain.services.LoggerFactory;
import com.belman.domain.user.UserReference;
import com.belman.domain.user.UserId;
import com.belman.domain.user.Username;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the OrderProvider interface that provides orders from a mock folder.
 * This implementation scans a folder for order directories, parses metadata.json files,
 * and creates OrderBusiness objects from the metadata.
 */
public class MockFolderOrderProvider implements OrderProvider {

    private static final String MOCK_CAMERA_PATH = "src/main/resources/mock/camera";
    private static final String METADATA_FILENAME = "metadata.json";
    private final LoggerFactory loggerFactory;

    /**
     * Creates a new MockFolderOrderProvider with the specified logger factory.
     *
     * @param loggerFactory the logger factory
     */
    public MockFolderOrderProvider(LoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public List<OrderBusiness> fetchNewOrders() {
        List<OrderBusiness> orders = new ArrayList<>();

        try {
            // Recursively find all metadata.json files in the mock camera directory
            List<Path> metadataFiles = findMetadataFiles(Paths.get(MOCK_CAMERA_PATH));

            for (Path metadataPath : metadataFiles) {
                try {
                    // Get the order directory (parent of metadata.json)
                    Path orderDir = metadataPath.getParent();

                    // Parse metadata.json
                    String metadataJson = Files.readString(metadataPath);
                    OrderBusiness order = parseOrderFromMetadata(metadataJson, orderDir);
                    if (order != null) {
                        orders.add(order);
                    }
                } catch (Exception e) {
                    logError("Error processing metadata file: " + metadataPath, e);
                }
            }
        } catch (IOException e) {
            logError("Error accessing mock camera directory", e);
        }

        return orders;
    }

    /**
     * Recursively finds all metadata.json files in the specified directory and its subdirectories.
     *
     * @param directory the directory to search
     * @return a list of paths to metadata.json files
     * @throws IOException if an I/O error occurs
     */
    private List<Path> findMetadataFiles(Path directory) throws IOException {
        List<Path> metadataFiles = new ArrayList<>();

        if (!Files.exists(directory)) {
            logError("Directory does not exist: " + directory, null);
            return metadataFiles;
        }

        // Use Files.walk to recursively traverse the directory structure
        Files.walk(directory)
            .filter(path -> path.getFileName().toString().equals(METADATA_FILENAME))
            .forEach(metadataFiles::add);

        return metadataFiles;
    }

    @Override
    public boolean hasNewOrders() {
        try {
            // Use the findMetadataFiles method to check if any metadata.json files exist
            List<Path> metadataFiles = findMetadataFiles(Paths.get(MOCK_CAMERA_PATH));
            return !metadataFiles.isEmpty();
        } catch (IOException e) {
            logError("Error accessing mock camera directory", e);
            return false;
        }
    }

    @Override
    public String getName() {
        return "Mock Folder Order Provider";
    }

    @Override
    public String getDescription() {
        return "Provides orders from mock camera folder";
    }

    /**
     * Parses an OrderBusiness object from metadata JSON.
     *
     * @param metadataJson the metadata JSON
     * @param orderDir     the order directory
     * @return the parsed OrderBusiness object, or null if parsing failed
     */
    private OrderBusiness parseOrderFromMetadata(String metadataJson, Path orderDir) {
        try {
            // Parse JSON using simple string operations for demonstration
            // In a real application, you would use a proper JSON parser
            String orderNumber = extractJsonValue(metadataJson, "orderNumber");
            String customer = extractJsonValue(metadataJson, "customer");
            String date = extractJsonValue(metadataJson, "date");
            String templates = extractJsonValue(metadataJson, "templates");
            String createdBy = extractJsonValue(metadataJson, "createdBy");
            String productType = extractJsonValue(metadataJson, "productType");

            // Validate required fields
            if (orderNumber == null || orderNumber.isBlank()) {
                logError("Missing or empty orderNumber in metadata.json: " + orderDir, null);
                return null;
            }

            // If customer is not specified, use a default value
            if (customer == null || customer.isBlank()) {
                customer = "Belman A/S";
                logError("Missing customer in metadata.json: " + orderDir + ", using default: " + customer, null);
            }

            if (date == null || date.isBlank()) {
                // Try to extract date from order number or directory structure
                date = extractDateFromOrderNumberOrPath(orderNumber, orderDir);
                if (date == null) {
                    logError("Missing date in metadata.json and could not extract from order number or path: " + orderDir, null);
                    return null;
                }
            }

            // Validate date format (YYYY-MM-DD)
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                logError("Invalid date format in metadata.json: " + date + ". Expected format: YYYY-MM-DD", null);
                return null;
            }

            // Create OrderBusiness object
            OrderId orderId = OrderId.newId();
            OrderNumber orderNumberObj = new OrderNumber(orderNumber);

            // Parse date
            LocalDate orderDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            Instant orderInstant = orderDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            Timestamp orderTimestamp = new Timestamp(orderInstant);

            // Create customer
            CustomerId customerId = CustomerId.newId();
            // Create a simple company customer with a dummy email
            EmailAddress dummyEmail = new EmailAddress("info@" + customer.toLowerCase().replace(" ", "") + ".com");
            Company company = new Company(customer, "123 Main St", "VAT12345");
            CustomerBusiness customerObj = CustomerBusiness.company(customerId, company, dummyEmail);

            // Create user reference for created by
            UserId userId = UserId.newId();
            Username username = new Username(createdBy != null ? createdBy : "admin");
            UserReference userRef = new UserReference(userId, username);

            // Create OrderBusiness
            OrderBusiness order = new OrderBusiness(orderId, orderNumberObj, userRef, orderTimestamp);

            // Set customer ID
            order.setCustomerId(customerId);

            // Set product description if available
            if (productType != null && !productType.isBlank()) {
                com.belman.domain.order.ProductDescription productDesc = 
                    new com.belman.domain.order.ProductDescription(
                        productType,
                        "Specifications for " + productType,
                        "Notes for " + productType
                    );
                order.setProductDescription(productDesc);
            }

            // Set delivery information
            com.belman.domain.order.DeliveryInformation deliveryInfo = 
                new com.belman.domain.order.DeliveryInformation(
                    "Belman A/S, Oddesundvej 18, 6715 Esbjerg N",
                    orderDate.plusDays(14), // Due date is 2 weeks after order date
                    "Contact Person",
                    dummyEmail,
                    "Handle with care"
                );
            order.setDeliveryInformation(deliveryInfo);

            // Parse templates and add them to the order
            if (templates != null && !templates.isEmpty()) {
                String[] templateArray = templates.replace("[", "").replace("]", "").replace("\"", "").split(",");
                for (String template : templateArray) {
                    try {
                        PhotoTemplate photoTemplate = getPhotoTemplateByName(template.trim());
                        if (photoTemplate != null) {
                            // In a real implementation, we would associate the template with the order
                            // For now, we'll just log that we found a template
                            logInfo("Found template for order " + orderNumber + ": " + photoTemplate.name());
                        } else {
                            logError("Unknown template: " + template, null);
                        }
                    } catch (Exception e) {
                        logError("Error processing template: " + template, e);
                    }
                }
            }

            return order;
        } catch (Exception e) {
            logError("Error parsing metadata.json: " + orderDir, e);
            return null;
        }
    }

    /**
     * Extracts a date from an order number or directory path.
     * Order numbers often contain dates in the format ORD-XX-YYMMDD-XXX-XXXX.
     * Directory paths may contain dates in the structure /YYYY/MM/DD/.
     *
     * @param orderNumber the order number
     * @param orderDir    the order directory
     * @return a date string in the format YYYY-MM-DD, or null if no date could be extracted
     */
    private String extractDateFromOrderNumberOrPath(String orderNumber, Path orderDir) {
        // Try to extract date from order number (format: ORD-XX-YYMMDD-XXX-XXXX)
        if (orderNumber != null && orderNumber.contains("-")) {
            String[] parts = orderNumber.split("-");
            if (parts.length >= 3) {
                String datePart = parts[2];
                if (datePart.length() == 6 && datePart.matches("\\d{6}")) {
                    // Extract year, month, day
                    String year = "20" + datePart.substring(0, 2); // Assuming 20xx
                    String month = datePart.substring(2, 4);
                    String day = datePart.substring(4, 6);
                    return year + "-" + month + "-" + day;
                }
            }
        }

        // Try to extract date from directory path
        try {
            String pathStr = orderDir.toString();
            String[] pathParts = pathStr.split("[\\\\/]"); // Split by / or \

            // Look for year/month/day pattern
            for (int i = 0; i < pathParts.length - 2; i++) {
                if (pathParts[i].matches("\\d{4}") && // Year
                    pathParts[i+1].matches("\\d{2}") && // Month
                    pathParts[i+2].matches("\\d{2}")) { // Day
                    return pathParts[i] + "-" + pathParts[i+1] + "-" + pathParts[i+2];
                }
            }
        } catch (Exception e) {
            logError("Error extracting date from path: " + orderDir, e);
        }

        return null;
    }

    /**
     * Logs an info message.
     *
     * @param message the info message
     */
    private void logInfo(String message) {
        if (loggerFactory != null) {
            loggerFactory.getLogger(this.getClass()).info(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * Extracts a value from a JSON string.
     *
     * @param json the JSON string
     * @param key  the key to extract
     * @return the extracted value, or null if not found
     */
    private String extractJsonValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*";
        int start = json.indexOf(pattern);
        if (start == -1) {
            return null;
        }

        start += pattern.length();

        // Check if the value is a string
        if (json.charAt(start) == '"') {
            start++;
            int end = json.indexOf('"', start);
            if (end == -1) {
                return null;
            }
            return json.substring(start, end);
        }

        // Check if the value is an array
        if (json.charAt(start) == '[') {
            int end = json.indexOf(']', start) + 1;
            if (end == 0) {
                return null;
            }
            return json.substring(start, end);
        }

        // Assume the value is a number or boolean
        int end = json.indexOf(',', start);
        if (end == -1) {
            end = json.indexOf('}', start);
        }
        if (end == -1) {
            return null;
        }
        return json.substring(start, end).trim();
    }

    /**
     * Gets a PhotoTemplate by name.
     *
     * @param name the name of the template
     * @return the PhotoTemplate with the specified name, or null if not found
     */
    private PhotoTemplate getPhotoTemplateByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        // Check against all predefined templates
        if (name.equalsIgnoreCase("TOP_VIEW_OF_JOINT")) {
            return PhotoTemplate.TOP_VIEW_OF_JOINT;
        } else if (name.equalsIgnoreCase("SIDE_VIEW_OF_WELD")) {
            return PhotoTemplate.SIDE_VIEW_OF_WELD;
        } else if (name.equalsIgnoreCase("FRONT_VIEW_OF_ASSEMBLY")) {
            return PhotoTemplate.FRONT_VIEW_OF_ASSEMBLY;
        } else if (name.equalsIgnoreCase("BACK_VIEW_OF_ASSEMBLY")) {
            return PhotoTemplate.BACK_VIEW_OF_ASSEMBLY;
        } else if (name.equalsIgnoreCase("LEFT_VIEW_OF_ASSEMBLY")) {
            return PhotoTemplate.LEFT_VIEW_OF_ASSEMBLY;
        } else if (name.equalsIgnoreCase("RIGHT_VIEW_OF_ASSEMBLY")) {
            return PhotoTemplate.RIGHT_VIEW_OF_ASSEMBLY;
        } else if (name.equalsIgnoreCase("BOTTOM_VIEW_OF_ASSEMBLY")) {
            return PhotoTemplate.BOTTOM_VIEW_OF_ASSEMBLY;
        } else if (name.equalsIgnoreCase("CLOSE_UP_OF_WELD")) {
            return PhotoTemplate.CLOSE_UP_OF_WELD;
        } else if (name.equalsIgnoreCase("ANGLED_VIEW_OF_JOINT")) {
            return PhotoTemplate.ANGLED_VIEW_OF_JOINT;
        } else if (name.equalsIgnoreCase("OVERVIEW_OF_ASSEMBLY")) {
            return PhotoTemplate.OVERVIEW_OF_ASSEMBLY;
        } else if (name.equalsIgnoreCase("CUSTOM")) {
            return PhotoTemplate.CUSTOM;
        }

        // If no match is found, return null
        return null;
    }

    /**
     * Logs an error message.
     *
     * @param message the error message
     * @param e       the exception, or null if there is no exception
     */
    private void logError(String message, Exception e) {
        if (loggerFactory != null) {
            if (e != null) {
                loggerFactory.getLogger(this.getClass()).error(message, e);
            } else {
                loggerFactory.getLogger(this.getClass()).error(message);
            }
        } else {
            System.err.println(message);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
}
