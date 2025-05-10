package com.belman.architecture.analysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class to analyze the package structure of the business layer
 * and identify classes that should be moved to different packages based on
 * responsibility or feature.
 */
public class BusinessLayerPackageAnalyzer {

    private static final String BUSINESS_LAYER_PATH = "src/main/java/com/belman/business";
    private static final String MODULE_PATH = BUSINESS_LAYER_PATH + "/module";
    
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public\\s+(abstract\\s+)?(class|interface|enum)\\s+([A-Za-z0-9_]+)");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("package\\s+([A-Za-z0-9_.]+);");
    
    public static void main(String[] args) throws IOException {
        List<ClassInfo> classes = scanBusinessLayer();
        Map<String, List<ClassInfo>> violations = identifyViolations(classes);
        printViolations(violations);
    }
    
    private static List<ClassInfo> scanBusinessLayer() throws IOException {
        List<ClassInfo> classes = new ArrayList<>();
        
        try (Stream<Path> paths = Files.walk(Paths.get(BUSINESS_LAYER_PATH))) {
            List<Path> javaFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .collect(Collectors.toList());
            
            for (Path path : javaFiles) {
                ClassInfo classInfo = extractClassInfo(path);
                if (classInfo != null) {
                    classes.add(classInfo);
                }
            }
        }
        
        return classes;
    }
    
    private static ClassInfo extractClassInfo(Path path) throws IOException {
        String content = Files.readString(path);
        
        Matcher packageMatcher = PACKAGE_PATTERN.matcher(content);
        if (!packageMatcher.find()) {
            return null;
        }
        String packageName = packageMatcher.group(1);
        
        Matcher classMatcher = CLASS_NAME_PATTERN.matcher(content);
        if (!classMatcher.find()) {
            return null;
        }
        String className = classMatcher.group(3);
        
        return new ClassInfo(className, packageName, path);
    }
    
    private static Map<String, List<ClassInfo>> identifyViolations(List<ClassInfo> classes) {
        Map<String, List<ClassInfo>> violations = new HashMap<>();
        
        // Business objects should be in the module package
        List<ClassInfo> businessObjectViolations = classes.stream()
                .filter(c -> c.className.endsWith("Business") && !c.packageName.startsWith("com.belman.business.module"))
                .collect(Collectors.toList());
        if (!businessObjectViolations.isEmpty()) {
            violations.put("Business objects should be in the module package", businessObjectViolations);
        }
        
        // Business components should be in the module package
        List<ClassInfo> businessComponentViolations = classes.stream()
                .filter(c -> c.className.endsWith("Component") && !c.packageName.startsWith("com.belman.business.module"))
                .collect(Collectors.toList());
        if (!businessComponentViolations.isEmpty()) {
            violations.put("Business components should be in the module package", businessComponentViolations);
        }
        
        // Data access interfaces should be in the module package
        List<ClassInfo> dataAccessViolations = classes.stream()
                .filter(c -> (c.className.endsWith("DataAccess") || c.className.endsWith("Repository")) && 
                        !c.packageName.startsWith("com.belman.business.module"))
                .collect(Collectors.toList());
        if (!dataAccessViolations.isEmpty()) {
            violations.put("Data access interfaces should be in the module package", dataAccessViolations);
        }
        
        // Business services should be in the module package
        List<ClassInfo> businessServiceViolations = classes.stream()
                .filter(c -> (c.className.endsWith("BusinessService") || c.className.endsWith("DomainService")) && 
                        !c.packageName.startsWith("com.belman.business.module"))
                .collect(Collectors.toList());
        if (!businessServiceViolations.isEmpty()) {
            violations.put("Business services should be in the module package", businessServiceViolations);
        }
        
        // Data objects should be in the module package
        List<ClassInfo> dataObjectViolations = classes.stream()
                .filter(c -> (c.className.endsWith("DataObject") || c.className.endsWith("ValueObject")) && 
                        !c.packageName.startsWith("com.belman.business.module"))
                .collect(Collectors.toList());
        if (!dataObjectViolations.isEmpty()) {
            violations.put("Data objects should be in the module package", dataObjectViolations);
        }
        
        // Audit events should be in the module.events package or a feature-specific events package
        List<ClassInfo> auditEventViolations = classes.stream()
                .filter(c -> c.className.endsWith("Event") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.equals("com.belman.business.module.events") &&
                        !c.packageName.matches("com\\.belman\\.business\\.module\\.[^\\.]+\\.events"))
                .collect(Collectors.toList());
        if (!auditEventViolations.isEmpty()) {
            violations.put("Audit events should be in the module.events package or a feature-specific events package", auditEventViolations);
        }
        
        // Order-related classes should be in the module.order package
        List<ClassInfo> orderViolations = classes.stream()
                .filter(c -> c.className.contains("Order") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.order"))
                .collect(Collectors.toList());
        if (!orderViolations.isEmpty()) {
            violations.put("Order-related classes should be in the module.order package", orderViolations);
        }
        
        // User-related classes should be in the module.user package
        List<ClassInfo> userViolations = classes.stream()
                .filter(c -> c.className.contains("User") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.user"))
                .collect(Collectors.toList());
        if (!userViolations.isEmpty()) {
            violations.put("User-related classes should be in the module.user package", userViolations);
        }
        
        // Customer-related classes should be in the module.customer package
        List<ClassInfo> customerViolations = classes.stream()
                .filter(c -> c.className.contains("Customer") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.customer"))
                .collect(Collectors.toList());
        if (!customerViolations.isEmpty()) {
            violations.put("Customer-related classes should be in the module.customer package", customerViolations);
        }
        
        // Report-related classes should be in the module.report package
        List<ClassInfo> reportViolations = classes.stream()
                .filter(c -> c.className.contains("Report") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.report"))
                .collect(Collectors.toList());
        if (!reportViolations.isEmpty()) {
            violations.put("Report-related classes should be in the module.report package", reportViolations);
        }
        
        // Photo-related classes should be in the module.order.photo package
        List<ClassInfo> photoViolations = classes.stream()
                .filter(c -> c.className.contains("Photo") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.order.photo"))
                .collect(Collectors.toList());
        if (!photoViolations.isEmpty()) {
            violations.put("Photo-related classes should be in the module.order.photo package", photoViolations);
        }
        
        // Security-related classes should be in the module.security package
        List<ClassInfo> securityViolations = classes.stream()
                .filter(c -> (c.className.contains("Security") || 
                        c.className.contains("Authentication") || 
                        c.className.contains("Authorization") || 
                        c.className.contains("Password")) && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.security"))
                .collect(Collectors.toList());
        if (!securityViolations.isEmpty()) {
            violations.put("Security-related classes should be in the module.security package", securityViolations);
        }
        
        // Common value objects should be in the module.common package
        List<ClassInfo> commonValueObjectViolations = classes.stream()
                .filter(c -> (c.className.endsWith("Address") || 
                        c.className.endsWith("Name") || 
                        c.className.endsWith("Email") || 
                        c.className.endsWith("Phone") || 
                        c.className.endsWith("Money") || 
                        c.className.endsWith("Timestamp")) && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.startsWith("com.belman.business.module.common"))
                .collect(Collectors.toList());
        if (!commonValueObjectViolations.isEmpty()) {
            violations.put("Common value objects should be in the module.common package", commonValueObjectViolations);
        }
        
        // Specifications should be in the module.specification package or a feature-specific specification package
        List<ClassInfo> specificationViolations = classes.stream()
                .filter(c -> c.className.endsWith("Specification") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.equals("com.belman.business.module.specification") &&
                        !c.packageName.matches("com\\.belman\\.business\\.module\\.[^\\.]+\\.specification"))
                .collect(Collectors.toList());
        if (!specificationViolations.isEmpty()) {
            violations.put("Specifications should be in the module.specification package or a feature-specific specification package", specificationViolations);
        }
        
        // Exceptions should be in the module.exceptions package
        List<ClassInfo> exceptionViolations = classes.stream()
                .filter(c -> c.className.endsWith("Exception") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.equals("com.belman.business.module.exceptions"))
                .collect(Collectors.toList());
        if (!exceptionViolations.isEmpty()) {
            violations.put("Exceptions should be in the module.exceptions package", exceptionViolations);
        }
        
        // Service interfaces should be in the module.services package or a feature-specific services package
        List<ClassInfo> serviceViolations = classes.stream()
                .filter(c -> c.className.endsWith("Service") && 
                        c.packageName.startsWith("com.belman.business.module") &&
                        !c.packageName.equals("com.belman.business.module.services") &&
                        !c.packageName.matches("com\\.belman\\.business\\.module\\.[^\\.]+\\.services"))
                .collect(Collectors.toList());
        if (!serviceViolations.isEmpty()) {
            violations.put("Service interfaces should be in the module.services package or a feature-specific services package", serviceViolations);
        }
        
        return violations;
    }
    
    private static void printViolations(Map<String, List<ClassInfo>> violations) {
        if (violations.isEmpty()) {
            System.out.println("No violations found.");
            return;
        }
        
        System.out.println("Found " + violations.size() + " types of violations:");
        System.out.println();
        
        for (Map.Entry<String, List<ClassInfo>> entry : violations.entrySet()) {
            System.out.println("Rule: " + entry.getKey());
            System.out.println("Violations:");
            
            for (ClassInfo classInfo : entry.getValue()) {
                System.out.println("  - " + classInfo.className + " in " + classInfo.packageName);
                System.out.println("    Move to: " + getSuggestedPackage(classInfo, entry.getKey()));
            }
            
            System.out.println();
        }
    }
    
    private static String getSuggestedPackage(ClassInfo classInfo, String rule) {
        if (rule.contains("Business objects")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report";
            } else {
                return "com.belman.business.module";
            }
        } else if (rule.contains("Business components")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report";
            } else {
                return "com.belman.business.module";
            }
        } else if (rule.contains("Data access interfaces")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report";
            } else if (classInfo.className.contains("Photo")) {
                return "com.belman.business.module.order.photo";
            } else {
                return "com.belman.business.module";
            }
        } else if (rule.contains("Business services")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order.services";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user.services";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer.services";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report.services";
            } else if (classInfo.className.contains("Photo")) {
                return "com.belman.business.module.order.photo.services";
            } else {
                return "com.belman.business.module.services";
            }
        } else if (rule.contains("Data objects")) {
            return "com.belman.business.module.common.base";
        } else if (rule.contains("Audit events")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order.events";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user.events";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer.events";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report.events";
            } else if (classInfo.className.contains("Photo")) {
                return "com.belman.business.module.order.photo.events";
            } else {
                return "com.belman.business.module.events";
            }
        } else if (rule.contains("Order-related classes")) {
            if (classInfo.className.contains("Photo")) {
                return "com.belman.business.module.order.photo";
            } else {
                return "com.belman.business.module.order";
            }
        } else if (rule.contains("User-related classes")) {
            return "com.belman.business.module.user";
        } else if (rule.contains("Customer-related classes")) {
            return "com.belman.business.module.customer";
        } else if (rule.contains("Report-related classes")) {
            return "com.belman.business.module.report";
        } else if (rule.contains("Photo-related classes")) {
            return "com.belman.business.module.order.photo";
        } else if (rule.contains("Security-related classes")) {
            return "com.belman.business.module.security";
        } else if (rule.contains("Common value objects")) {
            return "com.belman.business.module.common";
        } else if (rule.contains("Specifications")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order.specification";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user.specification";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer.specification";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report.specification";
            } else if (classInfo.className.contains("Photo")) {
                return "com.belman.business.module.order.photo.specification";
            } else {
                return "com.belman.business.module.specification";
            }
        } else if (rule.contains("Exceptions")) {
            return "com.belman.business.module.exceptions";
        } else if (rule.contains("Service interfaces")) {
            if (classInfo.className.contains("Order")) {
                return "com.belman.business.module.order.services";
            } else if (classInfo.className.contains("User")) {
                return "com.belman.business.module.user.services";
            } else if (classInfo.className.contains("Customer")) {
                return "com.belman.business.module.customer.services";
            } else if (classInfo.className.contains("Report")) {
                return "com.belman.business.module.report.services";
            } else if (classInfo.className.contains("Photo")) {
                return "com.belman.business.module.order.photo.services";
            } else {
                return "com.belman.business.module.services";
            }
        } else {
            return "com.belman.business.module";
        }
    }
    
    private static class ClassInfo {
        private final String className;
        private final String packageName;
        private final Path path;
        
        public ClassInfo(String className, String packageName, Path path) {
            this.className = className;
            this.packageName = packageName;
            this.path = path;
        }
    }
}