package util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for enforcing naming conventions in the athomefx framework.
 * This class provides methods to validate and generate names for views, controllers, and view models.
 */
public class NamingConventions {
    
    // Patterns for different component types
    private static final Pattern VIEW_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*View$");
    private static final Pattern CONTROLLER_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*Controller$");
    private static final Pattern VIEWMODEL_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*(ViewModel|Model)$");
    private static final Pattern SERVICE_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*Service$");
    
    // Suffixes for different component types
    private static final String VIEW_SUFFIX = "View";
    private static final String CONTROLLER_SUFFIX = "Controller";
    private static final String VIEWMODEL_SUFFIX = "ViewModel";
    private static final String MODEL_SUFFIX = "Model";
    private static final String SERVICE_SUFFIX = "Service";
    
    /**
     * Validates that a view class name follows the naming convention.
     * 
     * @param className the class name to validate
     * @return true if the class name follows the convention, false otherwise
     */
    public static boolean isValidViewName(String className) {
        return VIEW_PATTERN.matcher(className).matches();
    }
    
    /**
     * Validates that a controller class name follows the naming convention.
     * 
     * @param className the class name to validate
     * @return true if the class name follows the convention, false otherwise
     */
    public static boolean isValidControllerName(String className) {
        return CONTROLLER_PATTERN.matcher(className).matches();
    }
    
    /**
     * Validates that a view model class name follows the naming convention.
     * 
     * @param className the class name to validate
     * @return true if the class name follows the convention, false otherwise
     */
    public static boolean isValidViewModelName(String className) {
        return VIEWMODEL_PATTERN.matcher(className).matches();
    }
    
    /**
     * Validates that a service class name follows the naming convention.
     * 
     * @param className the class name to validate
     * @return true if the class name follows the convention, false otherwise
     */
    public static boolean isValidServiceName(String className) {
        return SERVICE_PATTERN.matcher(className).matches();
    }
    
    /**
     * Gets the base name from a component name by removing the suffix.
     * For example, "LoginView" -> "Login".
     * 
     * @param componentName the component name
     * @return the base name
     */
    public static String getBaseName(String componentName) {
        if (componentName.endsWith(VIEW_SUFFIX)) {
            return componentName.substring(0, componentName.length() - VIEW_SUFFIX.length());
        } else if (componentName.endsWith(CONTROLLER_SUFFIX)) {
            return componentName.substring(0, componentName.length() - CONTROLLER_SUFFIX.length());
        } else if (componentName.endsWith(VIEWMODEL_SUFFIX)) {
            return componentName.substring(0, componentName.length() - VIEWMODEL_SUFFIX.length());
        } else if (componentName.endsWith(MODEL_SUFFIX)) {
            return componentName.substring(0, componentName.length() - MODEL_SUFFIX.length());
        } else if (componentName.endsWith(SERVICE_SUFFIX)) {
            return componentName.substring(0, componentName.length() - SERVICE_SUFFIX.length());
        }
        return componentName;
    }
    
    /**
     * Gets the view name for a given base name.
     * For example, "Login" -> "LoginView".
     * 
     * @param baseName the base name
     * @return the view name
     */
    public static String getViewName(String baseName) {
        return baseName + VIEW_SUFFIX;
    }
    
    /**
     * Gets the controller name for a given base name.
     * For example, "Login" -> "LoginController".
     * 
     * @param baseName the base name
     * @return the controller name
     */
    public static String getControllerName(String baseName) {
        return baseName + CONTROLLER_SUFFIX;
    }
    
    /**
     * Gets the view model name for a given base name.
     * For example, "Login" -> "LoginViewModel".
     * 
     * @param baseName the base name
     * @return the view model name
     */
    public static String getViewModelName(String baseName) {
        return baseName + VIEWMODEL_SUFFIX;
    }
    
    /**
     * Gets the model name for a given base name.
     * For example, "Login" -> "LoginModel".
     * 
     * @param baseName the base name
     * @return the model name
     */
    public static String getModelName(String baseName) {
        return baseName + MODEL_SUFFIX;
    }
    
    /**
     * Gets the service name for a given base name.
     * For example, "Login" -> "LoginService".
     * 
     * @param baseName the base name
     * @return the service name
     */
    public static String getServiceName(String baseName) {
        return baseName + SERVICE_SUFFIX;
    }
    
    /**
     * Gets all possible view model names for a given view name.
     * For example, "LoginView" -> ["LoginViewModel", "LoginModel"].
     * 
     * @param viewName the view name
     * @return a list of possible view model names
     */
    public static List<String> getPossibleViewModelNames(String viewName) {
        String baseName = getBaseName(viewName);
        return Arrays.asList(
            getViewModelName(baseName),
            getModelName(baseName)
        );
    }
    
    /**
     * Gets all possible view model class names for a given view class.
     * This includes different package patterns.
     * 
     * @param viewClass the view class
     * @return a list of possible view model class names
     */
    public static List<String> getPossibleViewModelClassNames(Class<?> viewClass) {
        String viewClassName = viewClass.getSimpleName();
        String packageName = viewClass.getPackageName();
        String baseName = getBaseName(viewClassName);
        
        return Arrays.asList(
            packageName + "." + getViewModelName(baseName),
            packageName + "." + getModelName(baseName),
            packageName + ".viewmodel." + getViewModelName(baseName),
            packageName + ".viewmodel." + getModelName(baseName)
        );
    }
    
    /**
     * Gets the FXML file name for a given view name.
     * For example, "LoginView" -> "LoginView.fxml".
     * 
     * @param viewName the view name
     * @return the FXML file name
     */
    public static String getFxmlFileName(String viewName) {
        return viewName + ".fxml";
    }
}