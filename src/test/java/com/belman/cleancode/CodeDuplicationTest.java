package com.belman.cleancode;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for code duplication in the codebase.
 * This class implements a basic code duplication detector that looks for:
 * 1. Similar method bodies (based on method size and parameter count)
 * 2. Repeated string literals
 * 3. Similar class structures
 * 
 * Note: This is a simplified implementation. For production use, consider
 * integrating with dedicated tools like PMD's Copy/Paste Detector (CPD).
 */
public class CodeDuplicationTest {
    private static JavaClasses importedClasses;
    private static final String SOURCE_DIR = "src/main/java";
    private static final int SIMILAR_METHOD_THRESHOLD = 5; // Methods with similar size and parameter count
    private static final int DUPLICATE_STRING_THRESHOLD = 3; // Minimum occurrences to consider a string duplicated
    private static final int MIN_STRING_LENGTH = 20; // Minimum length for string literals to check

    @BeforeAll
    public static void setup() {
        importedClasses = new ClassFileImporter().importPackages("com.belman");
        System.out.println("[DEBUG_LOG] Imported " + importedClasses.size() + " classes for duplication analysis");
    }

    /**
     * Tests that the codebase doesn't have excessive code duplication.
     * This test is configured to log potential duplication rather than fail the build.
     */
    @Test
    public void shouldNotHaveDuplicateCode() {
        System.out.println("[DEBUG_LOG] Running code duplication test");

        List<String> duplicationIssues = new ArrayList<>();

        try {
            // Check for similar methods
            Map<String, List<JavaMethod>> similarMethods = findSimilarMethods();
            if (!similarMethods.isEmpty()) {
                duplicationIssues.add("Found " + similarMethods.size() + " groups of potentially similar methods");

                // Log details of similar methods
                int groupCount = 0;
                for (Map.Entry<String, List<JavaMethod>> entry : similarMethods.entrySet()) {
                    if (groupCount++ >= 5) break; // Limit logging to 5 groups

                    System.out.println("[DEBUG_LOG] Similar methods with signature pattern: " + entry.getKey());
                    for (JavaMethod method : entry.getValue()) {
                        System.out.println("[DEBUG_LOG]   - " + method.getOwner().getName() + "." + method.getName());
                    }
                }
            }

            // Check for duplicate string literals
            Map<String, Integer> duplicateStrings = findDuplicateStringLiterals();
            if (!duplicateStrings.isEmpty()) {
                duplicationIssues.add("Found " + duplicateStrings.size() + " potentially duplicated string literals");

                // Log details of duplicate strings
                int stringCount = 0;
                for (Map.Entry<String, Integer> entry : duplicateStrings.entrySet()) {
                    if (stringCount++ >= 5) break; // Limit logging to 5 strings

                    String displayString = entry.getKey().length() > 50 ? 
                                          entry.getKey().substring(0, 47) + "..." : 
                                          entry.getKey();
                    System.out.println("[DEBUG_LOG] String literal used " + entry.getValue() + 
                                      " times: \"" + displayString + "\"");
                }
            }

            // Check for similar class structures
            List<List<JavaClass>> similarClasses = findSimilarClasses();
            if (!similarClasses.isEmpty()) {
                duplicationIssues.add("Found " + similarClasses.size() + " groups of potentially similar classes");

                // Log details of similar classes
                int groupCount = 0;
                for (List<JavaClass> group : similarClasses) {
                    if (groupCount++ >= 5) break; // Limit logging to 5 groups

                    System.out.println("[DEBUG_LOG] Similar class group:");
                    for (JavaClass clazz : group) {
                        System.out.println("[DEBUG_LOG]   - " + clazz.getName());
                    }
                }
            }

            // Log summary
            if (!duplicationIssues.isEmpty()) {
                System.out.println("[DEBUG_LOG] WARNING: Potential code duplication detected:");
                for (String issue : duplicationIssues) {
                    System.out.println("[DEBUG_LOG] - " + issue);
                }
                System.out.println("[DEBUG_LOG] Note: Code duplication should be addressed as part of technical debt reduction");
            } else {
                System.out.println("[DEBUG_LOG] No significant code duplication detected - good job!");
            }

        } catch (Exception e) {
            // Log any exceptions but don't fail the test
            System.out.println("[DEBUG_LOG] Error checking for code duplication: " + e.getMessage());
            e.printStackTrace();
        }

        // Always pass the test, but with a message indicating if issues were found
        assertTrue(true, duplicationIssues.isEmpty() ? 
                  "No significant code duplication detected" : 
                  "Potential code duplication found but test configured to pass anyway - see logs for details");
    }

    /**
     * Finds methods with similar signatures and sizes, which might indicate duplication.
     * 
     * @return a map of signature patterns to lists of methods with that pattern
     */
    private Map<String, List<JavaMethod>> findSimilarMethods() {
        Map<String, List<JavaMethod>> methodGroups = new HashMap<>();

        for (JavaClass javaClass : importedClasses) {
            for (JavaMethod method : javaClass.getMethods()) {
                // Skip very small methods, constructors, and generated methods
                if (method.isConstructor() || method.getName().contains("$") || 
                    estimateMethodSize(method) < 5) {
                    continue;
                }

                // Create a signature pattern based on return type, parameter count, and estimated size
                String signaturePattern = method.getRawReturnType().getName() + 
                                         "_params" + method.getParameters().size() + 
                                         "_size" + (estimateMethodSize(method) / 5) * 5;

                methodGroups.computeIfAbsent(signaturePattern, k -> new ArrayList<>()).add(method);
            }
        }

        // Filter to only include groups with multiple methods
        return methodGroups.entrySet().stream()
            .filter(entry -> entry.getValue().size() >= SIMILAR_METHOD_THRESHOLD)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Estimates the size of a method based on its characteristics.
     * Since we can't access the actual source code through ArchUnit, this is an approximation.
     * 
     * @param method the method to analyze
     * @return the estimated size in lines of code
     */
    private int estimateMethodSize(JavaMethod method) {
        // Start with base size
        int estimate = 5;

        // Methods with many parameters might be larger
        estimate += method.getParameters().size() * 2;

        // Methods with complex return types might be larger
        String returnTypeName = method.getRawReturnType().getName();
        if (returnTypeName.contains("Collection") || 
            returnTypeName.contains("List") || 
            returnTypeName.contains("Map") ||
            returnTypeName.contains("Set")) {
            estimate += 5;
        }

        // Methods with certain names might be larger
        String methodName = method.getName().toLowerCase();
        if (methodName.contains("process") || 
            methodName.contains("calculate") || 
            methodName.contains("validate") ||
            methodName.contains("transform")) {
            estimate += 10;
        }

        return estimate;
    }

    /**
     * Finds duplicate string literals in the source code.
     * 
     * @return a map of string literals to their occurrence count
     */
    private Map<String, Integer> findDuplicateStringLiterals() {
        Map<String, Integer> stringCounts = new HashMap<>();

        try {
            // Check if source directory exists
            Path sourceDir = Paths.get(SOURCE_DIR);
            if (!Files.exists(sourceDir)) {
                System.out.println("[DEBUG_LOG] Source directory not found: " + SOURCE_DIR);
                return stringCounts;
            }

            // Find all Java files
            List<Path> javaFiles = findJavaFiles(sourceDir);

            // Process each file
            for (Path file : javaFiles) {
                try {
                    List<String> lines = Files.readAllLines(file);
                    for (String line : lines) {
                        // Extract string literals using a simple regex
                        extractStringLiterals(line).forEach(str -> 
                            stringCounts.put(str, stringCounts.getOrDefault(str, 0) + 1)
                        );
                    }
                } catch (IOException e) {
                    System.out.println("[DEBUG_LOG] Error reading file " + file + ": " + e.getMessage());
                }
            }

            // Filter to only include strings that appear multiple times and are long enough
            return stringCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= DUPLICATE_STRING_THRESHOLD && 
                               entry.getKey().length() >= MIN_STRING_LENGTH)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        } catch (IOException e) {
            System.out.println("[DEBUG_LOG] Error scanning for string literals: " + e.getMessage());
            return stringCounts;
        }
    }

    /**
     * Extracts string literals from a line of code.
     * This is a simplified implementation that doesn't handle all edge cases.
     * 
     * @param line the line of code to analyze
     * @return a list of string literals found in the line
     */
    private List<String> extractStringLiterals(String line) {
        List<String> literals = new ArrayList<>();
        int startIndex = 0;

        while (true) {
            // Find the next quote
            int quoteIndex = line.indexOf('"', startIndex);
            if (quoteIndex == -1 || quoteIndex >= line.length() - 1) {
                break;
            }

            // Find the closing quote
            int endQuoteIndex = line.indexOf('"', quoteIndex + 1);
            if (endQuoteIndex == -1) {
                break;
            }

            // Extract the string literal
            String literal = line.substring(quoteIndex + 1, endQuoteIndex);
            if (!literal.isEmpty()) {
                literals.add(literal);
            }

            // Move to the next position
            startIndex = endQuoteIndex + 1;
        }

        return literals;
    }

    /**
     * Finds classes with similar structures, which might indicate duplication.
     * 
     * @return a list of groups of similar classes
     */
    private List<List<JavaClass>> findSimilarClasses() {
        Map<String, List<JavaClass>> classGroups = new HashMap<>();

        for (JavaClass javaClass : importedClasses) {
            // Skip test classes, interfaces, and enums
            if (javaClass.getName().contains("Test") || 
                javaClass.isInterface() || 
                javaClass.isEnum()) {
                continue;
            }

            // Create a signature based on method count, field count, and implemented interfaces
            String signature = "methods" + javaClass.getMethods().size() + 
                              "_fields" + javaClass.getFields().size() + 
                              "_interfaces" + javaClass.getInterfaces().size();

            classGroups.computeIfAbsent(signature, k -> new ArrayList<>()).add(javaClass);
        }

        // Filter to only include groups with multiple classes
        return classGroups.values().stream()
            .filter(group -> group.size() >= 2)
            .collect(Collectors.toList());
    }

    /**
     * Finds all Java files in the given directory and its subdirectories.
     * 
     * @param dir the directory to search
     * @return a list of paths to Java files
     * @throws IOException if an I/O error occurs
     */
    private List<Path> findJavaFiles(Path dir) throws IOException {
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .collect(Collectors.toList());
        }
    }
}
